package me.skidsense.module.collection.visual.clickgui.Skidsense;


import java.awt.Color;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureLoader;

import me.skidsense.Client;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.ModuleManager;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class ClickGUI
extends GuiScreen {
	public static float startX = 100.0F;
	public float startY = 100.0F;
	public float movex = 0.0F;
	public float movey = 0.0F;
    int drawy;
   	int valuewheely;
   	int wheely;
   	int animawheely;
   	int animavaluewheely;
	boolean move;
	boolean click;
	public static String category;
	public static String modname;
	public static String valuename;
    public Module cheat;
    public Value value;
    
    public ClickGUI() {
    	
    }
    
    @Override    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	//TODO 窗体拖动
    	if(this.isHovered(startX, startY - 25.0F, startX + 400.0F, startY + 25.0F, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if(this.movex == 0.0F && this.movey == 0.0F) {
               this.movex = (int) ((float)mouseX - startX);
               this.movey = (int) ((float)mouseY - startY);
            } else {
               startX = (float)mouseX - this.movex;
               startY = (float)mouseY - this.movey;
            }
         } else if (this.movey != 0.0F || this.movey != 0.0F) {
            this.movex = (int) 0.0F;
            this.movey = (int) 0.0F;
         }
        
    	//TODO 字体
    	FontRenderer font = Client.fontManager.zeroarr;
        int x2 = 180;
        float valueY=this.startY;
        Gui.drawRect(startX, startY + 30, startX+50, startY+250,new Color(25,25,25).getRGB());
        Gui.drawRect(startX+55, startY, startX+380, startY+280,new Color(25,25,25).getRGB());
        Gui.drawRect(startX + 55, startY, startX+380, startY-0.5,new Color(225,30,30).getRGB());
        Gui.drawRect(startX, startY + 30, startX+50, startY+29.5,new Color(225,30,30).getRGB());
        try {
        	//TODO Category按钮
        	if(category==ModuleType.Fight.toString()) {
        		//combat
                Gui.drawRect(startX+4, startY+40, startX+5, startY+62,new Color(230,30,30).getRGB());
                Gui.drawRect(startX+1, startY+30, startX+50, startY+70,new Color(15,15,15,130).getRGB());
                }
    		RenderUtil.rectTexture(startX+17, startY+42, 16, 16,TextureLoader.getTexture("PNG",this.mc.getTextureManager().getClass().getClassLoader().getResourceAsStream("assets/minecraft/skidsense/clickgui/combat.png")),-1);
    	       //movement
     	   if(category==ModuleType.Move.toString()) {
     	    Gui.drawRect(startX+4, startY+85, startX+5, startY+107,new Color(230,30,30).getRGB());
            Gui.drawRect(startX+1, startY+75, startX+50, startY+115,new Color(15,15,15,130).getRGB());
            }
    	       RenderUtil.rectTexture(startX+17, startY+88, 16, 16,TextureLoader.getTexture("PNG",this.mc.getTextureManager().getClass().getClassLoader().getResourceAsStream("assets/minecraft/skidsense/clickgui/movement.png")),-1);
    	       //render
        	   if(category==ModuleType.Visual.toString()) {
        	        Gui.drawRect(startX+4, startY+130, startX+5, startY+150,new Color(230,30,30).getRGB());
    	        Gui.drawRect(startX+1, startY+120, startX+50, startY+160,new Color(15,15,15,130).getRGB());
    	        }
    	       RenderUtil.rectTexture( startX+17, startY+132, 16, 16,TextureLoader.getTexture("PNG",this.mc.getTextureManager().getClass().getClassLoader().getResourceAsStream("assets/minecraft/skidsense/clickgui/render.png")),-1);
    	       //player
        	   if(category==ModuleType.Player.toString()) {
       	        Gui.drawRect(startX+4, startY+175, startX+5, startY+197,new Color(230,30,30).getRGB());
    	        Gui.drawRect(startX+1, startY+165, startX+50, startY+205,new Color(15,15,15,130).getRGB());
    	        }
    	       RenderUtil.rectTexture( startX+17, startY+178, 16, 16,TextureLoader.getTexture("PNG",this.mc.getTextureManager().getClass().getClassLoader().getResourceAsStream("assets/minecraft/skidsense/clickgui/player.png")),-1);
    	       //world
        	   if(category==ModuleType.World.toString()) {
       	        Gui.drawRect(startX+4, startY+218, startX+5, startY+242,new Color(230,30,30).getRGB());
    	        Gui.drawRect(startX+1, startY+210, startX+50, startY+250,new Color(15,15,15,130).getRGB());
    	        }
    	       RenderUtil.rectTexture( startX+17, startY+222, 16, 16,TextureLoader.getTexture("PNG",this.mc.getTextureManager().getClass().getClassLoader().getResourceAsStream("assets/minecraft/skidsense/clickgui/world.png")),-1);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
        
     	font.drawStringWithShadow(category, (int) (startX+66), (int) (startY+10), -1);
       	if(move) {  		
    		startX=mouseX-movex;
    		startY=mouseY-movey;
    		} 
            float y2=this.startY;
     	//button
        for (Module c2 : ModuleManager.getModules()) {
            if (c2.getType().toString() != category) continue;
            this.cheat=c2;
            y2+=27; 
          //TODO 一大堆BUG的滑轮
           	if(mouseX>startX+52&&mouseX<startX+120&&mouseY>this.startY+26&&mouseY<this.startY+280) {
               	drawy=Mouse.getDWheel();  
           	if(drawy!=0) {
           		if(drawy==120) {
           			animawheely+=11; 
           		} 
           		if(drawy==-120) {
           			animawheely-=11; 
           		} 
           	//Helper.sendMessage("滚动数值为"+drawy+" "+animawheely+"  "+wheely);
           	}
           	}
       		if(wheely<animawheely) {
       			wheely++;
       		}
       		if(wheely>animawheely) {
       			wheely--;
       		}
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtil.doGlScissor((int) (this.startX+54), (int) (this.startY+27) , 120, 250);
            RenderUtil.drawRoundRect(startX+64, y2+3+wheely, startX+150, y2+27+wheely,new Color(20,20,20,200).getRGB());
            RenderUtil.drawRoundRect(startX+69, y2+wheely+14, startX+73, y2+wheely+18, cheat.isEnabled()?new Color(230,41,41).getRGB():new Color(180,180,180).getRGB());
            font.drawStringWithShadow(this.cheat.name, (int) (startX+77), (int) (y2+wheely+11), new Color(220,220,220).getRGB());
                 
            GL11.glDisable(3089);
            GL11.glPopMatrix();  //value
            
    for (Value v2 : this.cheat.getValues()) {
	this.value=v2;
    if (this.cheat.getName()!=modname) continue;
	if(this.value instanceof Mode) {
	this.valuename = "" + ((Mode)this.value).getValue();
	valueY+=27;
	}
	if(this.value instanceof Option) {
		valueY+=27;
	}        
    int render;
    font.drawStringWithShadow(this.modname, (int) (startX+165), (int) (this.startY+10), new Color(180,180,180).getRGB());
    GL11.glPushMatrix();
    GL11.glEnable(3089);
    RenderUtil.doGlScissor((int) (this.startX+160), (int) (this.startY+27) , 230, 250);
   	if(mouseX>startX+160&&mouseX<startX+410&&mouseY>this.startY+26&&mouseY<this.startY+280) {
       	drawy=Mouse.getDWheel();  
   	if(drawy!=0) {
   		if(drawy==120) {
   			valuewheely+=10; 
   		} 
   		if(drawy==-120) {
   			valuewheely-=10; 
   		}
   	}
   	}
   	
	if(this.value instanceof Numbers) {
		Numbers v1 = (Numbers)this.value;
    	this.valuename = "" + (v1.isInteger()?(double)((Number)v1.getValue()).intValue():((Number)v1.getValue()).doubleValue());
    	valueY+=27;
		render = (int)(115.0F * (((Number)v1.getValue()).floatValue() - v1.getMinimum().floatValue()) / (v1.getMaximum().floatValue() - v1.getMinimum().floatValue()));
		Gui.drawRect(startX+250, valueY+valuewheely+24, startX+370, valueY+valuewheely+25, new Color(9,9,9,255).getRGB());
		Gui.drawRect(startX+250, valueY+valuewheely+24, startX+250+render, valueY+valuewheely+25, new Color(250,40,40,250).getRGB());
		RenderUtil.drawRoundRect(startX+250+render, valueY+valuewheely+22, startX+255+render, valueY+valuewheely+27, new Color(205,205,205,255).getRGB());
		font.drawStringWithShadow(this.valuename, (int) (startX+286-font.getStringWidth(this.valuename)), (int) (valueY+valuewheely+10), -1);
        if(mouseX > this.startX+250 && mouseX < startX+370 && mouseY > valueY+valuewheely+22 && mouseY < valueY+valuewheely+27 && Mouse.isButtonDown(0)) {
            render = (int) v1.getMinimum().doubleValue();
             double max = v1.getMaximum().doubleValue();
             double min = v1.getIncrement().doubleValue();
             double valAbs = mouseX-(this.startX+250);
             double perc = valAbs / 77.2D;
             perc = Math.min(Math.max(0.0D, perc), 1.0D);
             double valRel = (max - render) * perc;
             double val = render + valRel;
             val = (double)Math.round(val * (1.0D / min)) / (1.0D / min);
             v1.setValue(Double.valueOf(val));
          }
	}
	if(this.value instanceof Mode) {
		Gui.drawRect(startX+249, valueY+valuewheely+7, startX+371, valueY+valuewheely+30, new Color(220,40,40,150).getRGB());
	Gui.drawRect(startX+250, valueY+valuewheely+8, startX+370, valueY+valuewheely+29, new Color(40,40,40).getRGB());
	font.drawStringWithShadow(this.valuename, (int)startX+255, (int)valueY+valuewheely+14, new Color(180,180,180).getRGB());
	}
	if(this.value instanceof Option) {
		Gui.drawRect(startX+360, valueY+valuewheely+14, startX+370, valueY+valuewheely+24, ((Boolean) this.value.getValue()).booleanValue()?new Color(230,40,40,150).getRGB():new Color(56,56,56).getRGB());
	}
	font.drawStringWithShadow(this.value.getName(), (int)startX+170, (int)valueY+valuewheely+14, -1);
 
    GL11.glDisable(3089);
    GL11.glPopMatrix();
	}  
        }


    	super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX <= x2 && (float)mouseY >= y && (float)mouseY <= y2;
     }
    
    @Override
    public void onGuiClosed() {    	
    	  if (mc.entityRenderer.theShaderGroup != null) {
              mc.entityRenderer.theShaderGroup.deleteShaderGroup();
              mc.entityRenderer.theShaderGroup = null;
          }
    	Module.lastX=(int) this.startX;
    	Module.lastY=(int) this.startY;
    	Module.clickguicategory=this.category;
    	Module.clickguimodname=this.modname;
    	Module.clickguivaluename=this.valuename;
    	  
    	super.onGuiClosed();
    	
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

       	if(mouseX>startX+5&&mouseX<startX+50&&mouseY>startY+30&&mouseY<startY+70&&mouseButton==0) {
       		category = ModuleType.Fight.toString();
       	}
      	if(mouseX>startX+5&&mouseX<startX+50&&mouseY>startY+75&&mouseY<startY+115&&mouseButton==0) {
       		category=ModuleType.Move.toString();
       	}
     	if(mouseX>startX+5&&mouseX<startX+50&&mouseY>startY+120&&mouseY<startY+160&&mouseButton==0) {
       		category=ModuleType.Visual.toString();
       	}
     	if(mouseX>startX+5&&mouseX<startX+50&&mouseY>startY+165&&mouseY<startY+205&&mouseButton==0) {
       		category=ModuleType.Player.toString();
       	}
     	if(mouseX>startX+5&&mouseX<startX+50&&mouseY>startY+210&&mouseY<startY+250&&mouseButton==0) {
       		category=ModuleType.World.toString();
       	}
        float y2=this.startY;
        float valueY=this.startY;


        for (Module c2 : ModuleManager.getModules()) {
            if (c2.getType().toString() != category) continue;
            this.cheat=c2;
            y2+=27; 
           	//滑轮
            if(y2>this.startY+350) {
               	drawy=Mouse.getDWheel();  
               	if(mouseX>startX+52&&mouseX<startX+120&&mouseY>this.startY+26&&mouseY<this.startY+350) {
               	if(drawy!=0) {
               		if(drawy==120) {
               			wheely+=10; 
               		} 
               		if(drawy==-120) {
               			wheely-=10; 
               		} 
               //	Helper.sendMessage("滚动数值为"+drawy);
               	}
               	}
               	}
         	if(mouseX>startX+52&&mouseX<startX+120&&mouseY>y2+this.wheely+6&&mouseY<y2+this.wheely+26) {
         		if(mouseButton==0) {
           		this.cheat.setEnabled(!this.cheat.isEnabled());
                Mouse.destroy();
                try {
   				Mouse.create();
   			} catch (LWJGLException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
           		}
         		if(mouseButton==1) {
               		this.modname=this.cheat.getName();
               		}
           	}
            for (Value v2 : this.cheat.getValues()) {
            	this.value=v2;
                if (this.cheat.getName()!=modname) continue;
            	if(this.value instanceof Numbers) {
            		valueY+=27;
            		}
            		if(this.value instanceof Mode) {
            		valueY+=27;
            		}
            		if(this.value instanceof Option) {
            		valueY+=27;
            		}          
         	if(mouseX>startX+360&&mouseX<startX+370&&mouseY>valueY+valuewheely+14&&mouseY<valueY+valuewheely+24&&this.value instanceof Option) {
         		if(mouseButton==0) {
           		this.value.setValue(!((Boolean) this.value.getValue()).booleanValue());
                Mouse.destroy();
                try {
   				Mouse.create();
   			} catch (LWJGLException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
           		}
         	}
         	if(mouseX>startX+250&&mouseX<startX+370&&mouseY>valueY+valuewheely+10&&mouseY<valueY+valuewheely+28&&this.value instanceof Mode) {
         		if(mouseButton==0) {
                    Mode m = (Mode)this.value;
                    Enum current = (Enum)m.getValue();
                    int next = current.ordinal() + 1 >= m.getModes().length?0:current.ordinal() + 1;
                    this.value.setValue(m.getModes()[next]);
                    Mouse.destroy();
                    try {
       				Mouse.create();
       			} catch (LWJGLException e) {
       				e.printStackTrace();
       			}
                    }
         	}
           	}
           	}
    	   super.mouseClicked(mouseX, mouseY, mouseButton);
    		 
}
}

