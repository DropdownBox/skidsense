package me.skidsense.module.collection.player;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class NoFall
extends Module {
	private float fall;
    public static Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])fallmode.values(), (Enum)fallmode.Normal);
    public NoFall() {
        super("No Fall", new String[]{"Nofalldamage"}, ModuleType.Player);
        this.addValues(mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(mode.getValue());  
        if (mode.getValue() == fallmode.Normal) {
            if(Minecraft.getMinecraft().thePlayer.fallDistance > 3.0F) {
               if(!Minecraft.getMinecraft().thePlayer.isInWater()) {
                  if(!Minecraft.getMinecraft().thePlayer.isInLava()) {
      				if(isBlockUnder()) {
                  	  Minecraft.getMinecraft().thePlayer.fallDistance = 0.0F;
      					Notifications.getManager().post("NoFall触发");
                        e.setOnground(true);
      				}
                  }
               }
            }

         }
      }

    private boolean isBlockUnder() {
        int i2 = (int)(Minecraft.getMinecraft().thePlayer.posY - 1.0);
        while (i2 > 0) {
            double var10003 = i2;
            BlockPos pos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, var10003, Minecraft.getMinecraft().thePlayer.posZ);
            if (!(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return true;
            }
            --i2;
        }
        return false;
    }
    
    static enum fallmode {
        Normal;
    }        
}