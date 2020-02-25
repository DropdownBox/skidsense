package me.skidsense.module.collection.move;

import net.minecraft.client.*;
import java.awt.*;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.ModuleManager;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.move.speed.HypixelCN;
import me.skidsense.util.BlockUtil;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.network.play.server.*;
import net.minecraft.entity.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.block.material.*;
import net.minecraft.block.*;

public class Speed extends Module
{
    public static Mode<Enum> mode;
    public static int stage;
    double moveSpeed;
	private int counter=0;
    public boolean shouldslow;
    private double speed;
    int steps;
    private double lastDist;
    public static int aacCount;
    boolean collided;
    boolean lessSlow;
    double less;
    double stair;
    public double slow;
    public HypixelCN gethypixelcn = new HypixelCN();
    TimerUtil timer;
    TimerUtil lastCheck;
    
    static {
        Speed.mode = (Mode<Enum>)new Mode("Mode", "Mode", (Enum[])SpeedMode.values(), (Enum)SpeedMode.Hypixel);
    }
    
    public Speed() {
        super("Speed", new String[] { "zoom" }, ModuleType.Move);
        this.shouldslow = false;
        this.collided = false;
        this.timer = new TimerUtil();
        this.lastCheck = new TimerUtil();
        this.setColor(new Color(99, 248, 91).getRGB());
        this.addValues(new Value[] { (Value)Speed.mode });
    }
    
