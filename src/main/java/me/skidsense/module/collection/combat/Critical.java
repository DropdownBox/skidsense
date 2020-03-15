package me.skidsense.module.collection.combat;

import java.awt.Color;
import java.util.Random;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventAttack;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.greenrobot.eventbus.Subscribe;

public class Critical extends Module {
    static Numbers<Double> delay = new Numbers<Double>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static TimerUtil timer =new TimerUtil();


    public Critical() {
        super("Critical", new String[]{"Critical"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        addValues(delay);
    }

    @EventHandler
    public void onAttack(EventAttack ea){
        if(canCrit())
            doEditCrit();
    }

    @Override
    public void onEnable(){
        setSuffix("Edit");
    }

    public static boolean canCrit() {
        return !mc.thePlayer.isOnLadder()
		        && !mc.thePlayer.isInWater()
		        && !mc.thePlayer.isPotionActive(Potion.blindness)
		        && mc.thePlayer.ridingEntity == null
		        && mc.thePlayer.onGround
		        && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()
                && Critical.timer.hasReached(delay.getValue());
    }
    
	public static void doEditCrit() {
        Random random = new Random();
        double[] offsets = new double[] { 0.041, 0.002 };
        for (int i = 0; i < offsets.length; ++i) {
            EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
            p.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(p.posX,
                    p.posY + offsets[i] + random.nextDouble() / 10000000, p.posZ, false));
        }
        timer.reset();
        Notifications.getManager().post( "Do Critical" + " " + offsets[0] + random.nextDouble() + " " + offsets[1] + random.nextDouble());
	}
}
