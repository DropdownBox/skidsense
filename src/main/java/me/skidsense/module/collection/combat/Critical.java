package me.skidsense.module.collection.combat;

import java.awt.Color;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.util.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class Critical extends Module {
    public Mode<Enum> mode = new Mode<Enum>("Mode", "Mode", CritMode.values(), CritMode.Packet1);
    static Numbers<Double> delay = new Numbers<Double>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static TimerUtil timer = new TimerUtil();


    public Critical() {
        super("Critical", new String[]{"Critical"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        addValues(mode, delay);
    }

    @EventHandler
    public void onAttack(EventAttack e) {
        if (canCrit())
            doCrit();
    }

    @Override
    public void onEnable() {
        setSuffix(this.mode.getValue());
    }

    public static boolean canCrit() {
        return !mc.thePlayer.isOnLadder()
                && !mc.thePlayer.isInWater()
                && !mc.thePlayer.isPotionActive(Potion.blindness)
                && mc.thePlayer.ridingEntity == null
                && mc.thePlayer.onGround
                && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()
                && !Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()
                && Critical.timer.hasReached(delay.getValue());
    }

    public void doCrit() {
        double[] offsets = new double[] {0.0312622959183674, 0.0, 0.0312622959183674, 0.0};
        int l = offsets.length;
        for(int i = 0; i < l; ++i) {
            double offset = offsets[i];
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
        }
        Notifications.getManager().post("Do criticals.");
    }

    enum CritMode {
        Packet1,
        Packet2;
    }
}

