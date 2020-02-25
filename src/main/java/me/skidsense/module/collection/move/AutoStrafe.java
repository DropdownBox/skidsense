package me.skidsense.module.collection.move;

import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.RotationUtil;

public class AutoStrafe extends Module {
	public static Numbers<Double> MaxDistance = new Numbers<Double>("MaxDistance", "MaxDistance", 3.0d, 1.0d, 5.1d,
			0.5d);
	public static Option<Boolean> keep = new Option<Boolean>("KeepDist", "KeepDist", true);

	public AutoStrafe() {
		super("Auto Strafe", new String[] { "AutoStrafe" }, ModuleType.Fight);
		this.addValues(MaxDistance, keep);
	}

	@Override
	public void onDisable() {
		// KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(),
		// false);
		super.onDisable();
	}

	@EventHandler
	private void onPre(EventPreUpdate eventPPUpdate) {
		/*
		 * if (entityList != null && entityList.size() > 0 && !mc.thePlayer.isOnLadder()
		 * && Aura.target != null && mc.thePlayer.canEntityBeSeen(Aura.target) &&
		 * BlockCheck()) {
		 * KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(),
		 * true); }
		 */
	}

	public static boolean isEntityAllowed(Entity entity) {
		if (!(entity instanceof EntityLivingBase) || !(((EntityLivingBase) entity).getHealth() > 0)) {
			return false;
		}
		if (entity.isDead) {
			return false;
		}
		if (entity.equals(mc.thePlayer)) {
			return false;
		}
		if (entity instanceof EntityPlayer) {
			return true;
		}
		if (entity instanceof EntitySlime || entity instanceof EntityMob || entity instanceof EntityAnimal || entity instanceof EntityVillager) {
			return true;
		}
		return false;
	}

	public static double getSpeedByXZ(double motionX, double motionZ) {
		final double vel = Math.sqrt(motionX * motionX + motionZ * motionZ);
		return vel;
	}

	@EventHandler
	public void onMotion(EventMove eventMove) {
		if (Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && !mc.thePlayer.isOnLadder() && BlockCheck()) {
			onStrafe(eventMove, MaxBlock(), 1.0F);
		}

	}

	public static void onStrafe(EventMove eventMove, float dist, float p) {
		if (KillAura.target != null) {
			if (!RotationUtil.canEntityBeSeen(KillAura.target)) {
				return;
			}
			double speed = getSpeedByXZ(eventMove.getX(), eventMove.getZ());
			if (!Client.getModuleManager().getModuleByClass(Flight.class).isEnabled())
				setMoveSpeed(speed * p, (float) RotationUtil.getRotationToEntity(KillAura.target)[0],
						Math.abs(mc.thePlayer.getDistanceToEntity(KillAura.target) - dist) <= 0.4, eventMove, dist);
		}
	}

	public static void setMoveSpeed(final double speed, float yaw, boolean forwardTo, EventMove eventMove, float dist) {
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		if (keep.getValue()) {
			if (forwardTo) {
				if (forward > 0) {
					forward = 0;
				}
			} else {
				if (mc.thePlayer.getDistanceToEntity(KillAura.target) < dist)
					forward = -speed;
			}
		} else {
			forward = forward > 0 ? 1 : (forward < 0 ? -1 : 0);
			forward *= speed;
		}
		strafe = strafe > 0 ? 1 : (strafe < 0 ? -1 : 1);
		eventMove.x = (forward * speed * Math.cos(Math.toRadians((double) (yaw + 90.0f)))
				+ strafe * speed * Math.sin(Math.toRadians((double) (yaw + 90.0f))));
		eventMove.z = (forward * speed * Math.sin(Math.toRadians((double) (yaw + 90.0f)))
				- strafe * speed * Math.cos(Math.toRadians((double) (yaw + 90.0f))));
	}

