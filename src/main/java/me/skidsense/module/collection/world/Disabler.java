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
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Disabler extends Mod {

    public TimerUtil disableDelay = new TimerUtil();
    public TimerUtil updateDelay = new TimerUtil();

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
        if (e.getPacket() instanceof C00PacketKeepAlive) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
            final C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction) e.getPacket();
            if (c0FPacketConfirmTransaction.getUid() < 0 && c0FPacketConfirmTransaction.getWindowId() == 0) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(-Integer.MAX_VALUE, c0FPacketConfirmTransaction.getUid(), false));
                e.setCancelled(true);
            }
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

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if (Client.getModuleManager().getModuleByClass(Flight.class).isEnabled() || Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()) {
            if (disableDelay.hasReached(250)) {
                PlayerCapabilities playerCapabilities = new PlayerCapabilities();
                playerCapabilities.allowFlying = true;
                playerCapabilities.isFlying = true;
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
                System.out.println("a");
                disableDelay.reset();
            }
            if ((Client.getModuleManager().getModuleByClass(Flight.class).isEnabled() && Flight.hurtted) || Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()) {
                mc.getNetHandler().sendpacketNoEvent(new C0FPacketConfirmTransaction(0, (short) (-1), false));
                mc.getNetHandler().sendpacketNoEvent(new C0CPacketInput(Integer.MAX_VALUE, Integer.MAX_VALUE, true, true));
                mc.getNetHandler().sendpacketNoEvent(new C00PacketKeepAlive(0));
                System.out.println("b");
            }
        }
    }
}

