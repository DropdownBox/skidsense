package me.skidsense.module.collection.player;

import java.awt.Color;

import me.skidsense.Client;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.collection.move.Flight;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class AntiFall extends Module {

    private boolean saveMe;
    private TimerUtil timer = new TimerUtil();
    private Mode<Enum> mode = new Mode("Mode", "Mode", (Enum[]) AntiMode.values(), (Enum) AntiMode.Motion);
    private Option<Boolean> ov = new Option<Boolean>("OnlyVoid", "OnlyVoid", true);
    private static Numbers<Double> distance = new Numbers<Double>("Distance", "Distance", 5.0, 1.0, 10.0, 1.0);

    public AntiFall() {
        super("Anti Void", new String[] { "novoid", "antifall" }, ModuleType.Move);
        this.setColor(new Color(223, 233, 233).getRGB());
        this.addValues(this.ov, this.distance, this.mode);
    }

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0)
            return false;
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = mc.thePlayer.boundingBox.offset(0, -off, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    private void onMove(EventMove e) {
        if (mc.thePlayer.fallDistance > this.distance.getValue()
                && !Client.instance.getModuleManager().getModuleByClass(Flight.class).isEnabled()
                && Minecraft.getMinecraft().thePlayer.motionY < 0.0
                && !mc.thePlayer.onGround) {
            if (!(this.ov.getValue()) || !isBlockUnder()) {
                if (!saveMe) {
                    saveMe = true;
                    timer.reset();
                }
                if (this.mode.getValue() == AntiMode.Hypixel) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                            mc.thePlayer.posY + 12, mc.thePlayer.posZ, false));


                } else if (this.mode.getValue() == AntiMode.Motion) {
                    e.setY(mc.thePlayer.motionY = 0);
                }
            }
        }
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
        if ((saveMe && timer.delay(150F)) || mc.thePlayer.isCollidedVertically) {
            saveMe = false;
            timer.reset();
        }
    }

    @Override
    public void onEnable() {
    }

    static enum AntiMode {
        Motion, Hypixel;
    }
}
