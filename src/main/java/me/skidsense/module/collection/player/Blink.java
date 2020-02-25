package me.skidsense.module.collection.player;

import com.mojang.authlib.GameProfile;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Blink
extends Module {
    private EntityOtherPlayerMP blinkEntity;
    private List<Packet> packetList = new ArrayList<Packet>();

    public Blink() {
        super("Blink", new String[]{"Blink"}, ModuleType.Player);
    }

    @Override
    public void onEnable() {
        this.setColor(new Color(200, 100, 200).getRGB());
        if (this.mc.thePlayer == null) {
            return;
        }
        this.blinkEntity = new EntityOtherPlayerMP(this.mc.theWorld, new GameProfile(new UUID(69L, 96L), "Blink"));
        this.blinkEntity.inventory = this.mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = this.mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), this.blinkEntity);
    }

    @EventHandler
    private void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.packetList.add(event.getPacket());
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        for (Packet packet : this.packetList) {
            this.mc.getNetHandler().addToSendQueue(packet);
        }
        this.packetList.clear();
        this.mc.theWorld.removeEntityFromWorld(this.blinkEntity.getEntityId());
    }
}

