package me.skidsense.management.notifications.skid;

public class AnimationUtil {
   public static float calculateCompensation(float target, float current, long delta, int speed) {
      float diff = current - target;
      if (delta < 1L) {
         delta = 1L;
      }

      double xD;
      if (diff > (float)speed) {
         xD = (double)((long)speed * delta / 16L) < 0.25D ? 0.5D : (double)((long)speed * delta / 16L);
         current = (float)((double)current - xD);
         if (current < target) {
            current = target;
         }
      } else if (diff < (float)(-speed)) {
         xD = (double)((long)speed * delta / 16L) < 0.25D ? 0.5D : (double)((long)speed * delta / 16L);
         current = (float)((double)current + xD);
         if (current > target) {
            current = target;
         }
      } else {
         current = target;
      }

      return current;
   }

   public static float calculateCompensation(float target, float current, long delta, double speed) {
       float diff = current - target;
       if (delta < 1) {
           delta = 1;
       }
       if (delta > 1000) {
           delta = 16;
       }
       if (diff > speed) {
           double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
           current -= xD;
           if (current < target) {
               current = target;
           }
       } else if (diff < -speed) {
           double xD = (speed * delta / (1000 / 60) < 0.5 ? 0.5 : speed * delta / (1000 / 60));
           current += xD;
           if (current > target) {
               current = target;
           }
       } else {
           current = target;
       }
       return current;
   }
}
