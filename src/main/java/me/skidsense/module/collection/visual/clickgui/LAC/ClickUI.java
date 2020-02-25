/*
 * Decompiled with CFR 0.136.
 */
package me.skidsense.module.collection.visual.clickgui.LAC;

import com.google.common.collect.Lists;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ClickUI
extends GuiScreen {
    public static ArrayList<Window> windows = Lists.newArrayList();
    //private ArrayList<Particle> particles7789;
    public double opacity = 0.0;
    public int scrollVelocity;
    public static boolean binding = false;

    public ClickUI() {
        if (windows.isEmpty()) {
            int x2 = 5;
            ModuleType[] arrmoduleType = ModuleType.values();
            int n2 = arrmoduleType.length;
            int n22 = 0;
            while (n22 < n2) {
                ModuleType c2 = arrmoduleType[n22];
                windows.add(new Window(c2, x2, 10));
                x2 += 100;
                ++n22;
            }
        }
    }
    Random random = new Random();
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0,0,RenderUtil.width(),RenderUtil.height(), new Color(0,0,0,150).getRGB());
    	//this.particles7789 = new ArrayList();
    	ScaledResolution resolution = new ScaledResolution(this.mc);
        int i = 0;
        while (i < 2) {
            //this.particles7789.add(new Particle(this.random.nextInt(resolution.getScaledWidth()), this.random.nextInt(resolution.getScaledHeight())));
            i=i+50;
        }
        this.opacity = this.opacity + 10.0 < 200.0 ? (this.opacity = this.opacity + 10.0) : 200.0;
        Color color = new Color(-2146365167);
        GlStateManager.pushMatrix();
        ScaledResolution scaledRes = new ScaledResolution(this.mc);
        float scale = (float)scaledRes.getScaleFactor() / (float)Math.pow(scaledRes.getScaleFactor(), 5.0);
        windows.forEach(w2 -> w2.render(mouseX, mouseY));
        GlStateManager.popMatrix();

        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            this.scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 130 : 0);
        }
        windows.forEach(w2 -> w2.mouseScroll(mouseX, mouseY, this.scrollVelocity));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public void initGui() {
        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }
    @Override
    public void onGuiClosed() {
        if (mc.entityRenderer.theShaderGroup != null) {
            mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            mc.entityRenderer.theShaderGroup = null;
        }
        super.onGuiClosed();
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        windows.forEach(w2 -> w2.click(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 1 && !binding) {
			this.mc.displayGuiScreen(null);
			return;
		}
		windows.forEach(w2 -> w2.key(typedChar, keyCode));
	}

    public synchronized void sendToFront(Window window) {
        int panelIndex = 0;
        int i2 = 0;
        while (i2 < windows.size()) {
            if (windows.get(i2) == window) {
                panelIndex = i2;
                break;
            }
            ++i2;
        }
        Window t2 = windows.get(windows.size() - 1);
        windows.set(windows.size() - 1, windows.get(panelIndex));
        windows.set(panelIndex, t2);
    }
}

