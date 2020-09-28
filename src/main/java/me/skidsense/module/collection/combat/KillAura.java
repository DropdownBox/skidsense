package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.EventManager;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.friend.FriendManager;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.GLUtil;
import me.skidsense.util.MathUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TeamUtils;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToDoubleFunction;

import org.lwjgl.opengl.GL11;

public class KillAura extends Mod {
	public Mode<AuraPriority> priority = new Mode<>("Priority", "Priority", AuraPriority.values(), AuraPriority.Angle);
	public Mode<AuraMode> mode = new Mode<>("Mode", "Mode", AuraMode.values(), AuraMode.Switch);
	public Option<Boolean> players = new Option<Boolean>("Players", "Players", true);
	public Option<Boolean> mobs = new Option<Boolean>("Mobs", "Mobs", false);
	public Option<Boolean> animals = new Option<Boolean>("Animals", "Animals", false);
	public Option<Boolean> invis = new Option<Boolean>("Invisible", "Invisible", false);
	public Option<Boolean> villager = new Option<Boolean>("Villager", "Villager", false);
	public Option<Boolean> autoBlock = new Option<Boolean>("AutoBlock", "AutoBlock", true);
	public Option<Boolean> targetinfo = new Option<Boolean>("TargetInfo", "TargetInfo", true);
	public Option<Boolean> walls = new Option<Boolean>("ThroughWalls", "ThroughWalls", true);
	public Option<Boolean> autodisable = new Option<Boolean>("AutoDisable", "AutoDisable", true);
	public Numbers<Double> range = new Numbers<Double>("Range", "Range", 4.2, 3.5, 7.0, 0.1);
	public Numbers<Double> cps = new Numbers<Double>("APS", "APS", 9.0, 1.0, 20.0, 1.0);
	public Numbers<Double> switchdelay = new Numbers<Double>("SwitchDelay", "SwitchDelay", 100.0, 0.0, 1000.0, 50.0);
	public boolean isBlocking;
	private int index;
	TimerUtil switchtimer = new TimerUtil();
	TimerUtil timer = new TimerUtil();
	private float[] serverAngles = new float[2];
	private List<EntityLivingBase> loaded = new CopyOnWriteArrayList<EntityLivingBase>();
	public static EntityLivingBase target;
	public static EntityLivingBase slowtarget;

	public KillAura() {
		super("Kill Aura", new String[] { "Aura","KillAura" }, ModuleType.Fight);
		//this.addValues(this.priority, this.mode,this.range, this.cps, this.blockrange, this.hitswitch, this.players, this.mobs, this.animals, this.invis,
		//		this.autoBlock, this.targetinfo, this.walls, this.autodisable);
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (this.isBlocking) {
			mc.playerController.onStoppedUsingItem(mc.thePlayer);
			this.isBlocking = false;
		}
		target = null;
	}
	
	@Sub
	public void onPreMotion(EventPreUpdate eventMotion) {
		if(Client.getModuleManager().getModuleByClass(Flight.class).isEnabled() && Flight.mode.getValue() == Flight.FlyMode.HypixelDamage){
			Notifications.getManager().post("KillAura is not compatible with Flight.");
			setEnabled(false);
		}
		if(target != null) {
			slowtarget = target;
		}
		if (!mc.thePlayer.isEntityAlive() && this.autodisable.getValue()) {
			this.setEnabled(false);
		}
		this.setSuffix(mode.getValue());
		if (autoBlock.getValue() && this.canBlock() && this.isBlocking) {
			mc.playerController.onStoppedUsingItem(mc.thePlayer);
			this.isBlocking = false;
		}
		this.loaded = this.sortList(this.getTargets());
		if (!this.loaded.isEmpty()) {
			if (this.loaded.size() > 1 && target != null && mode.getValue() == AuraMode.Switch) {
		        if(target.hurtTime != 0 && switchtimer.hasReached(switchdelay.getValue())) {
		            ++this.index;
		            switchtimer.reset();
		        }
			}else if (mode.getValue() == AuraMode.Single) {
			    if (target.getDistanceToEntity(mc.thePlayer) > range.getValue()) {
			        ++index;
			    } else if (target.isDead) {
			        ++index;
			    }
			}
		    if (this.index >= this.loaded.size()) {
		        this.index = 0;
		    }
		    target = (EntityLivingBase) this.loaded.get(this.index);
			float[] array = getRotationsToEnt(target , Minecraft.getMinecraft().thePlayer);
            final float[] srcAngle = new float[]{serverAngles[0], serverAngles[1]};
            serverAngles = smoothAngle(array, srcAngle);
            eventMotion.setYaw(serverAngles[0]);
            eventMotion.setPitch(serverAngles[1]);
			mc.thePlayer.rotationYawHead = array[0];
			mc.thePlayer.renderYawOffset = array[0];
		}else {
			target = null;
		}
	}

