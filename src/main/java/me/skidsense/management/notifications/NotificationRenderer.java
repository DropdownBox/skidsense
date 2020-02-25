package me.skidsense.management.notifications;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import me.skidsense.color.Colors;
import net.minecraft.client.Minecraft;


public class NotificationRenderer implements INotificationRenderer {
	
   public void draw(List notifications) {
      int y = 3;
      Iterator var3 = notifications.iterator();

      while(var3.hasNext()) {
         Notification notification = (Notification)var3.next();
         notification.opacity.interpolate((float)notification.targetOpacity);
         notification.translate.interpolate(60.0F, (float)y, 0.5F);
         GL11.glPushMatrix();
         GL11.glScaled(0.9f, 0.9f, 0.9f);
         Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(notification.getMessage(), 80.0F, notification.translate.getY(), Colors.getColor(255, (int)notification.opacity.getOpacity()));
         GL11.glPopMatrix();
         //Client.fontManager.verdana16.drawStringWithShadow(notification.getMessage(), 60.0F, notification.translate.getY(), Colors.getColor(255, (int)notification.opacity.getOpacity()));
         y += 10;
         if (notification.checkTime() >= notification.getDisplayTime() + notification.getInitializeTime()) {
            notification.targetOpacity = 0;
            if (notification.opacity.getOpacity() <= 0.0F) {
               notifications.remove(notification);
            }
         }
      }

   }
}
