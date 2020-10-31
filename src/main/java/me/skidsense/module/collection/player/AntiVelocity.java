
package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import java.awt.Color;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class AntiVelocity
extends Mod {

    public Numbers<Double> vertical = new Numbers<Double>("Vertical", "Vertical",0.0, 0.0, 1.0, 0.01);
    public Numbers<Double> horizontal = new Numbers<Double>("Horizontal", "Horizontal",0.0, 0.0, 1.0, 0.01);
    
    public AntiVelocity() {
        super("Anti KB", new String[]{"antivelocity", "antiknockback", "antikb"}, ModuleType.Player);
        //this.addValues();
        this.setColor(new Color(191, 191, 191).getRGB());
    }

    @Sub
    public void onUpdate(EventPreUpdate eventPreUpdate) {
    	this.setSuffix((vertical.getValue() * 100) + "% " + (horizontal.getValue() * 100) + "%");
	}
    @Sub
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if (packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                if (((Double) this.vertical.getValue()).doubleValue() == 0.0D && (Double) this.horizontal.getValue() == 0.0D) {
                    e.setCancelled(true);
                } else {
                    packet.motionX = (int) ((double) packet.motionX * (Double) this.horizontal.getValue());
                    packet.motionZ = (int) ((double) packet.motionZ * (Double) this.horizontal.getValue());
                    packet.motionY = (int) ((double) packet.motionY * (Double) this.vertical.getValue());
                }
            }
        }


        if (e.getPacket() instanceof S27PacketExplosion) {
            S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
            if ((Double) this.vertical.getValue() == 0.0D && (Double) this.horizontal.getValue() == 0.0D) {
                e.setCancelled(true);
            } else {
                packet.field_149152_f = (float) ((double) packet.field_149152_f * (Double) this.horizontal.getValue());
                packet.field_149153_g = (float) ((double) packet.field_149153_g * (Double) this.horizontal.getValue());
                packet.field_149159_h = (float) ((double) packet.field_149159_h * (Double) this.vertical.getValue());
            }
        }
    }
}

