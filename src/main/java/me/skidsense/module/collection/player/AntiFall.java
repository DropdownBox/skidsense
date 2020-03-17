package me.skidsense.module.collection.player;

import java.awt.Color;
import java.lang.reflect.Field;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class AntiFall extends Module {

    public AntiFall() {
        super("Anti Void", new String[] { "novoid", "antifall" }, ModuleType.World);
        setColor(new Color(223,233,233).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        //variable to hold if a block is underneath us
        boolean blockUnderneath = false;
        //for the players posy
        for (int i = 0; i < mc.thePlayer.posY + 2; i++) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            //if block underneath is air stop the code
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            //else set the boolean to true
            blockUnderneath = true;
        }
        //if blockunderneath return
        if (blockUnderneath)
            return;
        //if the fall distance is over 2
        if (mc.thePlayer.fallDistance < 2)
            return;
        //and if the player isnt onground or vertically colided put the player up
        if (!mc.thePlayer.onGround && !mc.thePlayer.isCollidedVertically) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY + 12, mc.thePlayer.posZ, false));
        }
    }
}