	@Sub
	public void EventPostUpdate(me.skidsense.hooks.events.EventPostUpdate e) {
		if (target != null && this.shouldAttack()) {
			this.attack();
			timer.reset();
		}

		if (target != null && this.canBlock() && !this.isBlocking && autoBlock.getValue()) {
			mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
			this.isBlocking = true;
		}
	}

	@Sub
	public void onRender3D(EventRender3D eventRender3D) {
		if (target != null) {
            final double x = RenderUtil.interpolate(target.posX, target.lastTickPosX, eventRender3D.getPartialTicks());
            final double y = RenderUtil.interpolate(target.posY, target.lastTickPosY, eventRender3D.getPartialTicks());
            final double z = RenderUtil.interpolate(target.posZ, target.lastTickPosZ, eventRender3D.getPartialTicks());
            drawEntityESP(x - Minecraft.getMinecraft().getRenderManager().renderPosX, y + target.height + 0.1 - target.height - Minecraft.getMinecraft().getRenderManager().renderPosY, z - Minecraft.getMinecraft().getRenderManager().renderPosZ, target.height, 0.65, new Color(target.hurtTime > 0 ? 0xE33726 : RenderUtil.getRainbow(4000, 0, 0.85f)));
        }
	}
	
	private boolean shouldAttack() {
		return this.timer.hasReached(1000.0 / (this.cps.getValue() + MathUtil.randomDouble(0.0, 3.0)));
	}

	public void attack() {
		if (this.isBlocking && this.canBlock()) {
			mc.playerController.onStoppedUsingItem(mc.thePlayer);
			mc.thePlayer.clearItemInUse();
			this.isBlocking = false;
		}
		EventAttack ent = new EventAttack(target);
		EventManager.postAll(ent);
		if(ent.isCancelled())
			return;
		mc.thePlayer.swingItem();
		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
		if (this.canBlock() && !this.isBlocking && autoBlock.getValue()) {
			mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
			this.isBlocking = true;
		}
	}

	private List<EntityLivingBase> getTargets() {
		ArrayList<EntityLivingBase> list = new ArrayList<EntityLivingBase>();
		Iterator<Entity> loadedentity = mc.theWorld.getLoadedEntityList().iterator();
		while(loadedentity.hasNext()) {
			Object o = loadedentity.next();
			if (o instanceof EntityLivingBase) {
				EntityLivingBase entityLivingBase = (EntityLivingBase)o;
				if (validEntity(entityLivingBase))
					list.add(entityLivingBase);
			}
		}
		return list;
	}

	public boolean canBlock() {
		return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
	}

    private float[] smoothAngle(float[] dst, float[] src) {
        float[] smoothedAngle = new float[2];
        smoothedAngle[0] = (src[0] - dst[0]);
        smoothedAngle[1] = (src[1] - dst[1]);
        smoothedAngle = MathUtil.constrainAngle(smoothedAngle);
        smoothedAngle[0] = (src[0] - smoothedAngle[0] / 100 * MathUtil.getRandomInRange(10, 20));
        smoothedAngle[1] = (src[1] - smoothedAngle[1] / 100 * MathUtil.getRandomInRange(0, 15));
        return smoothedAngle;
    }
    
	   private boolean validEntity(EntityLivingBase entity) {
		   	AntiBot ab = (AntiBot) Client.getModuleManager().getModuleByClass(AntiBot.class);
		      boolean players = this.players.getValue();
		      boolean animals = this.animals.getValue();
		      boolean mobs = this.mobs.getValue();
		      boolean villager = this.villager.getValue();
		      boolean invis = this.invis.getValue();
		      boolean teams = Client.getModuleManager().getModuleByClass(Teams.class).isEnabled();
		      float range = this.range.getValue().floatValue();
		      if (mc.thePlayer.getHealth() > 0.0F && entity.getHealth() > 0.0F && !entity.isDead) {
		         boolean raytrace = walls.getValue() || mc.thePlayer.canEntityBeSeen(entity);
		         if (mc.thePlayer.getDistanceToEntity(entity) <= range && raytrace) {

		            if (entity instanceof EntityPlayer && players) {
		            	
		               if (!ab.isServerBot(entity) && !entity.isPlayerSleeping()) {
		                  EntityPlayer ent = (EntityPlayer)entity;
		                  return (!TeamUtils.isTeam(mc.thePlayer, ent) || !teams) && (!ent.isInvisible() || invis) && !FriendManager.isFriend(ent.getName());
		               }

		               return false;
		            }

		            return (entity instanceof EntityMob || entity instanceof EntitySlime) && mobs || entity instanceof EntityAnimal && animals || entity instanceof EntityVillager && villager;
		         }
		      }

		      return false;
		   }

