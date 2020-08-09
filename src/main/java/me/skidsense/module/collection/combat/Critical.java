package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.module.collection.move.Speed;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

import java.awt.*;


public class Critical extends Mod {
    private static Mode <Enum> mode =new Mode<>("Mode","Mode",CritMode.values(),CritMode.Packet);
    private static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static Numbers<Double> ht = new Numbers<>("Hurttime", "Hurttime", 15.0, 0.0, 20.0, 1.0);
    private static TimerUtil timer = new TimerUtil();
    private EntityLivingBase lastTarget;



    public Critical() {
        super("Critical", new String[]{"criticals"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        //addValues(mode,delay,ht);
    }

    @Sub
    public void onAttack(EventAttack ent) {
        if (canCrit() && lastTarget != ent.targetEntity) {
            doCrit(ent.targetEntity);
            lastTarget = (EntityLivingBase) ent.targetEntity;
        }else if(canCrit() && ent.targetEntity.hurtResistantTime <= ht.getValue() && ent.targetEntity.hurtResistantTime>0){
            doCrit(ent.targetEntity);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
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
        switch(mode.getValue().toString()) {
            case "Packet": {
                int a1 = 1;
                while (a1 <= 4) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
                    ++a1;
                }
                final double[] array = {0.06260000000000000015515, 0};
                final int length = array.length;
                int v0 = 0;
                while (v0 < length) {
                    final double v2 = array[v0];
                    if (Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() && KillAura.target != null) {
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                                mc.thePlayer.posY + v2, mc.thePlayer.posZ,
                                KillAura.rotateNCP(KillAura.target)[0], KillAura.rotateNCP(KillAura.target)[1], false));
                    } else {
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + v2, mc.thePlayer.posZ, false));
                    }
                    ++v0;
                    Client.sendMessage("暴击。");
                }
                break;
            }
        }
                this.timer.reset();
        }

    enum CritMode {
        Packet,
    }
}

