
package me.skidsense.module.collection.player;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import java.awt.Color;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class AntiVelocity
extends Module {

    public AntiVelocity() {
        super("Anti KB", new String[]{"antivelocity", "antiknockback", "antikb"}, ModuleType.Player);
        this.addValues();
        this.setColor(new Color(191, 191, 191).getRGB());
    }

    @EventHandler
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
        	e.setCancelled(true);
        }
    }
}

