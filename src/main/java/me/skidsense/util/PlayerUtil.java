package me.skidsense.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil {

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
}
