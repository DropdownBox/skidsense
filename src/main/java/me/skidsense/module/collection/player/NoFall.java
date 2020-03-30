package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class NoFall extends Mod {
    public NoFall() {
        super("NoFall", new String[] { "Nofalldamage" }, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.fallDistance > 3.0f && isBlockUnder()) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch, true));
        }
    }

    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0D)
            return false;
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = mc.thePlayer.boundingBox.offset(0.0D, -off, 0.0D);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty())
                return true;
        }
        return false;
    }
}