package me.skidsense.module.collection.move;

import java.awt.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.BlockUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;


public class Speed
        extends Module {
    private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])SpeedMode.values(), (Enum)SpeedMode.Hypixel);
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

    public Speed() {
        super("Speed", new String[]{"zoom"}, ModuleType.Move);
        this.setColor(new Color(99, 248, 91).getRGB());
        this.addValues(this.mode,setback);
    }
    @Override
    public void onEnable() {
        firstJump = true;
        mc.timer.timerSpeed=1;
        this.movementSpeed = this.defaultSpeed();
        this.lastDist = 0.0D;
        boolean player = Minecraft.getMinecraft().thePlayer == null;
        slow = randomNumber(1000,2000)/100000;
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

    @EventHandler
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
        }
    }
    @EventHandler
    public void onPacket(EventPacketRecieve e) {
        if(e.getPacket() instanceof S08PacketPlayerPosLook&&setback.getValue()) {
            this.setEnabled(false);
        }
    }
    List getCollidingList(double motionY){
        return this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, this.mc.thePlayer.boundingBox.offset(0.0, motionY, 0.0));
    }
    @EventHandler
    public void onMove(EventMove em){
        if(mode.getValue() == SpeedMode.Hypixel){
            if (Speed.mc.thePlayer.movementInput.moveForward == 0.0f && Speed.mc.thePlayer.movementInput.moveStrafe == 0.0f) {
                this.speed = MoveUtil.defaultSpeed();
            }
            if (Speed.stage == 1 && Speed.mc.thePlayer.isCollidedVertically && (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                this.speed = 1.344521 + MoveUtil.defaultSpeed() - 0.01;
            }
            if (!BlockUtil.isInLiquid() && Speed.stage == 2 && Speed.mc.thePlayer.isCollidedVertically && MoveUtil.isOnGround(0.01) && (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
                    em.setY(Speed.mc.thePlayer.motionY = 0.40999998688698 + (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1);
                }
                else {
                    em.setY(Speed.mc.thePlayer.motionY = 0.40999998688698);
                }
                Speed.mc.thePlayer.jump();
                this.speed *= 1.34698;
            }
            else if (Speed.stage == 3) {
                final double difference = 0.52 * (this.lastDist - MoveUtil.defaultSpeed());
                this.speed = this.lastDist - difference;
            }
            else {
                final List collidingList = Speed.mc.theWorld.getCollidingBoundingBoxes((Entity)Speed.mc.thePlayer, Speed.mc.thePlayer.boundingBox.offset(0.0, Speed.mc.thePlayer.motionY, 0.0));
                if ((collidingList.size() > 0 || Speed.mc.thePlayer.isCollidedVertically) && Speed.stage > 0) {
                    Speed.stage = ((Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f) ? 1 : 0);
                }
                this.speed = this.lastDist - this.lastDist / 159.0;
            }
            this.speed = Math.max(this.speed, MoveUtil.defaultSpeed());
            if (Speed.stage > 0) {
                if (BlockUtil.isInLiquid()) {
                    this.speed = 0.1;
                }
                this.setMotion(em, this.speed);
            }
            if (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f) {
                ++Speed.stage;
            }
        }
    }
    @EventHandler
    public void onMovement(EventMove em){
        if(mode.getValue() == SpeedMode.Bhop){
            if (Speed.mc.thePlayer.movementInput.moveForward == 0.0f && Speed.mc.thePlayer.movementInput.moveStrafe == 0.0f) {
                this.speed = MoveUtil.defaultSpeed();
            }
            if (Speed.stage == 1 && Speed.mc.thePlayer.isCollidedVertically && (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                this.speed = 1.309 + MoveUtil.defaultSpeed() - 0.01;
            }
            if (!BlockUtil.isInLiquid() && Speed.stage == 2 && Speed.mc.thePlayer.isCollidedVertically && MoveUtil.isOnGround(0.01) && (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
                    em.setY(Speed.mc.thePlayer.motionY = 0.41999998688698 + (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1);
                }
                else {
                    em.setY(Speed.mc.thePlayer.motionY = 0.41999998688698);
                }
                Speed.mc.thePlayer.jump();
                this.speed *= 1.455;
            }
            else if (Speed.stage == 3) {
                final double difference = 0.66 * (this.lastDist - MoveUtil.defaultSpeed());
                this.speed = this.lastDist - difference;
            }
            else {
                final List collidingList = Speed.mc.theWorld.getCollidingBoundingBoxes((Entity)Speed.mc.thePlayer, Speed.mc.thePlayer.boundingBox.offset(0.0, Speed.mc.thePlayer.motionY, 0.0));
                if ((collidingList.size() > 0 || Speed.mc.thePlayer.isCollidedVertically) && Speed.stage > 0) {
                    Speed.stage = ((Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f) ? 1 : 0);
                }
                this.speed = this.lastDist - this.lastDist / 159.0;
            }
            this.speed = Math.max(this.speed, MoveUtil.defaultSpeed());
            if (Speed.stage > 0) {
                if (BlockUtil.isInLiquid()) {
                    this.speed = 0.1;
                }
                this.setMotion(em, this.speed);
            }
            if (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f) {
                ++Speed.stage;
            }
        }else if(mode.getValue() == SpeedMode.HypixelPort){
            if(mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() ||!mc.thePlayer.moving())
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
                    this.stage = this.mc.thePlayer.moving() ? 1 : 0;
                }
                this.movementSpeed = this.lastDist - this.lastDist / ((mc.thePlayer.ticksExisted%2 == 0 ? -0.5 : -1)+159.21);
            }
            if(!mc.thePlayer.onGround)
                em.setY(mc.thePlayer.motionY -= 1D);
            this.movementSpeed = Math.max(this.movementSpeed, defaultSpeed());
            if(isInLiquid())movementSpeed=0.12;
            mc.thePlayer.setMoveSpeed(em,movementSpeed);
            stage++;
            mc.thePlayer.stepHeight = 0.6F;
        }else if(mode.getValue() == SpeedMode.FastPort){
            if(mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || !mc.thePlayer.moving())
                return;
            double gay2 = 0.399921;
            if (mc.thePlayer.isPotionActive(Potion.jump)) {
                gay2 += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f);
            }
            if (this.canZoom() && this.stage == 2&&(mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
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
                    this.stage = this.mc.thePlayer.moving() ? 1 : 0;
                }

                this.movementSpeed = this.lastDist - this.lastDist / ((mc.thePlayer.ticksExisted%2 == 0 ? -0.5 : -1)+159.21);

            }
            if(!mc.thePlayer.onGround)
                em.setY(mc.thePlayer.motionY -= 2D);
            this.movementSpeed = Math.max(this.movementSpeed, defaultSpeed());
            if(isInLiquid())movementSpeed=0.12;
            mc.thePlayer.setMoveSpeed(em,movementSpeed);
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
        if (this.mc.thePlayer.moving() && this.mc.thePlayer.onGround) {
            return true;
        }
        return false;
    }

    static enum SpeedMode {
        HypixelPort,
        FastPort,
        Bhop,
        Hypixel,
        AAC;
    }

    private void setMotion(EventMove em, double speed) {
        double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            em.setX(0.0);
            em.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -40 : 40);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 40 : -40);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
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

