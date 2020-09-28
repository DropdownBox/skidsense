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
import me.skidsense.module.collection.player.Scaffold;
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
import net.optifine.util.MathUtils;

import java.awt.*;
import java.util.List;


public class Speed2 extends Mod {
    private int stage = 1;
    private double moveSpeed, lastDist;
    public static boolean strafeDirection;
    private int voidTicks;
    
    public Speed2() {
        super("Bunny Hop", new String[]{"BunnyHop"}, ModuleType.Move);
        this.setColor(new Color(99, 248, 91).getRGB());
    }
    
    @Override
    public void onEnable() {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        lastDist = 0;
        moveSpeed = 0;
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().timer.timerSpeed = 1f;
        super.onDisable();
    }
    
    @Sub
    public void onUpdate(EventPreUpdate event) {

    }
    
    @Sub
    public final void onMove(EventMove event) {
    	if (!Minecraft.getMinecraft().thePlayer.isCollidedHorizontally) {
            if (MathUtils.roundToPlace(Minecraft.getMinecraft().thePlayer.posY - (int) Minecraft.getMinecraft().thePlayer.posY, 3) == MathUtils.roundToPlace(0.4, 3)) {
                event.setY((Minecraft.getMinecraft().thePlayer.motionY = 0.2));
            }
            if (MathUtils.roundToPlace(Minecraft.getMinecraft().thePlayer.posY - (int) Minecraft.getMinecraft().thePlayer.posY, 3) == MathUtils.roundToPlace(0.6, 3)) {
                event.setY((Minecraft.getMinecraft().thePlayer.motionY = -0.2));
            }
            if (MathUtils.roundToPlace(Minecraft.getMinecraft().thePlayer.posY - (int) Minecraft.getMinecraft().thePlayer.posY, 3) == MathUtils.roundToPlace(0.4, 3)) {
            	event.setY((Minecraft.getMinecraft().thePlayer.motionY = -0.2));
            }
        }
        switch (stage) {
            case 0:
                ++stage;
                lastDist = 0.0D;
                break;
            case 2:
                lastDist = 0.0D;
                float motionY = 0.4001f;
                if ((Minecraft.getMinecraft().thePlayer.moveForward != 0.0F || Minecraft.getMinecraft().thePlayer.moveStrafing != 0.0F) && Minecraft.getMinecraft().thePlayer.onGround) {
                    if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump))
                        motionY += ((Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.099F);
                    event.setY(Minecraft.getMinecraft().thePlayer.motionY = motionY);
                    if (!Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
                        moveSpeed *= Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed) ? (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump) ? 1.95F : 2.05F) : 1.895F;
                    } else {
                        moveSpeed *= 1.4F;
                    }
                } else if ((Minecraft.getMinecraft().thePlayer.moveForward != 0.0F || Minecraft.getMinecraft().thePlayer.moveStrafing != 0.0F)) {

                }
                break;
            case 3:
                double boost = Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled() ? 0.725 : (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed) ? (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump) ? 0.915f : 0.725f) :
                        0.71625f);
                moveSpeed = lastDist - boost * (lastDist - getBaseMoveSpeed());
                break;
            default:
                ++stage;
                if ((Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0D, Minecraft.getMinecraft().thePlayer.motionY, 0.0D)).size() > 0 || Minecraft.getMinecraft().thePlayer.isCollidedVertically) && stage > 0) {
                    stage = Minecraft.getMinecraft().thePlayer.moveForward == 0.0F && Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0F ? 0 : 1;
                }
                moveSpeed = lastDist - lastDist / 159D;
                break;
        }
        moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
        setMoveSpeed(event, moveSpeed);
        ++stage;
    }
    
    @Sub
    public void onPacket(EventPacketRecieve event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            lastDist = 0;
        }
    }
    
    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }
    
    public float getRotationFromPosition(final double x, final double z) {
        final double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        final double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        return (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0f;
    }
    
    public boolean inVoid() {
        for (int i = (int) Math.ceil(Minecraft.getMinecraft().thePlayer.posY); i >= 0; i--) {
            if (Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, i, Minecraft.getMinecraft().thePlayer.posZ)).getBlock() != Blocks.air) {
                return false;
            }
        }
        return true;
    }
    
    private void setMoveSpeed(final EventMove event, final double speed) {
        voidTicks++;
        if (KillAura.target != null) {
            if (inVoid() && voidTicks > 4) {
                voidTicks = 0;
                strafeDirection = !strafeDirection;
            }
        }
        AutoStrafe target_strafemod = (AutoStrafe) Client.getModuleManager().getModuleByClass(AutoStrafe.class);
        boolean shouldStrafe = Client.getModuleManager().getModuleByClass(AutoStrafe.class).isEnabled() && target_strafemod.indexPos != null && target_strafemod.target != null && !(!Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && target_strafemod.OnSpace.getValue());
        double forward = shouldStrafe ? ((Math.abs(Minecraft.getMinecraft().thePlayer.movementInput.moveForward) > 0 || Math.abs(Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe) > 0) ? 1 : 0) : Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = shouldStrafe ? 0 : Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = shouldStrafe ? getRotationFromPosition(target_strafemod.indexPos.xCoord, target_strafemod.indexPos.zCoord) : Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
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
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }
}

