package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

import java.awt.*;
import java.util.Random;


public class Critical extends Mod {
    static Mode <Enum> mode =new Mode<>("Mode","Mode",CritMode.values(),CritMode.Hypixel);
    static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    static Numbers<Double> ht = new Numbers<>("Hurttime", "Hurttime", 15.0, 0.0, 20.0, 1.0);
    private static TimerUtil timer = new TimerUtil();



    public Critical() {
        super("Critical", new String[]{"criticals"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        //addValues(mode,delay,ht);
    }

    @Sub
    public void onAttack(EventAttack ent){
        if(canCrit())
            doCrit(ent.targetEntity);
    }

    @Override
    public void onEnable() {
        setSuffix(mode.getValue());
    }

    public static boolean canCrit() {
            return !mc.thePlayer.isOnLadder()
                    && !mc.thePlayer.isInWater()
                    && !mc.thePlayer.isPotionActive(Potion.blindness)
                    && mc.thePlayer.ridingEntity == null
                    && MoveUtil.isOnGround(0.001)
                    && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()
                    && !Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()
                    && !Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()
                    && Critical.timer.hasReached(delay.getValue());
    }

    public void doCrit(Entity e) {
        if(e.hurtResistantTime<=ht.getValue()&&e.hurtResistantTime>0) {
            switch (this.mode.getValue().toString()) {
                case "Old":
                    Random randomValue = new Random(System.currentTimeMillis() + System.nanoTime());
                    double[] oldoffsets = new double[]{0.041, 0.002};
                    for (int i = 0; i < oldoffsets.length; ++i) {
                        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                mc.thePlayer.posY + oldoffsets[i] + randomValue.nextDouble(), mc.thePlayer.posZ,
                                KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                    }
                    break;
                case "Hypixel":
                    double[] hypixeloffsets = new double[]{0.033600000987064504, 0.000650000001769514, 0.032300000774313276, 0.000650000001769514};
                    int l = hypixeloffsets.length;
                    for (int i = 0; i < l; ++i) {
                        double offset = hypixeloffsets[i];
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                mc.thePlayer.posY + offset, mc.thePlayer.posZ,
                                KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                    }
                    break;
                case "HVH":
                    double[] offsets = new double[]{0.41888898688697815, 0.33320000767707825, 0.00120000005699695};
                    int HVHl = offsets.length;
                    for (int i = 0; i < HVHl; ++i) {
                        double offset = offsets[i];
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                mc.thePlayer.posY + offset, mc.thePlayer.posZ, KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                    }
                    break;
                case "Experimental":
                    int a1 = 1;
                    while (a1 <= 4) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                        ++a1;
                    }
                    final double[] array = {0.0312622959183674, 0.0, 0.0312622959183674, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                    final int length = array.length;
                    int v0 = 0;
                    while (v0 < length) {
                        final double v2 = array[v0];
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                mc.thePlayer.posY + v2, mc.thePlayer.posZ, KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                        ++v0;
                    }
                    break;
            }
            timer.reset();
            Notifications.getManager().post("Do criticals. HurtTime:" + e.hurtResistantTime);
        }
    }

    enum CritMode {
        Hypixel,
        HVH,
        Experimental,
        Old;
    }
}

