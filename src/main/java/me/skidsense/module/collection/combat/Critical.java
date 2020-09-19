package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.notifications.Notification;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;

import java.awt.*;


public class Critical extends Mod {
    private static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static Numbers<Double> ht = new Numbers<>("HurtTime", "HurtTime", 15.0, 1.0, 20.0, 1.0);

    private static TimerUtil timer = new TimerUtil();
    private EntityLivingBase lastTarget;


    public Critical() {
        super("Critical", new String[]{"criticals"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
    }

    @Sub
    public void onAttack(EventAttack ent) {
        if (canCrit() && ent.targetEntity.hurtResistantTime <= ht.getValue() && ent.targetEntity.hurtResistantTime > 0) {
            doCrit();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    public static boolean canCrit() {
        return !mc.thePlayer.isOnLadder()
                && !mc.thePlayer.isInWater()
                && !mc.thePlayer.isPotionActive(Potion.blindness)
                && mc.thePlayer.ridingEntity == null
                && mc.thePlayer.onGround
                && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()
                && !Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()
                && !Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()
                && Critical.timer.hasReached(delay.getValue());
    }

    public void doCrit() {
        double[] offset = {0.0624, -0.001500000013038516, 0.0010999999940395355};
        for (double d : offset) {
            mc.getNetHandler().sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
        }
        timer.reset();
    }
}
