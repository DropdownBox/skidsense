package me.skidsense.module.collection.move;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.*;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import java.util.*;


public class Flight extends Mod {
    private int stage, posYStage;
    private double moveSpeed, lastDist, boostTimerSpeed, timerDelValue, y;
    private boolean start = true;
    private boolean prepare = false;

    private Option<Boolean> blink = new Option<>("Blink","Blink", true);
    private Option<Boolean> blinkAttack = new Option<>("BlinkAttack","BlinkAttack", false);
    private Numbers<Double> speed = new Numbers<>("Speed","Speed", 0.32, 0.2, 0.4, 0.01);
    private Numbers<Double> timerSpeed = new Numbers<>("Boost Timer","Boost Timer", 1.8, 1.0, 5.0, 0.1);
    private Numbers<Double> boostTime = new Numbers<>("Boost Time","Boost Time", 4.0, 1.0, 10.0, 1.0);

    private final List<Packet<?>> packets = new ArrayList<>();

    public Flight() {
        super("Flight", new String[]{"Flight", "Fly"}, ModuleType.Move);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null || !mc.thePlayer.onGround) {
            start = false;
            return;
        }
        this.stage = 1;
        this.posYStage = 0;
        this.moveSpeed = 0;
        this.y = 0;
        this.boostTimerSpeed = timerSpeed.getValue();
        this.timerDelValue = (timerSpeed.getValue() - 1.0) / (20 * boostTime.getValue());
        if (mc.thePlayer.onGround) {
            final double offset = 0.4122222218322211111111F;
            final NetHandlerPlayClient netHandler = mc.getNetHandler();
            final EntityPlayerSP player = mc.thePlayer;
            final double x = player.posX;
            final double y = player.posY;
            final double z = player.posZ;
            for (int i = 0; i < 9; i++) {
                netHandler.sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, false));
                netHandler.sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.000002737372, z, false));
                netHandler.sendpacketNoEvent(new C03PacketPlayer(false));
            }
            netHandler.sendpacketNoEvent(new C03PacketPlayer(true));
            System.out.println("执行白细胞伤害！");
        }
        mc.thePlayer.jump();
        prepare = true;
        packets.clear();
    }

    private void sendBlinkPacket() {
        PlayerCapabilities playerCapabilities = new PlayerCapabilities();
        playerCapabilities.allowFlying = true;
        playerCapabilities.isFlying = true;
        playerCapabilities.isCreativeMode = true;
        playerCapabilities.disableDamage = true;
        playerCapabilities.setFlySpeed(Float.MAX_EXPONENT);
        playerCapabilities.setPlayerWalkSpeed(Float.MAX_EXPONENT);
        playerCapabilities.allowEdit = true;
        for (Packet<?> p : packets) {
            if(p instanceof C03PacketPlayer && (((C03PacketPlayer) p).isMoving() || !((C03PacketPlayer) p).onGround)){
                    ((C03PacketPlayer) p).setMoving(false);
                    ((C03PacketPlayer) p).onGround = true;
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
                    mc.thePlayer.sendQueue.sendpacketNoEvent(p);
                    System.out.println("Spoofed.");
                }else {
                    mc.thePlayer.sendQueue.sendpacketNoEvent(p);
                }
        }
        packets.clear();
    }

    @Override
    public void onDisable() {
        if (blink.getValue() && !packets.isEmpty() && mc.thePlayer.sendQueue != null) {
            sendBlinkPacket();
        }

        start = true;
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.stepHeight = 0.625f;
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
        prepare = false;
    }

    @Sub
    public void onMove(EventMove event) {
        if (!MoveUtil.isMoving()) {
            event.setX(0);
            event.setZ(0);
            return;
        }
        if (!prepare) return;

        switch (this.stage) {
            case 1: {
                this.moveSpeed = 1.777734 * MoveUtil.getBaseMoveSpeed();
                this.stage = 2;
                break;
            }
            case 2: {
                this.moveSpeed *= 2.2772;
                this.stage = 3;
                break;
            }
            case 3: {
                this.moveSpeed = this.lastDist - ((mc.thePlayer.ticksExisted % 2 == 0) ? 0.0093 : 0.0143) * (this.lastDist - MoveUtil.getBaseMoveSpeed());
                this.stage = 4;
                break;
            }
            default: {
                this.moveSpeed = this.lastDist - this.lastDist / 159;
                break;
            }
        }
        this.moveSpeed = Math.max(this.moveSpeed, speed.getValue());
        MoveUtil.setMotion(event, moveSpeed);
    }

    @Sub
    public void onMotion(EventPreUpdate event) {
        if (!start) {
            //setEnabled(false);
            return;
        }
        if (!prepare) return;

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
        if (stage > 1) mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0016, mc.thePlayer.posZ);
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-16, mc.thePlayer.posZ);
        mc.thePlayer.motionY = 0;
        mc.thePlayer.jumpMovementFactor = 0;
        mc.timer.timerSpeed = (float) boostTimerSpeed;
        this.boostTimerSpeed = Math.max(this.boostTimerSpeed - this.timerDelValue, 1.0);
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @Sub
    public void onPacket(EventPacketRecieve event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            setEnabled(false);
        }
    }

    @Sub
    public final void onPacket(EventPacketSend e) {
        if (blink.getValue()) {
            Packet<?> packet = e.getPacket();
            if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition
                    || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook
                    || packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C0APacketAnimation
                    || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity) {
//                if(packet instanceof C03PacketPlayer){
//                    if(!((C03PacketPlayer) packet).isMoving()){
//                        e.setCancelled(true);
//                    }
//                }else
                    this.packets.add(packet);
                    e.setCancelled(true);
                if (packet instanceof C02PacketUseEntity && packets.size() > 8 && blinkAttack.getValue() || packets.size() > 41) {
                    sendBlinkPacket();
                }
            }
        }else if(e.getPacket() instanceof C03PacketPlayer){
            C03PacketPlayer c03PacketPlayer = (C03PacketPlayer) e.packet;
            if(c03PacketPlayer.isMoving() || !c03PacketPlayer.isOnGround()){
                System.out.println("找到一个可以chock的数据包。 MovingState:" + c03PacketPlayer.isMoving() + " onGround:" + c03PacketPlayer.isOnGround());
                c03PacketPlayer.setMoving(false);
                c03PacketPlayer.onGround = true;
            }
        }
    }
}
