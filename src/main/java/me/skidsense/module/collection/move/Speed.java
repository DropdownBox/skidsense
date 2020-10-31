package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.*;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

import java.awt.*;
import java.util.List;

public class Speed
        extends Mod {
    private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[]) SpeedMode.values(), (Enum) SpeedMode.HypixelSlow);
    public static Option<Boolean> TTimer = new Option<Boolean>("Timer", "Timer", true);
    public static Option<Boolean> Jello = new Option<Boolean>("JelloTimer", "JelloTimer", true);
    public double movementSpeed;
    int level = 1;
    public static int stage;
    private double lastDist;
    public boolean shouldslow = false;
    private double speed;
    public static int aacCount;
    boolean collided = false;
    boolean lessSlow;
    TimerUtil lastCheck = new TimerUtil();
    double less;
    double stair;
    boolean iscolod;
    boolean isJump;
    public static TimerUtil timer2 = new TimerUtil();
    int stoptick;
    private TimerUtil timer = new TimerUtil();

    public Speed() {
        super("Speed", new String[]{"zoom"}, ModuleType.Move);
        this.setColor(new Color(99, 248, 91).getRGB());
    }

    @Override
    public void onEnable() {
        boolean player = mc.thePlayer == null;
        this.collided = player ? false : mc.thePlayer.isCollidedHorizontally;
        this.lessSlow = false;
        if (mc.thePlayer != null) {
            this.speed = defaultSpeed();
        }
        this.less = 0.0;
        this.lastDist = 0.0;
        stage = 2;
        mc.timer.timerSpeed = 1.0f;
    }

    @Override
    public void onDisable() {
        speed = MoveUtil.getBaseMoveSpeed();
        level = 0;
        mc.timer.timerSpeed = 1.0f;
        aacCount = 0;
        lastSpeed = -1;
    }

    @Sub
    public void onPacket(EventPacketRecieve event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            setEnabled(false);
        }
    }

    @Sub
    public void onMotion(EventPreUpdate event) {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public void setSpeed(double speed) {
        mc.thePlayer.motionX = -Math.sin(this.getDirection()) * speed;
        mc.thePlayer.motionZ = Math.cos(this.getDirection()) * speed;
    }

    public float getDirection() {
        float yaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.thePlayer.moveStrafing > 0.0f) {
            yaw -= 90.0f * forward;
        }
        if (mc.thePlayer.moveStrafing < 0.0f) {
            yaw += 90.0f * forward;
        }
        return yaw *= 0.017453292f;
    }


    private boolean canZoom() {
        return MoveUtil.isMoving() && mc.thePlayer.onGround;
    }

    boolean isstep = false;

    @Sub
    public void onStep(EventStep e) {
        isstep = false;
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
    }

    private boolean checksoulsand() {
        if (issoulsand(0, 0)) {
            return true;
        }
        return false;
    }

    private boolean issoulsand(int X, int Z) {
        if (mc.thePlayer.posY < 0.0) {
        }
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            Block block = mc.theWorld.getBlockState(new BlockPos(X, -off, Z)).getBlock();
            if (!block.blockRegistry.equals("soul_sand") || !Block.blockRegistry.equals("soul_sand"))
                return false;
        }
        return true;
    }

    private double getHypixelBest(double speed, int T) {
        double base = MoveUtil.getBaseMoveSpeed();
        boolean slow = false;

        if (T == 1) {
            speed = 0.028;
        } else if (mc.thePlayer.onGround && PlayerUtil.isMoving() && T == 2) {
            speed *= 2.149;
        } else if (T == 3) {
            double str = 0.7095;
            double fe = 1.0E-18;
            double strafe = str * (lastDist - base);
            speed = lastDist - (strafe + fe);
            iscolod = true;
        } else {
            if (T == 2 && mc.thePlayer.fallDistance > 0.0) {
                slow = true;
            }
            level = 1;
            speed = lastDist - lastDist / 159.0;
        }

        speed = Math.max(speed - (slow ? (lastDist * speed) * 0.0149336 : (0.0049336 * lastDist)), base);
        return speed;
    }

    double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return baseSpeed;
    }

    @Sub
    private void onMove(EventMove e) {
        if (mode.getValue() == SpeedMode.HypixelLow) {
            final Minecraft mc = Speed.mc;
            if (mc.thePlayer.isCollidedHorizontally) {
                this.collided = true;
            }
            if (this.collided) {
                mc.timer.timerSpeed = 1.0f;
                Speed.stage = -1;
            }
            if (this.stair > 0.0) {
                this.stair -= 0.25;
            }
            this.less -= ((this.less > 1.0) ? 0.12 : 0.11);
            if (this.less < 0.0) {
                this.less = 0.0;
            }
            if (!this.isInLiquid() && this.isOnGround(0.01) && isMoving2()) {
                final Minecraft mc2 = Speed.mc;
                this.collided = mc.thePlayer.isCollidedHorizontally;
                if (Speed.stage >= 0 || this.collided) {
                    Speed.stage = 0;
                    final double motY2 = 0.2 + getJumpEffect() * 0.1;
                    if (this.stair == 0.0) {
                        final Minecraft mc3 = Speed.mc;
                        mc.thePlayer.motionY = motY2;
                        final Minecraft mc4 = Speed.mc;
                        e.setY(mc.thePlayer.motionY);
                        if(this.TTimer.getValue()) {
                            this.mc.timer.timerSpeed = 1.07f;
                        }else {
                            this.mc.timer.timerSpeed = 1.0f;
                        }
                    }
                }
            }
            this.movementSpeed = this.getHypixelSpeed(Speed.stage) + 0.0331;
            this.movementSpeed *= 0.81;
            if (this.stair > 0.0) {
                this.movementSpeed *= 0.7 - this.getSpeedEffect() * 0.1;
            }
            if (Speed.stage < 0) {
                this.movementSpeed = this.defaultSpeed();
            }
            if (this.lessSlow) {
                this.movementSpeed *= 0.6;
            }
            if (this.lessSlow) {
                this.movementSpeed *= 0.6;
            }
            if (this.isInLiquid()) {
                this.movementSpeed = 0.12;
            }
        }
        if (mode.getValue() == SpeedMode.Bhop) {
            if (mc.thePlayer.moveForward == 0.0f && mc.thePlayer.moveStrafing == 0.0f) {
                speed = MoveUtil.defaultSpeed();
            }
            if (stage == 1 && mc.thePlayer.isCollidedVertically && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                speed = 1.35 + MoveUtil.defaultSpeed() - 0.01;
            }
            if (!isInLiquid() && stage == 2 && mc.thePlayer.isCollidedVertically && MoveUtil.isOnGround(0.01) && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump))
                    e.setY(mc.thePlayer.motionY = 0.41999998688698 + (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1);
                else
                    e.setY(mc.thePlayer.motionY = 0.41999998688698);
                mc.thePlayer.jump();
                speed *= 1.533D;
            } else if (stage == 3) {
                final double difference = 0.66 * (lastDist - MoveUtil.defaultSpeed());
                speed = lastDist - difference;
            } else {
                final List collidingList = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
                if ((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                    stage = ((mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) ? 1 : 0);
                }
                speed = lastDist - lastDist / 159.0;
            }
            speed = Math.max(speed, MoveUtil.defaultSpeed());

            //Stage checks if you're greater than 0 as step sets you -6 stage to make sure the player wont flag.
            if (stage > 0) {
                //Set strafe motion.
                if (BlockUtil.isInLiquid())
                    speed = 0.1;
                setMotion(e, speed);
            }
            //If the player is moving, step the stage up.
            if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
                ++stage;
            }
        }
        if (mode.getValue() == SpeedMode.YPort) {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.collided = true;
            }
            if (this.collided) {
                mc.timer.timerSpeed = 1.0f;
                this.stage = -1;
            }
            if (this.stair > 0.0) {
                this.stair -= 0.25;
            }
            this.less -= this.less > 1.0 ? 0.12 : 0.11;
            if (this.less < 0.0) {
                this.less = 0.0;
            }
            if (!this.isInLiquid() && isOnGround(0.01) && isMoving2()) {
                this.collided = mc.thePlayer.isCollidedHorizontally;
                if (this.stage >= 0 || this.collided) {
                    this.stage = 0;
                    double motY = 0.218289D + (double) getJumpEffect() * 0.1;
                    if (this.stair == 0.0) {
                        mc.thePlayer.motionY = motY;
                        e.setY(mc.thePlayer.motionY = motY);
                        if(this.TTimer.getValue()) {
                            this.mc.timer.timerSpeed = 1.07f;
                        }else {
                            this.mc.timer.timerSpeed = 1.0f;
                        }
                    }

                }

            }
            this.movementSpeed = this.getHypixelSpeed(this.stage) + 0.0331;

            if (stage > 0 && stage == mc.thePlayer.ticksExisted % 3) {
                mc.thePlayer.motionY *= -0.01779;
            }

            this.movementSpeed *= 0.77546804732863495341;
            if (this.stair > 0.0) {
                this.movementSpeed *= 0.7 - (double) this.getSpeedEffect() * 0.1;
            }
            if (this.stage < 0) {
                this.movementSpeed = this.defaultSpeed();
            }
            if (this.lessSlow) {
                this.movementSpeed *= 0.654460312863;
            }
            if (this.lessSlow) {
                this.movementSpeed *= 0.6544602329874;
            }
            if (this.isInLiquid()) {
                this.movementSpeed = 0.12;
            }
            if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {

                this.setMotion(e, this.movementSpeed);
                ++this.stage;
            }
        }
        if (this.mode.getValue() == SpeedMode.HypixelSlow) {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.collided = true;
            }
            if (this.collided) {
                mc.timer.timerSpeed = 1.0f;
                Speed.stage = -1;
            }
            if (this.stair > 0.0) {
                this.stair -= 0.25;
            }
            this.less -= ((this.less > 1.0) ? 0.12 : 0.11);
            if (this.less < 0.0) {
                this.less = 0.0;
            }
            if (!this.isInLiquid() && MoveUtil.isOnGround(0.01) && PlayerUtil.isMoving2()) {
                this.collided = mc.thePlayer.isCollidedHorizontally;
                if (Speed.stage >= 0 || this.collided) {
                    Speed.stage = 0;
                    final double motY = 0.41999998688698 + MoveUtil.getJumpEffect() * 0.1;
                    final double JellomotY = 0.4074196 + MoveUtil.getJumpEffect() * 0.1;
                    if (this.stair == 0.0) {
                        PlayerCapabilities playerCapabilities = new PlayerCapabilities();
                        playerCapabilities.allowFlying = true;
                        playerCapabilities.isFlying = true;
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
                        if(this.Jello.getValue()) {
                            e.setY(mc.thePlayer.motionY = JellomotY);
                        }else {
                            e.setY(mc.thePlayer.motionY = motY);
                        }
                        if(this.TTimer.getValue()) {
                            this.mc.timer.timerSpeed = 1.07f;
                        }else {
                            this.mc.timer.timerSpeed = 1.0f;
                        }
                    }
                    ++this.less;
                    this.lessSlow = (this.less > 1.0 && !this.lessSlow);
                    if (this.less > 1.12) {
                        this.less = 1.12;
                    }
                }
            }
