package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventStep;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.stats.StatList;

import java.awt.*;
import java.util.List;

public class Speed extends Mod {

    private Mode<Enum> mode = new Mode("Mode", "mode", SpeedMode.values(), SpeedMode.HypixelHop);
    private Option<Boolean> setback = new Option("Setback", "Setback", false);

    private int stage;
    private double movementSpeed;
    private double distance;
    private double speed, speedvalue;
    private double lastDist;
    public static int aacCount;
    boolean collided, lessSlow, shouldslow = false;
    double less, stair;
    TimerUtil lastCheck = new TimerUtil();


    public Speed() {
        super("Speed", new String[]{"zoom"}, ModuleType.Move);
        setColor(new Color(99, 248, 91).getRGB());
    }

    private TimerUtil timer = new TimerUtil();

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @Sub
    public void onStep(EventStep event) {
        double height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
        if (height > 0.7D) {
            this.less = 0.0D;
        }

        if (height == 0.5D) {
            this.stair = 0.75D;
        }

    }


    @Override
    public void onEnable() {
        boolean isCollidedHorizontally;
        final boolean player = mc.thePlayer == null;
        if (player) {
            isCollidedHorizontally = false;
        }
        else {
            isCollidedHorizontally = mc.thePlayer.isCollidedHorizontally;
        }
        this.collided = isCollidedHorizontally;
        this.lessSlow = false;
        if (mc.thePlayer != null) {
            this.speed = MoveUtil.defaultSpeed();
        }
        this.less = 0.0;
        this.lastDist = 0.0;
        stage = 2;
        mc.timer.timerSpeed = 1.0f;
        this.movementSpeed = ((mc.thePlayer == null) ? 0.2873 : MoveUtil.getBaseMoveSpeed());
        super.onEnable();
    }

    private boolean canZoom() {
        return MoveUtil.isMoving() && mc.thePlayer.onGround;
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        setSuffix(mode.getValue());
        if (mode.getValue().equals(SpeedMode.HypixelHop)) {
            final double var7 = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            final double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            this.distance = Math.sqrt(var7 * var7 + zDist * zDist);
        } else if (mode.getValue() == SpeedMode.Onground && canZoom()) {
            switch (this.stage) {
                case 1:
                    e.setY(e.getY() + 0.4D);
                    e.setOnGround(false);
                    this.stage += 1;
                    break;
                case 2:
                    e.setY(e.getY() + 0.4D);
                    e.setOnGround(false);
                    this.stage += 1;
                    break;
                default:
                    this.stage = 1;
                    break;

                // this.momentum = ((float) EntityHelper.getNormalMovementSpeed());
            }
        }
    }

    @Sub
    public void onPacket(EventPacketRecieve e) {
        if (setback.getValue()) {
            if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                this.setEnabled(false);
            }
        }
    }

    // Hypixel Mode
    @Sub
    private void onMove(EventMove e) {
        if (mode.getValue() == SpeedMode.HypixelHop) {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.collided = true;
            }

            if (this.collided) {
                mc.timer.timerSpeed = 1.0F;
                this.stage = -1;
            }

            if (this.stair > 0.0D) {
                this.stair -= 0.25D;
            }

            this.less -= this.less > 1.0D ? 0.12D : 0.11D;
            if (this.less < 0.0D) {
                this.less = 0.0D;
            }

            if (!BlockUtil.isInLiquid() && MoveUtil.isOnGround(0.01D) && MoveUtil.isMoving()) {
                this.collided = mc.thePlayer.isCollidedHorizontally;
                if (this.stage >= 0 || this.collided) {
                    this.stage = 0;
                    double a = 0.4086666D + (double) MoveUtil.getJumpEffect() * 0.1D;
                    if (this.stair == 0.0D) {
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY = a;
                        e.setY(mc.thePlayer.motionY);
                    }

                    ++this.less;
                    boolean bl = this.lessSlow = this.less > 1.0D && !this.lessSlow;
                    if (this.less > 1.12D) {
                        this.less = 1.12D;
                    }
                }
            }

            this.speed = this.getHypixelSpeed(this.stage) + 0.0331D;
            this.speed *= 0.91D;
            if (this.stair > 0.0D) {
                this.speed *= 0.66D - (double) MoveUtil.getSpeedEffect() * 0.1D;
            }

            if (this.stage < 0) {
                this.speed = MoveUtil.getBaseMoveSpeed();
            }

            if (this.lessSlow) {
                this.speed *= 0.93D;
            }

            if (PlayerUtil.isInLiquid()) {
                this.speed = 0.12D;
            }

            if ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                MoveUtil.setMotion(e, speed);
                ++stage;
            }
        }
    }

    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }


    private double getHypixelSpeed(final int stage) {
        double value = MoveUtil.getBaseMoveSpeed() + 0.028 * MoveUtil.getSpeedEffect() + MoveUtil.getSpeedEffect() / 15.0;
        final double firstvalue = 0.4145 + MoveUtil.getSpeedEffect() / 12.5;
        final double decr = stage / 500.0 * 2.0;
        if (stage == 0) {
            if (this.timer.delay(300.0f)) {
                this.timer.reset();
            }
            if (!this.lastCheck.delay(500.0f)) {
                if (!this.shouldslow) {
                    this.shouldslow = true;
                }
            }
            else if (this.shouldslow) {
                this.shouldslow = false;
            }
            value = 0.64 + (MoveUtil.getSpeedEffect() + 0.028 * MoveUtil.getSpeedEffect()) * 0.134;
        }
        else if (stage == 1) {
            value = firstvalue;
        }
        else if (stage >= 2) {
            value = firstvalue - decr;
        }
        if (this.shouldslow || !this.lastCheck.delay(500.0f) || this.collided) {
            value = 0.2;
            if (stage == 0) {
                value = 0.0;
            }
        }
        return Math.max(value, this.shouldslow ? value : (MoveUtil.getBaseMoveSpeed() + 0.028 * MoveUtil.getSpeedEffect()));
    }

    private void setMotion(EventMove em, double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            em.setX(0.0);
            em.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90)));

            if (forward == 0.0F && strafe == 0.0F) {
                em.setX(0.0);
                em.setZ(0.0);
            }
        }
    }


    public List getCollidingList(double motionY){
        return this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, this.mc.thePlayer.boundingBox.offset(0.0, motionY, 0.0));
    }

    enum SpeedMode {
        HypixelHop, Onground
    }
}
