package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.io.IOException;

import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiScreen;

public class CustomUI extends GuiScreen {
	public static float dragX, dragY, x=10, y=10;
	public static int red = 20, green = 128, blue = 225, alpha = 185;
    int[] valueType = {20, 128, 225, 185};
	float cusX = RenderUtil.width()/2 - 75, cusY = 5, width = 150, height = 15;
	boolean drag;
    @Override
    public void initGui(){
        super.initGui();
    }
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = mc.fontRendererObj;
        font.drawStringWithShadow("CustomUI Screen", RenderUtil.width()-font.getStringWidth("CustomUI Screen")-5, 5,-1);
        if (this.drag) {
            if (!Mouse.isButtonDown((int)0)) {
                this.drag = false;
            }
            this.x = mouseX - this.dragX;
            this.y = mouseY - this.dragY;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
	}
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton)throws IOException{
        if(mouseButton == 0 && mouseX >= this.x-1 && mouseX <= this.x+90 && mouseY >= this.y-1 && mouseY <= this.y+90) {
            this.drag = true;
            this.dragX = mouseX - this.x;
            this.dragY = mouseY - this.y;
        }
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
