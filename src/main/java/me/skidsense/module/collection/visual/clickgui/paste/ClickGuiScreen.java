package me.skidsense.module.collection.visual.clickgui.paste;

import com.google.common.collect.Lists;

import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.visual.clickgui.paste.panel.Panel;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;

public final class ClickGuiScreen
extends GuiScreen {
    //private static final HUDMod HUD = Autumn.MANAGER_REGISTRY.moduleManager.getModuleOrNull(HUDMod.class);
    private static ClickGuiScreen INSTANCE;
    private final List<Panel> panels = Lists.newArrayList();

    public ClickGuiScreen() {
        ModuleType[] categories = ModuleType.values();
        for (int i = categories.length - 1; i >= 0; --i) {
            this.panels.add(new Panel(categories[i], 5 + 120 * i, 5));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            this.panels.get(i).onDraw(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            this.panels.get(i).onMouseClick(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            this.panels.get(i).onMouseRelease(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int panelsSize = this.panels.size();
        for (int i = 0; i < panelsSize; ++i) {
            this.panels.get(i).onKeyPress(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static ClickGuiScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGuiScreen();
        }
        return INSTANCE;
    }

    public static Color getColor() {
        return new Color(255,255,255);
    }
}

