package me.skidsense.module.collection.visual.clickgui.paste.panel.component.impl;

import java.awt.Color;

import me.skidsense.hooks.value.Option;
import me.skidsense.module.collection.visual.clickgui.paste.ClickGuiScreen;
import me.skidsense.module.collection.visual.clickgui.paste.ColorUtils;
import me.skidsense.module.collection.visual.clickgui.paste.panel.Panel;
import me.skidsense.module.collection.visual.clickgui.paste.panel.component.Component;
import net.minecraft.client.gui.Gui;

public final class BoolOptionComponent
extends Component {
    private final Option option;
    private int opacity = 120;

    public BoolOptionComponent(Option option, Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.option = option;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x = parent.getX() + this.getX();
        int y = parent.getY() + this.getY();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        Gui.drawRect(x, y, x + this.getWidth(), y + this.getHeight(), ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        int color = (boolean)this.option.getValue() != false ? ClickGuiScreen.getColor().getRGB() : new Color(this.opacity, this.opacity, this.opacity).getRGB();
        FONT_RENDERER.drawStringWithShadow(this.option.getName(), (float)x + 2.0f, (float)y + (float)this.getHeight() / 2.0f - 4.0f, color);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.option.setValue((boolean)this.option.getValue() == false);
        }
    }

}

