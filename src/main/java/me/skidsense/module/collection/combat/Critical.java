package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

import java.awt.*;


public class Critical extends Mod {
    private static Mode <Enum> mode =new Mode<>("Mode","Mode",CritMode.values(),CritMode.Hypixel);
    private static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static Numbers<Double> ht = new Numbers<>("Hurttime", "Hurttime", 15.0, 0.0, 20.0, 1.0);
    private static Option<Boolean> nodeelay = new Option("Nodelay", "Nodelay", false);
    private static TimerUtil timer = new TimerUtil();


    public Critical() {
        super("Critical", new String[]{"criticals"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        //addValues(mode,delay,ht);
    }

    @Sub
    public void onAttack(EventAttack ent) {
        if (canCrit()) {
            doCrit(ent.targetEntity);
        }
    }

    @Sub
    public void others() {
        setSuffix(mode.getValue());
    }

    @Override
    public void onEnable() {
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
                case "Packet2":
                    final double[] arrayp = {0.06200999766588211, 0.0010100000072270632, 0.06200999766588211, 0.050999999046325684};
                    for (int length = arrayp.length, i = 0; i < length; ++i) {
                        if (Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && KillAura.target != null) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                    mc.thePlayer.posY + arrayp[i], mc.thePlayer.posZ, KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                        } else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + arrayp[i], mc.thePlayer.posZ, false));
                        }
                    }
                    break;
                case "Hypixel":
                    double[] hypixeloffsets = new double[]{0.03500000014901161, 0.0, 0.03359999880194664, 0.0};
                    int l = hypixeloffsets.length;
                    for (int i = 0; i < l; ++i) {
                        double offset = hypixeloffsets[i];
                        if (Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && KillAura.target != null) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                    mc.thePlayer.posY + offset, mc.thePlayer.posZ, KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                        } else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                        }
                    }
                    break;
                case "HVH":

                    double[] offsets = new double[]{0.41888898688697815, 0.33320000767707825, 0.00120000005699695};
                    int HVHl = offsets.length;
                    for (int i = 0; i < HVHl; ++i) {
                        double offset = offsets[i];
                        if (Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && KillAura.target != null) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                    mc.thePlayer.posY + offset, mc.thePlayer.posZ, KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                        } else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                        }
                    }
                    break;
                case "Packet":
                    int a1 = 1;
                    while (a1 <= 4) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                        ++a1;
                    }
                    final double[] array = { 0.03480000014901161, 0.0, 0.03329999880194664, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
                    final int length = array.length;
                    int v0 = 0;
                    while (v0 < length) {
                        final double v2 = array[v0];
                        if (Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && KillAura.target != null) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                    mc.thePlayer.posY + v2, mc.thePlayer.posZ,
                                    KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                        } else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + v2, mc.thePlayer.posZ, false));
                        }
                        ++v0;
                    }
                    break;
            }
            if (this.mode.getValue().equals(CritMode.Packet)) {
            } else if (!this.nodeelay.getValue()) {
                this.timer.reset();
            }

        }
    }

    enum CritMode {
        Packet,
        Hypixel,
        HVH,
        Packet2;
    }
}

