/*
 * Exusiai Client
 * Copyright (c) 2020.
 */

package me.skidsense.module.collection.world;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.*;

import java.util.UUID;

import com.ibm.icu.text.UFormat;

public class Disabler extends Mod {
    public static Mode mode = new Mode("Mode", "Mode", (Enum[]) DisablerMode.values(), (Enum) DisablerMode.TimeOut);
    public TimerUtil timerUtil = new TimerUtil();
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
        if (mc.theWorld != null && mc.thePlayer != null && !Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled()) {
            switch (mode.getValue().toString()) {
                case "TimeOut":
                case "SpoofSpectator": {
                    if (ep.getPacket() instanceof C0FPacketConfirmTransaction || ep.getPacket() instanceof C00PacketKeepAlive || ep.getPacket() instanceof C13PacketPlayerAbilities) {
                        if(ep.getPacket() instanceof C0FPacketConfirmTransaction) {
                        	C0FPacketConfirmTransaction c0fPacketConfirmTransaction = (C0FPacketConfirmTransaction) ep.getPacket();
                            if (c0fPacketConfirmTransaction.getUid() < 0 && c0fPacketConfirmTransaction.getWindowId() == 0) {
                            	if(timerUtil.check(QuickMath.getRandom(500, 1000))) {
                                	Minecraft.getMinecraft().getNetHandler().sendpacketNoEvent(new C0FPacketConfirmTransaction(c0fPacketConfirmTransaction.getWindowId() , (short) -c0fPacketConfirmTransaction.getUid(),true));
                                	timerUtil.reset();
                            	}
                             }
                        }
                    	ep.setCancelled(true);
                    }
                    break;
                }
                case "NoPayload": {
                    if (ep.getPacket() instanceof C17PacketCustomPayload) {
                        ep.setCancelled(true);
                    }
                    break;
                }
            }
        }
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if (mc.theWorld != null && mc.thePlayer != null && !Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled()) {
            switch (mode.getValue().toString()) {
                case "SpamTransaction": {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(0, (short) QuickMath.getRandomInRange(-32767, 32767), false));
                    break;
                }
                case "SpoofSpectator": {
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C18PacketSpectate((UUID.randomUUID())));
                    break;
                }
            }
        }
    }

    enum DisablerMode {
        SpamTransaction,
        TimeOut,
        NoPayload,
        SpoofSpectator
    }
}

