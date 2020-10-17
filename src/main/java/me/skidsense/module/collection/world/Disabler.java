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
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Random;
import java.util.UUID;

import com.ibm.icu.text.UFormat;
import net.minecraft.network.play.server.S00PacketKeepAlive;

public class Disabler extends Mod {

    public TimerUtil timerUtil = new TimerUtil();
    public Disabler() {
        super("Disabler", new String[]{"noac"}, ModuleType.World);
    }
    private int delta;

    @Override
    public void onEnable(){
        super.onEnable();
        delta = 0;
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    @Sub
    private void onPacketSend(EventPacketSend ep) {
//        if (ep.getPacket() instanceof C0FPacketConfirmTransaction) {
//            C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction) ep.getPacket();
//            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, packetConfirmTransaction.getUid(), false));
//            ep.setCancelled(true);
//        }
//
//        if (ep.getPacket() instanceof C00PacketKeepAlive) {
//            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C00PacketKeepAlive(Integer.MIN_VALUE + (new Random()).nextInt(100)));
//            ep.setCancelled(true);
//        }
        if (ep.getPacket() instanceof C00PacketKeepAlive || ep.getPacket() instanceof C13PacketPlayerAbilities) {
            ep.setCancelled(true);
        }
        if (ep.getPacket() instanceof C0FPacketConfirmTransaction) {
            final C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction) ep.getPacket();
            if (c0FPacketConfirmTransaction.getUid() < 0 && c0FPacketConfirmTransaction.getWindowId() == 0) {
                ep.setCancelled(true);
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, (short) -(c0FPacketConfirmTransaction.getUid()), false));
            }
        }
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        ++delta;
        if(delta % 2 == 0 && (Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()) || Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()){
            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
            playerCapabilities.allowFlying = true;
            playerCapabilities.isFlying = true;
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
        }
    }

    @Sub
    private void onPacketReceive(EventPacketRecieve ep) {
        if (mc.theWorld != null) {
            if (ep.getPacket() instanceof S00PacketKeepAlive) {
                ep.setCancelled(true);
            }
        }
    }
}

