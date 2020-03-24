package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.awt.*;

public class NoFall extends Mod {
    private int state;


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
    private void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.fallDistance > 3.0f && this.isBlockUnder()) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
            state = 2;
        } else if (state == 2 && mc.thePlayer.fallDistance < 2) {
            mc.thePlayer.motionY = 0.001D;
            state = 3;
            return;
        }
        switch (state) {
            case 3:
                mc.thePlayer.motionY = 0.001D;
                state = 4;
                break;
            case 4:
                mc.thePlayer.motionY = 0.001D;
                state = 5;
                break;
            case 5:
                mc.thePlayer.motionY = 0.001D;
                state = 1;
                break;
        }
    }

    private boolean isBlockUnder() {
        for (int i = (int) mc.thePlayer.posY; i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, (double) i, mc.thePlayer.posZ);
            if (this.mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return true;
        }
        return false;
    }
}