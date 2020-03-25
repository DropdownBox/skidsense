package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class NoFall extends Mod {
    public NoFall() {
        super("No Fall", new String[]{"Nofalldamage", "nofall"}, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Sub
    private void onUpdate(EventPacketSend e) {
        if (mc.thePlayer.fallDistance > 3.0f) {
            C03PacketPlayer Packet = (C03PacketPlayer) e.getPacket();
            Packet.onGround =(mc.thePlayer.fallDistance) % 3 == 0;
        }
    }
}