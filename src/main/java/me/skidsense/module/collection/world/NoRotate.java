/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.module.collection.world;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import java.awt.Color;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate
extends Mod {
    public NoRotate() {
        super("No Rotate", new String[]{"rotate"}, ModuleType.World);
        this.setColor(new Color(17, 250, 154).getRGB());
    }
    @Sub
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook look = (S08PacketPlayerPosLook)e.getPacket();
            look.yaw = this.mc.thePlayer.rotationYaw;
            look.pitch = this.mc.thePlayer.rotationPitch;
        }
    }
}

