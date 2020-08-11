package me.skidsense.module.collection.move;

import com.sun.xml.internal.bind.v2.model.annotation.Quick;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import org.lwjgl.Sys;

import java.awt.*;
import java.util.TimerTask;

public class Flight extends Mod {
    public Mode mode = new Mode("Mode", "Mode", (Enum[]) FlightMode.values(), (Enum) FlightMode.Vanilla);
    private Option<Boolean> UHC = new Option("UHC", "UHC", Boolean.valueOf(false));
    private int counter;
    private boolean allowed;
    public boolean reset;
    public float timerSpeed;

    private double x, y, z, mineplexSpeed, lastDist, speed,randomValue;

    TimerUtil timer = new TimerUtil();


    public Flight() {
        super("Flight", new String[]{"Fly"}, ModuleType.Move);
    }

    @Override
    public void onEnable(){
        super.onEnable();
        speed = 0;
        if (MoveUtil.isMoving() && !mc.gameSettings.keyBindSprint.isKeyDown()) {
            allowed = !allowed;
        }
    }

    public static void damageHypixel() {
        if (mc.thePlayer.onGround) {
            final double offset = 0.4122222218322211111111F;
            final NetHandlerPlayClient netHandler = mc.getNetHandler();
            final EntityPlayerSP player = mc.thePlayer;
            final double x = player.posX;
            final double y = player.posY;
            final double z = player.posZ;
            for (int i = 0; i < 9; i++) {
                netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, false));
                netHandler.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.000002737272, z, false));
                netHandler.addToSendQueue(new C03PacketPlayer(false));
            }
            netHandler.addToSendQueue(new C03PacketPlayer(true));
        }
    }

    @Override
    public void onDisable(){
        mc.timer.timerSpeed = 1f;
        super.onDisable();
    }

    @Sub
    public void onUpdate(EventPreUpdate event){
        if (mode.getValue().equals(mode.getValue() == FlightMode.Damage)) {
            mc.thePlayer.onGround = true;
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt((xDist * xDist) + (zDist * zDist));
            if (counter > 1) {
                mc.thePlayer.motionY = 0;
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    reset = true;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + randomValue, mc.thePlayer.posZ);
                } else {
                    reset = false;
                }
                if (!MoveUtil.isMoving()) {
                    double speed = 0.1;

                    mc.thePlayer.motionX = (-Math.sin(MoveUtil.getDirection())) * speed;
                    mc.thePlayer.motionZ = Math.cos(MoveUtil.getDirection()) * speed;

                }


                if (mc.thePlayer.ticksExisted % 5 == 0) {
                    randomValue += QuickMath.getRandomInRange(-0.000009D, 0.000009D);
                }
            }
        }
    }

    @Sub
    public void onMove(EventMove e) {
        if (mode.getValue() == FlightMode.Damage) {
            mc.thePlayer.onGround = true;
            if (mc.thePlayer.ticksExisted % 10 == 0 && MoveUtil.isMoving()) {
                mc.thePlayer.cameraYaw = 0.16f;
            }
                switch (counter) {
                    case 0:
                        if (timer.delay(allowed ? 250 : 150)) {
                            damageHypixel();
                            speed = MoveUtil.getBaseMoveSpeed() * (allowed ? 1.25 : 1.25);
                            timer.reset();


                            counter = 1;
                        } else {
                            speed = 0;
                            e.setX(mc.thePlayer.motionX = 0);
                            e.setY(mc.thePlayer.motionY = 0);
                            e.setZ(mc.thePlayer.motionZ = 0);
                        }
                        break;
                    case 1:
                        speed *= 2.14999;
                        e.setY(mc.thePlayer.motionY = 0.41999998688697815D);
                        counter = 2;
                        break;
                    case 2:
                        speed = (allowed ? 1.37 : 1.42);
                        counter = 3;
                        break;
                    default:
                        if (counter > 10) {
                            if (timerSpeed > 1.0) {
                                mc.timer.timerSpeed = timerSpeed -= 0.055;
                            } else {
                                mc.timer.timerSpeed = 1.0f;
                            }
                        } else if (counter == 9) {
                            // timerSpeed = 2.6f;
                            timerSpeed = 1.4f;
                        }

                        if (mc.thePlayer.isCollidedHorizontally) {
                            mc.timer.timerSpeed = 1.0f;
                            speed *= .5;
                        }
                        speed -= speed / 159;
                        counter++;
                        break;
                }
                MoveUtil.setSpeed(speed == 0 ? 0 : Math.max(speed, MoveUtil.getBaseMoveSpeed()));
        }
    }

    enum FlightMode{
        Vanilla,Damage
    }
}