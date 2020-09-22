package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import me.skidsense.Client;
import me.skidsense.color.ColorManager;
import me.skidsense.color.Colors;
import me.skidsense.management.alt.GuiAltManager;
import me.skidsense.management.fontRenderer.FontManager;
import me.skidsense.util.ColorCreator;
import me.skidsense.util.Draw;
import me.skidsense.util.IconButton;
import me.skidsense.util.Panorama;
import me.skidsense.util.RenderUtil;

import java.awt.*;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    private final Panorama panorama;
    
    private boolean margeleStyle;
    
    public GuiMainMenu() {
        panorama = new Panorama(this,
                new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_0.png"),
                new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_1.png"),
                new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_2.png"),
                new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_3.png"),
                new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_4.png"),
                new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_5.png"));
    }

    @Override
    public void initGui() {
        super.initGui();

        panorama.init();
        
        if(System.getProperty("user.name").contains("argel")) {
        	margeleStyle = true;
        }
        
        this.buttonList.add(
                new IconButton(1, this.width / 2 - 80,this.height / 3 + 40, 40, 40,
                        new ResourceLocation("skidsense/mainmenu/icon/person/single/1_white_32.png"), 16));
        this.buttonList.add(
                new IconButton(2, this.width / 2 - 40, this.height / 3 + 40, 40, 40,
                        new ResourceLocation("skidsense/mainmenu/icon/person/multiple/1_white_32.png"), 16));

        this.buttonList.add(
                new IconButton(999, this.width / 2 - 0, this.height / 3 + 40, 40, 40,
                        new ResourceLocation("skidsense/mainmenu/icon/account/1_white_32.png"), 16));

        this.buttonList.add(
                new IconButton(0, this.width / 2 + 40, this.height / 3 + 40, 40, 40,
                        new ResourceLocation("skidsense/mainmenu/icon/cog/1_white_32.png"), 16));

        this.mc.setConnectedToRealms(false);
    }

    @Override
    public void updateScreen() {
        panorama.update();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (button.id == 999) {
            this.mc.displayGuiScreen(new GuiAltManager());
        }

        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    	if(margeleStyle) {
            RenderUtil.drawImage(new ResourceLocation("pack.png"), (int)0, 0, sr.getScaledWidth(), sr.getScaledHeight());
    	}else {
    		panorama.draw(mouseX, mouseY, partialTicks);	
		}
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableAlpha();
        Gui.drawRect(0,0,width,height,new Color(0,0,0, 150).getRGB());
        //drawGradientRect(0, 0, width, height, ColorCreator.createRainbowFromOffset(-6000, 10), new Color(0,0,0, 50).getRGB());
        drawGradientRect(0, 0, width, height, new Color(0,0,0, 0).getRGB(), ColorCreator.createRainbowFromOffset(-6000, 5));
        GlStateManager.popMatrix();
        GlStateManager.color(1f, 1f, 1f);

        int spacing = 110;
        if (spacing < 110)
            spacing = 110;
        else if (spacing > 140)
            spacing = 140;
        
        GL11.glPushMatrix();
        String[] v8 = new String[]{"Changelog 091020","+ client reloaded"};
        int v9 = 5;
        double v11 = (double)sr.getScaleFactor() / Math.pow((double)sr.getScaleFactor(), 2.0D);
        GL11.glScaled(v11, v11, v11);
        for (int i = 0; i < v8.length; i++) {
        	String a12 = v8[i].startsWith("-") ? EnumChatFormatting.RED + v8[i] + EnumChatFormatting.RESET : (v8[i].startsWith("+") ? EnumChatFormatting.GREEN + v8[i] + EnumChatFormatting.RESET : (v8[i].startsWith("*") ? EnumChatFormatting.AQUA + v8[i] + EnumChatFormatting.RESET : v8[i]));
        	Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(a12, 3, v9 - 2, Colors.getColor(255, 255, 255, 180));
			v9 += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 1;
        }
        GL11.glPopMatrix();


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }
}
