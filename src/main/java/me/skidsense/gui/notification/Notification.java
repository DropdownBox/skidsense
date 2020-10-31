package me.skidsense.gui.notification;

import me.skidsense.management.animation.Translate;
import me.skidsense.management.fontRenderer.TTFFontRenderer;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class Notification {
    public static final int HEIGHT = 30;
    private final String title;
    private final String content;
    private final int time;
    private final NotificationType type;
    private final NotificationTimer timer;
    private final Translate translate;
    private final TTFFontRenderer fontRenderer;
    public double scissorBoxWidth;

    public Notification(String title, String content, NotificationType type, TTFFontRenderer fontRenderer) {
        this.title = title;
        this.content = content;
        this.time = 2500;
        this.type = type;
        this.timer = new NotificationTimer();
        this.fontRenderer = fontRenderer;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.translate = new Translate((sr.getScaledWidth() - getWidth()), (sr.getScaledHeight() - 30));
    }

    public final int getWidth() {
        return Math.max(100, Math.max(this.fontRenderer.getStringWidth(this.title), this.fontRenderer.getStringWidth(this.content)) + 80);
    }

    public final String getTitle() {
        return this.title;
    }

    public final String getContent() {
        return this.content;
    }

    public final int getTime() {
        return this.time;
    }

    public final NotificationType getType() {
        return this.type;
    }

    public final NotificationTimer getTimer() {
        return this.timer;
    }

    public Translate getTranslate() {
        return translate;
    }
}
