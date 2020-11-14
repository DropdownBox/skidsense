package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.*;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.QuickMath;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

import java.util.*;


public class Flight extends Mod {
    public Mode<FlightMode> mode = new Mode<>("Mode", "Mode", FlightMode.values(), FlightMode.Motion);
    public Numbers<Double> MotionSpeed = new Numbers<Double>("MotionSpeed", "MotionSpeed", 1.0, 1.0, 10.0, 0.25);
    public Numbers<Double> WattingTime = new Numbers<Double>("WattingTime", "WattingTime", 2.1, 1.0, 5.0, 0.1);
    public Numbers<Double> zoomboost = new Numbers<Double>("Zoomboost", "Zoomboost", 1.0, 0.0, 10.0, 1.0);
    public Numbers<Double> timerboost = new Numbers<Double>("Timerboost", "Timerboost", 0.0, 0.0, 5.0, 0.1);
    public Numbers<Double> groundboost = new Numbers<Double>("Groundboost", "Groundboost", 2.1, 1.0, 5.0, 0.1);
    public Option<Boolean> damage = new Option<Boolean>("Damage", "Damage", true);
    public Option<Boolean> uhc = new Option<Boolean>("UHC", "UHC", false);
    public Option<Boolean> lagback = new Option<Boolean>("Lagback", "Lagback", true);
    public Option<Boolean> Watting = new Option<Boolean>("Watting", "Watting", false);

    double beforeFlyY;
    double TimerSpeed;
    double currentSpeed;
    double moveSpeed;
    float y;
    int posYStage;
    private boolean failedStart;
    private double lastDistance;
    private int boostHypixelState;
    double thisY;
    public static boolean hurtted = false;

    public Flight() {
        super("Flight", new String[] { "Fly" }, ModuleType.Move);
    }

    enum FlightMode{
        Hypixel,
        HypixelZoom,
        NewMotion,
        Motion
    }

