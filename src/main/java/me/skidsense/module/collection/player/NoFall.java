package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.awt.*;

public class NoFall
extends Mod {
	private float fall;
    //public static Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])fallmode.values(), (Enum)fallmode.Normal);

    public NoFall() {
        super("No Fall", new String[] { "Nofalldamage","nofall" }, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.fallDistance > 3.0f) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                    mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
            mc.thePlayer.fallDistance = 0.0f;
            Notifications.getManager().post("触发NoFall");
        }
    }
}