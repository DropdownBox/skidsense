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
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Disabler extends Mod {

    public TimerUtil timerUtil = new TimerUtil();

    public Disabler() {
        super("Disabler", new String[]{"noac"}, ModuleType.World);
    }

    private int delta;

    @Override
    public void onEnable() {
        super.onEnable();
        delta = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Sub
    private void onPacketReceive(EventPacketRecieve e) {
        if (e.getPacket() instanceof S00PacketKeepAlive) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof S32PacketConfirmTransaction) {
            e.setCancelled(true);
        }
    }

    @Sub
    private void onPacketSend(EventPacketSend e) {
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
        if (e.getPacket() instanceof C00PacketKeepAlive || e.getPacket() instanceof C13PacketPlayerAbilities) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
            final C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction) e.getPacket();
            c0FPacketConfirmTransaction.setUid((short) (-c0FPacketConfirmTransaction.getUid()));
        }

//    @Sub
//    private void onUpdate(EventPreUpdate e) {
//        ++delta;
//        if(delta % 50 == 0 && Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()){
//            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
//            playerCapabilities.allowFlying = true;
//            playerCapabilities.isFlying = true;
//            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
//        }
//    }
    }
}

