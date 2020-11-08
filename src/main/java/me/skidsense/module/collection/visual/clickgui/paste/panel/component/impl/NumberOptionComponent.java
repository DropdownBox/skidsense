package me.skidsense.module.collection.visual.clickgui.paste.panel.component.impl;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.collection.visual.clickgui.paste.ColorUtils;
import me.skidsense.module.collection.visual.clickgui.paste.panel.Panel;
import me.skidsense.module.collection.visual.clickgui.paste.panel.component.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public final class NumberOptionComponent extends Component {
    private final Numbers option;
    private boolean dragging = false;
    private int opacity = 120;

    public NumberOptionComponent(Numbers option, Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.option = option;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x = parent.getX() + this.getX();
        int y = parent.getY() + this.getY();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        int height = this.getHeight();
        int width = this.getWidth();
        Numbers option = this.option;
        double min = (double)option.getMinimum();
        double max = (double)option.getMaximum();
        double inc = (double)option.getIncrement();
        if (this.dragging) {
            option.setValue(this.round((double)(mouseX - x) * (max - min) / (double)width + min, inc));
            if ((Double)option.getValue() > max) {
                option.setValue(max);
            } else if ((Double)option.getValue() < min) {
                option.setValue(min);
            }
        }
        double optionValue = this.round((Double)option.getValue(), inc);
        String optionValueStr = String.valueOf(optionValue);
        int color = Color.WHITE.getRGB();
        double renderPerc = (double)(width - 3) / (max - min);
        double barWidth = renderPerc * optionValue - renderPerc * min;
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        FontRenderer small = Minecraft.getMinecraft().fontRendererObj;
        Gui.drawRect(x, y, x + width, y + height, ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        Gui.drawRect(x + 3, y + height - 4, x + width - 3, y + height - 3, -1436524448);
        Gui.drawRect(x + 3, y + height - 4, (double)x + Math.max(barWidth, 4.0), y + height - 3, color);
        small.drawStringWithShadow(option.getName(), (float)x + 2.0f, (float)y + (float)height / 2.0f - 4.0f, color);
        small.drawStringWithShadow(optionValueStr, x + width - small.getStringWidth(optionValueStr) - 3, (float)y + (float)height / 2.0f - 4.0f, color);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.dragging = true;
        }
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }

    private double round(double num, double increment) {
        double v = (double)Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

