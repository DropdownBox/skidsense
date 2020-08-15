package me.skidsense.module.collection.player;

import java.util.concurrent.ThreadLocalRandom;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class NoFall extends Mod {
    private double actualFallDistance;

    public NoFall() {
        super("NoFall", new String[]{"Nofalldamage"}, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if (this.mc.thePlayer.fallDistance > 3.0f) {
            mc.thePlayer.onGround = false;
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
        }
    }
}