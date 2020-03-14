package me.skidsense.module.collection.visual.clickgui.LAC;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import org.lwjgl.input.Mouse;

import me.skidsense.Client;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.fontRenderer.FontLoaders;
import me.skidsense.management.fontRenderer.UnicodeFontRenderer;
import me.skidsense.util.RenderUtil;

public class ValueButton {
   public Value value;
   public String name;
   public boolean custom = false;
   public boolean change;
   public int x;
   public int y;
   public double opacity = 0.0D;
   public UnicodeFontRenderer mainfont = (UnicodeFontRenderer) Client.fontManager.sansation14;

   public ValueButton(Value value, int x, int y) {
      this.value = value;
      this.x = x;
      this.y = y;
      this.name = "";
      if(this.value instanceof Option) {
         this.change = ((Boolean)((Option)this.value).getValue()).booleanValue();
      } else if(this.value instanceof Mode) {
         this.name = "" + ((Mode)this.value).getValue();
      } else if(value instanceof Numbers) {
         Numbers v = (Numbers)value;
         this.name = String.valueOf(this.name) + (v.isInteger()?(double)((Number)v.getValue()).intValue():((Number)v.getValue()).doubleValue());
      }

      this.opacity = 0.0D;
   }

   public void render(int mouseX, int mouseY) {
      if(!this.custom) {
         if(mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + mainfont.getStringHeight(this.value.getName()) + 5) {
            if(this.opacity + 10.0D < 200.0D) {
               this.opacity += 10.0D;
            } else {
               this.opacity = 200.0D;
            }
         } else if(this.opacity - 6.0D > 0.0D) {
            this.opacity -= 6.0D;
         } else {
            this.opacity = 0.0D;
         }
         if(this.value instanceof Option) {
         if(this.change) {
            Gui.drawRect(this.x-7 ,this.y+2 ,this.x+1,this.y + 10, new Color(255,255,255).getRGB());
         }else {
        	 RenderUtil.rectangleBordered(this.x-7 ,this.y+2 ,this.x+1,this.y + 10, 0.5f,new Color(61,141,255,0).getRGB(),new Color(255,255,255).getRGB());
         }
         }
         Numbers v1;
         double render;
         if(this.value instanceof Option) {
            this.change = ((Boolean)((Option)this.value).getValue()).booleanValue();
         } else if(this.value instanceof Mode) {
            this.name = "" + ((Mode)this.value).getValue();
         } else if(this.value instanceof Numbers) {
            v1 = (Numbers)this.value;
            this.name = "" + (v1.isInteger()?(double)((Number)v1.getValue()).intValue():((Number)v1.getValue()).doubleValue());
            if(mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y+6 && mouseY < this.y + 11 && Mouse.isButtonDown(0)) {
               render = v1.getMinimum().doubleValue();
               double max = v1.getMaximum().doubleValue();
               double inc = v1.getIncrement().doubleValue();
               double valAbs = (double)mouseX - ((double)this.x + 1.0D);
               double perc = valAbs / 68.0D;
               perc = Math.min(Math.max(0.0D, perc), 1.0D);
               double valRel = (max - render) * perc;
               double val = render + valRel;
               val = (double)Math.round(val * (1.0D / inc)) / (1.0D / inc);
               v1.setValue(Double.valueOf(val));
            }
         }
         if(this.value instanceof Numbers) {
            v1 = (Numbers)this.value;
            render = (double)(68.0F * (((Number)v1.getValue()).floatValue() - v1.getMinimum().floatValue()) / (v1.getMaximum().floatValue() - v1.getMinimum().floatValue()));
            RenderUtil.drawRect((float)this.x-6,this.y+10, (float)((double)this.x + 2), this.y +11, (new Color(50,50,50)).getRGB());
            RenderUtil.drawRect((float)this.x-6,this.y+10, (float)((double)this.x + render + 2), this.y +11, (new Color(255,255,255)).getRGB());
       	   //Gui.drawFilledCircle((float) ((double)this.x + render + 4D), (float) (this.y+10.5), 2, new Color(255,255,255).getRGB(), mouseY);
            }
         if(this.value instanceof Option) {
        	 mainfont.drawStringWithShadow(this.value.getName(), this.x+12, this.y+1 - 1 + 5, -1);
         }else {
        	 mainfont.drawStringWithShadow(this.value.getName(), this.x-2, this.y+4 , -10); 
         }
         if(this.name!="") {
        	 mainfont.drawStringWithShadow(this.name,this.x- mainfont.getStringWidth(this.name)+75, this.y + 4, -5);
         }
         /*
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5, 0.5, 0.5);
         font.drawStringWithShadowWithShadow(this.value.getName(), this.x-5, this.y-2, -1);
         if(this.name!="") {
         font.drawStringWithShadowWithShadow(": "+this.name,this.x-4 + font.getStringWidth(this.value.getName()), this.y-2, -1);
         }
         GlStateManager.scale(1.0,1.0,1.0);
         GlStateManager.popMatrix();
         */
      }
   }

   public void key(char typedChar, int keyCode) {
   }
   public void click(int mouseX, int mouseY, int button) {
      if(!this.custom && mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 4 && mouseY < this.y + mainfont.getStringHeight(this.value.getName())+2) {
         if(this.value instanceof Option) {
            Option m1 = (Option)this.value;
            m1.setValue(Boolean.valueOf(!((Boolean)m1.getValue()).booleanValue()));
            return;
         }

         if(this.value instanceof Mode) {
            Mode m = (Mode)this.value;
            Enum current = (Enum)m.getValue();
            int next = current.ordinal() + 1 >= m.getModes().length?0:current.ordinal() + 1;
            this.value.setValue(m.getModes()[next]);
         }
      }

   }
}
