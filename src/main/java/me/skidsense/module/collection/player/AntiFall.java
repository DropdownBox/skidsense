package me.skidsense.module.collection.player;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class AntiFall
extends Module {
    private Numbers<Double> Distance = new Numbers<Double>("Distance", "Distance", 1.0, 1.0, 100.0, 1.0);
    private TimerUtil timer = new TimerUtil();

    public AntiFall() {
        super("Anti Void", new String[]{"novoid", "antifall"}, ModuleType.Move);
        this.setColor(new Color(223, 233, 233).getRGB());
        this.addValues(this.Distance);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e2) {
        if (!this.isBlockUnder()) {
            if (!Minecraft.getMinecraft().thePlayer.onGround) {
                if (Minecraft.getMinecraft().thePlayer.motionY < 0.0) {
                    if (Minecraft.getMinecraft().thePlayer.fallDistance >= Distance.getValue()) {
                        if (!Minecraft.getMinecraft().thePlayer.onGround && timer.hasReached(100)) {
                            //mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 12, mc.thePlayer.posZ, false));
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Distance.getValue() + 1, mc.thePlayer.posZ);
                            mc.thePlayer.fallDistance = 0;
                			//mc.thePlayer.motionY = 2.0;
                            //Minecraft.thePlayer.moveEntity(0.0,Distance.getValue()+0.01212121, 0.0);
                            //Minecraft.thePlayer.fallDistance = 0.0f;
                        }
                    }
                    return;
                }
            }
        }
    }
    
    public double getDistanceToFall() {
        double distance = 0.0;
        double distancetofall = Minecraft.getMinecraft().thePlayer.posY;
        while (distancetofall > 0.0) {
            Block block = this.getBlockWithBlockPos(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, distancetofall, Minecraft.getMinecraft().thePlayer.posZ));
            if (block.getMaterial() != Material.air && block.isFullCube() && block.isCollidable()) {
                distance = distancetofall;
                break;
            }
            distancetofall -= 1.0;
        }
        distancetofall = Minecraft.getMinecraft().thePlayer.posY - distance - 1.0;
        return distancetofall;
    }

    public Block getBlockWithBlockPos(BlockPos blockPos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
    }

    private boolean isBlockUnder() {
        int i2 = (int)(Minecraft.getMinecraft().thePlayer.posY - 1.0);
        while (i2 > 0) {
            double var10003 = i2;
            BlockPos pos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, var10003, Minecraft.getMinecraft().thePlayer.posZ);
            if (!(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return true;
            }
            --i2;
        }
        return false;
    }
}