//            if (!mc.thePlayer.onGround) {
//                mc.thePlayer.motionY *= -0.977;
//            } else {
//            }
            this.movementSpeed = this.getHypixelSpeed(Speed.stage) + 0.0331;
            this.movementSpeed *= 0.89;
            if (this.stair > 0.0) {
                this.movementSpeed *= 0.66 - this.getSpeedEffect() * 0.1;
            }
            if (Speed.stage < 0) {
                this.movementSpeed = this.getBaseMoveSpeed();
            }
            if (this.lessSlow) {
                this.movementSpeed *= 0.89;
            }
            if (this.isInLiquid()) {
                this.movementSpeed = 0.12;
            }
            if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
                if (!Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
                    this.setMotion(e, this.movementSpeed);
                }
            }
            ++Speed.stage;
        }
        if (this.mode.getValue() == SpeedMode.Hypixel) {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.collided = true;
            }
            if (this.collided) {
                mc.timer.timerSpeed = 1.0f;
                Speed.stage = -1;
            }
            if (this.stair > 0.0) {
                this.stair -= 0.25;
            }
            this.less -= ((this.less > 1.0) ? 0.12 : 0.11);
            if (this.less < 0.0) {
                this.less = 0.0;
            }
            if (!this.isInLiquid() && MoveUtil.isOnGround(0.01) && PlayerUtil.isMoving2()) {
                this.collided = mc.thePlayer.isCollidedHorizontally;
                if (Speed.stage >= 0 || this.collided) {
                    Speed.stage = 0;
                    final double motY = 0.4110779998688698 + MoveUtil.getJumpEffect() * 0.1;
                    final double JellomotY = 0.4079196 + MoveUtil.getJumpEffect() * 0.1;
                    if (this.stair == 0.0) {
                        if(this.Jello.getValue()) {
                            e.setY(mc.thePlayer.motionY = JellomotY);
                        }else {
                            e.setY(mc.thePlayer.motionY = motY);
                        }
                        if(this.TTimer.getValue()) {
                            this.mc.timer.timerSpeed = 1.07f;
                        }else {
                            this.mc.timer.timerSpeed = 1.0f;
                        }
                    }
                    ++this.less;
                    this.lessSlow = (this.less > 1.0 && !this.lessSlow);
                    if (this.less > 1.12) {
                        this.less = 1.12;
                    }
                }
            }