	public static boolean BlockCheck() {
		for (int j = (int) 1; j <= 2; j++)
			if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + j, mc.thePlayer.posZ))
					.getBlock() instanceof BlockAir)) {
				return false;
			}
		return true;
	}

	public static int MaxBlock() {
		int max = 0;
		for (int i = 1; i < MaxDistance.getValue(); i++) {
			for (int j = (int) 0; j <= 2; j++) {
				boolean check = true;
				for (int x = -i; x <= i; x++) {
					for (int z = -i; z <= i; z++) {
						if (!(mc.theWorld.getBlockState(
								new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + j, mc.thePlayer.posZ + z))
								.getBlock() instanceof BlockAir)) {
							check = false;
						}
					}
				}
				if (!check) {
					return max;
				} else {
					max = i;
				}
			}

		}

		return max;
	}

	@EventHandler
	public void onRender(EventRender3D render) {
		if (KillAura.target != null) {
			double x = KillAura.target.lastTickPosX + (KillAura.target.posX - KillAura.target.lastTickPosX) * render.ticks
					- mc.getRenderManager().viewerPosX;
			double y = KillAura.target.lastTickPosY + (KillAura.target.posY - KillAura.target.lastTickPosY) * render.ticks
					- mc.getRenderManager().viewerPosY;
			double z = KillAura.target.lastTickPosZ + (KillAura.target.posZ - KillAura.target.lastTickPosZ) * render.ticks
					- mc.getRenderManager().viewerPosZ;
			esp(KillAura.target, x, y, z);
		}
			
	}

	public void esp(final Entity player, final double x, final double y, final double z) {
		GL11.glPushMatrix();
		GL11.glDisable(2896);
		GL11.glDisable(3553);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glDisable(2929);
		GL11.glEnable(2848);
		GL11.glDepthMask(true);
		GlStateManager.translate(x, y, z);
		if (!(KillAura.target.hurtTime > 0)) {
			GlStateManager.color(0.25f, 2.0f, 0.0f, 1.0f);
		} else {
			GlStateManager.color(1.35f, 0.0f, 0.0f, 1.0f);
		}
		// RenderUtil.color(Colors.WHITE.c);
		GlStateManager.rotate(180, 90.0f, 0, 2.0f);
		GlStateManager.rotate(180, 0.0f, 90, 90.0f);
		Cylinder c = new Cylinder();
		// if (targetStrafe.espmode.isCurrentMode("Point")) {
		// c.setDrawStyle(100010);
		/*
		 * } else { c.setDrawStyle(100011); }
		 */
		c.setDrawStyle(100011);
		c.draw(AutoStrafe.MaxDistance.getValue().floatValue(), AutoStrafe.MaxDistance.getValue().floatValue(), 0f,
				120, 2);

		GL11.glDepthMask(true);
		GL11.glDisable(2848);
		GL11.glEnable(2929);
		GL11.glDisable(3042);
		GL11.glEnable(2896);
		GL11.glEnable(3553);
		GL11.glPopMatrix();
	}

	public static void renderCircle(final double x, final double y, final double z, final double radius,
			final float lineWidth, final Color color, final float opacity) {
		GlStateManager.pushMatrix();
		GL11.glLineWidth(lineWidth + 1.2f);
		GL11.glColor3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
		GL11.glBegin(1);
		for (int i = 0; i <= 90; ++i) {
			final double angle = i * ((3.141592653589793 * 2) / 45);
			GL11.glColor4f((float) 0, (float) 0, (float) 0, opacity);
			GL11.glVertex3d(x + radius * Math.cos(angle), y, z + radius * Math.sin(angle));
		}
		GL11.glEnd();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GL11.glLineWidth(lineWidth);
		GL11.glColor3f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
		GL11.glBegin(1);
		for (int i = 0; i <= 90; ++i) {
			final double angle = i * ((3.141592653589793 * 2) / 45);
			GL11.glColor4f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), opacity);
			GL11.glVertex3d(x + radius * Math.cos(angle), y, z + radius * Math.sin(angle));
		}
		GL11.glEnd();
		GlStateManager.popMatrix();
	}
}
