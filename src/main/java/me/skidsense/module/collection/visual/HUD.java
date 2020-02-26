package me.skidsense.module.collection.visual;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.FriendManager;
import me.skidsense.management.ModuleManager;
import me.skidsense.management.fontRenderer.UnicodeFontRenderer;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class HUD
extends Module {
    private Option<Boolean> info = new Option<>("Information", "information", true);
    public Option<Boolean> tabgui = new Option<>("Tabgui", "Tabgui", false);
    private Numbers<Double> rainbowspeed = new Numbers<>("Rainbow", "Rainbow", 0.5, 0.0, 1.0, 0.1);
    public Mode<Enum<?>> color = new Mode("ColorMode", "ColorMode", colormode.values(), colormode.Client);
    public static boolean shouldMove;
    public static boolean useFont;
    private double anima;
    float hue=0f;

    public HUD() {
        super("HUD", new String[]{"gui"}, ModuleType.Visual);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        this.addValues(this.info,this.tabgui,this.rainbowspeed,this.color);
		this.removed=true;
    }

    @EventHandler
    private void renderHud(EventRender2D event) {
    	UnicodeFontRenderer font =Client.fontManager.comfortaa18;
        if (!this.mc.gameSettings.showDebugInfo) {
            int y = 1;
            int rainbowTick = 0;
    		ArrayList<Module> mods = (ArrayList<Module>) ((ArrayList<Module>) Client.instance.getModuleManager().getModules()).clone();
            Collections.sort(mods, new Comparator<Module>() { public int compare(Module m1, Module m2) { if (Client.fontManager.comfortaa18.getStringWidth(m1.getName()+m1.getSuffix()) > Client.fontManager.comfortaa18.getStringWidth(m2.getName()+m2.getSuffix())) { return -1; } if (Client.fontManager.comfortaa18.getStringWidth(m1.getName()+m1.getSuffix()) < Client.fontManager.comfortaa18.getStringWidth(m2.getName()+m2.getSuffix())) { return 1; } return 0; } }); 
            
            String clientNameString = "Exusiai";
            Client.fontManager.comfortaa18.drawStringWithShadow(clientNameString.substring(0,1), 4, (float)2, new Color(220,1,5).getRGB());
            Client.fontManager.comfortaa18.drawStringWithShadow(clientNameString.substring(1,clientNameString.length()), Client.fontManager.comfortaa18.getStringWidth(clientNameString.substring(0,1))+5, (float)2, new Color(255,255,255).getRGB());
            Client.fontManager.comfortaa18.drawStringWithShadow("#001", Client.fontManager.comfortaa18.getStringWidth(clientNameString)+8, 2, new Color(180,180,180).getRGB());
            /*Client.fontManager.comfortaa18.drawStringWithShadow("Skid", 4, (float)2, new Color(255,255,255).getRGB());
            Client.fontManager.comfortaa18.drawStringWithShadow("sense", Client.fontManager.comfortaa18.getStringWidth("Skid")+5, (float)2, new Color(220,1,5).getRGB());
            Client.fontManager.comfortaa18.drawStringWithShadow("#001", Client.fontManager.comfortaa18.getStringWidth("Skidsense")+7, 2, new Color(180,180,180).getRGB());*/
            
            if(this.info.getValue().booleanValue() && !(mc.currentScreen instanceof GuiChat)) {
         	   ScaledResolution res = new ScaledResolution(mc);
     	       RenderUtil.drawImage(new ResourceLocation("skidsense/char_103_angel_1.png.merge.png"), RenderUtil.width() - 175, RenderUtil.height() - 63, 256, 256);
        		StringBuilder FPSStringBuilder = new StringBuilder();
        		FPSStringBuilder.append("FPS: ");
        		FPSStringBuilder.append(Minecraft.getDebugFPS());
            	StringBuilder CoordStringBuilder = new StringBuilder();
        		CoordStringBuilder.append("Coords: ");
        		CoordStringBuilder.append((int)mc.thePlayer.posX);
        		CoordStringBuilder.append(" ");
        		CoordStringBuilder.append((int)mc.thePlayer.posY);
        		CoordStringBuilder.append(" ");
        		CoordStringBuilder.append((int)mc.thePlayer.posZ);
        		//黑色Rect
            	Gui.drawRect(-1,RenderUtil.height()-10,font.getStringWidth("Coords: "+(int)mc.thePlayer.posX+" "+(int)mc.thePlayer.posY+" "+(int)mc.thePlayer.posZ)+10,RenderUtil.height()-28, new Color(15,15,15,190).getRGB());
            	//红色Rect
            	Gui.drawRect(font.getStringWidth("Coords: "+(int)mc.thePlayer.posX+" "+(int)mc.thePlayer.posY+" "+(int)mc.thePlayer.posZ)+10,RenderUtil.height()-11,font.getStringWidth("Coords: "+(int)mc.thePlayer.posX+" "+(int)mc.thePlayer.posY+" "+(int)mc.thePlayer.posZ)+9,RenderUtil.height()-27, new Color(220,1,5).getRGB());
            	//FPS
            	font.drawStringWithShadow(FPSStringBuilder.toString(), 3, RenderUtil.height()-29, new Color(255,255,255).getRGB());
                //坐标
            	font.drawStringWithShadow(CoordStringBuilder.toString(), 3, RenderUtil.height()-20, new Color(255,255,255).getRGB());
			}
            for (Module m : mods) {
            	if(m.wasRemoved()) {
            		continue;
            	}
                    	Color customrainbow = new Color(Color.HSBtoRGB((float)((double)(double)rainbowTick / 80.0*1.6 + Math.sin(this.mc.thePlayer.ticksExisted /100.0*1.0)) % 1.0f, (float)this.rainbowspeed.getValue().floatValue(), 0.6f));
            	String text =  m.getName()+m.getSuffix();
                    	float x = RenderUtil.width();                   	
            			if (m.getAnim() != -1) {
            				if(this.color.getValue()==colormode.Client) {
                        	//Gui.drawRect(RenderUtil.width(), y-1,x-m.getAnim()-5, y, new Color(220,20,20).getRGB());
                        	Gui.drawRect(RenderUtil.width(), y+10,x-m.getAnim()-5, y+9, new Color(220,20,20,0).getRGB());
                        	Gui.drawRect(RenderUtil.width(), y-1,x-m.getAnim()-4, y+10, new Color(12, 12, 12).getRGB());
                        	Gui.drawRect(x-m.getAnim()-5, y,x-m.getAnim()-4, y+9, new Color(220,20,20).getRGB());
                        	Client.fontManager.comfortaa18.drawStringWithShadow(m.getName()+m.getSuffix(), x-m.getAnim()-2, y-1,new Color(255,255,255).getRGB());
                        	}
            				
            				if(this.color.getValue()==colormode.Rainbow) {
            	            	UnicodeFontRenderer afont = Client.fontManager.roboto19;
                            	Gui.drawRect(x-m.getAnim()-5, y-1,x-m.getAnim()-4, y+9, customrainbow.getRGB());
                            	//Gui.drawRect(RenderUtil.width(), y-1,x-m.getAnim()-5, y, customrainbow.getRGB());
                            	Gui.drawRect(RenderUtil.width(), y-1,x-m.getAnim()-4, y+9, new Color(12, 12, 12).getRGB());
                            	Client.fontManager.comfortaa18.drawStringWithShadow(m.getName()+m.getSuffix(), x-m.getAnim()-2, y-1,customrainbow.getRGB());
                            	}
            				if (m.getAnim() < Client.fontManager.comfortaa18.getStringWidth(text) && m.isEnabled()) {
            					m.setAnim(m.getAnim() + 1);
            				}
            				if (m.getAnim() > -1 && !m.isEnabled()) {
            					m.setAnim(m.getAnim() - 1);
            				}
            				if (m.getAnim() > Client.fontManager.comfortaa18.getStringWidth(text) && m.isEnabled()) {
            					m.setAnim(Client.fontManager.comfortaa18.getStringWidth(text));
            				}
            				
                    		y +=Math.min(Client.fontManager.comfortaa18.FONT_HEIGHT, m.getAnim());
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    }
            			}
            this.drawPotionStatus(new ScaledResolution(this.mc));
                    }
        }
	
	private void drawPotionStatus(ScaledResolution sr) {
		List<PotionEffect> potions = new ArrayList<>();
		for (Object o : mc.thePlayer.getActivePotionEffects())
			potions.add((PotionEffect) o);
		potions.sort(Comparator.comparingDouble(effect -> -mc.fontRendererObj.getStringWidth(I18n.format((Potion.potionTypes[effect.getPotionID()]).getName()))));
		float pY = -2;
		for (PotionEffect effect : potions) {
			Potion potion = Potion.potionTypes[effect.getPotionID()];
			String name = I18n.format(potion.getName());
			String PType = "";
			if (effect.getAmplifier() == 1) {
				name = name + " II";
			} else if (effect.getAmplifier() == 2) {
				name = name + " III";
			} else if (effect.getAmplifier() == 3) {
				name = name + " IV";
			}
			if ((effect.getDuration() < 600) && (effect.getDuration() > 300)) {
				PType = PType + "\2476 " + Potion.getDurationString(effect);
			} else if (effect.getDuration() < 300) {
				PType = PType + "\247c " + Potion.getDurationString(effect);
			} else if (effect.getDuration() > 600) {
				PType = PType + "\2477 " + Potion.getDurationString(effect);
			}
			mc.fontRendererObj.drawStringWithShadow(name,1,204 + pY, potion.getLiquidColor());
			mc.fontRendererObj.drawStringWithShadow(PType,
					mc.fontRendererObj.getStringWidth(name), 204 + pY, -1);
			pY -= 9;
		}
	}

    enum colormode{
    	Client,
    	Rainbow;
    }
}

