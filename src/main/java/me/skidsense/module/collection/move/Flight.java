package me.skidsense.module.collection.move;

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
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

import java.awt.*;
import java.util.TimerTask;

public class Flight extends Mod {
    public Mode dmode = new Mode("Damage", "Damage", (Enum[]) DamageMode.values(), (Enum) DamageMode.Random);
    public Mode mode = new Mode("Mode", "Mode", (Enum[]) FlightMode.values(), (Enum) FlightMode.Vanilla);
    private Option<Boolean> UHC = new Option("UHC", "UHC", Boolean.valueOf(false));
    int counter, level;
    double moveSpeed, lastDist;
    boolean FirstBoost;
    int packetOrder;

    public Flight() {
        super("Flight", new String[] { "MotionFly" }, ModuleType.Move);
        this.setColor(new Color(158, 114, 243).getRGB());
    }

    public void damage(int d) {
        if (this.dmode.getValue() == DamageMode.Random) {
            Random(d);
        }
    }

    public void Random(int floor_double) {
        if (mc.thePlayer.onGround) {
            final double[] offsets = new double[]{0.06D, 0.0001D};
            for (int i = 0; i < 53; i++) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offsets[0], mc.thePlayer.posZ, false));
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offsets[1], mc.thePlayer.posZ, false));
            }
            }
        }


    void sendPacket(double addY, boolean ground) {
        mc.thePlayer.sendQueue
                .addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + addY,
                        mc.thePlayer.posZ, mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitch, ground));
    }

    double posY;

    @Override
    public void onEnable() {
        if (this.mode.getValue() == FlightMode.Damage) {
            damage(1);
            new java.util.Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                }
            }, 240L);

        }
        level = 1;
        moveSpeed = 0.1D;
        FirstBoost = true;
        lastDist = 0.0D;
        posY = mc.thePlayer.posY;
    }

    @Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
        level = 1;
        moveSpeed = 0.1D;
        lastDist = 0.0D;
    }

    private boolean canZoom() {
        if (MoveUtil.isMoving() && this.mc.thePlayer.onGround) {
            return true;
        }
        return false;
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
        if (this.mode.getValue() == FlightMode.Vanilla) {
            this.mc.thePlayer.motionY = this.mc.thePlayer.movementInput.jump ? 1.0
                    : (this.mc.thePlayer.movementInput.sneak ? -1.0 : 0.0);
            if (MoveUtil.isMoving()) {
                MoveUtil.setSpeed(2.0);
            } else {
                MoveUtil.setSpeed(0.0);
            }
        } else if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {

            Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
                mc.thePlayer.motionZ *= 0.0;
                mc.thePlayer.motionX *= 0.0;
                mc.thePlayer.onGround = false;
            ++counter;
            if (counter % 2 == 0)
                posY++;
            if (Minecraft.getMinecraft().gameSettings.keyBindJump.pressed)
                Minecraft.getMinecraft().thePlayer.motionY += 0.5f;
            if (Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed)
                Minecraft.getMinecraft().thePlayer.motionY -= 0.5f;
            e.setY(mc.thePlayer.posY + posY / 10000000);
        }
    }

    public float getRealWalkYaw() {
        float curYaw = mc.thePlayer.rotationYaw, realYaw;
        boolean keyFor = mc.gameSettings.keyBindForward.pressed;
        boolean keyBack = mc.gameSettings.keyBindBack.pressed;
        boolean keyLeft = mc.gameSettings.keyBindLeft.pressed;
        boolean keyRight = mc.gameSettings.keyBindRight.pressed;
        if (keyFor) {
            if (keyLeft) {
                realYaw = curYaw - 45;
            } else if (keyRight) {
                realYaw = curYaw + 45;
            } else {
                realYaw = curYaw;
            }
        } else if (keyBack) {
            if (keyLeft) {
                realYaw = curYaw - 135;
            } else if (keyRight) {
                realYaw = curYaw + 135;
            } else {
                realYaw = curYaw - 180;
            }
        } else {
            if (keyLeft) {
                realYaw = curYaw - 90;
            } else if (keyRight) {
                realYaw = curYaw + 90;
            } else {
                realYaw = curYaw;
            }
        }

        return realYaw;

    }

    public double radions(float degrees) {
        return degrees * Math.PI / 180;
    }

    public void hClip(double offset) {
        double playerYaw = radions(getRealWalkYaw());
        mc.thePlayer.setPosition(mc.thePlayer.posX - (Math.sin(playerYaw) * offset),
                mc.thePlayer.posY + 0.0000000000001, mc.thePlayer.posZ + (Math.cos(playerYaw) * offset));
    }

    @Sub
    public void onPost(EventPostUpdate e) {
        if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {
            double xDist = Minecraft.getMinecraft().thePlayer.posX - Minecraft.getMinecraft().thePlayer.prevPosX;
            double zDist = Minecraft.getMinecraft().thePlayer.posZ - Minecraft.getMinecraft().thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }
    }

    int stage;
    private double distance;

    @Sub
    private void onMove(EventMove e) {
        if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {
                if (moveSpeed == 7D) {
                    if (!FirstBoost)
                        moveSpeed = 0.1D;
                    else
                        FirstBoost = false;
                }
                 else {
                    if (level != 1 || Minecraft.getMinecraft().thePlayer.moveForward == 0.0F
                            && Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0F) {
                        if (level == 2) {
                            level = 3;
                            moveSpeed *= 2.1499999D;
                        } else if (level == 3) {
                            level = 4;
                            double difference = (0.011D) * (lastDist - MathUtil.getBaseMovementSpeed());
                            moveSpeed = lastDist - difference;
                        } else {
                            if (Minecraft.getMinecraft().theWorld
                                    .getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer,
                                            Minecraft.getMinecraft().thePlayer.boundingBox.offset(0.0D,
                                                    Minecraft.getMinecraft().thePlayer.motionY, 0.0D))
                                    .size() > 0 || Minecraft.getMinecraft().thePlayer.isCollidedVertically) {
                                level = 1;
                            }
                            moveSpeed = lastDist - lastDist / 159.0D;
                        }
                    } else {
                        level = 2;
                        int amplifier = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)
                                ? Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
                                .getAmplifier() + 1
                                : 0;
                        double boost = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed) ? 1.7 : 2.1;
                        moveSpeed = boost * MathUtil.getBaseMovementSpeed();
                    }
                }

                moveSpeed = this.mode.getValue() == FlightMode.Damage
                        ? Math.max(moveSpeed, MathUtil.getBaseMovementSpeed())
                        : MathUtil.getBaseMovementSpeed();
                MoveUtil.setMoveSpeed(e, moveSpeed);

            }
        }

    static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return baseSpeed;
    }

    public static enum FlightMode {
        Vanilla, Hypixel, Damage,
    }

    public static enum DamageMode {
        Random,
    }
}
