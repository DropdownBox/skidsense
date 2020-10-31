package me.skidsense.module.collection.player;


import io.netty.util.internal.ThreadLocalRandom;
import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.util.MoveUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class NoFall extends Mod {
    float lastFall;
    int times;
    boolean showed;

    public NoFall() {
        super("No Fall", new String[]{"Nofalldamage", "NoFall"}, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Sub
    private void onEventPreUpdate(EventPreUpdate e) {
        float falldis = 0.825f + (float) MoveUtil.getJumpEffect();
        if (!Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()) {
            if (mc.thePlayer.fallDistance - this.lastFall >= falldis) {
                this.lastFall = mc.thePlayer.fallDistance;
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
            } else if (mc.thePlayer.isCollidedVertically) {
                this.lastFall = 0f;
                this.times = 0;
                this.showed = false;
            }
        }
    }
}