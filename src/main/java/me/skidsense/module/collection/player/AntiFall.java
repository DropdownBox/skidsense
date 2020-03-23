package me.skidsense.module.collection.player;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.Flight;
import me.skidsense.util.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class AntiFall extends Mod {
    public Mode<Enum> Mode = new Mode<Enum>("Mode", "Mode", CatchMode.values(), CatchMode.Motion);
    public Numbers<Double> Distance = new Numbers<Double>("Distance", "Distance", 6.0, 1.0, 20.0, 0.5);
    public Option<Boolean> Onlyvoid = new Option<Boolean>("OnlyVoid", "OnlyVoid", true);
    TimerUtil timer = new TimerUtil();
    private boolean saveMe;

    public AntiFall() {
        super("Anti Void", new String[]{"novoid", "antifall"}, ModuleType.World);
        setColor(new Color(223, 233, 233).getRGB());
    }


    @Sub
    private void onUpdate(EventMove em) {
        if ((saveMe && timer.delay(50)) || mc.thePlayer.isCollidedVertically) {
            saveMe = false;
            timer.reset();
        }
        int dist = Distance.getValue().intValue();
        if (mc.thePlayer.fallDistance > dist && !Client.getModuleManager().getModuleByClass(Flight.class).isEnabled()) {
            if (!Onlyvoid.getValue() || !isBlockUnder()) {
                if (!saveMe) {
                    saveMe = true;
                    timer.reset();
                }
                switch (Mode.getValue().toString()) {
                    case "Hypixel":
                        em.setY(em.getY() + dist);
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
                        break;
                    case "Motion":
                        em.setY(mc.thePlayer.motionY = 0);
                        break;
                }
            }
        }
    }

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0)
            return false;
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB offset = mc.thePlayer.boundingBox.offset(0, -off, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, offset).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    enum CatchMode {
        Motion, Hypixel
    }
}

