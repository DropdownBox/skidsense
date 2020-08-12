/*
 * Exusiai Client
 * Copyright (c) 2020.
 */

package me.skidsense.module.collection.player;

import com.google.gson.internal.$Gson$Preconditions;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Event;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AntiDesync extends Mod {
    private int lastSlot = -1;

    public AntiDesync() {
        super("AntiDesync", new String[]{}, ModuleType.Player);
    }

    @Override
    public void onEnable(){
        super.onEnable();
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    @Sub
    public void onEvent(Event e) {
        if (e instanceof EventPreUpdate) {
            EventPreUpdate eu = (EventPreUpdate) e;
            if (this.lastSlot != -1 && this.lastSlot != mc.thePlayer.inventory.currentItem) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            } else if (e instanceof EventPacketSend) {
            EventPacketSend ep = (EventPacketSend) e;
            if (ep.getPacket() instanceof C09PacketHeldItemChange) {
                C09PacketHeldItemChange packet = (C09PacketHeldItemChange) ep.getPacket();
                this.lastSlot = packet.getSlotId();
            }
        }
    }
}
