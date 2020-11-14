package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.friend.FriendManager;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.QuickMath;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TimerUtil;
import me.skidsense.util.tojatta.api.utilities.angle.Angle;
import me.skidsense.util.tojatta.api.utilities.angle.AngleUtility;
import me.skidsense.util.tojatta.api.utilities.vector.impl.Vector3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Mod {
	public EntityLivingBase curtarget;
	public static EntityLivingBase target = null;
	private List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>(0);
	
	public static Numbers<Double> Range = new Numbers<Double>("Range", "Range", 4.5, 1.0, 10.0, 0.1);
	private Numbers<Double> Cps = new Numbers<Double>("Cps", "Cps", 10.0, 1.0, 20.0, 0.50);
	private Numbers<Double> SwitchDelay = new Numbers<Double>("SwitchDelay", "SwitchDelay", 100.0, 0.0, 1000.0, 25.0);
	private Numbers<Double> Existed = new Numbers<Double>("Existed", "Existed", 30.0, 0.0, 100.0, 1.0);
	private Option<Boolean> Autoblock = new Option<Boolean>("AutoBlock", "AutoBlock", true);
	private Option<Boolean> Players = new Option<Boolean>("Players", "Players", true);
	private Option<Boolean> Animals = new Option<Boolean>("Animals", "Animals", true);
	private Option<Boolean> Mobs = new Option<Boolean>("Mobs", "Mobs", false);
	private Option<Boolean> Invis = new Option<Boolean>("Invisibles", "Invisibles", false);
	private Option<Boolean> Raycast = new Option<Boolean>("Raycast", "Raycast", false);
	private Option<Boolean> Rot = new Option<Boolean>("Rotation", "Rotation", true);
	private Mode<EventMode> attackmode = new Mode<EventMode>("EventMode", "EventMode",EventMode.values(), EventMode.Pre);
	private Mode<AuraMode> Mode = new Mode<AuraMode>("Mode", "Mode", AuraMode.values(), AuraMode.Switch);
	private Mode<Priority> priority = new Mode<Priority>("Priority", "Priority", Priority.values(), Priority.Angle);
	private Mode<SwitchMode> SwitchMod = new Mode<SwitchMode>("SwitchMode", "SwitchMode", SwitchMode.values(), SwitchMode.HurtTime);
	
	private TimerUtil AttackTimer = new TimerUtil();
	private TimerUtil SwitchTimer = new TimerUtil();
	private int index;
	private boolean isBlocking;

	public KillAura() {
		super("KillAura", new String[]{"ka", "aura", "killa"}, ModuleType.Fight);
	}

	@Override
	public void onDisable() {
		this.curtarget = null;
		this.targets.clear();
		if (this.Autoblock.getValue().booleanValue() && this.hasSword() && mc.thePlayer.isBlocking()) {
			if (attackmode.getValue() == EventMode.Post) {
				this.UnBlock();
			}
		}
		if (attackmode.getValue() == EventMode.Pre) {
			this.PreUnBlock();
		}
	}

	@Override
	public void onEnable() {
		this.curtarget = null;
		this.index = 0;
	}

	public Entity Raycast(Entity fromEntity) {
		if ((this.Raycast.getValue()))
			for (Entity en2 : mc.theWorld.loadedEntityList) {
				if (en2 == mc.thePlayer || en2.equals(mc.thePlayer) || en2 == fromEntity || en2.equals(fromEntity) || (!en2.isInvisible() && !(en2 instanceof EntityArmorStand)) || !en2.boundingBox.intersectsWith(fromEntity.boundingBox))
					continue;
				return mc.thePlayer.canEntityBeSeen(en2) ? en2 : en2;
			}
		return fromEntity;
	}


	private void sortList() {
		if (this.priority.getValue() == Priority.Armor) {
			targets.sort(Comparator.comparingInt((o) -> {
				return o instanceof EntityPlayer ? ((EntityPlayer) o).inventory.getTotalArmorValue()
						: (int) o.getHealth();
			}));
		}

		if (this.priority.getValue() == Priority.Range) {
			targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
		}
		if (this.priority.getValue() == Priority.Fov) {
			targets.sort(Comparator.comparingDouble(o -> RotationUtil.getDistanceBetweenAngles(mc.thePlayer.rotationPitch,
					KillAura.getRotationToEntity(o)[0])));
		}
		if (this.priority.getValue() == Priority.Angle) {
			targets.sort((o1, o2) -> {
				float[] rot1 = getRotationToEntity(o1);
				float[] rot2 = getRotationToEntity(o2);
				return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
			});
		}
		if (this.priority.getValue() == Priority.Slowly) {
			targets.sort((ent1, ent2) -> {
				float f2 = 0.0F;
				float e1 = RotationUtil.getRotations((Entity) ent1)[0];
				float e2 = RotationUtil.getRotations((Entity) ent2)[0];
				return (e1 < f2) ? 1 : ((e1 == e2) ? 0 : -2);

			});
		}
		if (this.priority.getValue() == Priority.Health) {
			targets.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
		}
	}

	public static float[] getRotationToEntity(Entity target) {
		Minecraft.getMinecraft();
		double xDiff = target.posX - mc.thePlayer.posX;
		Minecraft.getMinecraft();
		double yDiff = target.posY - mc.thePlayer.posY;
		Minecraft.getMinecraft();
		double zDiff = target.posZ - mc.thePlayer.posZ;
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
		Minecraft.getMinecraft();
		Minecraft.getMinecraft();
		float pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / 0.0
				- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0
				/ 3.141592653589793);
		if (yDiff > -0.2 && yDiff < 0.2) {
			Minecraft.getMinecraft();
			Minecraft.getMinecraft();
			pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / HitLocation.CHEST.getOffset()
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0
					/ 3.141592653589793);
		} else if (yDiff > -0.2) {
			Minecraft.getMinecraft();
			Minecraft.getMinecraft();
			pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / HitLocation.FEET.getOffset()
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0
					/ 3.141592653589793);
		} else if (yDiff < 0.3) {
			Minecraft.getMinecraft();
			Minecraft.getMinecraft();
			pitch = (float) (-Math.atan2(target.posY + (double) target.getEyeHeight() / HitLocation.HEAD.getOffset()
					- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0
					/ 3.141592653589793);
		}
		return new float[]{yaw, pitch};
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

	private boolean hasSword() {
		if (mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
			return true;
		}
		return false;
	}

	private boolean shouldAttack() {
		return this.AttackTimer.hasReached(1000.0D / (((Double) this.Cps.getValue()).doubleValue() + QuickMath.getRandomDoubleInRange(0.0D, 5.0D)));
	}

	@Sub
	private void onUpdate(EventPreUpdate event) {
		this.setSuffix(this.attackmode.getValue());
		if (curtarget == null && Autoblock.getValue()) {
			if (hasSword()) {
				if (attackmode.getValue() == EventMode.Pre) {
					PreUnBlock();
				}
			}
		}
		if (hasSword() && curtarget != null && Autoblock.getValue() && !isBlocking) {
			if (attackmode.getValue() == EventMode.Pre) {
				Preblock();
			}
		}
		this.targets = this.getTargets();
		sortList();
		if (this.targets.size() > 1 && this.Mode.getValue() == AuraMode.Switch) {
			if (SwitchMod.getValue() == SwitchMode.Delay && SwitchTimer.delay(SwitchDelay.getValue().longValue())) {
				++this.index;
				SwitchTimer.reset();
			} else if (SwitchMod.getValue() == SwitchMode.HurtTime && curtarget != null) {
				if (curtarget.hurtTime != 0) {
					++this.index;
				}
			}
		}
		if (mc.thePlayer.ticksExisted % SwitchDelay.getValue().intValue() == 0 && this.targets.size() > 1 && this.Mode.getValue() == AuraMode.Single) {
			if (curtarget.getDistanceToEntity(mc.thePlayer) > Range.getValue()) {
				++index;
			} else if (curtarget.isDead) {
				++index;
			}
		}
		if (curtarget != null) {
			curtarget = null;
		}
		if (!this.targets.isEmpty()) {
			if (this.index >= this.targets.size()) {
				this.index = 0;
			}
			curtarget = (EntityLivingBase) this.targets.get(this.index);
			{
				float[] rotations = RotationLib(curtarget);

				event.setYaw(rotations[0]);
				event.setPitch(rotations[1]);
				if (this.Rot.getValue()) {
					mc.thePlayer.rotationYawHead = rotations[0];
				}
			}
		}
		if (attackmode.getValue() == EventMode.Pre)
			if (curtarget != null && shouldAttack()) {
				attack(curtarget);
			}
	}

	@Sub
	private void onUpdatePost(EventPostUpdate e) {
		if (curtarget != null) {
			if (this.shouldAttack()) {
				if (this.hasSword() && mc.thePlayer.isBlocking() && this.CanAttack(this.curtarget)) {
					if (attackmode.getValue() == EventMode.Post) {
						UnBlock();
					}
				}
				if (attackmode.getValue() == EventMode.Post)
					if (curtarget != null && shouldAttack()) {
						attack(curtarget);
					}
				this.AttackTimer.reset();
			}
			if (!mc.thePlayer.isBlocking() && this.hasSword() && Autoblock.getValue().booleanValue()) {
				if (attackmode.getValue() == EventMode.Post) {
					block();
				}
				mc.thePlayer.itemInUseCount = mc.thePlayer.getHeldItem().getMaxItemUseDuration();
				if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
					mc.getItemRenderer().resetEquippedProgress2();
				}
			}
		}
	}

	private List<EntityLivingBase> getTargets() {
		ArrayList<EntityLivingBase> list = new ArrayList<EntityLivingBase>();
		for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
				if (CanAttack(entityLivingBase))
					list.add(entityLivingBase);
			}
		}
		return list;
	}

	private boolean CanAttack(Entity e) {
		if (e.ticksExisted <= Existed.getValue().intValue()) {
			return false;
		}
		if (e == mc.thePlayer) {
			return false;
		}
		
		if (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(e) > Range.getValue()) {
			return false;
		}
		
		AntiBot ab = (AntiBot) Client.instance.getModuleManager().getModuleByClass(AntiBot.class);
		if (ab.isServerBot(e)) {
			return false;
		}

		if (!e.isEntityAlive()) {
			return false;
		}

		if (FriendManager.isFriend(e.getName())) {
			return false;
		}

		if (e instanceof EntityPlayer && Players.getValue().booleanValue() && !Teams.isOnSameTeam(e)) {
			return true;
		}
		if (e instanceof EntityMob && Mobs.getValue().booleanValue()) {
			return true;
		}
		if (e instanceof EntityAnimal && Animals.getValue().booleanValue()) {
			return true;
		}
		if (e.isInvisible() && Invis.getValue().booleanValue() && e instanceof EntityPlayer) {
			return true;
		}
		return false;
	}

	public float[] RotationLib(EntityLivingBase target) {
		AngleUtility angleUtility = new AngleUtility(6, 60, 3, 30);
		Vector3<Double> enemyCoords = new Vector3<>(target.posX, target.posY, target.posZ);
		Vector3<Double> myCoords = new Vector3<>(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		Angle dstAngle = angleUtility.calculateAngle(enemyCoords, myCoords);
		Angle smoothedAngle1 = angleUtility.smoothAngle(dstAngle, dstAngle);
		return new float[] { smoothedAngle1.getYaw(), smoothedAngle1.getPitch() };
	}

	private void attack(EntityLivingBase entity) {
		mc.thePlayer.swingItem();
		mc.getNetHandler().addToSendQueue(new C02PacketUseEntity((Entity) entity, C02PacketUseEntity.Action.ATTACK));
	}


	private void block() {
		if (Autoblock.getValue() && !mc.gameSettings.keyBindUseItem.isPressed() && !isBlocking) {
			mc.thePlayer.itemInUseCount = mc.thePlayer.getHeldItem().getMaxItemUseDuration();
			mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
			isBlocking = true;
		}
	}

	private void PreUnBlock() {
		if (hasSword()) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			mc.playerController.onStoppedUsingItem(mc.thePlayer);
		}
	}


	public void Preblock() {
		if (hasSword()) {
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
			mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
		}
	}

	private void UnBlock() {
		if (Autoblock.getValue() && isBlocking) {
			mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
			mc.thePlayer.clearItemInUse();
			isBlocking = false;
		}
	}

	@Sub
	public void onRender(EventRender3D render) {
		if (curtarget == null) return;
		Color color = (curtarget.hurtTime > 0) ? new Color(-1618884) : new Color(-13330213);
		double x = curtarget.lastTickPosX + (curtarget.posX - curtarget.lastTickPosX) * mc.timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosX;
		double y = curtarget.lastTickPosY + (curtarget.posY - curtarget.lastTickPosY) * mc.timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosY;
		double z = curtarget.lastTickPosZ + (curtarget.posZ - curtarget.lastTickPosZ) * mc.timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosZ;
		x -= 0.5;
		z -= 0.5;
		y += curtarget.getEyeHeight() + 0.35 - (curtarget.isSneaking() ? 0.25 : 0.0);
		final double mid = 0.5;
		GL11.glPushMatrix();
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glTranslated(x + mid, y + mid, z + mid);
		GL11.glRotated((double) (-curtarget.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
		GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
		RenderUtil.drawBoundingBox(new AxisAlignedBB(x + 0.1, y, z + 0.2, x + 0.9, y + 0.1, z + 1.0));
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDepthMask(true);
		GL11.glDisable(3042);
		GL11.glPopMatrix();
	}

	private enum SwitchMode {
		Delay,
		HurtTime;
	}

	private enum Priority {
		Range, Fov, Angle, Health, Armor, Slowly;
	}

	private enum EventMode {
		Pre, Post;
	}
	
	private enum AuraMode {
		Switch,
		Single,
	}
}