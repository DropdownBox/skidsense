/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.util;

import java.util.Random;

import me.skidsense.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtil {
    private static double height;
    private static int facing = MathUtil.getRandomInRange(2, 4);

    public static Minecraft mc = Minecraft.getMinecraft();
    
    public static float pitch() {
        return Client.mc.thePlayer.rotationPitch;
    }

    public static void pitch(float pitch) {
        Client.mc.thePlayer.rotationPitch = pitch;
    }

    public static float yaw() {
        return Client.mc.thePlayer.rotationYaw;
    }

    public static void yaw(float yaw) {
        Client.mc.thePlayer.rotationYaw = yaw;
    }
    public static float[] getRotationsBlockBetter(BlockPos block, EnumFacing face) {
        double x = (double)block.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }
    public static float[] faceTarget(Entity target, float p_706252, float p_706253, boolean miss) {
        double var6;
        double var4 = target.posX - Client.mc.thePlayer.posX;
        double var8 = target.posZ - Client.mc.thePlayer.posZ;
        if (target instanceof EntityLivingBase) {
            EntityLivingBase var10 = (EntityLivingBase)target;
            var6 = var10.posY + (double)var10.getEyeHeight() - (Client.mc.thePlayer.posY + (double)Client.mc.thePlayer.getEyeHeight());
        } else {
            var6 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - (Client.mc.thePlayer.posY + (double)Client.mc.thePlayer.getEyeHeight());
        }
        Random rnd = new Random();
        double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0 / 3.141592653589793) - 90.0f;
        float var13 = (float)(- Math.atan2(var6 - (target instanceof EntityPlayer ? 0.25 : 0.0), var14) * 180.0 / 3.141592653589793);
        float pitch = RotationUtil.changeRotation(Client.mc.thePlayer.rotationPitch, var13, p_706253);
        float yaw = RotationUtil.changeRotation(Client.mc.thePlayer.rotationYaw, var12, p_706252);
        return new float[]{yaw, pitch};
    }

    public static float changeRotation(float p_706631, float p_706632, float p_706633) {
        float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
        if (var4 > p_706633) {
            var4 = p_706633;
        }
        if (var4 < - p_706633) {
            var4 = - p_706633;
        }
        return p_706631 + var4;
    }
    
    public static float[] getBlockRotations(double xCoord, double yCoord, double zCoord, EnumFacing facing) {
        Entity temp = new EntitySnowball(mc.theWorld);
        temp.posX = (xCoord + 0.5);
        temp.posY = (yCoord + (height = 0.5));
        temp.posZ = (zCoord + 0.5);
        return mc.thePlayer.canEntityBeSeen(temp) ? getAngles(temp) : getRotationToBlock(new BlockPos(xCoord, yCoord, zCoord), facing);
    }

    private static float[] getAngles(Entity e) {
        return new float[]{getYawChangeToEntity(e) + mc.thePlayer.rotationYaw, getPitchChangeToEntity(e) + mc.thePlayer.rotationPitch};
    }

    private static float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity;
        final double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if ((deltaZ < 0) && (deltaX < 0)) {
            yawToEntity = 90 + v;
        } else {
            if ((deltaZ < 0) && (deltaX > 0.0D)) {
                yawToEntity = -90 + v;
            } else {
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
            }
        }
        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
    }

    private static float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = entity.posY - 1.6D + entity.getEyeHeight() - 0.4 - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) pitchToEntity);
    }

    public static float[] getRotationToBlock(BlockPos pos, EnumFacing face) {
        double random = MathUtil.getRandomInRange(.45, .55);
        int ranface = MathUtil.getRandomInRange(2, 4);
        double xDiff = pos.getX() + (height = random) - mc.thePlayer.posX + face.getDirectionVec().getX() / (facing = ranface);
        double zDiff = pos.getZ() + (height = random) - mc.thePlayer.posZ + face.getDirectionVec().getZ() / (facing = ranface);
        double yDiff = pos.getY() - mc.thePlayer.posY - 1;
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) -Math.toDegrees(Math.atan2(xDiff, zDiff));
        float pitch = (float) -Math.toDegrees(Math.atan(yDiff / distance));

        return new float[]{Math.abs(yaw - mc.thePlayer.rotationYaw) < .1 ? mc.thePlayer.rotationYaw : yaw, Math.abs(pitch - mc.thePlayer.rotationPitch) < .1 ? mc.thePlayer.rotationPitch : pitch};
    }
    
    public static double[] getRotationToEntity(Entity entity) {
        double pX = Client.mc.thePlayer.posX;
        double pY = Client.mc.thePlayer.posY + (double)Client.mc.thePlayer.getEyeHeight();
        double pZ = Client.mc.thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double)(entity.height / 2.0f);
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new double[]{yaw, 90.0 - pitch};
    }

    public static float[] getRotations1(Entity entity) {
        double diffY;
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - Client.mc.thePlayer.posX;
        double diffZ = entity.posZ - Client.mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase)entity;
            diffY = elb.posY + ((double)elb.getEyeHeight() - 0.4) - (Client.mc.thePlayer.posY + (double)Client.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (Client.mc.thePlayer.posY + (double)Client.mc.thePlayer.getEyeHeight());
        }
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(- Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = (double)block.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }

    public static float[] getVecRotation(Vec3 position) {
        return RotationUtil.getVecRotation(Client.mc.thePlayer.getPositionVector().addVector(0.0, Client.mc.thePlayer.getEyeHeight(), 0.0), position);
    }

    public static float[] getVecRotation(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float)(- Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int)((double)(yaw + (float)(360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }

    public static boolean canEntityBeSeen(Entity e) {
		Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

		AxisAlignedBB box = e.getEntityBoundingBox();
		Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
		double minx = e.posX - 0.25;
		double maxx = e.posX + 0.25;
		double miny = e.posY;
		double maxy = e.posY + Math.abs(e.posY - box.maxY);
		double minz = e.posZ - 0.25;
		double maxz = e.posZ + 0.25;
		boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(maxx, miny, minz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(minx, miny, minz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see)
			return true;
		vec2 = new Vec3(minx, miny, maxz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(maxx, miny, maxz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;

		vec2 = new Vec3(maxx, maxy, minz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;

		if (see)
			return true;
		vec2 = new Vec3(minx, maxy, minz);

		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(minx, maxy, maxz - 0.1);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;
		vec2 = new Vec3(maxx, maxy, maxz);
		see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null ? true : false;
		if (see)
			return true;

		return false;
	}

    public static float angleDifference(float a, float b) {
        float c = Math.abs(a % 360.0f - b % 360.0f);
        c = Math.min(c, 360.0f - c);
        return c;
    }
}

