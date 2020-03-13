package me.skidsense.management.notifications;

import me.skidsense.management.animation.Opacity;
import me.skidsense.management.animation.Translate;

public class Notification implements INotification {
   private String text;
   private long start;
   private long displayTime = 550L;
   public int targetOpacity;
   public Translate translate;
   public Opacity opacity;

   public Notification(String text) {
      this.text = text;
      this.start = System.currentTimeMillis();
      this.translate = new Translate(2.0F, 0.0F);
      this.opacity = new Opacity(255);
      this.targetOpacity = 255;
   }

   public long checkTime() {
      return System.currentTimeMillis() - this.getDisplayTime();
   }

   public String getMessage() {
      return this.text;
   }

   public long getInitializeTime() {
      return this.start;
   }

   public long getDisplayTime() {
      return this.displayTime;
   }
}
