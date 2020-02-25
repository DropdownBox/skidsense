/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.module.collection.world;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate
extends Module {
    public NoRotate() {
        super("No Rotate", new String[]{"rotate"}, ModuleType.World);
        this.setColor(new Color(17, 250, 154).getRGB());
    }
    @EventHandler
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook look = (S08PacketPlayerPosLook)e.getPacket();
            look.yaw = this.mc.thePlayer.rotationYaw;
            look.pitch = this.mc.thePlayer.rotationPitch;
        }
    }
}

