package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.BlockUtil;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.List;


public class Speed2
        extends Mod {
    private Mode<Enum> mode = new Mode("Mode", "Mode", (Enum[])SpeedMode.values(), (Enum)SpeedMode.HypixelHop);
    private Mode<Enum> MotionMode = new Mode("MotionMode", "MotionMode", (Enum[])Motions.values(), (Enum)Motions.Basic);
    private boolean firstJump;
    private boolean waitForGround;
    public static Option<Boolean> setback = new Option<Boolean> ("SetBack", "SetBack", true);
    private static int stage;
    private double movementSpeed;
    private double lastDist;
    public boolean shouldslow = false;
    TimerUtil timer = new TimerUtil();
    TimerUtil lastCheck = new TimerUtil();
    boolean collided;
    boolean lessSlow;
    double less;
    double stair;
    private double speed;
    private double movespeed;
    public double slow;
    double xDist;
    double zDist;
    float yaw;
    private double distance;
    private int ticks;

    public Speed2() {
        super("Speed", new String[]{"zoom"}, ModuleType.Move);
        this.setColor(new Color(99, 248, 91).getRGB());
    }
    @Override
    public void onEnable() {
        firstJump = true;
        mc.timer.timerSpeed=1;
        this.movementSpeed = this.defaultSpeed();
        this.lastDist = 0.0D;
        boolean player = Minecraft.getMinecraft().thePlayer == null;
        slow = randomNumber(1000,2000)/100000F;
        this.stage = 0;
        this.mc.timer.timerSpeed = 1.0f;
        super.onEnable();
    }
    public static int randomNumber(int max, int min) {
        return Math.round((float)min + (float)Math.random() * (float)(max - min));
    }
    @Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(),false);
        super.onDisable();
    }

    @Sub
    public void onPre(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
        xDist = (mc.thePlayer.posX - mc.thePlayer.prevPosX);
        zDist = (mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
        this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        if(mode.getValue().equals(SpeedMode.AAC)){
            if(MoveUtil.isMoving()) {
                if(mc.thePlayer.hurtTime <= 0) {
                    if(mc.thePlayer.onGround) {
                        waitForGround = false;
                        if(!firstJump)
                            firstJump = true;
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY = 0.41;
                    }else{
                        if(waitForGround)
                            return;
                        if(mc.thePlayer.isCollidedHorizontally)
                            return;
                        firstJump = false;
                        mc.thePlayer.motionY -= 0.0149;
                    }

                }else{
                    firstJump = true;
                    waitForGround = true;
                }
            }else{
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionX = 0;
            }
            final double speed = MoveUtil.getBaseMoveSpeed();
            mc.thePlayer.motionX = -(Math.sin(MoveUtil.getDirection()) * speed);
            mc.thePlayer.motionZ = Math.cos(MoveUtil.getDirection()) * speed;
        }else if (mode.getValue() == SpeedMode.OtherHypixelHop){
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }
    }
    @Sub
    public void onPacket(EventPacketRecieve e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook&&setback.getValue()) {
            this.setEnabled(false);
        }
    }
    List getCollidingList(double motionY){
        return this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, this.mc.thePlayer.boundingBox.offset(0.0, motionY, 0.0));
    }
    @Sub
    public void onHypixelMove(EventMove em) {
        if (mode.getValue() == SpeedMode.HypixelHop) {
            if (this.stage == 1)
                stage++;
            double arithmo = getMotion();
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                arithmo += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
            }
            if (this.canZoom() && this.stage == 2
                    && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                this.mc.thePlayer.motionY = arithmo;
                em.setY(mc.thePlayer.motionY);
                this.movementSpeed *= 1.674415;
            } else if (this.stage == 3) {
                double diff = (0.666 + movementSpeed * 0.07) * (this.lastDist - this.defaultSpeed());
                this.movementSpeed = this.lastDist - diff;
            } else {
                if (getCollidingList(em.getY()).size() > 0
                        || this.mc.thePlayer.isCollidedVertically && this.stage > 0) {
                    this.stage = MoveUtil.isMoving() ? 1 : 0;
                }
                this.movementSpeed = this.lastDist
                        - this.lastDist / 159.21;
            }
            this.movementSpeed = Math.max(this.movementSpeed, this.defaultSpeed());
            if (Client.instance.getModuleManager().getModuleByClass( AutoStrafe.class).isEnabled()
                    && KillAura.target != null && mc.thePlayer.getDistanceToEntity(KillAura.target) < 2.4F) {
                NiggaStrafe.doStrafeAtSpeed(em, this.defaultSpeed());
                stage++;
            } else {
                setMoveSpeedNoStrafeEdit(movementSpeed, em);
                stage++;
            }

        }else if(this.mode.getValue() == SpeedMode.Test){
            if (this.stage < 1) {
                ++this.stage;
                return;
            }
            boolean inLiquid = mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
            boolean slow = mc.thePlayer.isCollidedHorizontally
                    || inLiquid;
            if(slow)
                collided = true;
            if(mc.thePlayer.onGround)
                stage = 2;
            if (this.stage == 2 && (this.mc.thePlayer.moveForward != 0.0f || this.mc.thePlayer.moveStrafing != 0.0f) && this.mc.thePlayer.onGround) {
                double y = getMotion();
                if (this.mc.thePlayer.isPotionActive(Potion.jump)) {
                    y += (this.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
                }
                collided = slow;
                em.setY(this.mc.thePlayer.motionY = y);
                this.movementSpeed = 0.62 * (1 + 0.119 * MoveUtil.getSpeedEffect());
            }else if(stage == 3) {
                movementSpeed = defaultSpeed() * (1.05 + 0.13 * MoveUtil.getSpeedEffect())
                        + 0.1;
            }else {
                this.movementSpeed = this.lastDist * 0.991;
            }
            this.movementSpeed = Math.max(this.movementSpeed, defaultSpeed());
            if(collided)
                movementSpeed = defaultSpeed();
                setMoveSpeedNoStrafeEdit(movementSpeed, em);
            ++this.stage;
        }else if(this.mode.getValue() == SpeedMode.OtherHypixelHop){
            if (MathUtil.round(mc.thePlayer.posY - ((int) mc.thePlayer.posY), 3) == MathUtil.round(0.138, 3)) {
                mc.thePlayer.motionY -= 1.0;
                em.setY(0.0931);
                //mc.timer.timerSpeed = 2.89f;
                //Artix.INSTANCE.chatMessage(true, "lol");
                mc.thePlayer.posY -= 0.0931;
                this.movementSpeed *= 1.25f;
            }
            //else
            //mc.timer.timerSpeed = 1f;

            mc.timer.timerSpeed = 1f;
            if (this.stage == 2
                    && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                mc.thePlayer.motionY = 0.39936;
                em.setY(0.39936);
                this.movementSpeed *= 2.0999;
            }
            else if (this.stage == 3) {
                double difference = 0.66 * (this.lastDist - MoveUtil.getBaseMoveSpeed());
                this.movementSpeed = this.lastDist - difference;
            }
            else {
                if (mc.theWorld
                        .getCollidingBoundingBoxes(mc.thePlayer,
                                mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0))
                        .size() > 0 || mc.thePlayer.isCollidedVertically) {
                    this.stage = 1;
                }
                this.movementSpeed = this.lastDist - this.lastDist / 159.0;
            }
            this.movementSpeed = Math.max(this.movementSpeed, MoveUtil.getBaseMoveSpeed());
            float forward = mc.thePlayer.movementInput.moveForward;
            float strafe = mc.thePlayer.movementInput.moveStrafe;
            float yaw = mc.thePlayer.rotationYaw;
            if (forward == 0.0f && strafe == 0.0f) {
                em.x = 0.0;
                em.z = 0.0;
            }
            else if (forward != 0.0f) {
                if (strafe >= 1.0f) {
                    yaw += forward > 0.0f ? -45 : 45;
                    strafe = 0.0f;
                }
                else if (strafe <= -1.0f) {
                    yaw += forward > 0.0f ? 45 : -45;
                    strafe = 0.0f;
                }
                if (forward > 0.0f) {
                    forward = 1.0f;
                }
                else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            double mx = Math.cos(Math.toRadians(yaw + 90.0f));
            double mz = Math.sin(Math.toRadians(yaw + 90.0f));
            em.x = (forward * this.movementSpeed * mx + strafe * this.movementSpeed * mz) * 0.99;
            em.z = (forward * this.movementSpeed * mz - strafe * this.movementSpeed * mx) * 0.99;
            if (forward == 0.0f && strafe == 0.0f) {
                em.x = 0.0;
                em.z = 0.0;
            }
            else if (forward != 0.0f) {
                if (strafe >= 1.0f) {
                    yaw += forward > 0.0f ? -45 : 45;
                    strafe = 0.0f;
                }
                else if (strafe <= -1.0f) {
                    yaw += forward > 0.0f ? 45 : -45;
                    strafe = 0.0f;
                }
                if (forward > 0.0f) {
                    forward = 1.0f;
                }
                else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            this.stage++;
        }
    }

    public boolean voidcheck() {
        for (int i = (int)Math.ceil(this.mc.thePlayer.posY); i >= 0; --i) {
            if (this.mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.air) {
                return false;
            }
        }
        return true;
    }

    public float angle(final double x, final double z) {
        return (float)(Math.atan2(z - mc.thePlayer.posZ, x - mc.thePlayer.posX) * 180.0 / 3.141592653589793) - 90.0f;
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

    @Sub
    public void onMovement(EventMove em){
        if(mode.getValue() == SpeedMode.Bhop){
            if (mc.thePlayer.movementInput.moveForward == 0.0f && mc.thePlayer.movementInput.moveStrafe == 0.0f) {
                this.speed = MoveUtil.defaultSpeed();
            }
            if (stage == 1 && mc.thePlayer.isCollidedVertically && (mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                this.speed = 1.309 + MoveUtil.defaultSpeed() - 0.01;
            }
            if (!BlockUtil.isInLiquid() && stage == 2 && mc.thePlayer.isCollidedVertically && MoveUtil.isOnGround(0.01) && (mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
                    em.setY(mc.thePlayer.motionY = 0.41999998688698 + (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1);
                }
                else {
                    em.setY(mc.thePlayer.motionY = 0.41999998688698);
                }
                mc.thePlayer.jump();
                this.speed *= 1.455;
            }
            else if (stage == 3) {
                final double difference = 0.66 * (this.lastDist - MoveUtil.defaultSpeed());
                this.speed = this.lastDist - difference;
            }
            else {
                final List collidingList = mc.theWorld.getCollidingBoundingBoxes((Entity)mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
                if ((collidingList.size() > 0 || mc.thePlayer.isCollidedVertically) && stage > 0) {
                    stage = ((mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f) ? 1 : 0);
                }
                this.speed = this.lastDist - this.lastDist / 159.0;
            }
            this.speed = Math.max(this.speed, MoveUtil.defaultSpeed());
            if (stage > 0) {
                if (BlockUtil.isInLiquid()) {
                    this.speed = 0.1;
                }
                NiggaStrafe.doStrafeAtSpeed(em, this.speed);
            }
            if (mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f) {
                ++stage;
            }
        }else if(mode.getValue() == SpeedMode.HypixelPort){
            if(mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() ||!MoveUtil.isMoving())
                return;
            double gay2 = 0.399921;
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                gay2 += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
            }
            if (this.canZoom() && this.stage == 2&&(mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                if(!BlockUtil.isInLiquid()){
                    this.mc.thePlayer.motionY = gay2;
                    em.setY(mc.thePlayer.motionY);
                }
                this.movementSpeed *=  1.7999;
            } else if (this.stage == 3) {
                double diff = (0.681+(mc.thePlayer.ticksExisted%2)/50) * (this.lastDist - this.defaultSpeed());
                this.movementSpeed = this.lastDist - diff;
            } else {
                if (getCollidingList(em.getY()).size() > 0 || this.mc.thePlayer.isCollidedVertically && this.stage > 0) {
                    this.stage = MoveUtil.isMoving() ? 1 : 0;
                }
                this.movementSpeed = this.lastDist - this.lastDist / ((mc.thePlayer.ticksExisted%2 == 0 ? -0.5 : -1)+159.21);
            }
            if(!mc.thePlayer.onGround)
                em.setY(mc.thePlayer.motionY -= 1D);
            this.movementSpeed = Math.max(this.movementSpeed, defaultSpeed());
            if(isInLiquid())movementSpeed=0.12;
            MoveUtil.setMoveSpeed(em,movementSpeed);
            stage++;
            mc.thePlayer.stepHeight = 0.6F;
        }else if(mode.getValue() == SpeedMode.FastPort){
            if(mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()  ||!MoveUtil.isMoving())
                return;
            double gay2 = 0.399921;
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                gay2 += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
            }
            if (this.canZoom() && this.stage == 2 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                if(!isInLiquid()){
                    this.mc.thePlayer.motionY = gay2;
                    em.setY(mc.thePlayer.motionY);
                }
                this.movementSpeed *=  2.1499;
            } else if (this.stage == 3) {
                double diff = (0.69+(mc.thePlayer.ticksExisted%2)/50) * (this.lastDist - this.defaultSpeed());
                this.movementSpeed = this.lastDist - diff;
            } else {
                if (getCollidingList(em.getY()).size() > 0 || this.mc.thePlayer.isCollidedVertically && this.stage > 0) {
                    this.stage = MoveUtil.isMoving() ? 1 : 0;
                }

                this.movementSpeed = this.lastDist - this.lastDist / ((mc.thePlayer.ticksExisted%2 == 0 ? -0.5 : -1)+159.21);

            }
            if(!mc.thePlayer.onGround)
                em.setY(mc.thePlayer.motionY -= 2D);
            this.movementSpeed = Math.max(this.movementSpeed, defaultSpeed());
            if(isInLiquid())movementSpeed=0.12;
            MoveUtil.setMoveSpeed(em,movementSpeed);
            stage++;
            mc.thePlayer.stepHeight = 0.6F;
        }
    }
    private double defaultSpeed() {
        double baseSpeed = 0.2873D;
        if(Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
        }

        return baseSpeed;
    }

    private boolean canZoom() {
        if (MoveUtil.isMoving() && this.mc.thePlayer.onGround) {
            return true;
        }
        return false;
    }

    static enum SpeedMode {
        OtherHypixelHop,
        HypixelPort,
        FastPort,
        Bhop,
        HypixelHop,
        Test,
        AAC;
    }

    static enum Motions{
        Basic,
        High,
        Low,
        Random,
        High2,
    }

    public double getMotion(){
        Enum curMode = MotionMode.getValue();
        if(curMode.equals(Motions.Basic)) {
            return 0.405412D;
        } else if(curMode.equals(Motions.High)) {
            return 0.408976666;
        } else if(curMode.equals(Motions.Low)) {
            return 0.4001754672;
        } else if(curMode.equals(Motions.Random)) {
            return 0.405412D + MathUtil.randomDouble(-20,20) / 10000;
        } else if(curMode.equals(Motions.High2)) {
            return 0.41999675;
        }
        return 0.408666666d;
    }

    public void setMoveSpeed(final double speed) {
        double forward = (double)this.mc.thePlayer.movementInput.moveForward;
        double strafe = (double)this.mc.thePlayer.movementInput.moveStrafe;
        float yaw;
        {
            yaw = mc.thePlayer.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                mc.thePlayer.motionX = (0.0);
                mc.thePlayer.motionZ = (0.0);
            }
            else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    }
                    else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    }
                    else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                mc.thePlayer.motionX = ((forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0f))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0f)))));
                mc.thePlayer.motionZ = ((forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0f))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0f)))));
            }
        }
    }

    public void setMoveSpeedNoStrafeEdit(final double speed,EventMove eventMove) {
        double forward = (double)this.mc.thePlayer.movementInput.moveForward;
        double strafe = (double)this.mc.thePlayer.movementInput.moveStrafe;
        float yaw;
        {
            yaw = mc.thePlayer.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                mc.thePlayer.motionX = (0.0);
                mc.thePlayer.motionZ = (0.0);
            }
            else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    }
                    else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    }
                    else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                eventMove.setX((forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0f))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0f)))));
                eventMove.setZ((forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0f))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0f)))));
            }
        }
    }

    public boolean isMoving2() {
        return Minecraft.getMinecraft().thePlayer.moveForward != 0.0f || Minecraft.getMinecraft().thePlayer.moveStrafing != 0.0f;
    }

    public boolean isOnGround(double height) {
        if (!this.mc.theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0, - height, 0.0)).isEmpty()) {
            return true;
        }
        return false;
    }

    public int getJumpEffect() {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            return Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        }
        return 0;
    }

    public int getSpeedEffect() {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            return Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }

    public boolean isInLiquid() {
        if (Minecraft.getMinecraft().thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        int y = (int)Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double((double)Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                Block block = this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block == null || block.getMaterial() == Material.air) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                inLiquid = true;
            }
        }
        return inLiquid;
    }

}

