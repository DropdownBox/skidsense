package me.skidsense.management.notifications;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;

public class Notifications {
   private static Notifications instance = new Notifications();
   private List notifications = new CopyOnWriteArrayList();
   private NotificationRenderer renderer = new NotificationRenderer();

   private Notifications() {
      instance = this;
   }

   public static Notifications getManager() {
      return instance;
   }

   public void post(String text) {
      //System.out.println(text);
      if (Minecraft.getMinecraft().thePlayer != null) {
         this.notifications.add(new Notification(text));
      //   Client.getSourceConsoleGUI().sourceConsole.addStringList(text);
      }
   }

   public void updateAndRender() {
      if (!this.notifications.isEmpty()) {
         this.renderer.draw(this.notifications);
      }
   }
}
