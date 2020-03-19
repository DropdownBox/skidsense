package me.skidsense.module.collection.move;

import java.awt.Color;

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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class Flight
extends Mod {
    private static final EntityPlayerSP MoveUtil = null;
	public Mode mode = new Mode("Mode", "Mode", FlightMode.values(), FlightMode.Guardian);
    private Option<Boolean> Stop = new Option("Stop", "Stop", Boolean.valueOf(true));
    private Option<Boolean> UHC = new Option("UHC", "UHC", Boolean.valueOf(true));
    int counter, level;
    double moveSpeed, lastDist;   
    boolean b2,FirstBoost;
    public Flight() {
        super("Flight", new String[]{"fly", "angel"}, ModuleType.Move);
        this.setColor(new Color(158, 114, 243).getRGB());
        //this.addValues(this.mode,UHC,Stop);
    }

	public void damagePlayerNew() {
		if (mc.thePlayer.onGround) {
			mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
					mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			for (int index = 0; index <= (UHC.getValue() ? 9 : 7); ++index) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY + 0.410781087633169896, mc.thePlayer.posZ, false));
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX, mc.thePlayer.posY + 0.034211255072711402, mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY + 0.014555072702198913, mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			}
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
					    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
		}
	}

    double posY;

    @Override
    public void onEnable() {
        if ( this.mode.getValue() == FlightMode.Damage) {
        	damagePlayerNew();
        	if(!(Stop.getValue().booleanValue())){
                b2 = true;
                //mc.thePlayer.motionY = 0.4067755549975;
            }else{
                b2=false;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
		level = 1;
		moveSpeed = 0.1D;
		b2 = false;
		lastDist = 0.0D;
    }
    private boolean canZoom() {
    	//FIXME
	    return /*mc.thePlayer.moving() &&*/ mc.thePlayer.onGround;
    }
    @Sub
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());

        if (this.mode.getValue() == FlightMode.Guardian) {
            mc.timer.timerSpeed = 1.7f;
            if (!mc.thePlayer.onGround && mc.thePlayer.ticksExisted % 2 == 0) {
                mc.thePlayer.motionY = 0.04;
            }
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.thePlayer.motionY += 1.0;
            }
            if (mc.gameSettings.keyBindSneak.pressed) {
                mc.thePlayer.motionY -= 1.0;
            }
        } else if (this.mode.getValue() == FlightMode.Motion) {
            mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 1.0 : (mc.thePlayer.movementInput.sneak ? -1.0 : 0.0);
            // FIXME
            //if (mc.thePlayer.moving()) {
            	// FIXME
                //mc.thePlayer.setSpeed(3.0);
            //} else {
            	// FIXME
                //mc.thePlayer.setSpeed(0.0);
            //}
        } else if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {

            Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
            if(mc.thePlayer.hurtResistantTime>=19
                    &&
                    !b2
                    &&
                    this.mode.getValue() == FlightMode.Damage
                    &&
                    (Stop.getValue().booleanValue()))
            {
                b2=true;
                //mc.thePlayer.motionY = 0.40674447999965f;
            }
            if(!b2&&
            this.mode.getValue() == FlightMode.Damage
                    &&
                    (Stop.getValue().booleanValue())){
                mc.thePlayer.motionZ *= 0.0;
                mc.thePlayer.motionX *= 0.0;
                mc.thePlayer.onGround = false;
            }
   			++counter;
   			if(counter%2==0)posY++;
   			if (Minecraft.getMinecraft().gameSettings.keyBindJump.pressed)
   				Minecraft.getMinecraft().thePlayer.motionY += 0.5f;
   			if (Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed)
   				Minecraft.getMinecraft().thePlayer.motionY -= 0.5f;
   			e.setY(mc.thePlayer.posY+posY/10000000);
        }
    }
    public float getRealWalkYaw() {
        float curYaw=mc.thePlayer.rotationYaw,realYaw;
        //moveStrafing左正右负
        //yaw左负右正 向左- 向右+
        //yaw 360°一整圈
        boolean keyFor = mc.gameSettings.keyBindForward.pressed;
        boolean keyBack=mc.gameSettings.keyBindBack.pressed;
        boolean keyLeft = mc.gameSettings.keyBindLeft.pressed;
        boolean keyRight=mc.gameSettings.keyBindRight.pressed;
        if(keyFor) {
            if(keyLeft) {
                realYaw = curYaw -45;
            }else if(keyRight) {
                realYaw = curYaw +45;
            }else {
                realYaw = curYaw;
            }
        }else if(keyBack){
            if(keyLeft) {
                realYaw = curYaw -135;
            }else if(keyRight) {
                realYaw = curYaw +135;
            }else {
                realYaw = curYaw-180;
            }
        }else {
            if(keyLeft) {
                realYaw = curYaw -90;
            }else if(keyRight) {
                realYaw = curYaw +90;
            }else {
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
        mc.thePlayer.setPosition(mc.thePlayer.posX - (Math.sin(playerYaw) * offset), mc.thePlayer.posY+0.0000000000001, mc.thePlayer.posZ + (Math.cos(playerYaw) * offset));
    }
    @Sub
    public void onPost(EventPostUpdate e) {
    	if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {
 			double xDist = Minecraft.getMinecraft().thePlayer.posX
 					- Minecraft.getMinecraft().thePlayer.prevPosX;
 			double zDist = Minecraft.getMinecraft().thePlayer.posZ
 					- Minecraft.getMinecraft().thePlayer.prevPosZ;
 			lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    	}
    }
    private int stage;
    private double distance;

    @Sub
    private void onMove(EventMove e) {
        if (mode.getValue() == FlightMode.Hypixel || mode.getValue() == FlightMode.Damage) {
            if (b2) {
                final float forward = mc.thePlayer.movementInput.moveForward;
                final float strafe = mc.thePlayer.movementInput.moveStrafe;
                final float yaw = mc.thePlayer.rotationYaw;
                final double mx = Math.cos(Math.toRadians(yaw + 90.0f));
                final double mz = Math.sin(Math.toRadians(yaw + 90.0f));
                if (forward == 0.0f && strafe == 0.0f) {
                    e.x = 0.0;
                    e.z = 0.0;
                }
                if (b2) {
                    Label_0393: {
                        Label_0137: {
                            if (level == 1) {

                                if (mc.thePlayer.moveForward == 0.0f) {

                                    if (mc.thePlayer.moveStrafing == 0.0f) {
                                        break Label_0137;
                                    }
                                }
                                level = 2;
                                final double boost = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.56 : 2.034;
                                moveSpeed = boost * MathUtil.getBaseMovementSpeed();
                                break Label_0393;
                            }
                        }
                        if (level == 2) {
                            level = 3;
                            moveSpeed *= 2.1399;
                        } else if (level == 3) {
                            level = 4;
                            final double difference = ((mc.thePlayer.ticksExisted % 2 == 0) ? 0.0103 : 0.0123)
                                    * (lastDist - MathUtil.getBaseMovementSpeed());
                            moveSpeed = lastDist - difference;
                        } else {

                            final WorldClient theWorld = mc.theWorld;

                            final EntityPlayerSP thePlayer = mc.thePlayer;

                            final AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox();
                            final double n2 = 0.0;

                            Label_0291: {
                                if (theWorld.getCollidingBoundingBoxes((Entity) thePlayer,
                                        boundingBox.offset(n2, mc.thePlayer.motionY, 0.0)).size() <= 0) {

                                    if (!mc.thePlayer.isCollidedVertically) {
                                        break Label_0291;
                                    }
                                }
                                level = 1;
                            }
                            moveSpeed = lastDist - lastDist / 159.0;
                        }
                    }
                    final double moveSpeed = (mode.getValue() == FlightMode.Damage)
                            ? Math.max(this.moveSpeed, MathUtil.getBaseMovementSpeed())
                            : MathUtil.getBaseMovementSpeed();
                    this.moveSpeed = moveSpeed;
                    if (strafe == 0.0f) {
                        e.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
                        e.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
                    } else if (strafe != 0.0f) {
                        me.skidsense.util.MoveUtil.setMotion(moveSpeed);
                    }
                    if (forward == 0.0f && strafe == 0.0f) {
                        e.x = 0.0;
                        e.z = 0.0;
                    }
                }
            }
        }
    }

    double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public enum FlightMode {
        Motion,
        Guardian,
        Hypixel,
        Damage;
    }
}
