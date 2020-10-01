package me.skidsense.module.collection.player;


import io.netty.util.internal.ThreadLocalRandom;
import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class NoFall extends Mod {
    double fall;

    public NoFall() {
        super("No Fall", new String[]{"Nofalldamage", "NoFall"}, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }
    @Sub
    private void onEventPreUpdate(EventPreUpdate e){
        if (mc.thePlayer.fallDistance > 2.0f) {
            mc.thePlayer.onGround = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            mc.thePlayer.fall(1,0);
        }else if (mc.thePlayer.isCollidedVertically) {
            mc.thePlayer.fallDistance = 0.0f;
        }
    }
}