    @EventHandler
    public void onPacket(final EventPacketRecieve event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            final S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
            if (this.lastCheck.delay(300.0f)) {
                packet.yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
                packet.pitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
            }
            this.lastCheck.reset();
        }
    }
    
    @EventHandler
    public void onPreUpdate(final EventPreUpdate event) {
		if (mode.getValue() == SpeedMode.HypixelTimer) {
			gethypixelcn.onPre(event);
        }
    }
    
    @EventHandler
    public void onMove(final EventMove event) {
        this.setSuffix(Speed.mode.getValue());
        if (Speed.mode.getValue() == SpeedMode.Bhop) {
        	  if (Speed.mc.thePlayer.movementInput.moveForward == 0.0f && Speed.mc.thePlayer.movementInput.moveStrafe == 0.0f) {
                  this.speed = defaultSpeed1();
              }
              if (Speed.stage == 1 && Speed.mc.thePlayer.isCollidedVertically && (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                  this.speed = 1.35 + defaultSpeed1() - 0.01;
              }
              if (!BlockUtil.isInLiquid() && Speed.stage == 2 && Speed.mc.thePlayer.isCollidedVertically && MoveUtil.isOnGround(0.01) && (Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
                  if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
                      event.setY(Speed.mc.thePlayer.motionY = 0.41999998688698 + (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1);
                  }
                  else {
                      event.setY(Speed.mc.thePlayer.motionY = 0.41999998688698);
                  }
                  Speed.mc.thePlayer.jump();
                  this.speed *= 1.533;
              }
              else if (Speed.stage == 3) {
                  final double difference = 0.52 * (this.lastDist - defaultSpeed1());
                  this.speed = this.lastDist - difference;
              }
              else {
              
                  if (Speed.stage >= 0 || this.collided) {
                      Speed.stage = ((Speed.mc.thePlayer.movementInput.moveForward != 0.0f || Speed.mc.thePlayer.movementInput.moveStrafe != 0.0f) ? 1 : 0);
                  }
                  this.speed = this.lastDist - this.lastDist / 159.0;
              }
              this.speed = Math.max(this.speed, defaultSpeed1());
              if (Speed.stage > 0) {
                  if (BlockUtil.isInLiquid()) {
                      this.speed = 0.1;
                  }
                  this.setMotion(event, this.speed);
              }
              if (Minecraft.getMinecraft().thePlayer.moveForward != 0.0f || Minecraft.getMinecraft().thePlayer.moveStrafing != 0.0f) {
                  ++Speed.stage;
              }
          }
        else if (Speed.mode.getValue() == SpeedMode.Hypixel) {
        	if (mc.thePlayer.isCollidedHorizontally) {
				this.collided = true;
			}
			if (this.collided) {
				mc.timer.timerSpeed = 1.0F;
				stage = -1;
			}
			if (this.stair > 0.0) {
				this.stair -= 0.25;
			}
			this.less -= this.less > 1.0 ? 0.12 : 0.11;
			if (this.less < 0.0) {
				this.less = 0.0;
			}
			if (!this.isInLiquid() && isOnGround(0.01) && MoveUtil.isMoving()) {
				this.collided = mc.thePlayer.isCollidedHorizontally;
				if (stage >= 0 || this.collided) {
					stage = 0;
					double motY = 0.40005114514 + (double) MoveUtil.getJumpEffect() * 0.1;
					if (this.stair == 0.0) {
						this.slow = (double) MathUtil.randomDouble(-10000, 0) / 1.0E8;
						mc.thePlayer.jump();
						mc.thePlayer.motionY = motY;
						EventMove.setY(mc.thePlayer.motionY);
					}
					this.less += 1.0;
					this.lessSlow = this.less > 1.0 && !this.lessSlow;
					if (this.less > 1.12) {
						this.less = 1.12;
					}
				}
			}
			this.speed = this.getHypixelSpeed(stage) + 0.0331;
			this.speed *= 0.91;
			this.speed += this.slow;
			if (this.stair > 0.0) {
				this.speed *= 0.7 - (double) MoveUtil.getSpeedEffect() * 0.1;
			}
			if (stage < 0) {
				this.speed = MoveUtil.defaultSpeed();
			}
			if (this.lessSlow) {
				this.speed *= 0.96;
			}
			if (this.lessSlow) {
				this.speed *= 0.95;
			}
			if (this.isInLiquid()) {
				this.speed = 0.12;
			}
			if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
				this.setMotion(event, this.speed);
				++stage;
			}
        }      
        else if (Speed.mode.getValue() == SpeedMode.NCPHop) {
            final Minecraft mc = Speed.mc;
            if (Minecraft.getMinecraft().thePlayer.isCollidedHorizontally) {
                this.collided = true;
            }
            if (this.collided) {
                final net.minecraft.util.Timer timer3 = Speed.mc.timer;
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
            if (!BlockUtil.isInLiquid() && MoveUtil.isOnGround(0.01) && this.isMoving2()) {
                final Minecraft mc2 = Speed.mc;
                this.collided = Minecraft.getMinecraft().thePlayer.isCollidedHorizontally;
                if (Speed.stage >= 0 || this.collided) {
                    Speed.stage = 0;
                    final double motY = 0.407 + MoveUtil.getJumpEffect() * 0.1;
                    if (this.stair == 0.0) {
                        final Minecraft mc3 = Speed.mc;
                        Minecraft.getMinecraft().thePlayer.jump();
                        final Minecraft mc4 = Speed.mc;
                        EventMove.setY(Minecraft.getMinecraft().thePlayer.motionY = motY);
                    }
                    ++this.less;
                    if (this.less > 1.0 && !this.lessSlow) {
                        this.lessSlow = true;
                    }
                    else {
                        this.lessSlow = false;
                    }
                    if (this.less > 1.12) {
                        this.less = 1.12;
                    }
                }
            }
            this.speed = this.getHypixelSpeed(Speed.stage) + 0.0331;
            this.speed *= 0.800000011920929;
            if (this.stair > 0.0) {
                this.speed *= 0.7 - MoveUtil.getSpeedEffect() * 0.1;
            }
            if (Speed.stage < 0) {
                this.speed = MoveUtil.defaultSpeed();
            }
            if (this.lessSlow) {
                this.speed *= 0.95;
            }
            if (BlockUtil.isInLiquid()) {
                this.speed = 0.12;
            }
            final Minecraft mc5 = Speed.mc;
            if (Minecraft.getMinecraft().thePlayer.moveForward == 0.0f) {
                final Minecraft mc6 = Speed.mc;
                if (Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0f) {
                    return;
                }
            }
            this.setMotion(event, this.speed);
            ++Speed.stage;
        }else if (Speed.mode.getValue() == SpeedMode.HypixelTimer) {
        	gethypixelcn.onMove(event);
		}
	}   
    
    private double getHypixelSpeed(final int stage) {
        double value = MoveUtil.defaultSpeed() + 0.028 * MoveUtil.getSpeedEffect() + MoveUtil.getSpeedEffect() / 15.0;
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
            final net.minecraft.util.Timer timer = Speed.mc.timer;
            final float timerSpeed = mc.timer.timerSpeed;
            value = firstvalue;
        }
        else if (stage >= 2) {
            final net.minecraft.util.Timer timer2 = Speed.mc.timer;
            final float timerSpeed2 = mc.timer.timerSpeed;
            value = firstvalue - decr;
        }
        if (this.shouldslow || !this.lastCheck.delay(500.0f) || this.collided) {
            value = 0.2;
            if (stage == 0) {
                value = 0.0;
            }
        }
        return Math.max(value, this.shouldslow ? value : (MoveUtil.defaultSpeed() + 0.028 * MoveUtil.getSpeedEffect()));
    }
    
    public void setMoveSpeed(final EventMove event, final double speed) {
        final Minecraft mc = Speed.mc;
        final MovementInput movementInput = Minecraft.getMinecraft().thePlayer.movementInput;
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        final Minecraft mc2 = Speed.mc;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            EventMove.x = 0.0;
            EventMove.x = 0.0;
        }
        else {
            if (forward != 0.0) {
                MoveUtil.setSpeed(0.279);
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
            EventMove.x = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            EventMove.z = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }
    
    public static void GetSpeed(final EventMove e, final double d) {
        Minecraft.getMinecraft();
        final MovementInput movementInput = Minecraft.getMinecraft().thePlayer.movementInput;
        double v3 = MovementInput.moveForward;
        Minecraft.getMinecraft();
        final MovementInput movementInput2 = Minecraft.getMinecraft().thePlayer.movementInput;
        double v4 = MovementInput.moveStrafe;
        Minecraft.getMinecraft();
        double v5 = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (v3 != 0.0 || v4 != 0.0) {
            if (v3 != 0.0) {
                if (v4 > 0.0) {
                    v5 += ((v3 > 0.0) ? -45 : 45);
                }
                else if (v4 < 0.0) {
                    v5 += ((v3 > 0.0) ? 45 : -45);
                }
                v4 = 0.0;
                if (v3 > 0.0) {
                    v3 = 1.0;
                }
                else if (v3 < 0.0) {
                    v3 = -1.0;
                }
            }
            e.setX(v3 * d * Math.cos(Math.toRadians(v5 + 88.0)) + v4 * d * Math.sin(Math.toRadians(v5 + 87.9000015258789)));
            e.setZ(v3 * d * Math.sin(Math.toRadians(v5 + 88.0)) - v4 * d * Math.cos(Math.toRadians(v5 + 87.9000015258789)));
        }
    }
    
    private boolean canZoom() {
        return this.isMoving2() && Minecraft.getMinecraft().thePlayer.onGround;
    }
    
    private void setMotion(final EventMove em, final double speed) {
        final MovementInput movementInput = Minecraft.getMinecraft().thePlayer.movementInput;
        double forward = MovementInput.moveForward;
        final MovementInput movementInput2 = Minecraft.getMinecraft().thePlayer.movementInput;
        double strafe = MovementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            em.setX(0.0);
            em.setZ(0.0);
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
            em.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            em.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }
    
    public boolean isMoving2() {
        return Minecraft.getMinecraft().thePlayer.moveForward != 0.0f || Minecraft.getMinecraft().thePlayer.moveStrafing != 0.0f;
    }
    
    public boolean isOnGround(final double height) {
        final Minecraft mc = Speed.mc;
        return !Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes((Entity)Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
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
        final int y = (int)Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                final Minecraft mc = Speed.mc;
                final Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null) {
                    if (block.getMaterial() != Material.air) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
        }
        return inLiquid;
    }
    public static double defaultSpeed1() {
        double baseSpeed = 0.3873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
       
            	baseSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        return baseSpeed;
    }
    public void onEnable() {
    	gethypixelcn.onEnable();
        final boolean player = Minecraft.getMinecraft().thePlayer == null;
        this.collided = (!player && Minecraft.getMinecraft().thePlayer.isCollidedHorizontally);
        this.lessSlow = false;
        if (Minecraft.getMinecraft().thePlayer != null) {
            this.speed = defaultSpeed1();
        }
        this.slow = randomNumber(-10000, 0) / 1.0E7;
        this.less = 0.0;
        Speed.stage = 2;
        final net.minecraft.util.Timer timer = Speed.mc.timer;
        mc.timer.timerSpeed = 1.0f;
        super.onEnable();
    }
    
    public static int randomNumber(final int max, final int min) {
        return Math.round(min + (float)Math.random() * (max - min));
    }
    
    public static double defaultSpeed() {
        double baseSpeed = 0.2873;
        Minecraft.getMinecraft();
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            Minecraft.getMinecraft();
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    
    public void onDisable() {
        final net.minecraft.util.Timer timer = Speed.mc.timer;
        mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}
enum SpeedMode {
   Hypixel,
   HypixelTimer,
   Bhop,
   NCPHop;
}
