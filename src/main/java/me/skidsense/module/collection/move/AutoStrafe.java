package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.player.AntiFall;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.stats.StatList;
import net.minecraft.util.MovementInput;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;


public class AutoStrafe extends Mod {
	public Option<Boolean> Render = new Option<Boolean>("Render", "Render", true);
	public static Option<Boolean> OnSpace = new Option<Boolean>("OnSpace", "OnSpace", true);
	public Numbers<Double> Distance = new Numbers<Double>("Distance", "Distance", 1.6,0.1,3.0,0.1);
	private int direction = -1;
//	private Setting range;
//	private Setting render;
//	private Setting renderheight;
//	private Setting space;

	public AutoStrafe() {
		super("Auto Strafe",new String[]{"TargetStrafe","AutoStrafe"}, ModuleType.Move);
//		Sight.instance.sm.rSetting(space = new Setting("OnSpace", this, true));
//		Sight.instance.sm.rSetting(render = new Setting("Render", this, true));
//		Sight.instance.sm.rSetting(renderheight = new Setting("RenderHeight", this, 0.05, 0.01, 1, false));
//		Sight.instance.sm.rSetting(range = new Setting("Range", this, 1.6, 0.1, 3, false));
	}



	@Sub
	public void onPreUpdate(EventPreUpdate e){
		if (mc.thePlayer.isCollidedHorizontally) {
			if (this.direction == -1) {
				this.direction = 1;
			} else {
				this.direction = -1;
			}
		}
	}

	@Sub
	public void onMove(EventMove e){
		if (this.canStrafe()) {
			this.strafe(e, MoveUtil.getBaseMoveSpeed());
			mc.thePlayer.isAirBorne = true;
			mc.thePlayer.triggerAchievement(StatList.jumpStat);
		}
	}

	@Sub
	public void onRender(EventRender3D e){
		if (KillAura.target != null) {
			this.drawRadius(KillAura.target, ((EventRender3D) e).getPartialTicks(), Distance.getValue());
		}
	}

	public void strafe(EventMove e, double moveSpeed) {
		mc.thePlayer.onGround = false;
		float[] rots = RotationUtil.getRotations(KillAura.target);
		double dist = mc.thePlayer.getDistanceToEntity(KillAura.target);
		if (dist >= Distance.getValue()) {
			setSpeed(e, moveSpeed, rots[0], direction, 1);
		} else {
			setSpeed(e, moveSpeed, rots[0], direction, 0);
		}
	}

	public static void setSpeed(final EventMove moveEvent, final double moveSpeed, final float pseudoYaw,
								final double pseudoStrafe, final double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;

		if (forward == 0.0 && strafe == 0.0) {
			moveEvent.setZ(0);
			moveEvent.setX(0);
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += ((forward > 0.0) ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += ((forward > 0.0) ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}
			final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
			final double sin = Math.sin(Math.toRadians(yaw + 90.0f));

			moveEvent.setX((forward * moveSpeed * cos + strafe * moveSpeed * sin));
			moveEvent.setZ((forward * moveSpeed * sin - strafe * moveSpeed * cos));
		}
	}

	private void drawRadius(final Entity entity, final float partialTicks, final double rad) {
		float points = 90F;
		GlStateManager.enableDepth();
		for (double il = 0; il < 4.9E-324; il += 4.9E-324) {
			GL11.glPushMatrix();
			GL11.glDisable(3553);
			GL11.glEnable(2848);
			GL11.glEnable(2881);
			GL11.glEnable(2832);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glHint(3154, 4354);
			GL11.glHint(3155, 4354);
			GL11.glHint(3153, 4354);
			GL11.glDisable(2929);
			GL11.glLineWidth(6.0f);
			GL11.glBegin(3);
			final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
			final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
			final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
			final double pix2 = 6.283185307179586;
			float speed = 5000f;
			float baseHue = System.currentTimeMillis() % (int)speed;
			while (baseHue > speed) {
				baseHue -= speed;
			}
			baseHue /= speed;
			for (int i = 0; i <= 90; ++i) {
				float max = ((float) i + (float)(il * 8)) / points;
				float hue = max + baseHue ;
				while (hue > 1) {
					hue -= 1;
				}
				final float r = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getRed();
				final float g = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getGreen();
				final float b = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getBlue();
				GL11.glColor3f(r, g, b);
				GL11.glVertex3d(x + rad * Math.cos(i * pix2 / points), y + il, z + rad * Math.sin(i * pix2 / points));
			}
			GL11.glEnd();
			GL11.glDepthMask(true);
			GL11.glEnable(2929);
			GL11.glDisable(2848);
			GL11.glDisable(2881);
			GL11.glEnable(2832);
			GL11.glEnable(3553);
			GL11.glPopMatrix();
			GlStateManager.color(255, 255, 255);
		}
	}

	public static boolean canStrafe() {
		if (OnSpace.getValue() && !mc.gameSettings.keyBindJump.isKeyDown()) {
			return false;
		}
		return KillAura.target != null && Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() &&Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() && !Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled();
	}
}
