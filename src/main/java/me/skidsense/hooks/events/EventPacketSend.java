package me.skidsense.hooks.events;

import me.skidsense.hooks.value.Event;
import net.minecraft.network.Packet;

public class EventPacketSend
extends Event {
    public Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

