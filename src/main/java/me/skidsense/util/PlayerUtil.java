package me.skidsense.util;

import me.skidsense.hooks.events.EventMove;
import me.skidsense.module.collection.combat.KillAura;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;

import java.util.List;

public class PlayerUtil {
	public static Minecraft mc = Minecraft.getMinecraft();

	public static boolean isOnLiquid() {
		AxisAlignedBB boundingBox = Minecraft.getMinecraft().thePlayer.getEntityBoundingBox();
		if (boundingBox == null) {
			return false;
		}
		boundingBox = boundingBox.contract(0.01D, 0.0D, 0.01D).offset(0.0D, -0.01D, 0.0D);
		boolean onLiquid = false;
		int y = (int) boundingBox.minY;
		for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper
				.floor_double(boundingBox.maxX + 1.0D); x++) {
	            for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper
	                    .floor_double(boundingBox.maxZ + 1.0D); z++) {
	                Block block = Minecraft.getMinecraft().theWorld.getBlockState((new BlockPos(x, y, z))).getBlock();
	                if (block != Blocks.air) {
	                    if (!(block instanceof BlockLiquid)) {
	                        return false;
	                    }
	                    onLiquid = true;
	                }
	            }
	        }
	        return onLiquid;
	    }

	 public static boolean isInLiquid() {
	        if (Minecraft.getMinecraft().thePlayer == null) {
	            return false;
	        }
	        for (int x = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.boundingBox.minX); x < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.boundingBox.maxX) + 1; x++) {
	            for (int z = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.boundingBox.minZ); z < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.boundingBox.maxZ) + 1; z++) {
	                BlockPos pos = new BlockPos(x, (int) Minecraft.getMinecraft().thePlayer.boundingBox.minY, z);
	                Block block = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
	                if ((block != null) && (!(block instanceof BlockAir))) {
	                    return block instanceof BlockLiquid;
	                }
	            }
	        }
	        return false;
	    }
	 
	 public static double getMotionY(double stage){
	    	stage --;
	    	double[] motion = new double[]{0.500,0.484,0.468,0.436,0.404,0.372,0.340,0.308,0.276,0.244,0.212,0.180,0.166,0.166,
	    			0.156,0.123,0.135,0.111,0.086,0.098,0.073,0.048,0.06,0.036,0.0106,0.015,0.004,0.004,0.004,0.004,
	    					-0.013,-0.045,-0.077,-0.109};
	    	if(stage < motion.length && stage >= 0)
	    		return motion[(int)stage];
	    	else
	    		return -999;
	    	
	    }
	 
	 public static boolean isTotalOnLiquid(double profondeur)
	  {	    
	    for(double x = Minecraft.getMinecraft().thePlayer.boundingBox.minX; x < Minecraft.getMinecraft().thePlayer.boundingBox.maxX; x +=0.01f){
	    	
			for(double z = Minecraft.getMinecraft().thePlayer.boundingBox.minZ; z < Minecraft.getMinecraft().thePlayer.boundingBox.maxZ; z +=0.01f){
				Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, Minecraft.getMinecraft().thePlayer.posY - profondeur,z)).getBlock();
   			if(!(block instanceof BlockLiquid) && !(block instanceof BlockAir)){
   				return false;
   			}
   		}
		}
	    return true;
	  }

	 public static boolean isOnLiquid(double profondeur)
	  {
	    boolean onLiquid = false;
	    
	    if(Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - profondeur, Minecraft.getMinecraft().thePlayer.posZ)).getBlock().getMaterial().isLiquid()) {
	      onLiquid = true;
	    }
	    return onLiquid;
	  }

		public static double getIncremental(final double val, final double inc) {
	        final double one = 1.0 / inc;
	        return Math.round(val * one) / one;
	    }

	    public static boolean MovementInput() {
	        return Minecraft.getMinecraft().gameSettings.keyBindForward.pressed || Minecraft.getMinecraft().gameSettings.keyBindLeft.pressed || Minecraft.getMinecraft().gameSettings.keyBindRight.pressed || Minecraft.getMinecraft().gameSettings.keyBindBack.pressed;
	    }

	    public static double getDistanceToFall(){
			double distance = 0;
			for(double i = Minecraft.getMinecraft().thePlayer.posY; i > 0; i -= 0.1){
				if(i < 0)
					break;
				Block block = BlockUtil.getBlock(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, i, Minecraft.getMinecraft().thePlayer.posZ));
				if(block.getMaterial() != Material.air  && (block.isCollidable()) && (block.isFullBlock() || block instanceof BlockSlab || block instanceof BlockBarrier || block instanceof BlockStairs || block instanceof BlockGlass || block instanceof BlockStainedGlass)){
					if(block instanceof BlockSlab)
						i -= 0.5;
					distance = i;
					break;
				}
			}
			return (Minecraft.getMinecraft().thePlayer.posY - distance);
		}

	public static void freezePlayer() {
		Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.posX + 1.0, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ + 1.0);
		Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.prevPosX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.prevPosZ);
	}

	public static float getDirection() {
		float yaw = mc.thePlayer.rotationYaw;
		if (mc.thePlayer.moveForward < 0.0f) {
			yaw += 180.0f;
		}
		float forward = 1.0f;
		if (mc.thePlayer.moveForward < 0.0f) {
			forward = -0.5f;
		} else if (mc.thePlayer.moveForward > 0.0f) {
			forward = 0.5f;
		}
		if (mc.thePlayer.moveStrafing > 0.0f) {
			yaw -= 90.0f * forward;
		}
		if (mc.thePlayer.moveStrafing < 0.0f) {
			yaw += 90.0f * forward;
		}
		return yaw *= 0.017453292f;
	}


	public static void setSpeed(double speed) {
		mc.thePlayer.motionX = (-Math.sin(PlayerUtil.getDirection())) * speed;
		mc.thePlayer.motionZ = Math.cos(PlayerUtil.getDirection()) * speed;
	}

	public static BlockPos getHypixelBlockpos(String str) {
		int val = 89;
		if (str != null && str.length() > 1) {
			char[] chs = str.toCharArray();

			int lenght = chs.length;
			for (int i = 0; i < lenght; i++)
				val += (int) chs[i] * str.length() * str.length() + (int) str.charAt(0) + (int) str.charAt(1);
			val /= str.length();
		}
		return new BlockPos(val, -val % 255, val);
	}

	public static void blinkToPos(final double[] startPos, final BlockPos endPos, final double slack, final double[] pOffset) {
		double curX = startPos[0];
		double curY = startPos[1];
		double curZ = startPos[2];
		try {
			final double endX = endPos.getX() + 0.5;
			final double endY = endPos.getY() + 1.0;
			final double endZ = endPos.getZ() + 0.5;

			double distance = Math.abs(curX - endX) + Math.abs(curY - endY) + Math.abs(curZ - endZ);
			int count = 0;
			while (distance > slack) {
				distance = Math.abs(curX - endX) + Math.abs(curY - endY) + Math.abs(curZ - endZ);
				if (count > 120) {
					break;
				}
				final boolean next = false;
				final double diffX = curX - endX;
				final double diffY = curY - endY;
				final double diffZ = curZ - endZ;
				final double offset = ((count & 0x1) == 0x0) ? pOffset[0] : pOffset[1];
				if (diffX < 0.0) {
					if (Math.abs(diffX) > offset) {
						curX += offset;
					} else {
						curX += Math.abs(diffX);
					}
				}
				if (diffX > 0.0) {
					if (Math.abs(diffX) > offset) {
						curX -= offset;
					} else {
						curX -= Math.abs(diffX);
					}
				}
				if (diffY < 0.0) {
					if (Math.abs(diffY) > 0.25) {
						curY += 0.25;
					} else {
						curY += Math.abs(diffY);
					}
				}
				if (diffY > 0.0) {
					if (Math.abs(diffY) > 0.25) {
						curY -= 0.25;
					} else {
						curY -= Math.abs(diffY);
					}
				}
				if (diffZ < 0.0) {
					if (Math.abs(diffZ) > offset) {
						curZ += offset;
					} else {
						curZ += Math.abs(diffZ);
					}
				}
				if (diffZ > 0.0) {
					if (Math.abs(diffZ) > offset) {
						curZ -= offset;
					} else {
						curZ -= Math.abs(diffZ);
					}
				}
				Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(curX, curY, curZ, true));
				++count;
			}
		} catch (Exception e) {

		}
	}

	public static void hypixelTeleport(final double[] startPos, final BlockPos endPos) {

		double distx = startPos[0] - endPos.getX() + 0.5;
		double disty = startPos[1] - endPos.getY();
		double distz = startPos[2] - endPos.getZ() + 0.5;
		double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
		double distanceEntreLesPackets = 0.31 + MoveUtil.getSpeedEffect() / 20;
		double xtp, ytp, ztp = 0;
		if (dist > distanceEntreLesPackets) {

			double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;

			xtp = mc.thePlayer.posX;
			ytp = mc.thePlayer.posY;
			ztp = mc.thePlayer.posZ;
			double count = 0;
			for (int i = 1; i < nbPackets; i++) {
				double xdi = (endPos.getX() - mc.thePlayer.posX) / (nbPackets);
				xtp += xdi;

				double zdi = (endPos.getZ() - mc.thePlayer.posZ) / (nbPackets);
				ztp += zdi;

				double ydi = (endPos.getY() - mc.thePlayer.posY) / (nbPackets);
				ytp += ydi;
				count++;

				if (!mc.theWorld.getBlockState(new BlockPos(xtp, ytp - 1, ztp)).getBlock().isFullCube()) {
					if (count <= 2) {
						ytp += 2E-8;
					} else if (count >= 4) {
						count = 0;
					}
				}
				C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, false);
				mc.thePlayer.sendQueue.addToSendQueue(Packet);
			}

			mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);

		} else {
			mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());

		}
	}

	public static void teleport(final double[] startPos, final BlockPos endPos) {
		double distx = startPos[0] - endPos.getX() + 0.5;
		double disty = startPos[1] - endPos.getY();
		double distz = startPos[2] - endPos.getZ() + 0.5;
		double dist = Math.sqrt(mc.thePlayer.getDistanceSq(endPos));
		double distanceEntreLesPackets = 5;
		double xtp, ytp, ztp = 0;

		if (dist > distanceEntreLesPackets) {
			double nbPackets = Math.round(dist / distanceEntreLesPackets + 0.49999999999) - 1;
			xtp = mc.thePlayer.posX;
			ytp = mc.thePlayer.posY;
			ztp = mc.thePlayer.posZ;
			double count = 0;
			for (int i = 1; i < nbPackets; i++) {
				double xdi = (endPos.getX() - mc.thePlayer.posX) / (nbPackets);
				xtp += xdi;

				double zdi = (endPos.getZ() - mc.thePlayer.posZ) / (nbPackets);
				ztp += zdi;

				double ydi = (endPos.getY() - mc.thePlayer.posY) / (nbPackets);
				ytp += ydi;
				count++;
				C03PacketPlayer.C04PacketPlayerPosition Packet = new C03PacketPlayer.C04PacketPlayerPosition(xtp, ytp, ztp, true);

				mc.thePlayer.sendQueue.addToSendQueue(Packet);
			}

			mc.thePlayer.setPosition(endPos.getX() + 0.5, endPos.getY(), endPos.getZ() + 0.5);
		} else {
			mc.thePlayer.setPosition(endPos.getX(), endPos.getY(), endPos.getZ());
		}
	}

	public static boolean isMoving() {
		if ((!mc.thePlayer.isCollidedHorizontally) && (!mc.thePlayer.isSneaking())) {
			return ((mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F));
		}
		return false;
	}

	public static boolean isMoving2() {
		return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
	}

	public static Entity raycast(Entity entiy) {
		EntityPlayerSP var2 = mc.thePlayer;
		Vec3 var9 = entiy.getPositionVector().add(new Vec3(0.0, entiy.getEyeHeight(), 0.0));
		Vec3 var7 = mc.thePlayer.getPositionVector().add(new Vec3(0.0, mc.thePlayer.getEyeHeight(), 0.0));
		Vec3 var10 = null;
		float var11 = 1.0f;
		AxisAlignedBB a2 = mc.thePlayer.getEntityBoundingBox().addCoord(var9.xCoord - var7.xCoord, var9.yCoord - var7.yCoord, var9.zCoord - var7.zCoord).expand(var11, var11, var11);
		List<Entity> var12 = PlayerUtil.mc.theWorld.getEntitiesWithinAABBExcludingEntity(var2, a2);
		double var13 = KillAura.range.getValue() + 0.5;
		Entity b2 = null;
		int var15 = 0;
		while (var15 < var12.size()) {
			Entity var16 = var12.get(var15);
			if (var16.canBeCollidedWith()) {
				double var20;
				float var17 = var16.getCollisionBorderSize();
				AxisAlignedBB var18 = var16.getEntityBoundingBox().expand(var17, var17, var17);
				MovingObjectPosition var19 = var18.calculateIntercept(var7, var9);
				if (var18.isVecInside(var7)) {
					if (0.0 < var13 || var13 == 0.0) {
						b2 = var16;
						var10 = var19 == null ? var7 : var19.hitVec;
						var13 = 0.0;
					}
				} else if (var19 != null && ((var20 = var7.distanceTo(var19.hitVec)) < var13 || var13 == 0.0)) {
					b2 = var16;
					var10 = var19.hitVec;
					var13 = var20;
				}
			}
			++var15;
		}
		return b2;
	}

	public static void setMotion(double speed) {
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
		if (forward == 0.0 && strafe == 0.0) {
			mc.thePlayer.motionX = 0.0;
			mc.thePlayer.motionZ = 0.0;
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += (float) (forward > 0.0 ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += (float) (forward > 0.0 ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}
			mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
			mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
		}
	}

	public static void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (forward == 0.0 && strafe == 0.0) {
			moveEvent.setZ(0.0);
			moveEvent.setX(0.0);
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += (float) (forward > 0.0 ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += (float) (forward > 0.0 ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}
			double cos = java.lang.Math.cos((double) java.lang.Math.toRadians((double) (yaw + 90.0f)));
			double sin = java.lang.Math.sin((double) java.lang.Math.toRadians((double) (yaw + 90.0f)));
			moveEvent.setX(forward * moveSpeed * cos + strafe * moveSpeed * sin);
			moveEvent.setZ(forward * moveSpeed * sin - strafe * moveSpeed * cos);
		}
	}

}
