/*
 * Exusiai Client
 * Copyright (c) 2020.
 */

package me.skidsense.module.collection.world;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.UUID;

import com.ibm.icu.text.UFormat;
import net.minecraft.network.play.server.S00PacketKeepAlive;

public class Disabler extends Mod {
    public TimerUtil timerUtil = new TimerUtil();
    private int delta;
    public Numbers<Double> ticks = new Numbers<Double>("Ticks", "Ticks", 4.0,2.0,16.0,2.0);
    public Disabler() {
        super("Disabler", new String[]{"noac"}, ModuleType.World);
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
    private void onPacketSend(EventPacketSend ep) {
        if (mc.theWorld != null) {
            if (ep.getPacket() instanceof C00PacketKeepAlive) {
                ep.setCancelled(true);
            }
            if (ep.getPacket() instanceof C0FPacketConfirmTransaction) {
                final C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction) ep.getPacket();
                if (c0FPacketConfirmTransaction.getUid() < 0 && c0FPacketConfirmTransaction.getWindowId() == 0) {
                    c0FPacketConfirmTransaction.setUid((short) (-c0FPacketConfirmTransaction.getUid()));
                }
            }
        }
    }

    @Sub
    private void onUpdate(EventPreUpdate e){
        ++delta;
        if(delta % 20 == 0){
            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
            playerCapabilities.allowFlying = true;
            playerCapabilities.isFlying = true;
            mc.thePlayer.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(playerCapabilities));
        }
    }

    @Sub
    private void onPacketReceive(EventPacketRecieve ep) {
        if (mc.theWorld != null) {
            if (ep.getPacket() instanceof S00PacketKeepAlive) {
                ep.setCancelled(true);
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C00PacketKeepAlive());
            }
        }
    }
}

