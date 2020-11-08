package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.color.Colors;
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
import me.skidsense.module.collection.combat.AntiBot;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TimerUtil;
import me.skidsense.util.tojatta.api.utilities.angle.AngleUtility;
import me.skidsense.util.tojatta.api.utilities.vector.impl.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToDoubleFunction;

public class KillAura extends Mod {
	public static float anima;
	public Mode<AuraPriority> priority = new Mode<>("Priority", "Priority", AuraPriority.values(), AuraPriority.Angle);
	public Mode<AuraMode> mode = new Mode<>("Mode", "Mode", AuraMode.values(), AuraMode.Switch);
	public Option<Boolean> players = new Option<Boolean>("Players", "Players", true);
	public Option<Boolean> mobs = new Option<Boolean>("Mobs", "Mobs", false);
	public Option<Boolean> animals = new Option<Boolean>("Animals", "Animals", false);
	public Option<Boolean> invis = new Option<Boolean>("Invisible", "Invisible", false);
	public static Option<Boolean> autoBlock = new Option<Boolean>("AutoBlock", "AutoBlock", true);
	public static Numbers<Double> range = new Numbers<Double>("Range", "Range", 4.2, 3.5, 7.0, 0.1);
	public Numbers<Double> cps = new Numbers<Double>("APS", "APS", 9.0, 1.0, 20.0, 1.0);
	public Numbers<Double> hitswitch = new Numbers<Double>("HitSwitch", "HitSwitch", 3.0, 1.0, 20.0, 1.0);
	public Option<Boolean> walls = new Option<Boolean>("ThroughWalls", "ThroughWalls", true);
	public Option<Boolean> autodisable = new Option<Boolean>("AutoDisable", "AutoDisable", true);
	public static Numbers<Double> blockrange = new Numbers<Double>("BlockRange", "BlockRange", 8.0, 3.5, 8.0, 0.1);
	public boolean isBlocking;
	TimerUtil timer = new TimerUtil();
	int hit;
	public static float yaw;
	public float[] rotation;
	private List<EntityLivingBase> loaded = new CopyOnWriteArrayList<EntityLivingBase>();
	private List<EntityLivingBase> attacktargets = new CopyOnWriteArrayList<EntityLivingBase>();
	public static EntityLivingBase target;
	public static EntityLivingBase attacktarget;
	public static EntityLivingBase slowtarget;
	public float[] lastRotations = new float[]{0.0f, 0.0f};
	;
	public DecimalFormat format = new DecimalFormat("0.0");
	public BlockPos AutoBlockPos = new BlockPos(-1, -1, -1);
	int attackSpeed;

	public KillAura() {
		super("Kill Aura", new String[] { "Aura","KillAura" }, ModuleType.Fight);
		//this.addValues(this.priority, this.mode,this.range, this.cps, this.blockrange, this.hitswitch, this.players, this.mobs, this.animals, this.invis,
		//		this.autoBlock, this.targetinfo, this.walls, this.autodisable);
	}

