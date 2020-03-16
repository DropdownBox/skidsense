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
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.module.collection.visual.clickgui.LAC.ClickUI;
import me.skidsense.module.collection.visual.clickgui.Skidsense.ClickGUI;
import me.skidsense.util.TimerUtil;
import me.theresa.music.ui.MusicWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import java.util.Random;


public class Critical extends Module {
    static Mode <Enum> mode =new Mode<>("Mode","Mode",CritMode.values(),CritMode.Hypixel);
    static Numbers<Double> delay = new Numbers<>("Delay", "Delay", 500.0, 0.0, 1000.0, 50.0);
    private static TimerUtil timer = new TimerUtil();


    public Critical() {
        super("Critical", new String[]{"Critical"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        addValues(mode,delay);
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
        switch (this.mode.getValue().toString()) {
            case "Old":
                Random randomValue = new Random(System.currentTimeMillis() + System.nanoTime());
                double[] oldoffsets = new double[]{0.041, 0.002};
                for (int i = 0; i < oldoffsets.length; ++i) {
                    EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                    p.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(p.posX,
                            p.posY + oldoffsets[i] + randomValue.nextDouble() / 10000000, p.posZ, false));
                }
                break;
            case "Hypixel":
                double[] hypixeloffsets = new double[]{0.033600000987064504, 0.000650000001769514, 0.032300000774313276, 0.000650000001769514};
                int l = hypixeloffsets.length;
                for (int i = 0; i < l; ++i) {
                    double offset = hypixeloffsets[i];
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                }
                break;
            case "HVH":
                double[] offsets = new double[]{0.41888898688697815,0.33320000767707825,0.00120000005699695};
                int HVHl = offsets.length;
                for (int i = 0; i < HVHl; ++i) {
                    double offset = offsets[i];
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                }
                break;
        }
        Notifications.getManager().post("Do criticals.");
        this.timer.reset();
    }
    enum CritMode{
        Hypixel,
        HVH,
        Old;
    }
}

