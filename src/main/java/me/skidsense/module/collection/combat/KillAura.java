package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.EventManager;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.events.EventPostUpdate;
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
import me.skidsense.util.QuickMath;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TimerUtil;
import me.skidsense.util.tojatta.api.utilities.angle.Angle;
import me.skidsense.util.tojatta.api.utilities.angle.AngleUtility;
import me.skidsense.util.tojatta.api.utilities.vector.impl.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityArmorStand;
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
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class KillAura
		extends Mod {
	public static EntityLivingBase target = null;
	private List targets = new ArrayList(0);
	private int index;
	private Numbers<Double> Cps = new Numbers<Double>("Cps", "Cps", Double.valueOf(10.0D), Double.valueOf(1.0D), Double.valueOf(20.0D), Double.valueOf(0.5D));
	public static Numbers<Double> Range = new Numbers<Double>("Range", "Range", 4.5, 1.0, 10.0, 0.1);
	public static Numbers<Double> SwitchDelay = new Numbers<Double>("SwitchDelay", "SwitchDelay", 100.0, 0.0, 1000.0, 25.0);
	public Mode attackmode = new Mode("AABMode", "AABMode", (Enum[]) typeMode.values(), (Enum) typeMode.Pre);
	private Option<Boolean> Autoblock = new Option<Boolean>("Autoblock", "Autoblock", true);
	public static Numbers<Double> Existed = new Numbers<Double>("Existed", "Existed", 30.0, 0.0, 100.0, 1.0);
	private static Option<Boolean> Players = new Option<Boolean>("Players", "Players", true);
	private static Option<Boolean> Animals = new Option<Boolean>("Animals", "Animals", true);
	private static Option<Boolean> Mobs = new Option<Boolean>("Mobs", "Mobs", false);
	private static Option<Boolean> Invis = new Option<Boolean>("Invisibles", "Invisibles", false);
	private Option<Boolean> Raycast = new Option("Raycast", "Raycast", false);
	private static Option<Boolean> Rot = new Option<Boolean>("Rotation", "Rotation", true);
	private Mode<Enum> espmode = new Mode("ShowTarget", "ShowTarget", (Enum[]) EMode.values(), (Enum) EMode.Box);
	private Mode<Enum> Mode = new Mode("Mode", "Mode", AuraMode.values(), AuraMode.Single);
	private Mode<Enum> priority = new Mode("Priority", "Priority", (Enum[]) Priority.values(), (Enum) Priority.Health);
	private Mode<Enum> SwitchMod = new Mode("SwitchMode", "SwitchMode", SwitchMode.values(), SwitchMode.Delay);
	private Mode<Enum> Rotation = new Mode("Rotation", "Rotation", RotationMode.values(), RotationMode.GodLike);
	private Comparator<Entity> angleComparator = Comparator.comparingDouble(e2 -> e2.getDistanceToEntity(mc.thePlayer));
	public static EntityLivingBase curtarget;
	private TimerUtil AttackTimer = new TimerUtil();
	private final TimerUtil critStopwatch = new TimerUtil();
	private TimerUtil SwitchTimer = new TimerUtil();
	public static EntityLivingBase curBot = null;

	private boolean isBlocking;
	private final double[] hypixelOffsets = new double[]{0.05f, 0.0016f, 0.03f, 0.0016f};
	private final double[] offsets = new double[]{0.05, 0.0, 0.012511, 0.0};

	static enum SwitchMode {
		Delay,
		HurtTime
	}

	static enum Priority {
		Range, Fov, Angle, Health, Armor, Slowly;
	}

	public static enum typeMode {
		Pre, Post;
	}

	public KillAura() {
		super("KillAura", new String[]{"ka", "aura", "killa"}, ModuleType.Fight);
	}


	@Override
	public void onDisable() {
		this.curtarget = null;
		this.targets.clear();
		if (this.Autoblock.getValue().booleanValue() && this.hasSword() && this.mc.thePlayer.isBlocking()) {
			if (attackmode.getValue() == typeMode.Post) {
				this.UnBlock();
			}
		}
		if (attackmode.getValue() == typeMode.Pre) {
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


	private void sortList(List<EntityLivingBase> weed) {
		if (this.priority.getValue() == Priority.Armor) {
			weed.sort(Comparator.comparingInt((o) -> {
				return o instanceof EntityPlayer ? ((EntityPlayer) o).inventory.getTotalArmorValue()
						: (int) o.getHealth();
			}));
		}

		if (this.priority.getValue() == Priority.Range) {
			weed.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
		}
		if (this.priority.getValue() == Priority.Fov) {
			weed.sort(Comparator.comparingDouble(o -> RotationUtil.getDistanceBetweenAngles(mc.thePlayer.rotationPitch,
					KillAura.getRotationToEntity(o)[0])));
		}
		if (this.priority.getValue() == Priority.Angle) {
			weed.sort((o1, o2) -> {
				float[] rot1 = getRotationToEntity(o1);
				float[] rot2 = getRotationToEntity(o2);
				return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
			});
		}
		if (this.priority.getValue() == Priority.Slowly) {
			weed.sort((ent1, ent2) -> {
				float f2 = 0.0F;
				float e1 = RotationUtil.getRotations((Entity) ent1)[0];
				float e2 = RotationUtil.getRotations((Entity) ent2)[0];
				return (e1 < f2) ? 1 : ((e1 == e2) ? 0 : -2);

			});
		}
		if (this.priority.getValue() == Priority.Health) {
			weed.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
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
		if (mc.thePlayer.inventory.getCurrentItem() != null) {
			if (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


	private final int randomNumber(int var1, int var2) {
		return (int) (Math.random() * (double) (var1 - var2)) + var2;
	}

	private boolean shouldAttack() {
		return this.AttackTimer.hasReached(1000.0D / (((Double) this.Cps.getValue()).doubleValue() + QuickMath.getRandomDoubleInRange(0.0D, 5.0D)));
	}


	@Sub
	private void onUpdate(EventPreUpdate event) {
		this.setSuffix(this.Mode.getValue() + " " + this.attackmode.getValue() + " " + this.Rotation.getValue());

		sortList(targets);

		if (curtarget == null && Autoblock.getValue()) {
			if (hasSword()) {
				if (attackmode.getValue() == typeMode.Pre) {
					PreUnBlock();
				}
			}
		}
		if (hasSword() && this.curtarget != null && Autoblock.getValue() && !isBlocking) {
			if (attackmode.getValue() == typeMode.Pre) {
				Preblock();
			}
		}
		this.targets = this.getTargets(Range.getValue());

		targets.sort(this.angleComparator);

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

		if (this.mc.thePlayer.ticksExisted % SwitchDelay.getValue().intValue() == 0 && this.targets.size() > 1 && this.Mode.getValue() == AuraMode.Single) {
			if (curtarget.getDistanceToEntity(mc.thePlayer) > Range.getValue()) {
				++index;
			} else if (curtarget.isDead) {
				++index;
			}
		}

		if (curtarget != null) {
			curtarget = null;
		}
		if (this.Rotation.getValue() == RotationMode.Loser) {
			if (!this.targets.isEmpty()) {
				if (this.index >= this.targets.size()) {
					this.index = 0;
				}
				curtarget = (EntityLivingBase) this.targets.get(this.index);
				event.setYaw(getLoserRotation(curtarget)[0]);
				event.setPitch(getLoserRotation(curtarget)[1]);
				if (this.Rot.getValue()) {
					mc.thePlayer.rotationYawHead = getLoserRotation(curtarget)[0];
				}

			}
		}
		if (this.Rotation.getValue() == RotationMode.Viro) {
			if (!this.targets.isEmpty()) {
				if (this.index >= this.targets.size()) {
					this.index = 0;
				}
				curtarget = (EntityLivingBase) this.targets.get(this.index);
				{
					event.setYaw(getRotations(curtarget)[0]);
					event.setPitch(getRotations(curtarget)[1]);
					if (this.Rot.getValue()) {
						mc.thePlayer.rotationYawHead = getRotations(curtarget)[0];
					}
				}
			}
		}
		if (this.Rotation.getValue() == RotationMode.Smart) {
			if (!this.targets.isEmpty()) {
				if (this.index >= this.targets.size()) {
					this.index = 0;
				}
				curtarget = (EntityLivingBase) this.targets.get(this.index);
				{
					event.setYaw(getRotation123(curtarget)[0]);
					event.setPitch(getRotation123(curtarget)[1]);
					if (this.Rot.getValue()) {
						mc.thePlayer.rotationYawHead = getRotation123(curtarget)[0];
					}
				}
			}
		}
		if (this.Rotation.getValue() == RotationMode.GodLike) {
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
		}
		if (attackmode.getValue() == typeMode.Pre)
			if (curtarget != null && shouldAttack()) {
				attack(curtarget);
			}
	}

	@Sub
	private void onUpdatePost(EventPostUpdate e) {
		if (curtarget != null) {
			double angle = Math.toRadians(this.curtarget.rotationYaw - 90.0f + 360.0f) % 360.0;
			if (this.shouldAttack()) {
				if (this.hasSword() && this.mc.thePlayer.isBlocking() && this.CanAttack(this.curtarget)) {
					if (attackmode.getValue() == typeMode.Post) {
						UnBlock();
					}
				}
				if (attackmode.getValue() == typeMode.Post)
					if (curtarget != null && shouldAttack()) {
						attack(curtarget);
					}
				this.AttackTimer.reset();
			}
			if (!mc.thePlayer.isBlocking() && this.hasSword() && Autoblock.getValue().booleanValue()) {
				if (attackmode.getValue() == typeMode.Post) {
					block();
				}
				mc.thePlayer.itemInUseCount = mc.thePlayer.getHeldItem().getMaxItemUseDuration();
				if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
					mc.getItemRenderer().resetEquippedProgress2();
				}
			}
		}
	}

	public static List<Entity> getTargets(Double value) {
		return mc.theWorld.loadedEntityList.stream().filter(e -> (double) mc.thePlayer.getDistanceToEntity((Entity) e) <= value && CanAttack((Entity) e)).collect(Collectors.toList());
	}

	private static boolean CanAttack(Entity e) {
		if (e.ticksExisted <= Existed.getValue().intValue()) {
			return false;
		}
		if (e == mc.thePlayer) {
			return false;
		}
		AntiBot ab2 = (AntiBot) Client.instance.getModuleManager().getModuleByClass(AntiBot.class);
		if (ab2.isServerBot(e)) {
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

	public static float[] getLoserRotation(Entity target) {
		Minecraft.getMinecraft();
		double xDiff = target.posX - mc.thePlayer.posX;
		double yDiff = target.posY - mc.thePlayer.posY - 0.4;
		double zDiff = target.posZ - mc.thePlayer.posZ;
		Minecraft.getMinecraft();

		Minecraft.getMinecraft();
		Minecraft.getMinecraft();

		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) ((-Math.atan2(yDiff, dist)) * 180.0 / 3.141592653589793);
		float[] array = new float[2];
		int n = 0;
		Minecraft.getMinecraft();
		float rotationYaw = mc.thePlayer.rotationYaw;
		float n2 = yaw;
		Minecraft.getMinecraft();
		array[n] = rotationYaw + MathHelper.wrapAngleTo180_float(n2 - mc.thePlayer.rotationYaw);
		int n3 = 1;
		Minecraft.getMinecraft();
		float rotationPitch = mc.thePlayer.rotationPitch;
		float n4 = pitch;
		Minecraft.getMinecraft();
		array[n3] = rotationPitch + MathHelper.wrapAngleTo180_float(n4 - mc.thePlayer.rotationPitch);
		return array;
	}

	public static float[] getRotations(EntityLivingBase curTarget2) {
		if (curTarget2 == null) {
			return null;
		}
		Minecraft.getMinecraft();
		double diffX = curTarget2.posX - mc.thePlayer.posX;
		Minecraft.getMinecraft();
		double diffZ = curTarget2.posZ - mc.thePlayer.posZ;
		double diffY = curTarget2.posY - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float) ((-Math.atan2(diffY, dist)) * 180.0 / 3.141592653589793);
		return new float[]{yaw, pitch};
	}


	public float[] RotationLib(EntityLivingBase target) {
		AngleUtility angleUtility = new AngleUtility(6, 60, 3, 30);
		Vector3<Double> enemyCoords = new Vector3<>(target.posX, target.posY, target.posZ);
		Vector3<Double> myCoords = new Vector3<>(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		Angle dstAngle = angleUtility.calculateAngle(enemyCoords, myCoords);
		Angle smoothedAngle1 = angleUtility.smoothAngle(dstAngle, dstAngle);
		return new float[] { smoothedAngle1.getYaw(), smoothedAngle1.getPitch() };
	}

	public static float[] getRotation123(EntityLivingBase curTarget2) {
		double xDiff = curTarget2.posX - mc.thePlayer.posX;
		double yDiff = curTarget2.posY - mc.thePlayer.posY;
		double zDiff = curTarget2.posZ - mc.thePlayer.posZ;
		MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float newYaw = (float) Math.toDegrees(-Math.atan(xDiff / zDiff));
		if (zDiff < 0.0 && xDiff < 0.0) {
			newYaw = (float) (90.0 + Math.toDegrees(Math.atan(zDiff / xDiff)));
		} else if (zDiff < 0.0 && xDiff > 0.0) {
			newYaw = (float) (-90.0 + Math.toDegrees(Math.atan(zDiff / xDiff)));
		}
		float newPitch = (float) (-Math.atan2(
				(double) (curTarget2.posY + (double) curTarget2.getEyeHeight() / 0.0
						- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight())),
				(double) Math.hypot((double) xDiff, (double) zDiff)) * 180.0 / 3.141592653589793);
		if (yDiff >= -0.2 && yDiff <= 0.2) {
			newPitch = (float) (-Math.atan2(
					(double) (curTarget2.posY + (double) curTarget2.getEyeHeight() / HitLocation.CHEST.getOffset()
							- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight())),
					(double) Math.hypot((double) xDiff, (double) zDiff)) * 180.0 / 3.141592653589793);
		} else if (yDiff > -0.2) {
			newPitch = (float) (-Math.atan2(
					(double) (curTarget2.posY + (double) curTarget2.getEyeHeight() / HitLocation.FEET.getOffset()
							- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight())),
					(double) Math.hypot((double) xDiff, (double) zDiff)) * 180.0 / 3.141592653589793);
		} else if (yDiff < 0.2) {
			newPitch = (float) (-Math.atan2(
					(double) (curTarget2.posY + (double) curTarget2.getEyeHeight() / HitLocation.HEAD.getOffset()
							- (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight())),
					(double) Math.hypot((double) xDiff, (double) zDiff)) * 180.0 / 3.141592653589793);
		}

		return new float[]{newYaw, newPitch};
	}



	private void attack(EntityLivingBase entity) {

		mc.thePlayer.swingItem();
		mc.getNetHandler().addToSendQueue(new C02PacketUseEntity((Entity) entity, C02PacketUseEntity.Action.ATTACK));


	}


	private void block() {
		if (Autoblock.getValue() && !mc.gameSettings.keyBindUseItem.isPressed() && !isBlocking) {
			mc.thePlayer.itemInUseCount = mc.thePlayer.getHeldItem().getMaxItemUseDuration();
			this.mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, this.mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
			isBlocking = true;
		}
	}

	private void PreUnBlock() {
		if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
			KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			this.mc.playerController.onStoppedUsingItem(this.mc.thePlayer);
		}
	}


	public static void Preblock() {
		if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
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
		if (curtarget == null || this.espmode.getValue() == EMode.None)
			return;
		Color color = (curtarget.hurtTime > 0) ? new Color(-1618884) : new Color(-13330213), color2 = color;
		if (curtarget != null && this.espmode.getValue() == EMode.Box) {
			mc.getRenderManager();
			double x = curtarget.lastTickPosX + (curtarget.posX - curtarget.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
			mc.getRenderManager();
			double y = curtarget.lastTickPosY + (curtarget.posY - curtarget.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
			mc.getRenderManager();
			double z = curtarget.lastTickPosZ + (curtarget.posZ - curtarget.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
			if (curtarget instanceof EntityPlayer) {
				double width = (curtarget.getEntityBoundingBox()).maxX - (curtarget.getEntityBoundingBox()).minX;
				double height2 = (curtarget.getEntityBoundingBox()).maxY - (curtarget.getEntityBoundingBox()).minY + 0.25D;
				float red = (curtarget.hurtTime > 0) ? 1.0F : 0.0F;
				float green = (curtarget.hurtTime > 0) ? 0.2F : 0.5F;
				float blue = (curtarget.hurtTime > 0) ? 0.0F : 1.0F;
				float alpha = 0.2F;
				float lineRed = (curtarget.hurtTime > 0) ? 1.0F : 0.0F;
				float lineGreen = (curtarget.hurtTime > 0) ? 0.2F : 0.5F;
				float lineBlue = (curtarget.hurtTime > 0) ? 0.0F : 1.0F;
				float lineAlpha = 1.0F;
				float lineWdith = 2.0F;
				RenderUtil.drawEntityESP(x, y, z, width, height2, red, green, blue, alpha, lineRed, lineGreen, lineBlue, lineAlpha, lineWdith);
			} else {
				double width = (curtarget.getEntityBoundingBox()).maxX - (curtarget.getEntityBoundingBox()).minX + 0.1D;
				double height3 = (curtarget.getEntityBoundingBox()).maxY - (curtarget.getEntityBoundingBox()).minY + 0.25D;
				float red = (curtarget.hurtTime > 0) ? 1.0F : 0.0F;
				float green = (curtarget.hurtTime > 0) ? 0.2F : 0.5F;
				float blue = (curtarget.hurtTime > 0) ? 0.0F : 1.0F;
				float alpha = 0.2F;
				float lineRed = (curtarget.hurtTime > 0) ? 1.0F : 0.0F;
				float lineGreen = (curtarget.hurtTime > 0) ? 0.2F : 0.5F;
				float lineBlue = (curtarget.hurtTime > 0) ? 0.0F : 1.0F;
				float lineAlpha = 1.0F;
				float lineWdith = 2.0F;
				RenderUtil.drawEntityESP(x, y, z, width, height3, red, green, blue, alpha, lineRed, lineGreen, lineBlue, lineAlpha, lineWdith);
			}
		} else if (this.espmode.getValue() == EMode.LiquidBounce) {
			double x;
			double y;
			double z;
			double width;
			double height;
			float red;
			float green;
			float blue;
			float alpha;
			float lineRed;
			float lineGreen;
			float lineBlue;
			float lineAlpha;
			float lineWdith;
			this.mc.getRenderManager();
			x = KillAura.curtarget.lastTickPosX + (KillAura.curtarget.posX - KillAura.curtarget.lastTickPosX) * this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
			this.mc.getRenderManager();
			y = KillAura.curtarget.lastTickPosY + (KillAura.curtarget.posY - KillAura.curtarget.lastTickPosY) * this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
			this.mc.getRenderManager();
			z = KillAura.curtarget.lastTickPosZ + (KillAura.curtarget.posZ - KillAura.curtarget.lastTickPosZ) * this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
			if (KillAura.curtarget instanceof EntityPlayer) {
				x -= 0.5;
				z -= 0.5;
				y += KillAura.curtarget.getEyeHeight() + 0.35 - (KillAura.curtarget.isSneaking() ? 0.25 : 0.0);
				final double mid = 0.5;
				GL11.glPushMatrix();
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				final double rotAdd = -0.25 * (Math.abs(KillAura.curtarget.rotationPitch) / 90.0f);
				GL11.glTranslated(x + mid, y + mid, z + mid);
				GL11.glRotated((double) (-KillAura.curtarget.rotationYaw % 360.0f), 0.0, 1.0, 0.0);
				GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
				GL11.glDisable(3553);
				GL11.glEnable(2848);
				GL11.glDisable(2929);
				GL11.glDepthMask(false);
				GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
				GL11.glLineWidth(2.0f);
				RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 0.05, z + 1.0));
				GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
				RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 0.05, z + 1.0));
				GL11.glDisable(2848);
				GL11.glEnable(3553);
				GL11.glEnable(2929);
				GL11.glDepthMask(true);
				GL11.glDisable(3042);
				GL11.glPopMatrix();
			} else {
				width = KillAura.curtarget.getEntityBoundingBox().maxZ - KillAura.curtarget.getEntityBoundingBox().minZ;
				height = 0.1;
				red = 0.0f;
				green = 0.5f;
				blue = 1.0f;
				alpha = 0.5f;
				lineRed = 0.0f;
				lineGreen = 0.5f;
				lineBlue = 1.0f;
				lineAlpha = 1.0f;
				lineWdith = 2.0f;
				RenderUtil.drawEntityESP(x, y + KillAura.curtarget.getEyeHeight() + 0.25, z, width, height, red, green, blue, alpha, lineRed, lineGreen, lineBlue, lineAlpha, lineWdith);
			}
		}
	}

	static enum AuraMode {
		Switch,
		Single,
	}

	static enum RotationMode {
		Viro,
		Loser,
		Smart,
		GodLike
	}

	static enum EMode {
		Box,
		None,
		RainBow,
		LiquidBounce;
	}
}