//            if (stage > 0 && !mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.motionY < 0) {
//                Helper.sendMessage(mc.thePlayer.motionY + " ");
//                mc.thePlayer.motionY *= 0.9997;
//            }
            this.movementSpeed = this.getHypixelSpeed(Speed.stage) + 0.0337;
            this.movementSpeed *= 0.93;
            if (this.stair > 0.0) {
                this.movementSpeed *= 0.66 - this.getSpeedEffect() * 0.1;
            }
            if (Speed.stage < 0) {
                this.movementSpeed = this.getBaseMoveSpeed();
            }
            if (this.lessSlow) {
                this.movementSpeed *= 0.92555;
            }
            if (this.isInLiquid()) {
                this.movementSpeed = 0.1;
            }
            if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
                if (Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
                    this.setMotion(e, this.movementSpeed);
                }
            }
            ++Speed.stage;

        }
    }

    private static double randomD(double min, double max, int scl) {
        int pow = (int) Math.pow(10.0D, scl);
        return Math.floor((Math.random() * (max - min) + min) * pow) / pow;
    }

    private void setMotion2(EventMove em, double speed) {
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
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    private boolean isInLiquid() {
        if (mc.thePlayer == null) {
            return false;
        }
        int x2 = MathHelper.floor_double(mc.thePlayer.boundingBox.minX);
        while (x2 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1) {
            int z2 = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ);
            while (z2 < MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ) + 1) {
                BlockPos pos = new BlockPos(x2, (int) mc.thePlayer.boundingBox.minY, z2);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    return block instanceof BlockLiquid;
                }
                ++z2;
            }
            ++x2;
        }
        return false;
    }

    public static int getJumpEffect() {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump))
            return Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        else
            return 0;
    }

    public boolean isMoving2() {
        Minecraft.getMinecraft();
        if (mc.thePlayer.moveForward == 0.0f) {
            if (mc.thePlayer.moveStrafing == 0.0f) {
                return false;
            }
        }
        return true;
    }

    public boolean isOnGround(double height) {
        if (!this.mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty()) {
            return true;
        }
        return false;
    }

    private double defaultSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double) (amplifier + 1);
        }
        return baseSpeed;
    }

    private void setMotion(EventMove em2, double speed) {
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            em2.setX(0.0);
            em2.setZ(0.0);
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
            em2.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            em2.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;

    }


    private double lastSpeed = -1;

    private double getHypixelSpeed(int stage) {
        double value = defaultSpeed() + 0.0294077 * (double) getSpeedEffect() + (double) getSpeedEffect() / 15.0;
        double firstvalue = 0.4156 + (double) getSpeedEffect() / 12.5;
        double decr = (double) stage / 500.0 * 2.0;
        if (stage == 0) {
            if (this.timer.delay(300.0f)) {
                this.timer.reset();
            }
            if (!this.lastCheck.delay(500.0f)) {
                if (!this.shouldslow) {
                    this.shouldslow = true;
                }
            } else if (this.shouldslow) {
                this.shouldslow = false;
            }
            value = 0.64 + ((double) getSpeedEffect() + 0.028 * (double) getSpeedEffect()) * 0.134;
        } else if (stage == 1) {
            value = firstvalue;
        } else if (stage >= 2) {
            value = firstvalue - decr;
        }
        if (this.shouldslow || !this.lastCheck.delay(500.0f) || this.collided) {
            value = 0.3;
            if (stage == 0) {
                value = 0.0;
            }
        }
        double speed = Math.max(value, this.shouldslow ? value : defaultSpeed() + 0.028 * (double) getSpeedEffect());

//        Helper.sendMessage(speed + " " + lastSpeed);

        if (lastSpeed != -1 && speed > lastSpeed && speed - lastSpeed > 0.15) {
            speed -= (speed - lastSpeed) / 4;
        }

        lastSpeed = speed;

        return speed;
    }

    static enum SpeedMode {
        HypixelLow,
        HypixelSlow,
        Bhop,
        Hypixel,
        YPort
    }
}

