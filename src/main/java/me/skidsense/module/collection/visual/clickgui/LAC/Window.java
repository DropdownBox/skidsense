/*
 * Decompiled with CFR 0.136.
 */
package me.skidsense.module.collection.visual.clickgui.LAC;

import com.google.common.collect.Lists;

import me.skidsense.Client;
import me.skidsense.management.ModuleManager;
import me.skidsense.management.fontRenderer.UnicodeFontRenderer;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;



public class Window {
    public ModuleType category;
    public ArrayList<Button> buttons = Lists.newArrayList();
    public boolean drag;
    public boolean extended=true;
    public int x;
    public int y;
    public int expand;
    public int dragX;
    public int dragY;
    public int max;
    public int scroll;
    public int scrollTo;
    int R;
    int G=0;
    int B=0;
    int Rx=0;
    int Gx=0;
    int Bx=0;
    public double angel;

    public Window(ModuleType category, int x2, int y2) {
        this.category = category;
        this.x = x2;
        this.y = y2;
        this.max = 120;
        int y22 = y2 + 22;
        for (Module c2 : ModuleManager.getModules()) {
            if (c2.getType() != category) continue;
            this.buttons.add(new Button(c2, x2 + 5, y22));
            y22 += 15;
        }
        for (Button b2 : this.buttons) {
            b2.setParent(this);
        }
    }
    public void render(int mouseX, int mouseY) {
    	UnicodeFontRenderer font = Client.fontManager.sansation28;
        int current = 0;
        for (Button b3 : this.buttons) {
            if (b3.expand) {
                for (ValueButton v2 : b3.buttons) {
                    current += 19;
                }
            }
            current += 19;
        }
        int height = 12+current;
        if (this.extended) {
            this.expand = this.expand + 5 < height ? (this.expand = this.expand + 5) : height;
            this.angel = this.angel + 20.0 < 180.0 ? (this.angel = this.angel + 20.0) : 180.0;
        } else {
            this.expand = this.expand - 5 > 0 ? (this.expand = this.expand - 5) : 0;
            this.angel = this.angel - 20.0 > 0.0 ? (this.angel = this.angel - 20.0) : 0.0;
        }
        Gui.drawRect(this.x, this.y+15, this.x + 90, this.y+3  + this.expand, new Color(0,0,0,200).getRGB());
        Gui.drawRect(this.x, this.y+3  + this.expand, this.x + 90, this.y+5  + this.expand, new Color(0,0,0,150).getRGB());
        Gui.drawRect(this.x+1, this.y+5  + this.expand, this.x + 89, this.y+6  + this.expand, new Color(0,0,0,150).getRGB());
        Gui.drawRect(this.x+1, this.y+5  + this.expand, this.x + 0.5, this.y+5.5  + this.expand, new Color(0,0,0,150).getRGB());
        Gui.drawRect(this.x+89.5, this.y+5  + this.expand, this.x + 89, this.y+5.5  + this.expand, new Color(0,0,0,150).getRGB());
        //
        Gui.drawRect(this.x, this.y-2, this.x + 90, this.y + 15, new Color(0,0,0,200).getRGB());
        Gui.drawRect(this.x+1, this.y-2, this.x + 89, this.y-3, new Color(0,0,0,200).getRGB());
        Gui.drawRect(this.x+1, this.y-2, this.x+0.5, this.y-2.5, new Color(0,0,0,200).getRGB());
        Gui.drawRect(this.x+89.5, this.y-2, this.x + 89, this.y-2.5, new Color(0,0,0,200).getRGB());

        Client.fontManager.sansation18.drawStringWithShadow(this.category.name(), this.x + 6, this.y+5, new Color(180,180,180).getRGB());
        Client.fontManager.sansation14.drawStringWithShadow(this.extended?"-":"+", this.x + 80, this.extended?this.y+3:this.y+3 , new Color(180,180,180).getRGB());
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 90 - 10, this.y + 5, 0.0f);
        GlStateManager.rotate((float)this.angel, 0.0f, 0.0f, -1.0f);
        GlStateManager.translate(-this.x + 90 - 10, -this.y + 5, 0.0f);
        GlStateManager.popMatrix();
        if (this.expand==height) {
            GlStateManager.pushMatrix();
            this.buttons.forEach(b2 -> b2.render(mouseX, mouseY));
            RenderUtil.post();
            GlStateManager.popMatrix();
        }
        if (this.drag) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.x = mouseX - this.dragX;
            this.y = mouseY - this.dragY;
            this.buttons.get((int)0).y = this.y + 22 - this.scroll;
            for (Button b4 : this.buttons) {
                b4.x = this.x + 5;
            }
        }
        }

    public void key(char typedChar, int keyCode) {
        this.buttons.forEach(b2 -> b2.key(typedChar, keyCode));
    }
    public void mouseScroll(int mouseX, int mouseY, int amount) {
        if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17 + this.expand) {
            this.scrollTo = (int)((float)this.scrollTo - (float)(amount / 120 * 28));
        }
    }

    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17) {
            if (button == 1) {
                this.extended = !this.extended;
                boolean bl2 = this.extended;
            }
            if (button == 0) {
                this.drag = true;
                this.dragX = mouseX - this.x;
                this.dragY = mouseY - this.y;
            }
        }
        if (this.extended) {
            this.buttons.stream().filter(b2 -> b2.y < this.y + this.expand).forEach(b2 -> b2.click(mouseX, mouseY, button));
        }
    }
}

