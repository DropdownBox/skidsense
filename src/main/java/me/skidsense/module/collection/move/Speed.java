package me.skidsense.module.collection.move;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Event;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.*;
import net.minecraft.block.BlockStairs;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Timer;

import java.awt.*;
import java.util.List;

public class Speed extends Mod {

    private Mode<Enum> mode = new Mode("Mode", "mode", SpeedMode.values(), SpeedMode.HypixelHop);
    private Option<Boolean> setback = new Option("Setback","Setback",false);

    private int stage;
    private double movementSpeed;
    private double distance;
    private double speed, speedvalue;
    private double lastDist;
    public static int  aacCount;
    boolean collided,lessSlow,shouldslow = false;
    double less, stair;
    TimerUtil lastCheck = new TimerUtil();




    public Speed() {
        super("Speed", new String[] { "zoom" }, ModuleType.Move);
        setColor(new Color(99, 248, 91).getRGB());
    }

    private TimerUtil timer = new TimerUtil();

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }

    @Override
    public void  onEnable(){
        super.onEnable();
    }

    private boolean canZoom() {
        return MoveUtil.isMoving() && mc.thePlayer.onGround;
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        setSuffix(mode.getValue());
        if(mode.getValue().equals(SpeedMode.HypixelHop)){
            final double var7 = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            final double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            this.distance = Math.sqrt(var7 * var7 + zDist * zDist);
        }else
        if (mode.getValue() == SpeedMode.Onground && canZoom()) {
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
        if(setback.getValue()) {
            if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                this.setEnabled(false);
            }
        }
    }

    // Hypixel Mode
    @Sub
    private void onMove(EventMove e) {
        switch (mode.getValue().toString()){
            case "HypixelHop": {
                if (mc.thePlayer.isCollidedHorizontally) {
                    this.collided = true;
                }
                if (this.collided) {
                    mc.timer.timerSpeed = 1.0f;
                    stage = -1;
                }
                if (this.stair > 0.0) {
                    this.stair -= 0.25;
                }
                this.less -= ((this.less > 1.0) ? 0.12 : 0.11);
                if (this.less < 0.0) {
                    this.less = 0.0;
                }
                if (!MoveUtil.isInLiquid() && MoveUtil.isOnGround(0.01) /*&& MoveUtil.()*/) {
                    this.collided = mc.thePlayer.isCollidedHorizontally;
                    if (stage >= 0 || this.collided) {
                        stage = 0;
                        final double a = 0.4086666 + MoveUtil.getJumpEffect() * 0.1;
                        if (this.stair == 0.0) {
                            mc.thePlayer.jump();
                            if(mc.thePlayer.motionY < 0.4){
                                e.setY(0.399999);
                                System.out.println(1);
                            }else
                            e.setY(mc.thePlayer.motionY);
                            System.out.println(2);
                        }
                        ++this.less;
                        this.lessSlow = (this.less > 1.0 && !this.lessSlow);
                        if (this.less > 1.12) {
                            this.less = 1.12;
                        }
                    }
                }
                this.speed = this.getHypixelSpeed(stage) + 0.0331;
                this.speed *= 0.91;
                if (this.stair > 0.0) {
                    this.speed *= 0.66 - MoveUtil.getSpeedEffect() * 0.1;
                }
                if (stage < 0) {
                    this.speed = MoveUtil.getBaseMoveSpeed();
                }
                if (this.lessSlow) {
                    this.speed *= 0.93;
                }
                if (MoveUtil.isInLiquid()) {
                    this.speed = 0.12;
                }
                if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
                    this.setMotion(e, this.speed);
                    ++stage;
                }
                System.out.println(mc.thePlayer.motionY);
                break;
            } case "Bhop":{
                if (mc.thePlayer.isCollidedHorizontally) {
                    collided = true;
                }
                if (collided) {
                    mc.timer.timerSpeed = 1;
                    stage = -1;
                }
                if (stair > 0)
                    stair -= 0.25;
                less -= less > 1 ? 0.12 : 0.11;
                if (less < 0)
                    less = 0;
                if (!BlockUtil.isInLiquid() && MoveUtil.isOnGround(0.01) && (PlayerUtil.isMoving2())) {
                    collided = mc.thePlayer.isCollidedHorizontally;
                    if (stage >= 0 || collided) {
                        stage = 0;

                        double motY = 0.407 + MoveUtil.getJumpEffect() * 0.1;
                        if (stair == 0) {
                            mc.thePlayer.jump();
                            e.setY(mc.thePlayer.motionY = motY);
                        } else {

                        }

                        less++;
                        if (less > 1 && !lessSlow)
                            lessSlow = true;
                        else
                            lessSlow = false;
                        if (less > 1.12)
                            less = 1.12;
                    }
                }
                speed = getHypixelSpeed(stage) + 0.0331;
                speed *= 0.91;
                if (stair > 0) {
                    speed *= 0.7 - MoveUtil.getSpeedEffect() * 0.1;
                }

                if (stage < 0)
                    speed = MoveUtil.defaultSpeed();
                if (lessSlow) {
                    speed *= 0.95;
                }


                if (BlockUtil.isInLiquid()) {
                    speed = 0.12;
                }

                if ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                    setMotion(e, speed);
                    ++stage;
                }
                break;
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
        Bhop, HypixelHop, Onground
    }
}
