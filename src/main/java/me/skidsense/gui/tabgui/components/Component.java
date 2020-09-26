package me.skidsense.gui.tabgui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Component {
    private float x, y, width, height;
    private String name;
    public static Minecraft mc = Minecraft.getMinecraft();
    public Component(String name, float x, float y, float width, float height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void init() {

    }
    public void onRender(ScaledResolution sr) {
    }

    public void onKeyPress(int key) {
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
