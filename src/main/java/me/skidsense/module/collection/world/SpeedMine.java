package me.skidsense.module.collection.world;

import java.awt.*;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.client.*;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpeedMine extends Module
{
    private boolean block22 = false;
    private float block1 = 0.0f;
    public BlockPos blockPos;
    public EnumFacing face;
    
    public SpeedMine() {
        super("Speed Mine", new String[] { "speedmine", "fastbreak" }, ModuleType.World);
        this.setColor(new Color(223, 233, 233).getRGB());
    }
    
    @EventHandler
    private void onPacket(EventPacketSend event) {
    	if (event.packet instanceof C07PacketPlayerDigging && !Minecraft.getMinecraft().playerController.extendedReach() && Minecraft.getMinecraft().playerController != null) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)event.packet;
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.block22 = true;
                this.blockPos = c07PacketPlayerDigging.getPosition();
                this.face = c07PacketPlayerDigging.getFacing();
                this.block1 = 0.0f;
            } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.block22 = false;
                this.blockPos = null;
                this.face = null;
            }
        }
    }
    
    @EventHandler
    private void onUpdate(EventPreUpdate e) {
    	String copy = "skidded kody";
    	  if (Minecraft.getMinecraft().playerController.extendedReach()) {
              Minecraft.getMinecraft().playerController.blockHitDelay = 0;
          } else if (this.block22) {
              Block block = this.mc.theWorld.getBlockState(this.blockPos).getBlock();
              this.block1 += (float)((double)block.getPlayerRelativeBlockHardness(Minecraft.getMinecraft().thePlayer, this.mc.theWorld, this.blockPos) * 1.4);
              if (this.block1 >= 1.0f) {
                  this.mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                  Minecraft.getMinecraft().thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.face));
                  this.block1 = 0.0f;
                  this.block22 = false;
              }
          }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
