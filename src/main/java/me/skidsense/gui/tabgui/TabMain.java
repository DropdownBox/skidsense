package me.skidsense.gui.tabgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import me.skidsense.gui.tabgui.components.Component;
import me.skidsense.gui.tabgui.components.impl.CategoryComponent;
import me.skidsense.gui.tabgui.util.TabGuiUtil;
import me.skidsense.module.ModuleType;

import java.awt.Color;
import java.util.ArrayList;

public class TabMain {
    public static Minecraft mc = Minecraft.getMinecraft();
    private float x, y;
    private ArrayList<Component> components = new ArrayList<>();
    private ArrayList<ModuleType> categories = new ArrayList<ModuleType>();
    private ModuleType selectedCategory = ModuleType.Fight;
    private float largestString;
    private boolean extended, extendedvalue;

    public TabMain(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void init() {
        GL11.glColor4f(1,1,1,1);
        float categoryY = getY() + 1;
        for (ModuleType category : ModuleType.values()) {
            categories.add(category);
        }
        largestString = mc.fontRendererObj.getStringWidth(StringUtils.capitalize(categories.get(0).name().toLowerCase()));
        for (int i = 0; i < categories.size(); i++) {
            if (mc.fontRendererObj.getStringWidth(StringUtils.capitalize(categories.get(i).name().toLowerCase())) > largestString) {
                largestString = mc.fontRendererObj.getStringWidth(StringUtils.capitalize(categories.get(i).name().toLowerCase()));
            }
        }
        for (ModuleType category : categories) {
            components.add(new CategoryComponent(this, category, StringUtils.capitalize(category.name().toLowerCase()), getX() + 1, categoryY, largestString + 18, 12));
            categoryY += 12;
        }
        components.forEach(component -> component.init());
    }

    public void onRender(ScaledResolution sr) {
    	TabGuiUtil.drawBorderedRect(x, y, largestString + 20, (ModuleType.values().length * 12) + 2, 1, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());
        components.forEach(component -> component.onRender(sr));
    }

    public void onKeypress(int key) {
        switch (key) {
            case Keyboard.KEY_LEFT:
                if (extended && !extendedvalue) {
                    extended = false;
                }
                break;
        }
        components.forEach(component -> component.onKeyPress(key));
        switch (key) {
            case Keyboard.KEY_DOWN:
                if (!extended) {
                    if (categories.indexOf(selectedCategory) + 1 >= categories.size()) {
                        selectedCategory = categories.get(0);
                        return;
                    }
                    selectedCategory = categories.get(categories.indexOf(selectedCategory) + 1);
                }
                break;
            case Keyboard.KEY_UP:
                if (!extended) {
                    if (categories.indexOf(selectedCategory) <= 0) {
                        selectedCategory = categories.get(categories.size() - 1);
                        return;
                    }
                    selectedCategory = categories.get(categories.indexOf(selectedCategory) - 1);
                }
                break;
            case Keyboard.KEY_RIGHT:
                if (!extended && !extendedvalue& !extendedvalue) {
                    extended = true;
                }
                break;
        }
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ModuleType getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(ModuleType selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public void setExtendedvalue(boolean extendedvalue) {
        this.extendedvalue = extendedvalue;
    }

    public boolean isExtended() {
        return extended;
    }
    public boolean isExtendedValue() {
        return extendedvalue;
    }
}
