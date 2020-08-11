package me.skidsense.module.collection.player;

import java.util.concurrent.ThreadLocalRandom;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class NoFall extends Mod {
    private float lastFall;

    public NoFall() {
        super("NoFall", new String[]{"Nofalldamage"}, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if(mc.thePlayer.fallDistance > 3.0F){
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.ticksExisted % ThreadLocalRandom.current().nextInt(45, 75) != 0));
        }
    }
}