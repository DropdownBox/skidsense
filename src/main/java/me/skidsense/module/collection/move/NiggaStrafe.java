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
import me.skidsense.util.PlayerUtil;
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
	public static int direction = -1;
	public static int ticks;

	public static Numbers<Double> MaxDistance = (Numbers<Double>)new Numbers("Distance", "Distance", 3.0, 0.1, 5.0, 0.1);

	public NiggaStrafe() {
		super("Nigga Strafe", new String[]{"NiggaStrafe"}, ModuleType.Move);
		this.entityList = new ArrayList<EntityLivingBase>();
	}

	public void onDisable() {
		super.onDisable();
	}

	private void switchDirection() {
		if (this.direction == 1) {
			this.direction = -1;
		} else {
			this.direction = 1;
		}

	}
	public static boolean canStrafe() {
		return Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && KillAura.target != null && Client.getModuleManager().getModuleByClass(NiggaStrafe.class).isEnabled();
	}


	public static final boolean doStrafeAtSpeed(EventMove event, double moveSpeed) {
		boolean strafe = canStrafe();
		if (strafe) {
			float[] rotations = RotationUtil.getRotations(KillAura.target);
			if ((double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(KillAura.target) <= (Double)MaxDistance.getValue()) {
				PlayerUtil.setSpeed(event, moveSpeed, rotations[0], (double)direction, 0.0D);
			} else {
				PlayerUtil.setSpeed(event, moveSpeed, rotations[0], (double)direction, 1.0D);
			}
		}

		return strafe;
	}


	@Sub
	public void onUpdate(EventPreUpdate updateEvent) {
		if (Minecraft.getMinecraft().thePlayer.isCollidedHorizontally) {
			this.switchDirection();
		}
	}
}