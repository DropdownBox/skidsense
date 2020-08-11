/*
 * Exusiai Client
 * Copyright (c) 2020.
 */

package me.skidsense.module.collection.player;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.value.Mode;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;

public class Disabler extends Mod {
    private Mode<Enum> mode = new Mode("Mode", "mode", DisablerMode.values(), DisablerMode.Hypixel);
    public Disabler() {
        super("Disabler", new String[]{"disabler"}, ModuleType.Player);
    }

    @Override
    public void onEnable(){
        Notifications.getManager().post("重新加入服务器使Disabler起作用.");
        super.onEnable();
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    @Sub
    public void onPacketSend(EventPacketSend ep){
        if(mode.getValue() == DisablerMode.Hypixel){
            if (mc.thePlayer.ticksExisted % 30 == 0) {
                PlayerCapabilities pc = new PlayerCapabilities();
                pc.isFlying = true;
                pc.setFlySpeed(Float.NaN);
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(pc));
            }
            if (ep.getPacket() instanceof C0FPacketConfirmTransaction) {
                ep.setPacket(new C0FPacketConfirmTransaction(Integer.MIN_VALUE, Short.MAX_VALUE, true));
            }
        }
    }

    enum DisablerMode{
        Hypixel,
    }
}
