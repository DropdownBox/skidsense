package me.skidsense.module.collection.move;

import java.awt.Color;
import java.util.Random;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class Flight
extends Module {
    private static final EntityPlayerSP MoveUtil = null;
	public Mode mode = new Mode("Mode", "Mode", (Enum[])FlightMode.values(), (Enum)FlightMode.Guardian);
    private Option<Boolean> Stop = new Option("Stop", "Stop", Boolean.valueOf(true));
    private Option<Boolean> UHC = new Option("UHC", "UHC", Boolean.valueOf(true));
    int counter, level;
    double moveSpeed, lastDist;   
    boolean b2,FirstBoost;
    public Flight() {
        super("Flight", new String[]{"fly", "angel"}, ModuleType.Move);
        this.setColor(new Color(158, 114, 243).getRGB());
        this.addValues(this.mode,UHC,Stop);
    }

	public void damagePlayerNew() {
		if (mc.thePlayer.onGround) {
			EntityPlayerSP player = mc.thePlayer;
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
					mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
			for (int index = 0; index <= (UHC.getValue() ? 9 : 7); ++index) {

				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY + 0.410791087633169896, mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
						mc.thePlayer.posX, mc.thePlayer.posY + 0.015555072702198913, mc.thePlayer.posZ, false));
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
                mc.thePlayer.motionY = 0.4067755549975;
            }else{
                b2=false;
            }
        }
    }

    @Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
		level = 1;
		moveSpeed = 0.1D;
		b2 = false;
		lastDist = 0.0D;
    }
    private boolean canZoom() {
        if (this.mc.thePlayer.moving() && this.mc.thePlayer.onGround) {
            return true;
        }
        return false;
    }
    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());

        if (this.mode.getValue() == FlightMode.Guardian) {
            this.mc.timer.timerSpeed = 1.7f;
            if (!this.mc.thePlayer.onGround && this.mc.thePlayer.ticksExisted % 2 == 0) {
                this.mc.thePlayer.motionY = 0.04;
            }
            if (this.mc.gameSettings.keyBindJump.pressed) {
                this.mc.thePlayer.motionY += 1.0;
            }
            if (this.mc.gameSettings.keyBindSneak.pressed) {
                this.mc.thePlayer.motionY -= 1.0;
            }
        } else if (this.mode.getValue() == FlightMode.Motion) {
            this.mc.thePlayer.motionY = this.mc.thePlayer.movementInput.jump ? 1.0 : (this.mc.thePlayer.movementInput.sneak ? -1.0 : 0.0);
            if (this.mc.thePlayer.moving()) {
                this.mc.thePlayer.setSpeed(3.0);
            } else {  
                this.mc.thePlayer.setSpeed(0.0);
            }
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
                this.mc.thePlayer.motionY = 0.40674447999965f;
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
        } else if (this.mode.getValue() == FlightMode.OldLongJumpFly && this.mc.thePlayer.moving() && !Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled()) {
            if (this.mc.thePlayer.isAirBorne) {
                if (this.mc.thePlayer.ticksExisted % 12 == 0 && this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) {
                    MoveUtil.setSpeed(6.5);
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-9, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                    this.mc.thePlayer.motionY = 0.455;
                } else {
                	MoveUtil.setSpeed((float)Math.sqrt(this.mc.thePlayer.motionX * this.mc.thePlayer.motionX + this.mc.thePlayer.motionZ * this.mc.thePlayer.motionZ));
                }
            } else {
                this.mc.thePlayer.motionX = 0.0;
                this.mc.thePlayer.motionZ = 0.0;
            }
            if (this.mc.thePlayer.movementInput.jump) {
                this.mc.thePlayer.motionY = 0.85;
            } else if (this.mc.thePlayer.movementInput.sneak) {
                this.mc.thePlayer.motionY = -0.85;
            }
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
    @EventHandler
    public void onPost(EventPostUpdate e) {
    	if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {
 			double xDist = Minecraft.getMinecraft().thePlayer.posX
 					- Minecraft.getMinecraft().thePlayer.prevPosX;
 			double zDist = Minecraft.getMinecraft().thePlayer.posZ
 					- Minecraft.getMinecraft().thePlayer.prevPosZ;
 			lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    	}
    }int stage;
    private double distance;
    @EventHandler
    private void onMove(EventMove e) {
		if (this.mode.getValue() == FlightMode.Hypixel || this.mode.getValue() == FlightMode.Damage) {
			if (b2) {
			    if(moveSpeed==7D){
                    if(!FirstBoost)moveSpeed =0.1D;
                    else FirstBoost=false;
                }else{
                    if (level != 1 || Minecraft.getMinecraft().thePlayer.moveForward == 0.0F
                            && Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0F) {
                        if (level == 2) {
                            level = 3;
                            moveSpeed *= 2.1499999D;
                        } else if (level == 3) {
                            level = 4;
                            double difference = (0.011D)
                                    * (lastDist - MathUtil.getBaseMovementSpeed());
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
                        double boost = Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed) ? 1.7
                                : 2.1;
                        moveSpeed = boost * MathUtil.getBaseMovementSpeed();
                    }
                }

				moveSpeed = this.mode.getValue() == FlightMode.Damage ? Math.max(moveSpeed, MathUtil.getBaseMovementSpeed()) : MathUtil.getBaseMovementSpeed();
				mc.thePlayer.setMoveSpeed(e,moveSpeed);

			}
		}
    }

    double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }

    public static enum FlightMode {
        Motion,
        Guardian,
        Hypixel,
        Damage,
        OldLongJumpFly;
    }
}
