package me.skidsense.module.collection.move.speed;

import java.util.List;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;

public class HypixelCN {
   Minecraft mc = Minecraft.getMinecraft();
   private double nextMotionSpeed;
   private double xMotionSpeed;
   private double zDist;
   private double moveSpeed;
   int stage = 0;
   public static double getBaseMoveSpeed() {


	      double var0 = 0.2873D;
	      if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
	         int var2 = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
	         var0 *= 1.0D + 0.2D * (double)(var2 + 1);
	      }

	      return var0;
	   }
   public void onMove(EventMove var1) {
      this.moveSpeed = getBaseMoveSpeed();
      if (this.stage < 1) {
         ++this.stage;
         this.nextMotionSpeed = 0.0D;
      }

      if (this.stage == 2 && (this.mc.thePlayer.moveForward != 0.0F || this.mc.thePlayer.moveStrafing != 0.0F) && this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.onGround) {
         this.xMotionSpeed = 0.4200123123131243D;
         if (this.mc.thePlayer.isPotionActive(Potion.jump)) {
            this.xMotionSpeed += (double)((float)(this.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
         }

         var1.setY(this.mc.thePlayer.motionY = this.xMotionSpeed);
         this.moveSpeed *= 2.1498624684D;
      } else if (this.stage == 3) {
         this.xMotionSpeed = (this.stage % 3 == 0 ? 0.678994565156D : 0.719499495154D) * (this.nextMotionSpeed - getBaseMoveSpeed());
         this.moveSpeed = this.nextMotionSpeed - this.xMotionSpeed;
      } else {
         List var2 = this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, this.mc.thePlayer.motionY, 0.0D));
         if ((var2.size() > 0 || this.mc.thePlayer.isCollidedVertically) && this.stage > 0) {
            this.stage = this.mc.thePlayer.moveForward == 0.0F && this.mc.thePlayer.moveStrafing == 0.0F ? 0 : 1;
         }

         this.moveSpeed = this.nextMotionSpeed - this.nextMotionSpeed / 159.0D;
      }

      this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
      this.xMotionSpeed = (double)this.mc.thePlayer.movementInput.moveForward;
      this.zDist = (double)this.mc.thePlayer.movementInput.moveStrafe;
      float var3 = this.mc.thePlayer.rotationYaw;
      if (this.xMotionSpeed == 0.0D && this.zDist == 0.0D) {
         this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + 1.0D, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + 1.0D);
         this.mc.thePlayer.setPosition(this.mc.thePlayer.prevPosX, this.mc.thePlayer.posY, this.mc.thePlayer.prevPosZ);
         var1.setX(0.0D);
         var1.setZ(0.0D);
      } else if (this.xMotionSpeed != 0.0D) {
         if (this.zDist >= 1.0D) {
            var3 += this.xMotionSpeed > 0.0D ? -45.0F : 45.0F;
            this.zDist = 0.0D;
         } else if (this.zDist <= -1.0D) {
            var3 += this.xMotionSpeed > 0.0D ? 45.0F : -45.0F;
            this.zDist = 0.0D;
         }

         if (this.xMotionSpeed > 0.0D) {
            this.xMotionSpeed = 1.0D;
         } else if (this.xMotionSpeed < 0.0D) {
            this.xMotionSpeed = -1.0D;
         }
      }

      double var4 = Math.cos(Math.toRadians((double)(var3 + 90.0F)));
      double var6 = Math.sin(Math.toRadians((double)(var3 + 90.0F)));
      double var8 = (this.xMotionSpeed * this.moveSpeed * var4 + this.zDist * this.moveSpeed * var6) * 0.987D;
      double var10 = (this.xMotionSpeed * this.moveSpeed * var6 - this.zDist * this.moveSpeed * var4) * 0.987D;
      if (Math.abs(var8) < 1.0D && Math.abs(var10) < 1.0D) {
         var1.setX(var8);
         var1.setZ(var10);
      }

      this.mc.thePlayer.stepHeight = 0.6F;
      if (this.xMotionSpeed == 0.0D && this.zDist == 0.0D) {
         var1.setX(0.0D);
         var1.setZ(0.0D);
         this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + 1.0D, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + 1.0D);
         this.mc.thePlayer.setPosition(this.mc.thePlayer.prevPosX, this.mc.thePlayer.posY, this.mc.thePlayer.prevPosZ);
      } else if (this.xMotionSpeed != 0.0D) {
         float var10000;
         if (this.zDist >= 1.0D) {
            var10000 = var3 + (this.xMotionSpeed > 0.0D ? -45.0F : 45.0F);
            this.zDist = 0.0D;
         } else if (this.zDist <= -1.0D) {
            var10000 = var3 + (this.xMotionSpeed > 0.0D ? 45.0F : -45.0F);
            this.zDist = 0.0D;
         }

         if (this.xMotionSpeed > 0.0D) {
            this.xMotionSpeed = 1.0D;
         } else if (this.xMotionSpeed < 0.0D) {
            this.xMotionSpeed = -1.0D;
         }
      }

      ++this.stage;
   }

   public void onEnable() {
      EntityPlayerSP var10000 = this.mc.thePlayer;
      var10000.motionX *= 0.0D;
      var10000 = this.mc.thePlayer;
      var10000.motionZ *= 0.0D;
      if (this.mc.thePlayer != null) {
         this.moveSpeed = getBaseMoveSpeed();
      }

      this.nextMotionSpeed = 0.0D;
      this.stage = 2;
      mc.timer.timerSpeed = 1.0F;
   }

   public void onPre(EventPreUpdate var1) {
      this.xMotionSpeed = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
      this.zDist = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
      this.nextMotionSpeed = Math.sqrt(this.xMotionSpeed * this.xMotionSpeed + this.zDist * this.zDist);
   }
}
