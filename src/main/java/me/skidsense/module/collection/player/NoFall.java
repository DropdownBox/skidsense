package me.skidsense.module.collection.player;


import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
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
    private void onPacketSend(EventPacketSend e){
        if(e.getPacket() instanceof C03PacketPlayer && mc.thePlayer.fallDistance > 3.0F && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()){
            C03PacketPlayer packetPlayer = (C03PacketPlayer)e.getPacket();
            if (!packetPlayer.isMoving()) {
                packetPlayer.onGround = true;
            } else {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
            }
        }
    }
}