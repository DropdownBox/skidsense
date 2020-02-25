package me.skidsense.module.collection.move;

import java.awt.Color;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow
extends Module {
    public NoSlow() {
        super("No Slow", new String[]{"noslow","noslowdown"}, ModuleType.Move);
        this.setColor(new Color(216, 253, 100).getRGB());
    }

    @EventHandler
    private void onPreUpdate(EventPreUpdate e) {
        if (mc.thePlayer.isUsingItem() && this.mc.thePlayer.isBlocking() && KillAura.target == null) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
    @EventHandler
    private void onPostUpdate(EventPreUpdate e) {
        if (mc.thePlayer.isUsingItem() && this.mc.thePlayer.isBlocking() && KillAura.target == null) {
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }
}

