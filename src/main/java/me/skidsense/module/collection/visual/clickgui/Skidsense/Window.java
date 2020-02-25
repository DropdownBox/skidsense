package me.skidsense.module.collection.visual.clickgui.Skidsense;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.TextureLoader;

import me.skidsense.Client;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Window

extends GuiScreen {
	public static int x;
	public static int y;
	public static String modulename;
	public static String valuename;
    public ModuleType category;
    public Module module;
    public Value option;
	int bg = 0;
    public Window() {
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) { 
    	
    	FontRenderer font = Client.mc.fontRendererObj;
    	if(bg<255) {
    		bg+=10;
    	}if(bg>255) {
    		bg=255;
    	}               
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date date = new Date();
    	RenderUtil.rectangleBordered(x, y, x+400, y+250, 3, new Color(14,14,14,bg).getRGB(), new Color(28,28,28,bg).getRGB());
    	font.drawStringWithShadow("Virtue"+" | "+dateFormat.format(date), x+7, y+7, -1);	
		int categoryY = y+15;
    	for(ModuleType m:category.values()) {
    		RenderUtil.drawRect(x+3, categoryY, x+65, categoryY+15, new Color(28,28,28,bg).getRGB());
    		RenderUtil.drawRect(x+3, categoryY, x+6, categoryY+15, modulename==m.name()?new Color(165,241,165,bg).getRGB():new Color(28,28,28,bg).getRGB());
    		font.drawStringWithShadow(m.name(), x+10, categoryY+6, -1);	
        	categoryY+=20;
    	}
		int moduleY = y;
		RenderUtil.rectangleBordered(x+70, y+15, x+390, y+240, 0.5, new Color(14,14,14,bg).getRGB(), new Color(25,25,25,bg).getRGB());
    	for(Module m:Client.instance.getModuleManager().modules) {
    		if(m.getType().toString()!=modulename) {
    			continue;
    		}
    		RenderUtil.rectangleBordered(x+72, moduleY+17, x+82, moduleY+27,0.5, m.isEnabled()?new Color(165,241,165,bg).getRGB():new Color(14,14,14,bg).getRGB(),new Color(165,241,165,bg).getRGB());
			font.drawStringWithShadow(m.getName(), x+85, moduleY+20, -1);
    		moduleY+=15;
    		int optionY = y;
    		int modeY = y;
    		int numberY = y;
        	for(Value v:m.getValues()){
           		if(m.getName()!=valuename) {
        			continue;
        		}
           		if(v instanceof Option) {
           			RenderUtil.rectangleBordered(x+135, optionY+17, x+145, optionY+27,0.5, ((Boolean) v.getValue()).booleanValue()?new Color(165,241,165,bg).getRGB():new Color(14,14,14,bg).getRGB(),new Color(165,241,165,bg).getRGB());
    			font.drawStringWithShadow(v.getName(), x+150, optionY+20, -1);
    			optionY+=15;
    			}           		
           		if(v instanceof Mode) {
           			RenderUtil.rectangleBordered(x+185, modeY+17, x+255, modeY+27,0.5, new Color(14,14,14,bg).getRGB(),new Color(28,28,28,bg).getRGB());
        			font.drawStringWithShadow(v.getName()+" : "+v.getValue().toString(), x+188, modeY+20, -1);
        			modeY+=15;
    			}
           		Numbers v1;
           	    int render;
           		if(v instanceof Numbers) {
           			
           			v1 = (Numbers)v;
           			render = (int)(100.0F * (((Number)v1.getValue()).floatValue() - v1.getMinimum().floatValue()) / (v1.getMaximum().floatValue() - v1.getMinimum().floatValue()));
           	        if(mouseX > this.x+270 && mouseX < x+370 && mouseY > numberY+26 && mouseY < numberY+35 && Mouse.isButtonDown(0)) {
           	            render = (int) v1.getMinimum().doubleValue();
           	             double max = v1.getMaximum().doubleValue();
           	             double min = v1.getIncrement().doubleValue();
           	             double valAbs = mouseX-(this.x+250);
           	             double perc = valAbs / 77.2D;
           	             perc = Math.min(Math.max(0.0D, perc), 1.0D);
           	             double valRel = (max - render) * perc;
           	             double val = render + valRel;
           	             val = (double)Math.round(val * (1.0D / min)) / (1.0D / min);
           	             v1.setValue(Double.valueOf(val));
           	          }
           			RenderUtil.rectangleBordered(x+270, numberY+26, x+370, numberY+35,0.5, new Color(14,14,14,bg).getRGB(),new Color(28,28,28,bg).getRGB());
           			RenderUtil.rectangleBordered(x+270, numberY+26, x+270+render, numberY+35,0.5, new Color(165,241,165,bg).getRGB(),new Color(28,28,28,bg).getRGB());
           			font.drawStringWithShadow(v.getName()+" : "+v.getValue().toString(), x+271, numberY+20, -1);
           			numberY+=20;
           		}
        	}
    	}
    	super.drawScreen(mouseX, mouseY, partialTicks);
    }	
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	if(mouseX>x&&mouseX<x+450&&mouseY>y&&mouseY<y+250) {
    		if(Mouse.isButtonDown(0)) {
    			this.x=50;
    			this.y=50;
    		}
    		int categoryY = y+15;
        	for(ModuleType m:category.values()) {
        		if(mouseX>x+3&&mouseX<x+65&&mouseY>categoryY&&mouseY<categoryY+15&&mouseButton==0) {
        			modulename=m.name();
        		}
            	categoryY+=20;
        	}
    		int moduleY = y;
        	for(Module m:Client.instance.getModuleManager().modules) {
        		if(m.getType().toString()!=modulename) {
        			continue;
        		}
        		if(mouseX>x+72&&mouseX<x+82&&mouseY>moduleY+17&&mouseY<moduleY+27) {
            		if(mouseButton==0) {
            			m.setEnabled(!m.isEnabled());
            			}
            		}
        		if(mouseX>x+72&&mouseX<x+122&&mouseY>moduleY+17&&mouseY<moduleY+27) {
            		if(mouseButton==1) {
            			this.valuename=m.getName();
            			}
        		}
        		moduleY+=15;
        		int optionY = y;
        		int modeY = y;
            	for(Value v:m.getValues()){
               		if(m.getName()!=valuename) {
            			continue;
            		}
               		if(v instanceof Option) {
                		if(mouseX>x+135&&mouseX<x+145&&mouseY>optionY+17&&mouseY<optionY+27) {
                    		if(mouseButton==0) {
                    			v.setValue(!((Boolean)v.getValue()).booleanValue());
                    		}
                		}
               			optionY+=15;
               			}
               		if(v instanceof Mode) {
                		if(mouseX>x+185&&mouseX<x+255&&mouseY>modeY+17&&mouseY<modeY+27) {
                    		if(mouseButton==0) {
                                Mode mode = (Mode)v;
                                Enum current = (Enum)mode.getValue();
                                int next = current.ordinal() + 1 >= mode.getModes().length?0:current.ordinal() + 1;
                                v.setValue(mode.getModes()[next]);
                    		}
                		}
            			modeY+=15;
        			}
               		}
        		}
    	}
 	   super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    @Override
    public void onGuiClosed() {
    	module.clickguicategory=this.modulename;
    	module.clickguivaluename=this.valuename;
    }
}
