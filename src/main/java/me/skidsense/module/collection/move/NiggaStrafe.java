package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class NiggaStrafe extends Mod {
	public static EntityLivingBase entity;
	private List<EntityLivingBase> entityList;
	public static Vec3f vec3;
	public static int index;
	public static int size;
	private boolean hadtarget;
	private boolean changedierection;
	private static boolean canstrafe;
	public static int ticks;

	public static Numbers<Double> MaxDistance = (Numbers<Double>)new Numbers("Distance", "Distance", 3.0, 0.1, 5.0, 0.1);
	public static Option<Boolean> press = (Option<Boolean>)new Option("Press Space only", "Press Space only", (Object)true);

	public NiggaStrafe() {
		super("Nigga Strafe", new String[]{"NiggaStrafe"}, ModuleType.Move);
		this.entityList = new ArrayList<EntityLivingBase>();
	}

	public void onDisable() {
		super.onDisable();
	}

	@Sub
	public void onUpdate(EventPreUpdate updateEvent) {
		if (!Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() || (!this.mc.gameSettings.keyBindJump.isKeyDown() && press.getValue())) {
			NiggaStrafe.entity = null;
			this.hadtarget = false;
			return;
		}
		NiggaStrafe.entity = this.getTargets();
		if (NiggaStrafe.entity != null) {
			ArrayList<Vec3f> list = new ArrayList<Vec3f>();
			for (float n = 0.0f; n < 6.283184051513672; n += 0.23271053f) {
				list.add(new Vec3f(MaxDistance.getValue() * Math.cos(n) + NiggaStrafe.entity.posX, NiggaStrafe.entity.posY, MaxDistance.getValue() * Math.sin(n) + NiggaStrafe.entity.posZ));
			}
			NiggaStrafe.size = list.size();
			if (!this.hadtarget) {
				ArrayList list2 = new ArrayList<Object>(list);
				list2.sort(Comparator.comparingDouble(vec3f -> this.mc.thePlayer.getDistance(vec3.getX(), vec3.getY(), vec3.getZ())));
				NiggaStrafe.index = list.indexOf(list2.get(0));
				this.hadtarget = true;
			}else {
				BlockPos blockPos = new BlockPos(list.get(NiggaStrafe.index).getX(), list.get(NiggaStrafe.index).getY(), list.get(NiggaStrafe.index).getZ());
				NiggaStrafe.vec3 = new Vec3f(blockPos.getX() + 0.5f, list.get(NiggaStrafe.index).getY(), blockPos.getZ());
				if (this.voidcheck(NiggaStrafe.vec3)
						|| this.mc.theWorld.getBlockState(new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY, NiggaStrafe.vec3.getZ())).getBlock().getCollisionBoundingBox(this.mc.theWorld, new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY, NiggaStrafe.vec3.getZ()), this.mc.theWorld.getBlockState(new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY, NiggaStrafe.vec3.getZ()))) != null
						|| this.mc.theWorld.getBlockState(new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY + 1.0, NiggaStrafe.vec3.getZ())).getBlock().getCollisionBoundingBox(this.mc.theWorld, new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY + 1.0, NiggaStrafe.vec3.getZ()), this.mc.theWorld.getBlockState(new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY + 1.0, NiggaStrafe.vec3.getZ()))) != null
						|| this.mc.theWorld.getBlockState(new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY + 2.0, NiggaStrafe.vec3.getZ())).getBlock().getCollisionBoundingBox(this.mc.theWorld, new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY + 2.0, NiggaStrafe.vec3.getZ()), this.mc.theWorld.getBlockState(new BlockPos(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY + 2.0, NiggaStrafe.vec3.getZ()))) != null) {

					if (!(canstrafe = !canstrafe)) {
						if (NiggaStrafe.index + 1 > list.size() - 1) {
							NiggaStrafe.index = 0;
						}
						else {
							++NiggaStrafe.index;
						}
					}
					else if (NiggaStrafe.index - 1 < 0) {
						NiggaStrafe.index = list.size() - 1;
					}
					else {
						--NiggaStrafe.index;
					}
				}else {
					if (this.mc.thePlayer.isCollidedHorizontally) {
						if (!this.changedierection) {
							canstrafe = !canstrafe;
							this.changedirection(list);
							this.changedierection = true;
						}
					}else {
						this.changedierection = false;
					}
					if (this.mc.gameSettings.keyBindRight.isPressed()) {
						canstrafe = true;
					}
					else if (this.mc.gameSettings.keyBindLeft.isPressed()) {
						canstrafe = false;
					}
					if (this.mc.thePlayer.getDistance(NiggaStrafe.vec3.getX(), this.mc.thePlayer.posY, NiggaStrafe.vec3.getZ()) <= this.mc.thePlayer.getDistance(this.mc.thePlayer.prevPosX, this.mc.thePlayer.prevPosY, this.mc.thePlayer.prevPosZ) * 2.0) {
						this.changedirection(list);
					}
				}
			}
		}
		else {
			this.hadtarget = false;
			NiggaStrafe.index = 0;
			NiggaStrafe.vec3 = null;
		}
	}

	private void changedirection(ArrayList<Vec3f> list) {
		if (!canstrafe) {
			if (NiggaStrafe.index + 1 > list.size() - 1) {
				NiggaStrafe.index = 0;
			}
			else {
				++NiggaStrafe.index;
			}
		}
		else if (NiggaStrafe.index - 1 < 0) {
			NiggaStrafe.index = list.size() - 1;
		}
		else {
			--NiggaStrafe.index;
		}
	}
	//cricle render


	private EntityLivingBase getTargets() {
		this.entityList.clear();
		double n = Double.MAX_VALUE;
		if (this.mc.theWorld != null) {
			for (Entity next : this.mc.theWorld.loadedEntityList) {
				if (next instanceof EntityLivingBase) {
					EntityLivingBase entityIn2 = (EntityLivingBase) next;
					if (this.mc.thePlayer.getDistanceToEntity(entityIn2) >= n && KillAura.target == entityIn2) {
						continue;
					}
					this.entityList.add(entityIn2);
				}
			}
		}
		if (this.entityList.isEmpty()) {
			return null;
		}
		this.entityList.sort(Comparator.comparingDouble(entityIn -> this.mc.thePlayer.getDistanceToEntity(entityIn)));
		return this.entityList.get(0);
	}

	public boolean voidcheck(Vec3f vec3) {
		for (int i = (int)Math.ceil(vec3.getX()); i >= 0; --i) {
			if (this.mc.theWorld.getBlockState(new BlockPos(vec3.getX(), i, vec3.getZ())).getBlock() != Blocks.air) {
				return false;
			}
		}
		return true;
	}

	public static void setMotionMoonx(EventMove motionEvent, double n) {//speed setmotion时使用这个 要不然会回弹
		++ticks;
		if (KillAura.target != null && voidcheck() && ticks > 4) {
			ticks = 0;
			canstrafe = canstrafe;
		}
		boolean b = Client.getModuleManager().getModuleByClass(NiggaStrafe.class).isEnabled()
				&& NiggaStrafe.vec3 != null
				&& NiggaStrafe.entity != null
				&& (mc.gameSettings.keyBindJump.isKeyDown()
				|| !NiggaStrafe.press.getValue());

		double n2 = b ? ((double)((Math.abs(Minecraft.getMinecraft().thePlayer.movementInput.moveForward) > 0.0f
				|| Math.abs(Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe) > 0.0f) ? 1 : 0)) : Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
		double n3 = b ? 0.0 : Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
		float n4 = b ? angle(NiggaStrafe.vec3.getX(), NiggaStrafe.vec3.getZ()) : Minecraft.getMinecraft().thePlayer.rotationYaw;
		if (n2 == 0.0 && n3 == 0.0) {
			motionEvent.setX(0.0);
			motionEvent.setZ(0.0);
		}
		else {
			if (n2 != 0.0) {
				if (n3 > 0.0) {
					n4 += ((n2 > 0.0) ? -45 : 45);
				}
				else if (n3 < 0.0) {
					n4 += ((n2 > 0.0) ? 45 : -45);
				}
				n3 = 0.0;
				if (n2 > 0.0) {
					n2 = 1.0;
				}
				else if (n2 < 0.0) {
					n2 = -1.0;
				}
			}
			motionEvent.setX(n2 * n * -Math.sin(Math.toRadians(n4)) + n3 * n * Math.cos(Math.toRadians(n4)));
			motionEvent.setZ(n2 * n * Math.cos(Math.toRadians(n4)) - n3 * n * -Math.sin(Math.toRadians(n4)));
		}
	}

	public static float angle(final double x, final double z) {
		return (float)(Math.atan2(z - Minecraft.getMinecraft().thePlayer.posZ, x - Minecraft.getMinecraft().thePlayer.posX) * 180.0 / 3.141592653589793) - 90.0f;
	}

	public static boolean voidcheck() {
		for (int i = (int)Math.ceil(Minecraft.getMinecraft().thePlayer.posY); i >= 0; --i) {
			if (mc.theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, i, Minecraft.getMinecraft().thePlayer.posZ)).getBlock() != Blocks.air) {
				return false;
			}
		}
		return true;
	}
}