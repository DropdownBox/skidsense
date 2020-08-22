package me.skidsense.module.collection.move;

import java.awt.Color;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.MoveUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow
extends Mod {
    public NoSlow() {
        super("No Slow", new String[]{"noslow","noslowdown"}, ModuleType.Move);
        this.setColor(new Color(216, 253, 100).getRGB());
    }

    @Sub
    private void onPreUpdate(EventPreUpdate e) {
        if (mc.thePlayer.isUsingItem() && mc.thePlayer.isBlocking() && KillAura.target == null && MoveUtil.isMoving()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
    @Sub
    private void onPostUpdate(EventPreUpdate e) {
        if (mc.thePlayer.isUsingItem() && mc.thePlayer.isBlocking() && KillAura.target == null && MoveUtil.isMoving()) {
            mc.thePlayer.sendQueue.addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }
}

