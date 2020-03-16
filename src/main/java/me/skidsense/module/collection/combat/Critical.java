package me.skidsense.module.collection.combat;

import java.awt.Color;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import java.util.Random;


public class Critical extends Module {
    static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static TimerUtil timer = new TimerUtil();


    public Critical() {
        super("Critical", new String[]{"Critical"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        addValues(delay);
    }

    @EventHandler
    public void onAttack(EventAttack e) {
        if (canCrit())
            doCrit();
    }

    @Override
    public void onEnable() {
        setSuffix("Packet");
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
        Random randomValue = new Random(System.currentTimeMillis()+System.nanoTime());
        double[] offsets = new double[] { 0.041, 0.002 };
        for (int i = 0; i < offsets.length; ++i) {
            EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
            p.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(p.posX,
                    p.posY + offsets[i] + randomValue.nextDouble() / 10000000, p.posZ, false));
        }
            this.timer.reset();
        Notifications.getManager().post("Do criticals.");
    }
}

