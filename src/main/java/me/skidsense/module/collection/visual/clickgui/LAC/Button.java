
package me.skidsense.module.collection.visual.clickgui.LAC;

import com.google.common.collect.Lists;

import me.skidsense.Client;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.collection.visual.HUD;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class Button {
    public Module cheat;
    public Window parent;
    public int anima;
    public int x;
    public int y;
    public int index;
    public int remander;
    public double opacity = 0.0;
    public ArrayList<ValueButton> buttons = Lists.newArrayList();
    public boolean expand;

    public Button(Module cheat, int x2, int y2) {
        this.cheat = cheat;
        this.x = x2;
        this.y = y2;
        int y22 = y2 + 14;
  
        for (Value v2 : cheat.getValues()) {
            this.buttons.add(new ValueButton(v2, x2 + 5, y22));
            y22 += 15;
        }
        this.buttons.add(new KeyBindButton(cheat, x2 + 5, y22));
    }

    public void render(int mouseX, int mouseY) {
        if (this.cheat.getValues().size() + (this.cheat == Client.getModuleManager().getModuleByClass(HUD.class) ? 2 : 1) != this.buttons.size()) {
            this.buttons.clear();
            int y2 = this.y + 14;
            
            for (Value v2 : this.cheat.getValues()) {
                this.buttons.add(new ValueButton(v2, this.x + 5, y2));
                y2 += 10;
            }
            this.buttons.add(new KeyBindButton(this.cheat, this.x + 5, y2));
        }
        if (this.index != 0) {
            Button b22 = this.parent.buttons.get(this.index - 1);
            this.y = b22.y + 19 + (b22.expand ? 19 * b22.buttons.size() : 0);
        }
        int i2 = 0;
        while (i2 < this.buttons.size()) {
            this.buttons.get((int)i2).y = this.y + 14 + 19 * i2;
            this.buttons.get((int)i2).x = this.x + 5;
            ++i2;
        }
        Client.fontManager.sansation14.drawStringWithShadow(this.cheat.getName(), this.x+2, this.y+3 , new Color(180,180,180).getRGB());
        if(this.cheat.isEnabled()) {
        Gui.drawRect(this.x-5, this.y-7, this.x+85, this.y + 12,new Color(255,255,255,30).getRGB());
        Client.fontManager.sansation14.drawStringWithShadow(this.cheat.getName(), this.x+2, this.y +3 , new Color(255,255,255).getRGB());
        }
        if (!this.expand && this.buttons.size() > 1) {
        	Client.fontManager.sansation16.drawStringWithShadow("+", this.x+75, this.y+3 , new Color(255,255,255).getRGB());
        } else if(this.expand ) {
        	Client.fontManager.sansation16.drawStringWithShadow("-", this.x+75, this.y+3 , new Color(255,255,255).getRGB());
        }
        if (this.expand) {
            this.buttons.forEach(b2 -> b2.render(mouseX, mouseY));
        }
    }

    public void key(char typedChar, int keyCode) {
        this.buttons.forEach(b2 -> b2.key(typedChar, keyCode));
    }

    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + Client.fontManager.sansation14.getStringHeight(this.cheat.getName())+2) {
            if (button == 0) {
                this.cheat.setEnabled(!this.cheat.isEnabled());
            }
            if (button == 1 && !this.buttons.isEmpty()) {
                this.expand = !this.expand;
                boolean bl2 = this.expand;
            }
        }
        if (this.expand) {
            this.buttons.forEach(b2 -> b2.click(mouseX, mouseY, button));
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
        int i2 = 0;
        while (i2 < this.parent.buttons.size()) {
            if (this.parent.buttons.get(i2) == this) {
                this.index = i2;
                this.remander = this.parent.buttons.size() - i2;
                break;
            }
            ++i2;
        }
    }
}

