package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.RotationUtil;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.util.glu.*;


public class AutoStrafe extends Module
{
	public static Numbers<Double> MaxDistance = (Numbers<Double>)new Numbers("Distance", "Distance", (Number)3.0, (Number)1.0, (Number)5.0, (Number)0.1);;
	public static Option<Boolean> keep = (Option<Boolean>)new Option("KeepDistance", "KeepDistance", (Object)true);;
	public static Option<Boolean> Esp = (Option<Boolean>)new Option("TargetESP", "TargetESP", (Object)true);;
	public static Option<Boolean> OnlySpeed = (Option<Boolean>)new Option("Speed", "Speed", (Object)true);
	public static Option<Boolean> Auto = (Option<Boolean>)new Option("Auto", "Auto", (Object)true);

	public AutoStrafe() {
		super("Auto Strafe", new String[] { "AutoStrafe" }, ModuleType.Move);
		this.addValues(new Value[] { (Value)AutoStrafe.MaxDistance, (Value)AutoStrafe.keep, (Value)AutoStrafe.Esp, (Value)AutoStrafe.OnlySpeed, (Value)AutoStrafe.Auto });
	}

	public void onDisable() {
		super.onDisable();
	}

	public static double getSpeedByXZ(final double motionX, final double motionZ) {
		final double vel = Math.sqrt(motionX * motionX + motionZ * motionZ);
		return vel;
	}

	@EventHandler
	public void onMotion(final EventMove eventMove) {
		if (KillAura.target != null && Client.instance.getModuleManager().getModuleByClass((Class)KillAura.class).isEnabled() && !AutoStrafe.mc.thePlayer.isOnLadder() && !(boolean)AutoStrafe.OnlySpeed.getValue()) {
			onStrafe(eventMove);
		}
	}

	public static void onStrafe(final EventMove eventMove) {
		if (KillAura.target != null) {
			if (!RotationUtil.canEntityBeSeen((Entity)KillAura.target)) {
				return;
			}
			final double speed = getSpeedByXZ(eventMove.getX(), eventMove.getZ());
			setMoveSpeed(speed * 0.9, KillAura.rotateNCP(KillAura.target)[0], Math.abs(AutoStrafe.mc.thePlayer.getDistanceToEntity((Entity)KillAura.target) - ((Double)AutoStrafe.MaxDistance.getValue()).floatValue()) <= 0.4, eventMove, ((Double)AutoStrafe.MaxDistance.getValue()).floatValue());
		}
	}

	public static void onStrafe(final EventMove eventMove, final double speed) {
		if (KillAura.target != null) {
			if (!RotationUtil.canEntityBeSeen((Entity)KillAura.target)) {
				return;
			}
			setMoveSpeed(speed * 0.9, KillAura.rotateNCP(KillAura.target)[0], Math.abs(AutoStrafe.mc.thePlayer.getDistanceToEntity((Entity)KillAura.target) - ((Double)AutoStrafe.MaxDistance.getValue()).floatValue()) <= 0.4, eventMove, ((Double)AutoStrafe.MaxDistance.getValue()).floatValue());
		}
	}

	public static void setMoveSpeed(final double speed, final float yaw, final boolean forwardTo, final EventMove eventMove, final float dist) {
		final MovementInput movementInput = AutoStrafe.mc.thePlayer.movementInput;
		double forward = MovementInput.moveForward;
		final MovementInput movementInput2 = AutoStrafe.mc.thePlayer.movementInput;
		double strafe = MovementInput.moveStrafe;
		if (AutoStrafe.keep.getValue()) {
			if (forwardTo) {
				if (forward > 0.0) {
					forward = 0.0;
				}
			}
			else if (AutoStrafe.mc.thePlayer.getDistanceToEntity((Entity)KillAura.target) < dist) {
				forward = -speed;
			}
		}
		else {
			forward = ((forward > 0.0) ? 1 : ((forward < 0.0) ? -1 : 0));
			forward *= speed;
		}
		strafe = ((strafe > 0.0) ? 1 : ((strafe < 0.0) ? -1 : 1));
		EventMove.x = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
		EventMove.z = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
	}

	@EventHandler
	public void onRender(final EventRender3D render) {
		if (KillAura.target != null) {
			this.drawESP(render);
		}
	}

	private void drawESP(final EventRender3D render) {
		if (!(boolean)AutoStrafe.Esp.getValue()) {
			return;
		}
		final double x = KillAura.target.lastTickPosX + (KillAura.target.posX - KillAura.target.lastTickPosX) * render.getPartialTicks() - AutoStrafe.mc.getRenderManager().viewerPosX;
		final double y = KillAura.target.lastTickPosY + (KillAura.target.posY - KillAura.target.lastTickPosY) * render.getPartialTicks() - AutoStrafe.mc.getRenderManager().viewerPosY;
		final double z = KillAura.target.lastTickPosZ + (KillAura.target.posZ - KillAura.target.lastTickPosZ) * render.getPartialTicks() - AutoStrafe.mc.getRenderManager().viewerPosZ;
		this.esp((Entity)KillAura.target, x, y, z);
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
		if (KillAura.target.hurtTime <= 0) {
			GlStateManager.color(0.9f, 0.9f, 0.9f, 0.9f);
		}
		else {
			GlStateManager.color(1.35f, 0.0f, 0.0f, 1.0f);
		}
		GlStateManager.rotate(180.0f, 90.0f, 0.0f, 2.0f);
		GlStateManager.rotate(180.0f, 0.0f, 90.0f, 90.0f);
		final Cylinder c = new Cylinder();
		c.setDrawStyle(100011);
		c.draw(((Double)AutoStrafe.MaxDistance.getValue()).floatValue(), ((Double)AutoStrafe.MaxDistance.getValue()).floatValue(), 0.0f, 500, 1);
		GL11.glDepthMask(true);
		GL11.glDisable(2848);
		GL11.glEnable(2929);
		GL11.glDisable(3042);
		GL11.glEnable(2896);
		GL11.glEnable(3553);
		GL11.glPopMatrix();
	}
}