	private List<EntityLivingBase> sortList(List<EntityLivingBase> list) {
		if (this.priority.getValue() == AuraPriority.Range) {
			list.sort(this::DistanceToEntity);
		}
		if (this.priority.getValue() == AuraPriority.Fov) {
			list.sort(Comparator.comparingDouble((ToDoubleFunction<? super Entity>) this::Fov));
		}
		if (this.priority.getValue() == AuraPriority.Angle) {
			list.sort(Comparator.comparingDouble((o) -> {return (double)RotationUtil.getRotations(o)[0];}));
		}
		if (this.priority.getValue() == AuraPriority.Health) {
			list.sort(KillAura::Health);
		}
		return list;
	}

    private float[] getRotationsToEnt(Entity ent, EntityPlayerSP playerSP) {
        final double differenceX = ent.posX - playerSP.posX;
        final double differenceY = (ent.posY + ent.height) - (playerSP.posY + playerSP.height);
        final double differenceZ = ent.posZ - playerSP.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, playerSP.getDistanceToEntity(ent)) * 180.0D / Math.PI);
        final float finishedYaw = playerSP.rotationYaw + MathHelper.wrapAngleTo180_float(rotationYaw - playerSP.rotationYaw);
        final float finishedPitch = playerSP.rotationPitch + MathHelper.wrapAngleTo180_float(rotationPitch - playerSP.rotationPitch);
        return new float[]{finishedYaw, -finishedPitch};
    }
    
	public static float getDistanceBetweenAngles(float n, float n2) {
		float n3 = Math.abs(n - n2) % 360.0f;
		if (n3 > 180.0f) {
			n3 = 360.0f - n3;
		}
		return n3;
	}

    private void drawEntityESP(double x, double y, double z, double height, double width, Color color) {
        GL11.glPushMatrix();
        GLUtil.setGLCap(3042, true);
        GLUtil.setGLCap(3553, false);
        GLUtil.setGLCap(2896, false);
        GLUtil.setGLCap(2929, false);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.8f);
        GL11.glBlendFunc(770, 771);
        GLUtil.setGLCap(2848, true);
        GL11.glDepthMask(true);
        RenderUtil.BB(new AxisAlignedBB(x - width + 0.25, y, z - width + 0.25, x + width - 0.25, y + height, z + width - 0.25), new Color(color.getRed(), color.getGreen(), color.getBlue(), 120).getRGB());
        RenderUtil.OutlinedBB(new AxisAlignedBB(x - width + 0.25, y, z - width + 0.25, x + width - 0.25, y + height, z + width - 0.25), 1, color.getRGB());
        GLUtil.revertAllCaps();
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }
    
	private static int Health(Entity entity, Entity entity2) {
		return (int) (((EntityLivingBase) entity).getHealth() - ((EntityLivingBase) entity2).getHealth());
	}

	private double Fov(Entity entity) {
		return getDistanceBetweenAngles(mc.thePlayer.rotationPitch, rotateNCP(entity)[0]);
	}

	public static float[] rotateNCP(Entity a1) {
		if (a1 == null) {
			return null;
		} else {
			double v1 = a1.posX - Minecraft.getMinecraft().thePlayer.posX;
			double v3 = a1.posY + (double)a1.getEyeHeight() * 0.9D - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
			double v5 = a1.posZ - Minecraft.getMinecraft().thePlayer.posZ;
			double v7 = (double)MathHelper.ceiling_float_int((float) (v1 * v1 + v5 * v5));
			float v9 = (float)(Math.atan2(v5, v1) * 180.0D / 3.141592653589793D) - 90.0F;
			float v10 = (float)(-(Math.atan2(v3, v7) * 180.0D / 3.141592653589793D));
			return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(v9 - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(v10 - Minecraft.getMinecraft().thePlayer.rotationPitch)};
		}
	}

	private float[] rotateNCP_backup(Entity entity) {
		double diffX = entity.posX - mc.thePlayer.posX;
		double diffY = entity.posY + (double)entity.getEyeHeight() * 0.9D - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
		double diffZ = entity.posZ - mc.thePlayer.posZ;
		double dist = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
		float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D));
		float[] neededRotations = new float[]{RotationUtil.yaw() + MathHelper.wrapAngleTo180_float(yaw - RotationUtil.yaw()), RotationUtil.pitch() + MathHelper.wrapAngleTo180_float(pitch - RotationUtil.pitch())};
		float[] rlyneed = (float[])neededRotations.clone();
		float d0 = 0.0F - RotationUtil.yaw();
		float d0y = neededRotations[0] + d0;
		boolean rotateRight = d0y > 0.0F;
		if(rotateRight) {
			neededRotations[0] = (RotationUtil.yaw()) + Math.min(Math.abs(0.0F - d0y), 25f);
		} else {
			neededRotations[0] = (RotationUtil.yaw()) - Math.min(Math.abs(0.0F - d0y), 35f);
		}

		return neededRotations;
	}

	private int DistanceToEntity(Entity entity, Entity entity2) {
		return (int) (entity.getDistanceToEntity(this.mc.thePlayer) - entity2.getDistanceToEntity(this.mc.thePlayer));
	}

	enum AuraPriority {
		Angle, Range, Fov, Health;
	}

	enum AuraMode {
		Switch, Single;
	}
}
