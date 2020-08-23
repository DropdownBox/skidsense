package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class AntiDesync extends Mod {
    private int lastSlot = -1;
    private TimerUtil senddelay = new TimerUtil();

    public AntiDesync() {
        super("Anti Desync", new String[]{"AntiDesync"}, ModuleType.Player);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        lastSlot = -1;
        super.onDisable();
    }

    @Sub
    public void onPreUpdate(EventPreUpdate e) {
        if (this.lastSlot != -1 && this.lastSlot != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        if(senddelay.delay(500)) {
            Minecraft.getMinecraft().getNetHandler().sendpacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));	
        }
    }

    @Sub
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange packet = (C09PacketHeldItemChange) e.getPacket();
            this.lastSlot = packet.getSlotId();
        }
    }
}
