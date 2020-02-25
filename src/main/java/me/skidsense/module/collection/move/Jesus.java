/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.module.collection.move;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventCollideWithBlock;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.BlockUtil;
import me.skidsense.util.TimerUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Jesus extends Module {

	int stage, water;
	private TimerUtil timer = new TimerUtil();
	private boolean wasWater = false;
	private int ticks = 0;
	private Mode<Enum> mode = new Mode("Mode", "Mode", (Enum[]) JMode.values(), (Enum) JMode.Dolphin);

	public Jesus() {
		super("Liquid Walk", new String[] { "LiquidWalk", "float" }, ModuleType.Move);
		this.setColor(new Color(188, 233, 248).getRGB());
		this.addValues(this.mode);
		this.removed=true;
	}

	@Override
	public void onEnable() {
		this.wasWater = false;
		super.onEnable();
	}

	private boolean canJeboos() {
		if (!(this.mc.thePlayer.fallDistance >= 3.0f || this.mc.gameSettings.keyBindJump.isPressed()
				|| BlockUtil.isInLiquid() || this.mc.thePlayer.isSneaking())) {
			return true;
		}
		return false;
	}

	boolean shouldJesus() {
		double x = mc.thePlayer.posX;
		double y = mc.thePlayer.posY;
		double z = mc.thePlayer.posZ;
		ArrayList<BlockPos> pos = new ArrayList<BlockPos>(
				Arrays.asList(new BlockPos(x + 0.3, y, z + 0.3), new BlockPos(x - 0.3, y, z + 0.3),
						new BlockPos(x + 0.3, y, z - 0.3), new BlockPos(x - 0.3, y, z - 0.3)));
		for (BlockPos po : pos) {
			if (!(mc.theWorld.getBlockState(po).getBlock() instanceof BlockLiquid))
				continue;
			if (mc.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) instanceof Integer) {
				if ((int) mc.theWorld.getBlockState(po).getProperties().get(BlockLiquid.LEVEL) <= 4) {
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onPre(EventPreUpdate e) {
		this.setSuffix(this.mode.getValue());
		if (this.mode.getValue() == JMode.Dolphin) {
			if (mc.thePlayer.isInWater() && !mc.thePlayer.isSneaking() && this.shouldJesus()) {
				mc.thePlayer.motionY = 0.09;
			}
			if (e.getType() == 1) {
				return;
			}
			if (this.mc.thePlayer.onGround || this.mc.thePlayer.isOnLadder()) {
				this.wasWater = false;
			}
			if (this.mc.thePlayer.motionY > 0.0 && this.wasWater) {
				if (this.mc.thePlayer.motionY <= 0.11) {
					EntityPlayerSP player = this.mc.thePlayer;
					player.motionY *= 1.2671;
				}
				EntityPlayerSP player2 = this.mc.thePlayer;
				player2.motionY += 0.05172;
			}
			if (isInLiquid() && !this.mc.thePlayer.isSneaking()) {
				if (this.ticks < 3) {
					this.mc.thePlayer.motionY = 0.13;
					++this.ticks;
					this.wasWater = false;
				} else {
					this.mc.thePlayer.motionY = 0.5;
					this.ticks = 0;
					this.wasWater = true;
				}
			}
		} else if (this.mode.getValue() == JMode.Motion) {
			if (BlockUtil.isInLiquid() && !this.mc.thePlayer.isSneaking()
					&& !this.mc.gameSettings.keyBindJump.isPressed()) {
				this.mc.thePlayer.motionY = 0.05;
				this.mc.thePlayer.onGround = true;
			}
		}
	}

	private boolean isInLiquid() {
		if (mc.thePlayer == null) {
			return false;
		}
		for (int x = MathHelper.floor_double(mc.thePlayer.boundingBox.minX); x < MathHelper
				.floor_double(mc.thePlayer.boundingBox.maxX) + 1; x++) {
			for (int z = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); z < MathHelper
					.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; z++) {
				BlockPos pos = new BlockPos(x, (int) mc.thePlayer.boundingBox.minY, z);
				Block block = mc.theWorld.getBlockState(pos).getBlock();
				if ((block != null) && (!(block instanceof BlockAir))) {
					return block instanceof BlockLiquid;
				}
			}
		}
		return false;
	}

	public double getMotionY(double stage) {
		stage--;
		double[] motion = new double[] { 0.500, 0.484, 0.468, 0.436, 0.404, 0.372, 0.340, 0.308, 0.276, 0.244, 0.212,
				0.180, 0.166, 0.166, 0.156, 0.123, 0.135, 0.111, 0.086, 0.098, 0.073, 0.048, 0.06, 0.036, 0.0106, 0.015,
				0.004, 0.004, 0.004, 0.004, -0.013, -0.045, -0.077, -0.109 };
		if (stage < motion.length && stage >= 0)
			return motion[(int) stage];
		else
			return -999;

	}

	public static boolean isOnGround(double height) {
		if (!Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer,
				Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static int getSpeedEffect() {
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed))
			return Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
		else
			return 0;
	}

	public static void setMotion(double Speed) {
		double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
		double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
		float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
		if ((forward == 0.0D) && (strafe == 0.0D)) {
			Minecraft.getMinecraft().thePlayer.motionX = 0;
			Minecraft.getMinecraft().thePlayer.motionZ = 0;
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0D) {
					yaw += (forward > 0.0D ? -45 : 45);
				} else if (strafe < 0.0D) {
					yaw += (forward > 0.0D ? 45 : -45);
				}
				strafe = 0.0D;
				if (forward > 0.0D) {
					forward = 1;
				} else if (forward < 0.0D) {
					forward = -1;
				}
			}
			Minecraft.getMinecraft().thePlayer.motionX = forward * Speed * Math.cos(Math.toRadians(yaw + 90.0F))
					+ strafe * Speed * Math.sin(Math.toRadians(yaw + 90.0F));
			Minecraft.getMinecraft().thePlayer.motionZ = forward * Speed * Math.sin(Math.toRadians(yaw + 90.0F))
					- strafe * Speed * Math.cos(Math.toRadians(yaw + 90.0F));
		}
	}

	@EventHandler
	public void onPacket(EventPacketSend e) {
		if (this.mode.getValue() == JMode.Motion) {
			if (e.getPacket() instanceof C03PacketPlayer && this.canJeboos() && BlockUtil.isOnLiquid()) {
				C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
				packet.y = this.mc.thePlayer.ticksExisted % 2 == 0 ? packet.getPositionY() + 0.01 : packet.getPositionY() - 0.01;
			}
		}
	}

	@EventHandler
	public void onBB(EventCollideWithBlock e) {
		if (this.mode.getValue() == JMode.Motion) {
			if (e.getBlock() instanceof BlockLiquid && this.canJeboos()) {
				e.setBoundingBox(new AxisAlignedBB(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(),
						(double) e.getPos().getX() + 1.0, (double) e.getPos().getY() + 1.0,
						(double) e.getPos().getZ() + 1.0));
			}
		}

	}

	static enum JMode {
		Motion, Dolphin;
	}
}

