package me.skidsense.module.collection.combat;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Event;
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
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

import java.awt.*;


public class Critical extends Mod {
    public Mode<modes> mode = new Mode<modes>("Mode", "Mode", modes.values(), modes.Packet);
    private static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static Numbers<Double> ht = new Numbers<>("HurtTime", "HurtTime", 15.0, 1.0, 20.0, 1.0);

    private static TimerUtil timer = new TimerUtil();
    private EntityLivingBase lastTarget;
    private float FallStack;



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

    private boolean isBlockUnder() {
        for (int i = (int) (mc.thePlayer.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }

    @Sub
    private void onUpdate(EventPreUpdate e){
        if (mc.thePlayer.motionY < 0 && isBlockUnder() && canCritWithoutTimer()) {
            e.setOnGround(false);
            if (FallStack >= 0 && FallStack < 0.1 && mc.thePlayer.ticksExisted % 2 == 0) {
                double value = 0.0624 + QuickMath.getRandomDoubleInRange(1E-8, 1E-7);
                FallStack += value;
                e.setY(mc.thePlayer.posY + value);
            } else {
                //event.setY(getMc().thePlayer.posY + MathUtils.getRandomInRange(1E-11, 1E-10));
                e.setY(mc.thePlayer.posY + 1E-8);
                if (FallStack < 0) {
                    FallStack = 0;
                    e.setOnGround(true);
                    e.setY(mc.thePlayer.posY);
                }
            }
        }
        else {
            FallStack = -1;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        FallStack = 0;
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    public static boolean canCritWithoutTimer() {
        return !mc.thePlayer.isOnLadder()
                && !mc.thePlayer.isInWater()
                && !mc.thePlayer.isPotionActive(Potion.blindness)
                && mc.thePlayer.ridingEntity == null
                && mc.thePlayer.onGround
                && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()
                && !Client.getModuleManager().getModuleByClass(Speed.class).isEnabled()
                && !Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled();
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
        switch (mode.getValue().toString()) {
            case "Edit": {
                double[] offsets = new double[]{0.0004999999595806003,
                        mc.thePlayer.ticksExisted % 60 == 0 ? 0.0000009999995958 : 0.0000008999995958, 0.0003999999595806003};
                for (int i = 0; i < offsets.length; ++i) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offsets[i], mc.thePlayer.posZ, false));
                }
                timer.reset();
            }
            break;
            case "Packet": {
                double packetoffsets[] = new double[]{0.0625, -0.001500000013038516, 0.0010999999940395355};
                for (double d : packetoffsets) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
                }
            }
            break;
        }
    }

    public enum modes{
        Edit,Packet,HvH
    }
}