    @Sub
    public void onMove(EventMove event) {
        if (mode.getValue() == FlightMode.Hypixel) {
            MoveUtil.setMotion(event, getBaseMoveSpeed());
        }

        if (mode.getValue() == FlightMode.HypixelZoom) {
            if (!hurtted)
                return;

            if (failedStart)
                return;

            if (!MoveUtil.isMoving()) {
                event.setX(0D);
                event.setZ(0D);
                return;
            }
            final double amplifier = 1 + (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    ? 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1)
                    : 0);
            final double baseSpeed = 0.29D * amplifier;

            switch (boostHypixelState) {
                case 1:
                    moveSpeed = (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.56 : 2.034) * baseSpeed;
                    boostHypixelState = 2;
                    break;
                case 2:
                    moveSpeed *= groundboost.getValue();
                    boostHypixelState = 3;
                    break;
                case 3:
                    moveSpeed = lastDistance
                            - (mc.thePlayer.ticksExisted % 2 == 0 ? 0.0103D : 0.0123D) * (lastDistance - baseSpeed);

                    boostHypixelState = 4;
                    break;
                default:
                    moveSpeed = lastDistance - lastDistance / 159.8D;
                    break;
            }
            moveSpeed = Math.max(moveSpeed, 0.3D);

            final double yaw = MoveUtil.getDirection();
            event.setX(-Math.sin(yaw) * moveSpeed);
            event.setZ(Math.cos(yaw) * moveSpeed);
            mc.thePlayer.motionX = event.getX();
            mc.thePlayer.motionZ = event.getZ();
        }

    }

    @Sub
    public void onPreUpdate(EventPreUpdate event) {
        if (mode.getValue() == FlightMode.Motion || mode.getValue() == FlightMode.NewMotion) {
            mc.thePlayer.motionY = 0;
            if (mc.thePlayer.movementInput.jump) {
                mc.thePlayer.motionY = 2.0;
            } else if (mc.thePlayer.movementInput.sneak) {
                mc.thePlayer.motionY = -2.0;
            }

            setMoveSpeed(MotionSpeed.getValue());
        }

        if (mode.getValue() == FlightMode.Hypixel) {
            mc.thePlayer.motionY = 0;
        }

        if (mode.getValue() == FlightMode.HypixelZoom) {
            if (!hurtted)
                return;



            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDistance = Math.sqrt(xDist * xDist + zDist * zDist);

            mc.thePlayer.lastReportedPosY = 0;
            mc.thePlayer.jumpMovementFactor = 0;
            if (posYStage > 1) mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0016, mc.thePlayer.posZ);
            switch (++this.posYStage) {
                case 1: {
                    this.y *= -0.94666665455465f;
                    break;
                }
                case 2 | 3 | 4: {
                    this.y += 1.45E-3f;
                    break;
                }
                case 5: {
                    this.y += 1.0E-3f;
                    this.posYStage = 0;
                    break;
                }
            }
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + this.y, mc.thePlayer.posZ);
            if (boostHypixelState > 1) mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0016, mc.thePlayer.posZ);
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-16, mc.thePlayer.posZ);

            mc.timer.timerSpeed = (float)(1.0+TimerSpeed/currentSpeed);
            currentSpeed = Math.max(currentSpeed + 1, TimerSpeed);
            if (!failedStart)
                mc.thePlayer.motionY = 0D;
        }
    }

    @Sub
    private void onPacketSend(EventPacketSend e){
//        if(e.getPacket() instanceof C03PacketPlayer){
//            C03PacketPlayer packetPlayer = (C03PacketPlayer)e.getPacket();
//            if (!packetPlayer.isMoving()) {
//                packetPlayer.onGround = true;
//            } else {
//                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
//            }
//            ++chocked;
//        }
    }

    @Override
    public void onEnable() {
        beforeFlyY = mc.thePlayer.posY;
        thisY = mc.thePlayer.posY;
        posYStage = 0;
        if (mode.getValue() == FlightMode.Hypixel) {
            mc.thePlayer.jump();
            beforeFlyY = mc.thePlayer.posY + 0.41999998688698;
            thisY = mc.thePlayer.posY + 0.41999998688698;
//			mc.thePlayer.motionY = 0.405;
        }

        if (mode.getValue() == FlightMode.HypixelZoom) {

            hurtted = false;
            if (damage.getValue())
                damagePlayer();
            if (Watting.getValue()) {
                (new Thread(() -> {
                    try {
                        Thread.sleep(WattingTime.getValue().longValue() * 100l);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    mc.thePlayer.jump();
                    beforeFlyY = mc.thePlayer.posY + 0.41999998688698;
                    thisY = mc.thePlayer.posY + 0.41999998688698;
                    hurtted = true;

                })).start();
            } else {
                mc.thePlayer.jump();
                beforeFlyY = mc.thePlayer.posY + 0.41999998688698;
                thisY = mc.thePlayer.posY + 0.41999998688698;
                hurtted = true;

            }
            TimerSpeed = timerboost.getValue() * 100 * (1+zoomboost.getValue() / 10);
            currentSpeed = 100 * (1+zoomboost.getValue() / 10);
            boostHypixelState = 1;
            moveSpeed = 0.1D;
            lastDistance = 0D;
            failedStart = false;
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(0 , (short) (-1), false));
        mc.thePlayer.sendQueue.sendpacketNoEvent(new C0CPacketInput( Integer.MAX_VALUE,Integer.MAX_VALUE, true, true ) );
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
        mc.timer.timerSpeed = 1.0f;
//		if (mode.getModeName() == "HypixelZoom") {
//			mc.thePlayer.motionX = 0.0;
//			mc.thePlayer.motionZ = 0.0;
//		}

        super.onDisable();
    }

    public void damagePlayer() {
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(0 , (short) (-1), false));
        mc.thePlayer.sendQueue.sendpacketNoEvent(new C0CPacketInput( Integer.MAX_VALUE,Integer.MAX_VALUE, true, true ) );
        finalDamage();
    }

    private void finalDamage(){
        if (mc.thePlayer.onGround) {
            for (int index = 0; index <= 67 + (23 * (uhc.getValue() ? 1 : 0)); ++index) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX, mc.thePlayer.posY + 2.535E-9D, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX, mc.thePlayer.posY + 1.05E-10D, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX, mc.thePlayer.posY + 0.0448865D, mc.thePlayer.posZ, false));
            }
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        }
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
        }

        return baseSpeed;
    }

    @Sub
    public void onPullback(EventPacketRecieve e) {
        if (lagback.getValue() && e.getPacket() instanceof S08PacketPlayerPosLook) {
            setEnabled(false);
        }

    }

    private void setMoveSpeed(double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.thePlayer.motionX = forward * speed * -Math.sin(Math.toRadians(yaw))
                    + strafe * speed * Math.cos(Math.toRadians(yaw));
            mc.thePlayer.motionZ = forward * speed * Math.cos(Math.toRadians(yaw))
                    - strafe * speed * -Math.sin(Math.toRadians(yaw));
        }
    }
}