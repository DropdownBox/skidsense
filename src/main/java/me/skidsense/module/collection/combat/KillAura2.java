package me.skidsense.module.collection.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.FriendManager;
import me.skidsense.management.ModuleManager;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MathUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class KillAura2
extends Module {
	protected ModelBase mainModel;
	TimerUtil kms = new TimerUtil();
	public static float rotationPitch;
	public ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
	public ArrayList<EntityLivingBase> attackedTargets = new ArrayList<EntityLivingBase>();
	public static EntityLivingBase target = null;
    public static Mode<Enum> emode = new Mode("EspMode", "EspMode", (Enum[])espmode.values(), (Enum)espmode.None);
    public static Mode<Enum> Priority = new Mode("Priority", "Priority", (Enum[]) priority.values(), (Enum) priority.Angle);
	public static Mode<Enum> mode = new Mode("Mode", "Mode", (Enum[]) AuraMode.values(), (Enum) AuraMode.Vanilla);
	public  static Numbers<Double> aps = new Numbers<Double>("CPS", "CPS", 10.0, 1.0, 20.0, 0.5);
	public static Numbers<Double> reach = new Numbers<Double>("Reach", "Reach", 4.5, 1.0, 6.0, 0.1);
	public static Option<Boolean> blocking = new Option<Boolean>("Autoblock", "Autoblock", true);
	public static Option<Boolean> players = new Option<Boolean>("Players", "Players", true);
	public static Option<Boolean> animals = new Option<Boolean>("Animals", "Animals", true);
	public static Option<Boolean> mobs = new Option<Boolean>("Mobs", "Mobs", false);
	public static Option<Boolean> invis = new Option<Boolean>("Invisibles", "Invisibles", false);
	public static Option<Boolean> teams = new Option<Boolean>("Teams", "Teams", false);
	private static long lastMS;
	private TimerUtil test = new TimerUtil();
	private boolean doBlock = false;
	private boolean unBlock = false;
	private long lastMs;
	private int delay = 0;
	private int index;
	private TimerUtil timer = new TimerUtil();
	public static float[] facing;
	static boolean allowCrits;
	   private EntityLivingBase currentEntity;

	public KillAura2() {
		super("Kill Aura", new String[] { "ka", "aura", "killa","killaura" }, ModuleType.Fight);
		this.addValues(this.emode, this.Priority, this.mode, this.aps, this.reach, this.blocking,this.players,
				this.animals, this.mobs, this.invis);
	}

	public static double random(double min, double max) {
		Random random = new Random();
		return min + (int) (random.nextDouble() * (max - min));
	}

	private boolean shouldAttack() {
		return this.timer.hasReached((int) (999 / this.aps.getValue().intValue()));
	}


	@EventHandler
	private void render(EventRender3D e) {
		int hurtcolor; 
		if(target != null && target.hurtResistantTime <= 0) {
			hurtcolor = Colors.getColor(new Color(255, 255, 255,255));
			drawESP2(hurtcolor);
		}else{
			hurtcolor = Colors.getColor(new Color(255, 0, 0,255));
			drawESP2(hurtcolor);
		}
	}

	public void drawESP2(int color) {     
        if(target != null) {
    	this.currentEntity = target;
        double attackDelay;
        double var31 = this.currentEntity.posX - this.currentEntity.prevPosX;
        double var32 = this.currentEntity.posZ - this.currentEntity.prevPosZ;
        attackDelay = this.currentEntity.posX + var31;
        double var33 = attackDelay - RenderManager.renderPosX;
        double var34 = this.currentEntity.posY + 1.0D;
        double y = var34 - RenderManager.renderPosY;
        double var39 = this.currentEntity.posZ + var32;
        double var42 = var39 - RenderManager.renderPosZ;
        double sin = Math.sin((double)System.currentTimeMillis() / 500.0D) * 50.0D;
        double xA = sin / 100.0D;
        double zA = sin / 100.0D;
        double yA = sin / 100.0D;
        RenderUtil.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderUtil.glColor(color);
        GL11.glLineWidth(3.0F);
        if(this.currentEntity.hurtTime <= 0) {
            RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(var33 - xA, y - yA - 0.1D, var42 - xA, var33 + zA, y + yA + 0.1D, var42 + zA));
         } else {
      	   RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(var33 - 0.2D, y - yA - 0.1D, var42 - 0.2D, var33 + 0.2D, y + yA + 0.2D, var42 + 0.2D));
         }
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderUtil.post3D();
        }
	}

	@EventHandler
	private void onPreUpdate(EventPreUpdate event) {
		this.setSuffix(this.mode.getValue());
		if (this.mc.thePlayer.getHealth() <= 0 && this.mode.getValue() == AuraMode.NCP && this.targets.size() > 0) {
			++this.index;
		}
		if (this.mode.getValue() == AuraMode.Vanilla && this.targets.size() > 0 && this.mc.thePlayer.ticksExisted % 80 == 0) {
			++this.index;
		}
		if (!this.targets.isEmpty()) {
			if (this.index >= this.targets.size()) {
				this.index = 0;
			}
		}
		this.doBlock = false;
		this.clear();
		this.findTargets(event);
		this.settarget();
		if (target != null) {
			final float[] rot;
			rot = getRotationToEntity(target);
            	
			event.setYaw((rot[0]));
			event.setPitch(rot[1]);
			mc.thePlayer.renderYawOffset = rot[0];
			mc.thePlayer.rotationYawHead = rot[0];
		}
		if (target != null && blocking.getValue().booleanValue() && isHoldingSword()) {
        	block();
			this.unBlock = true;
		}
		if (target == null) {
			this.targets.clear();
			this.attackedTargets.clear();
			this.lastMs = System.currentTimeMillis();
			if (this.unBlock) {
				unblock();
			}
		}
	}
	
    private void unblock() {
        if (blocking.getValue().booleanValue()) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
			this.unBlock = false;
        }
    }
    
	public static int randomNumber(double min, double max) {
		Random random = new Random();
		return (int) (min + (random.nextDouble() * (max - min)));
	}
		private void doAttack() {
		    int ticks = 1;
		    int MAX_TICK = 100;
		    int delayValue = ((Double)aps.getValue()).intValue();
		    int n2 = (20 / delayValue) * 50;
		    int n3 = 1000 / delayValue + random.nextInt(80) - 50;
		    if ((double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity((Entity)target) <= (Double)this.reach.getValue() && this.test.delay((float)((Minecraft.getMinecraft().thePlayer.addedToChunk ? n3 : n2 - 20) + random.nextInt(50)))) {
		        boolean miss = false;
		        this.test.reset();
		        if (Minecraft.getMinecraft().thePlayer.isBlocking() || Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSword && ((java.lang.Boolean)this.blocking.getValue()).booleanValue()) {
		            mc.getNetHandler().addToSendQueue((Packet)new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, net.minecraft.util.BlockPos.ORIGIN, net.minecraft.util.EnumFacing.DOWN));
		            this.unBlock = false;
		        }
		        if (!Minecraft.getMinecraft().thePlayer.isBlocking() && !((java.lang.Boolean)this.blocking.getValue()).booleanValue() && Minecraft.getMinecraft().thePlayer.itemInUseCount > 0) {
		            Minecraft.getMinecraft().thePlayer.itemInUseCount = 0;
		        }
		        this.attack();
		        this.doBlock = true;
		        if (!miss) {
		            for (Object o : Minecraft.getMinecraft().theWorld.loadedEntityList) {
		                EntityLivingBase entity;
		                if (!(o instanceof EntityLivingBase) || !this.isValidEntity(entity = (EntityLivingBase)o)) continue;
		                this.attackedTargets.add((EntityLivingBase)target);
		            }
		        }
		        if (java.lang.System.currentTimeMillis() - this.lastMs > (long)(this.delay + ticks * MAX_TICK)) {
		            this.lastMs = java.lang.System.currentTimeMillis();
		            this.delay = (int)((double)delayValue + (double)random.nextInt(100)) - ticks * MAX_TICK;
		            if (this.delay < 0) {
		                this.delay = 0;
		            }
		        }
		    }
		}
		


	@EventHandler
	public void onPost(EventPostUpdate event) {
		this.sortList(targets);
		if (this.target != null && this.shouldAttack()) {
			this.doAttack();
		}
	}

    private boolean isHoldingSword() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }
    
	private void attack() {
        this.mc.thePlayer.onCriticalHit(KillAura2.target);
			this.mc.thePlayer.swingItem();
	        this.doBlock = true;
	        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)target, C02PacketUseEntity.Action.ATTACK));
	        if (Minecraft.getMinecraft().thePlayer.isBlocking() && (this.blocking.getValue()).booleanValue() && Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null && Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
	        	block();
	        	this.unBlock = true;
	        }
	        
	        if (!Minecraft.getMinecraft().thePlayer.isBlocking() && !((java.lang.Boolean)this.blocking.getValue()).booleanValue() && Minecraft.getMinecraft().thePlayer.itemInUseCount > 0) {
	        	Minecraft.getMinecraft().thePlayer.itemInUseCount = 0;
	        }
	}

	
	private void block() {
		mc.playerController.csendUseItem(mc.thePlayer, this.mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
	}

	private void settarget() {
		if (targets.size() == 0) {
			target = null;
			return;
		}
		target = this.targets.get(index);
	}

	private void clear() {
		target = null;
		this.targets.clear();
		for (EntityLivingBase ent : this.targets) {
			if (this.isValidEntity(ent))
				continue;
			this.targets.remove(ent);
			if (!this.attackedTargets.contains(ent))
				continue;
			this.attackedTargets.remove(ent);
		}
	}

	private void findTargets(EventPreUpdate event) {
		int maxSize = this.mode.getValue() == AuraMode.Vanilla ? 3 : 2;
		if (targets.size() > maxSize) targets.clear();
        mc.theWorld.loadedEntityList.forEach(target1 -> {
        	EntityLivingBase curEnt;
			if (target1 instanceof EntityLivingBase && this.isValidEntity(curEnt = (EntityLivingBase) target1)
					&& !this.targets.contains(curEnt)) {
				this.targets.add(curEnt);
			}			
		this.targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(o2) - o2.getDistanceToEntity(o1)));
		if (this.targets.size() >= maxSize) {
			this.clear();
		}
        });
	}

	private boolean isValidEntity(EntityLivingBase ent) {
	    Client.getModuleManager();
	    AntiBot ab = (AntiBot)	Client.getModuleManager().getModuleByClass(AntiBot.class);
	    boolean accatk = ent == null ? false : (ent == Minecraft.getMinecraft().thePlayer ? false : (ent instanceof net.minecraft.entity.player.EntityPlayer && !((java.lang.Boolean)this.players.getValue()).booleanValue() ? false : ((ent instanceof net.minecraft.entity.passive.EntityAnimal || ent instanceof net.minecraft.entity.passive.EntitySquid) && !((java.lang.Boolean)this.animals.getValue()).booleanValue() ? false : ((ent instanceof net.minecraft.entity.monster.EntityMob || ent instanceof net.minecraft.entity.passive.EntityVillager || ent instanceof net.minecraft.entity.passive.EntityBat) && !((java.lang.Boolean)this.mobs.getValue()).booleanValue() ? false : ((double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity((net.minecraft.entity.Entity)ent) > (java.lang.Double)this.reach.getValue() + 0.4 ? false : (ent instanceof net.minecraft.entity.player.EntityPlayer && FriendManager.isFriend((java.lang.String)ent.getName()) || ent instanceof net.minecraft.entity.item.EntityArmorStand ? false : (!ent.isDead && ent.getHealth() > 0.0f ? (ent.isInvisible() && !((java.lang.Boolean)this.invis.getValue()).booleanValue() ? false : (ab.isServerBot((net.minecraft.entity.Entity)ent) ? false : (isTeam((Entity)ent) && teams.getValue() ? false : (Minecraft.getMinecraft().thePlayer.isDead ? false : !(ent instanceof net.minecraft.entity.player.EntityPlayer) || !isTeam((Entity)((net.minecraft.entity.player.EntityPlayer)ent)))))) : false)))))));
	    return accatk;
	}

	@Override
	public void onEnable() {
		index = 0;
		super.onEnable();
	}

	@EventHandler
	private void setPingSpoof(EventPacketSend event) {
        if (event.getPacket() instanceof S00PacketKeepAlive) {
            event.setCancelled(true);
            S00PacketKeepAlive packet = (S00PacketKeepAlive)event.getPacket();
            mc.thePlayer.sendQueue.addToSendQueue(new C00PacketKeepAlive(packet.func_149134_c() + MathUtil.getRandomInRange(300, 500)));
        }
	}
	public static boolean isTeam(Entity entity) {
		if(Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
            if(Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2|| entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            if(Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
		return false;
		}
	
	public static float[] getRotationToEntity(Entity target) {
		double xDiff = target.posX - Minecraft.getMinecraft().thePlayer.posX;
		double yDiff = target.posY - Minecraft.getMinecraft().thePlayer.posY;
		double zDiff = target.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.101592653589793) - 90.0f;
		float pitch = (float) ((-Math.atan2(
				target.posY + (double) target.getEyeHeight() / 0.0
						- (Minecraft.getMinecraft().thePlayer.posY
								+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
				Math.hypot(xDiff, zDiff))) * 180.0 / 3.111592653589793);
		if (yDiff > -0.2 && yDiff < 0.2) {
			pitch = (float) ((-Math.atan2(
					target.posY + (double) target.getEyeHeight() / HitLocation.CHEST.getOffset()
							- (Minecraft.getMinecraft().thePlayer.posY
									+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
					Math.hypot(xDiff, zDiff))) * 180.0 / 3.121592653589793);
		} else if (yDiff > -0.2) {
			pitch = (float) ((-Math.atan2(
					target.posY + (double) target.getEyeHeight() / HitLocation.FEET.getOffset()
							- (Minecraft.getMinecraft().thePlayer.posY
									+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
					Math.hypot(xDiff, zDiff))) * 180.0 / 3.131592653589793);
		} else if (yDiff < 0.3) {
			pitch = (float) ((-Math.atan2(
					target.posY + (double) target.getEyeHeight() / HitLocation.HEAD.getOffset()
							- (Minecraft.getMinecraft().thePlayer.posY
									+ (double) Minecraft.getMinecraft().thePlayer.getEyeHeight()),
					Math.hypot(xDiff, zDiff))) * 180.0 / 3.141592653589793);
		}
		return new float[] { yaw, pitch };
	}

	static enum HitLocation {
		AUTO(0.0), HEAD(1.0), CHEST(1.5), FEET(3.5);

		private double offset;

		HitLocation(double offset) {
			this.offset = offset;
		}

		public double getOffset() {
			return this.offset;
		}
	}

	@Override
	public void onDisable() {
		this.targets.clear();
		this.attackedTargets.clear();
		target = null;
		Minecraft.getMinecraft().thePlayer.itemInUseCount = 0;
		allowCrits = true;
		mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw;
		rotationPitch = 0.0f;

		super.onDisable();
	}

	private void sortList(List<EntityLivingBase> weed) {
		if (this.Priority.getValue() == priority.Range) {
			weed.sort(Comparator.comparingDouble(player -> mc.thePlayer.getDistanceToEntity(player)));
		}
		if (this.Priority.getValue() == priority.Fov) {
			weed.sort(Comparator.comparingDouble(o -> RotationUtil.getDistanceBetweenAngles(mc.thePlayer.rotationPitch,
					KillAura2.getRotationToEntity(o)[0])));
		}
		if (this.Priority.getValue() == priority.Angle) {
			weed.sort((o1, o2) -> {
				float[] rot1 = getRotationToEntity(o1);
				float[] rot2 = getRotationToEntity(o2);
				return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
			});
		}
		if (this.Priority.getValue() == priority.Health) {
			weed.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
		}
	}

	public static float getYawDifference(final float current, final float target) {
		final float rot = (target + 180.0f - current) % 360.0f;
		return rot + ((rot > 0.0f) ? -180.0f : 180.0f);
	}

    enum espmode{
    	None,
    	Box;
    }

	static enum priority {
		Range, Fov, Angle, Health;
	}

	static enum AuraMode {
		Vanilla, NCP;
	}
}
