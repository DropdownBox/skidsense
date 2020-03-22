package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class NoFall extends Mod {
    double fall;


    public NoFall() {
        super("No Fall", new String[]{"Nofalldamage", "nofall"}, ModuleType.Player);
        setColor(new Color(242, 137, 73).getRGB());
    }

    @Override
    public void onEnable(){
        fall = 0;
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if (!MoveUtil.isOnGround(0.001)) {
            if (mc.thePlayer.motionY < -0.08)
                fall -= mc.thePlayer.motionY;
            if (fall > 2) {
                fall = 0;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                        mc.thePlayer.posY,mc.thePlayer.posZ,mc.thePlayer.rotationYaw,mc.thePlayer.rotationPitch,true));
            }
        } else
            fall = 0;
    }
}