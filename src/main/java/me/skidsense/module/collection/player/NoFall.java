package me.skidsense.module.collection.player;


import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.util.MoveUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

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
        if (!Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()) {
            if(mc.thePlayer.fallDistance > 3.0f)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.ticksExisted % ThreadLocalRandom.current().nextInt(45, 75) != 0));
        }
    }
}