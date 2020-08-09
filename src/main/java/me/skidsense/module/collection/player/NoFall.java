package me.skidsense.module.collection.player;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Event;
import me.skidsense.management.notifications.Notification;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class NoFall extends Mod {
    private float lastFall;

    public NoFall() {
        super("NoFall", new String[] { "Nofalldamage" }, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
            final float falldis = 2.65f + MoveUtil.getJumpEffect();
            if (mc.thePlayer.fallDistance - this.lastFall >= falldis && isBlockUnder()) {
                this.lastFall = mc.thePlayer.fallDistance;
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                Client.sendMessage("NoFall触发 TicksExisted: " + mc.thePlayer.ticksExisted);
            } else {
                if (mc.thePlayer.isCollidedVertically) {
                    this.lastFall = 0.0f;
                }
            }
    }

    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0D)
            return false;
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = mc.thePlayer.boundingBox.offset(0.0D, -off, 0.0D);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty())
                return true;
        }
        return false;
    }
}