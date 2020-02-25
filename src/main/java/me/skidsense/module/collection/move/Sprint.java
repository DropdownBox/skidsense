package me.skidsense.module.collection.move;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.FoodStats;

public class Sprint
extends Module {
    private Option<Boolean> omni = new Option<Boolean>("Omni", "omni", true);
    private Option<Boolean> keepsprint = new Option<Boolean>("KeepSprint", "keepsprint", true);

    public Sprint() {
        super("Auto Sprint", new String[]{"run","sprint","autosprint"}, ModuleType.Move);
        this.setColor(new Color(158, 205, 125).getRGB());
        this.addValues(this.omni,keepsprint);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        if (this.mc.thePlayer.getFoodStats().getFoodLevel() > 6 && this.omni.getValue() != false ? this.mc.thePlayer.moving() : this.mc.thePlayer.moveForward > 0.0f) {
            this.mc.thePlayer.setSprinting(true);
        }
    }
    
    @EventHandler
    public void onEvent(EventPacketRecieve e) {
    	if(keepsprint.getValue()) {
            try {
                if (e.getPacket() instanceof C0BPacketEntityAction) {
                    C0BPacketEntityAction packet = (C0BPacketEntityAction) e.getPacket();
                    if (packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        e.setCancelled(true);
                    }
                }
            } catch (ClassCastException exception) {
            }
    	}

    }
}

