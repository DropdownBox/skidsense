/*
 * Decompiled with CFR 0.136.
 */
package me.skidsense.module.collection.visual.clickgui.LAC;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import org.lwjgl.input.Keyboard;

import me.skidsense.Client;
import me.skidsense.management.fontRenderer.UnicodeFontRenderer;
import me.skidsense.module.Module;

public class KeyBindButton
extends ValueButton {
    public Module cheat;
    public double opacity = 0.0;
    public boolean bind;

    public KeyBindButton(Module cheat, int x2, int y2) {
        super(null, x2, y2);
        this.custom = true;
        this.bind = false;
        this.cheat = cheat;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        double d2 = mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + Client.fontManager.sansation18.getStringHeight(this.cheat.getName()) + 5 ? (this.opacity + 10.0 < 200.0 ? (this.opacity = this.opacity + 10.0) : 200.0) : (this.opacity - 6.0 > 0.0 ? (this.opacity = this.opacity - 6.0) : 0.0);
        this.opacity = d2;
        UnicodeFontRenderer font = Client.fontManager.sansation14;
        font.drawStringWithShadow("Bind", this.x-3, this.y+10, -1);
        font.drawStringWithShadow( Keyboard.getKeyName(this.cheat.getKey()), this.x + 70 - font.getStringWidth(Keyboard.getKeyName(this.cheat.getKey())), this.y+10, -1);
    }

    @Override
    public void key(char typedChar, int keyCode) {
        if (this.bind) {
            this.cheat.setKey(keyCode);
            if (keyCode == 1) {
                this.cheat.setKey(0);
            }
            ClickUI.binding = false;
            this.bind = false;
        }
        super.key(typedChar, keyCode);
    }

    @Override
    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + Client.fontManager.sansation18.getStringHeight(this.cheat.getName()) + 5 && button == 0) {
            this.bind = !this.bind;
            ClickUI.binding = this.bind;
        }
        super.click(mouseX, mouseY, button);
    }
}