	@Override
	public void onEnable() {
		this.rotation = new float[] { this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch };
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (this.isBlocking) {
			C07PacketPlayerDigging.Action release_USE_ITEM = C07PacketPlayerDigging.Action.RELEASE_USE_ITEM;
			mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(release_USE_ITEM, AutoBlockPos, EnumFacing.DOWN));
			mc.thePlayer.clearItemInUse();
			this.isBlocking = false;
		}
		target = null;
	}

	@Sub
	public void onPreMotion(EventPreUpdate eventMotion) {
		if(Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()){
			Notifications.getManager().post("Flying with killaura enabled will cause bans.");
			setEnabled(false);
		}
		if(target != null) {
			slowtarget = target;
		}
		if (!this.mc.thePlayer.isEntityAlive() && this.autodisable.getValue()) {
			this.setEnabled(false);
		}
		this.setSuffix(mode.getValue());
		if (autoBlock.getValue() && this.canBlock() && this.isBlocking) {
			C07PacketPlayerDigging.Action release_USE_ITEM = C07PacketPlayerDigging.Action.RELEASE_USE_ITEM;
			mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(release_USE_ITEM, AutoBlockPos, EnumFacing.DOWN));
			this.mc.thePlayer.clearItemInUse();
			this.isBlocking = false;
		}
		List<EntityLivingBase> sortList = this.sortList(this.getTargets(range.getValue()));
		if (sortList.isEmpty() && !this.attacktargets.isEmpty()) {
			this.attacktargets.clear();
		}
		this.loaded = this.sortList(this.getTargets(range.getValue()));
		if (this.loaded.isEmpty()) {
			target = null;
			this.attackSpeed = 0;
			if (this.isBlocking) {
				C07PacketPlayerDigging.Action release_USE_ITEM2 = C07PacketPlayerDigging.Action.RELEASE_USE_ITEM;
				mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(release_USE_ITEM2, AutoBlockPos, EnumFacing.DOWN));
				this.mc.thePlayer.clearItemInUse();
				this.isBlocking = false;
			}
		} else {
			EntityLivingBase target;
			if (!sortList.isEmpty() && this.attacktargets == sortList.get(0) && this.loaded.size() > 1) {
				target = this.loaded.get(1);
			} else {
				target = this.loaded.get(0);
			}
			this.target = target;
			float[] array = TojattaRotation(this.target);
			eventMotion.yaw = array[0];
			eventMotion.pitch = array[1];
			KillAura.yaw = array[0];
			mc.thePlayer.rotationYawHead = array[0];
			mc.thePlayer.renderYawOffset = array[0];
		}
		this.lastRotations = new float[] { eventMotion.yaw, eventMotion.pitch };
	}

	@Sub
	public void EventPostUpdate(me.skidsense.hooks.events.EventPostUpdate e) {
		if (target != null && this.shouldAttack()) {
			this.attack();
			timer.reset();
		}

		if (!this.getTargets(blockrange.getValue()).isEmpty() && this.canBlock() && !this.isBlocking  && autoBlock.getValue()) {
			mc.thePlayer.sendQueue.addToSendQueue(
					new C08PacketPlayerBlockPlacement(AutoBlockPos, 255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
			mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 999);
			this.isBlocking = true;
		}
	}

	@Sub
	private void render(EventRender3D e) {
		int hurtcolor;
		boolean setcolor = target != null && target.hurtResistantTime <= 0;
		if(target != null && setcolor) {
			hurtcolor = Colors.getColor(new Color(254, 255, 255,255));
			drawESP2(hurtcolor);
		}else{
			hurtcolor = Colors.getColor(new Color(255, 0, 0,255));
			drawESP2(hurtcolor);
		}
	}

	public void drawESP2(int color) {
		if(target != null) {
			double attackDelay;
			double var31 = this.target.posX - this.target.prevPosX;
			double var32 = this.target.posZ - this.target.prevPosZ;
			attackDelay = this.target.posX + var31;
			double var33 = attackDelay - mc.getRenderManager().renderPosX;
			double var34 = this.target.posY + 1.0D;
			double y = var34 - mc.getRenderManager().renderPosY;
			double var39 = this.target.posZ + var32;
			double var42 = var39 - mc.getRenderManager().renderPosZ;
			double sin = Math.sin((double)System.currentTimeMillis() / 500.0D) * 50.0D;
			double xA = sin / 100.0D;
			double zA = sin / 100.0D;
			double yA = sin / 100.0D;
			RenderUtil.pre3D();
			mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
			RenderUtil.glColor(color);
			GL11.glLineWidth(3.0F);
			if(this.target.hurtTime <= 0) {
				RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(var33 - xA, y - yA - 0.1D, var42 - xA, var33 + zA, y + yA + 0.1D, var42 + zA));
			} else {
				RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(var33 - 0.2D, y - yA - 0.1D, var42 - 0.2D, var33 + 0.2D, y + yA + 0.2D, var42 + 0.2D));
			}
			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			RenderUtil.post3D();
		}
	}

	private boolean shouldAttack() {
		return this.timer.hasReached((int) (1000 / this.cps.getValue().intValue()));
	}

	public void attack() {
		float modifierForCreature = EnchantmentHelper.getModifierForCreature(this.mc.thePlayer.getHeldItem(),
				EnumCreatureAttribute.UNDEFINED);
		if (this.mc.thePlayer.fallDistance > 0.0f && !this.mc.thePlayer.onGround && !this.mc.thePlayer.isOnLadder()
				&& !this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isPotionActive(Potion.blindness)
				&& this.mc.thePlayer.ridingEntity == null) {
		}
		if (this.isBlocking && this.canBlock()) {
			NetworkManager networkManager = this.mc.thePlayer.sendQueue.getNetworkManager();
			C07PacketPlayerDigging.Action release_USE_ITEM = C07PacketPlayerDigging.Action.RELEASE_USE_ITEM;
			networkManager.sendPacket(new C07PacketPlayerDigging(release_USE_ITEM, AutoBlockPos, EnumFacing.DOWN));
			mc.thePlayer.clearItemInUse();
			this.isBlocking = false;
		}
		++this.attackSpeed;
		EventAttack ent = new EventAttack(target);
		EventManager.postAll(ent);
		if(ent.isCancelled())
			return;
		this.mc.thePlayer.swingItem();
		this.mc.thePlayer.sendQueue
				.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
		if (this.mode.getValue() == AuraMode.Switch) {
			++this.hit;
			if (this.hit >= this.hitswitch.getValue()) {
				this.attacktargets.add(target);
				attacktarget = target;
				this.hit = 0;
			}
		}
		if (this.canBlock() && !this.isBlocking && autoBlock.getValue()) {
			mc.thePlayer.sendQueue.getNetworkManager().sendPacket(
					new C08PacketPlayerBlockPlacement(AutoBlockPos, 255, this.mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
			mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 999);
			this.isBlocking = true;
		}
	}

	private List<EntityLivingBase> getTargets(double n) {
		ArrayList<EntityLivingBase> list = new ArrayList<EntityLivingBase>();
		for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
				if (isValidEntity(entityLivingBase, n))
					list.add(entityLivingBase);
			}
		}
		return list;
	}

	public boolean canBlock() {
		boolean b = this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
		return b;
	}

    public float[] TojattaRotation(EntityLivingBase target) {
        AngleUtility angleUtility = new AngleUtility(6, 60, 3, 30);
        Vector3<Double> targetCoords = new Vector3<>(target.posX, target.posY, target.posZ);
        Vector3<Double> selfCoords = new Vector3<>(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        me.skidsense.util.tojatta.api.utilities.angle.Angle dstAngle = angleUtility.calculateAngle(targetCoords, selfCoords);
        me.skidsense.util.tojatta.api.utilities.angle.Angle smoothedAngle1 = angleUtility.smoothAngle(dstAngle, dstAngle);
        return new float[] { smoothedAngle1.getYaw(), smoothedAngle1.getPitch() };
    }
    
	private boolean isValidEntity(Entity ent, double reach) {
		AntiBot ab = (AntiBot) Client.getModuleManager().getModuleByClass(AntiBot.class);
		boolean b;
		if (ent == null) {
			b = false;
		} else {
			if (ent == mc.thePlayer) {
				b = false;
			} else if (ent instanceof EntityPlayer && !this.players.getValue()) {
				b = false;
			} else if ((ent instanceof EntityAnimal || ent instanceof EntitySquid) && !this.animals.getValue()) {
				b = false;
			} else if ((ent instanceof EntityMob || ent instanceof EntityVillager || ent instanceof EntityBat)
					&& !this.mobs.getValue()) {
				b = false;
			} else {
				if (mc.thePlayer.getDistanceToEntity(ent) > reach) {
					b = false;
				} else if (ent instanceof EntityPlayer && FriendManager.isFriend(ent.getName())) {
					b = false;
				} else if (!ent.isDead && ((EntityLivingBase) ent).getHealth() > 0.0f) {
					if (ent.isInvisible() && !this.invis.getValue()) {
						b = false;
					} else if (ab.isServerBot(ent)) {
						b = false;
					} else {
						b = (!mc.thePlayer.isDead && (!(ent instanceof EntityPlayer) || !Teams.isOnSameTeam(ent)));
					}
				} else {
					b = false;
				}
			}
		}
		return b;
	}

	private List<EntityLivingBase> sortList(List<EntityLivingBase> list) {
		if (this.priority.getValue() == AuraPriority.Range) {
			list.sort(this::DistanceToEntity);
		}
		if (this.priority.getValue() == AuraPriority.Fov) {
			list.sort(Comparator.comparingDouble((ToDoubleFunction<? super Entity>) this::Fov));
		}
		if (this.priority.getValue() == AuraPriority.Angle) {
			list.sort(this::Angle);
		}
		if (this.priority.getValue() == AuraPriority.Health) {
			list.sort(KillAura::Health);
		}
		return list;
	}

	public static float getDistanceBetweenAngles(float n, float n2) {
		float n3 = Math.abs(n - n2) % 360.0f;
		if (n3 > 180.0f) {
			n3 = 360.0f - n3;
		}
		return n3;
	}

	private static int Health(Entity entity, Entity entity2) {
		return (int) (((EntityLivingBase) entity).getHealth() - ((EntityLivingBase) entity2).getHealth());
	}

	private int Angle(Entity entity, Entity entity2) {
		return Float.compare(RotationUtil.angleDifference(rotateNCP(entity)[0], this.lastRotations[0]),
				RotationUtil.angleDifference(rotateNCP(entity2)[0], this.lastRotations[0]));
	}

	private double Fov(Entity entity) {
		return getDistanceBetweenAngles(this.mc.thePlayer.rotationPitch, rotateNCP(entity)[0]);
	}

	public static float[] rotateNCP(Entity a1) {
		if (a1 == null) {
			return null;
		} else {
			double v1 = a1.posX - Minecraft.getMinecraft().thePlayer.posX;
			double v3 = a1.posY + (double)a1.getEyeHeight() * 0.9D - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
			double v5 = a1.posZ - Minecraft.getMinecraft().thePlayer.posZ;
			double v7 = (double) MathHelper.ceiling_float_int((float) (v1 * v1 + v5 * v5));
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
