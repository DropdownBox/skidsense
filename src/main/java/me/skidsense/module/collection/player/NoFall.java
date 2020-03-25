package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

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
    private void onUpdate(EventPacketRecieve e) {
            if (mc.thePlayer.fallDistance > 3.0 && e.getPacket() instanceof C03PacketPlayer && isBlockUnder()) {
                C03PacketPlayer Packet = (C03PacketPlayer) e.getPacket();
                Packet.onGround = (mc.thePlayer.fallDistance) % 3 == 0;
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