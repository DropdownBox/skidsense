package me.skidsense.gui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.optifine.util.RenderChunkUtils;

import org.lwjgl.opengl.GL11;

import me.skidsense.Client;
import me.skidsense.management.animation.AnimationUtil;
import me.skidsense.management.animation.Translate;
import me.skidsense.management.fontRenderer.TTFFontRenderer;
import me.skidsense.util.RenderUtil;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationPublisher {
    private static final List<Notification> NOTIFICATIONS = new CopyOnWriteArrayList<>();

    public static void publish() {
        if (NOTIFICATIONS.isEmpty())
            return;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int srScaledHeight = sr.getScaledHeight();
        int scaledWidth = sr.getScaledWidth();
        int y = srScaledHeight - 30;
        TTFFontRenderer title = Client.instance.fontManager.tahoma20;
        TTFFontRenderer content = Client.instance.fontManager.tahoma18;
        FontRenderer icon = Client.instance.fontManager.NOTIFICATION;
        for (Notification notification : NOTIFICATIONS) {
            Translate translate = notification.getTranslate();
            int width = notification.getWidth();
            if (!notification.getTimer().elapsed(notification.getTime())) {
                notification.scissorBoxWidth = AnimationUtil.animate(width, notification.scissorBoxWidth, 0.25D);
                translate.interpolate((scaledWidth - width), y, (float) 0.15D);
            } else {
                notification.scissorBoxWidth = AnimationUtil.animate(0.0, notification.scissorBoxWidth, 0.25D);
                if (notification.getWidth() > scaledWidth) {
                    System.out.println("Remove");
                    NOTIFICATIONS.remove(notification);
                }
                y += 35;
            }
            float translateX = (float) translate.getX();
            float translateY = (float) translate.getY();
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.prepareScissorBox((float) (scaledWidth - notification.scissorBoxWidth), translateY, scaledWidth, translateY + 30.0F);
            RenderUtil.drawRect(translateX, translateY, scaledWidth, (translateY + 28.0F), new Color(10, 10, 10, 180).getRGB());
            RenderUtil.drawRect(translateX, (translateY + 28.0F - 1.5F), scaledWidth, (translateY + 28.0F), new Color(10, 10, 10, 180).getRGB());
            RenderUtil.drawRect(translateX, (translateY + 28.0F - 1.5F), translateX + width * (notification.getTime() - notification.getTimer().getElapsedTime()) / notification.getTime(), (translateY + 28.0F), notification.getType().getColor());

            icon.drawString(notification.getType().getColorstr(), translateX + 2.0F, translateY + 3.0F, notification.getType().getColor());
            title.drawStringWithShadow(notification.getTitle(), translateX + 28.0F, translateY + 4.0F, -1);
            content.drawStringWithShadow(notification.getContent(), translateX + 28.0F, translateY + 15.0F, -1);
            GL11.glDisable(3089);
            GL11.glPopMatrix();
            y -= 35;
        }
    }

    public static void queue(String title, String content, NotificationType type) {
    	TTFFontRenderer fr = Client.instance.fontManager.tahoma16;
        NOTIFICATIONS.add(new Notification(title, content, type, fr));
    }
}
