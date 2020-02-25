package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.ibm.icu.text.NumberFormat;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.fontRenderer.CFontRenderer;
import me.skidsense.management.fontRenderer.FontLoaders;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.PlayerUtil;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TargetHUD
extends Module {
    public static boolean shouldMove;
    public static boolean useFont;
	public static float AnimotaiX;
	public static float AnimotaiSpeed;
    double anima = 0.0;
    private Option<Boolean> black = new Option<Boolean>("Black", "Black", false);
    private Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])rendermode.values(), (Enum)rendermode.Rainbowline);
    public TargetHUD() {
        super("TargetHUD", new String[]{"gui"}, ModuleType.Fight);
        this.setColor(new Color(244, 255, 149).getRGB());
        this.addValues(this.mode,black);
    }
    Colors hurtcolor;
    String hurtrender;
    String linehealth = null;
    @EventHandler
    public void onRender(EventRender2D event) {
        FontRenderer font2 = this.mc.fontRendererObj;
        FontRenderer font = Client.fontManager.sansation16;
        ScaledResolution res = new ScaledResolution(this.mc);
        ScaledResolution sr2 = new ScaledResolution(this.mc);

        int y2 = 2;
        boolean count = false;
        Color color = new Color(1.0f, 0.75f, 0.0f, 0.45f);
        int x1 = 600;
        int y1 = 355;
        int x2 = 750;
        int y22 = sr2.getScaledHeight() - 50;
        int nametag = y1 + 12;
        double thickness = 0.014999999664723873;
        double xLeft = -20.0;
        double xRight = 20.0;
        double yUp = 27.0;
        double yDown = 130.0;
        double size = 1.0;
        int red = 0;
        int green = 0;
        int blue = 0;
        double rainbowTick = 0;
		Color rainbow = new Color(Color.HSBtoRGB((float)((double)Minecraft.getMinecraft().thePlayer.ticksExisted / 100.0 + Math.sin((double)rainbowTick / 100.0 * 1.6)) % 1.0f, 1.0f, 1.0f));
        if (KillAura.target != null) {
        	
        	
        	tagrender();
        	if(this.mode.getValue() == rendermode.Rainbowline){
            RenderUtil.drawBorderedRect(new ScaledResolution(this.mc).getScaledWidth() - 5, new ScaledResolution(this.mc).getScaledHeight() - 30, new ScaledResolution(this.mc).getScaledWidth() - 140, new ScaledResolution(this.mc).getScaledHeight() - 80, 1.0f, new Color(35, 35, 35, 0).getRGB(), new Color(35, 35, 35, 180).getRGB());
            font2.drawString("Name:"+KillAura.target.getName(), new ScaledResolution(this.mc).getScaledWidth() -138, new ScaledResolution(this.mc).getScaledHeight() - 40+3, 16777215);
            font.drawString("\u00a7aHP:\u00a7c" + (int)((EntityLivingBase)KillAura.target).getHealth() + "/" + (int)((EntityLivingBase)KillAura.target).getMaxHealth(), new ScaledResolution(this.mc).getScaledWidth() - 108, new ScaledResolution(this.mc).getScaledHeight() - 75, 1677721);
            font.drawString("\u00a7bHurt:" + (KillAura.target.hurtResistantTime > 0), new ScaledResolution(this.mc).getScaledWidth() - 58, new ScaledResolution(this.mc).getScaledHeight() - 75, 16777215);
            font.drawString("\u00a7dArmor:" + ((EntityLivingBase)KillAura.target).getTotalArmorValue(), new ScaledResolution(this.mc).getScaledWidth() - 108, new ScaledResolution(this.mc).getScaledHeight() - 65+3, 16777215);
            font.drawString("\u00a7eDis:" + (int)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(KillAura.target), new ScaledResolution(this.mc).getScaledWidth() - 65, new ScaledResolution(this.mc).getScaledHeight() - 65+3, 16777215);
            font.drawString("X:" + (int)KillAura.target.posX + " " +"Y:" + (int)KillAura.target.posY + " " + "Z:" + (int)KillAura.target.posZ, new ScaledResolution(this.mc).getScaledWidth() - 108, new ScaledResolution(this.mc).getScaledHeight() - 55+3, 16777215);

            RenderUtil.drawEntityOnScreen(new ScaledResolution(this.mc).getScaledWidth() - 125, new ScaledResolution(this.mc).getScaledHeight() - 45, 15, 2.0f, 15.0f, (EntityLivingBase)KillAura.target);
        }
            //            	RenderUtil.drawBorderedRect(new ScaledResolution(this.mc).getScaledWidth() - 1, new ScaledResolution(this.mc).getScaledHeight() - 3, (float)new ScaledResolution(this.mc).getScaledWidth() - 119.0f * Math.min((float)(new ScaledResolution(this.mc).getScaledWidth() - 119), KillAura.target.getHealth() / (float)((int)KillAura.target.getMaxHealth())), new ScaledResolution(this.mc).getScaledHeight() - 1, 3.0f, new Color(255, 255, 255, 0).getRGB(), rainbow.getRGB());
            	if (this.mode.getValue() == rendermode.Rainbowline){
            		RenderUtil.drawBorderedRect(new ScaledResolution(this.mc).getScaledWidth() - 8, new ScaledResolution(this.mc).getScaledHeight() - 29, (float)new ScaledResolution(this.mc).getScaledWidth() - 110.5f * Math.min((float)(new ScaledResolution(this.mc).getScaledWidth() + 5), ((EntityLivingBase)KillAura.target).getHealth() / (float)((int)((EntityLivingBase)KillAura.target).getMaxHealth())), new ScaledResolution(this.mc).getScaledHeight() - 28, 3.0f, rainbow.getRGB(), rainbow.getRGB());
            	}
//            	


            	if (this.mode.getValue() == rendermode.HTB){
            		
                	int x = res.getScaledWidth() /2 + 10;
        			int y = res.getScaledHeight() - 90;
        			
                	final List var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy((Iterable)mc.thePlayer.sendQueue.getPlayerInfoMap());
                	
                	for (final Object aVar5 : var5) {
               		 final NetworkPlayerInfo var6 = (NetworkPlayerInfo) aVar5;
       	                    mc.getTextureManager().bindTexture(var6.getLocationSkin());
       	                    Gui.drawScaledCustomSizeModalRect(x+75, y-220, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
       	                    GlStateManager.bindTexture(0);
       	                    break;    
               	}
                	font2.drawString("Name:"+KillAura.target.getName(), new ScaledResolution(this.mc).getScaledWidth() -420, new ScaledResolution(this.mc).getScaledHeight() - 275, 16777215);
                	font2.drawString(linehealth, new ScaledResolution(this.mc).getScaledWidth() - 390, new ScaledResolution(this.mc).getScaledHeight() - 265,Colors.AQUA.c);
                	}
            	if(this.mode.getValue() == rendermode.Flat) {
            		ScaledResolution res1 = new ScaledResolution(this.mc);	 
            		int x = res1.getScaledWidth() /2 + 10;
            		int y = res1.getScaledHeight() - 90;
                    final EntityLivingBase player = (EntityLivingBase) KillAura.target;
                     if (player != null) {
                        GlStateManager.pushMatrix();
                        //BackGround
                        

                        Gui.drawRect(x+1.0, y+1.0, x+123.0, y+35.0,this.black.getValue().booleanValue() ? Integer.MIN_VALUE : new Color(240,238,225,150).getRGB() );
                        
                        RenderUtil.drawBorderedRect(x, (float)y, x+124, y+36, 2,this.black.getValue().booleanValue() ? new Color(240,238,225).getRGB() : new Color(0,0,0).getRGB(), 1);
                        
                        Gui.drawRect(x+35.0, y+1.0, x+123.0, y+35.0,this.black.getValue().booleanValue() ? Integer.MIN_VALUE : new Color(240,238,225,150).getRGB() );

                        Minecraft.getMinecraft().fontRendererObj.drawString(player.getName(), x+38.0f, y+4.0f,this.black.getValue().booleanValue() ? -1 : 1,false);
                        BigDecimal bigDecimal = new BigDecimal((double)player.getHealth());
                		bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
                		double HEALTH = bigDecimal.doubleValue();
                        BigDecimal DT = new BigDecimal((double)mc.thePlayer.getDistanceToEntity(player));
                		DT = DT.setScale(1, RoundingMode.HALF_UP);
                		double Dis = DT.doubleValue();
                        final float health = player.getHealth();
                        final float[] fractions = { 0.0f, 0.5f, 1.0f };
                        final Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
                        final float progress = health / player.getMaxHealth();
                        final Color customColor = (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter() : Color.RED;
                        double width = (double)mc.fontRendererObj.getStringWidth(player.getName());
                        width = getIncremental(width, 10.0);
                        if (width < 50.0) {
                            width = 50.0;
                        }
                        final double healthLocation = width * progress;
                        //health bar
                        Gui.drawRect(x+ 37.5, y+ 22.5, x+48.0 + healthLocation + 0.5, y+14.5, customColor.getRGB());
                        RenderUtil.rectangleBordered(x+37.0, y + 22.0, x+49.0 + width, y+15.0, 0.5, Colors.getColor(0, 0), Colors.getColor(0));
                       
                        String COLOR1;
                        if (health > 20.0D) {
                           COLOR1 = " \2479";
                        } else if (health >= 10.0D) {
                           COLOR1 = " \247a";
                        } else if (health >= 3.0D) {
                           COLOR1 = " \247e";
                        } else {
                           COLOR1 = " \2474";
                        }
                        
                        GlStateManager.scale(0.5, 0.5, 0.5);
                        final String str3 = String.format("HP: %s HURT: %s", Math.round(player.getHealth()), player.hurtTime);
                        Client.fontManager.comfortaa34.drawStringWithShadow(str3, x*2+76.0f, y*2+49.0f, this.black.getValue().booleanValue() ? -1 : 1);
//                      GuiInventory.drawEntityOnScreen(x*2-500s , 32, 25 , 2.0f, 2.0f, KillAura.target);

                        GlStateManager.scale(2.0f, 2.0f, 2.0f);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        
                        if(player instanceof EntityPlayer) {
                        final List var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy((Iterable)mc.thePlayer.sendQueue.getPlayerInfoMap());
                        for (final Object aVar5 : var5) {
                            final NetworkPlayerInfo var6 = (NetworkPlayerInfo)aVar5;
                            if (mc.theWorld.getPlayerEntityByUUID(var6.getGameProfile().getId()) == player) {
                                mc.getTextureManager().bindTexture(var6.getLocationSkin());
                                Gui.drawScaledCustomSizeModalRect(x+2, y+2, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                                if (((EntityPlayer)player).isWearing(EnumPlayerModelParts.HAT)) {
                                    Gui.drawScaledCustomSizeModalRect(x+2, y+2, 40.0f, 8.0f, 8, 8, 35, 32, 74.0f, 74.0f);
                                }
                                GlStateManager.bindTexture(0);
                                break;
                            }
                        }
                        
                        }
                        
                        GlStateManager.popMatrix();
                    }
            	}if(this.mode.getValue() == rendermode.New) {
            	      CFontRenderer font4 = FontLoaders.kiona18;
            	      ScaledResolution sr3 = new ScaledResolution(mc);
            	      int thecolor = (new Color(0, 230, 0, 220)).getRGB();
            	      FontRenderer font5 = mc.fontRendererObj;
          	        final EntityLivingBase player = KillAura.target;
            	      float opacity = 0.0F;
            	      if(player != null) {
            	         Minecraft var10000 = mc;
            	         int playerhp = (int)Minecraft.getMinecraft().thePlayer.getHealth();
            	         int targethp = (int)player.getHealth();
            	         float render = 150.0F * player.getHealth() / player.getMaxHealth();
            	         GlStateManager.pushMatrix();
            	         thecolor = (new Color(190, 250, 0, 150)).getRGB();
            	         if((double)player.getHealth() >= (double)player.getMaxHealth() * 0.8D) {
            	        	 thecolor = (new Color(0, 255, 0, 150)).getRGB();
            	         } else if((double)player.getHealth() < (double)player.getMaxHealth() * 0.8D && (double)player.getHealth() >= (double)player.getMaxHealth() * 0.6D) {
            	        	 thecolor = (new Color(190, 230, 0, 150)).getRGB();
            	         } else if((double)player.getHealth() < (double)player.getMaxHealth() * 0.8D && (double)player.getHealth() < (double)player.getMaxHealth() * 0.6D && (double)player.getHealth() >= (double)player.getMaxHealth() * 0.3D) {
            	        	 thecolor = (new Color(255, 255, 0, 150)).getRGB();
            	         } else if((double)player.getHealth() < (double)player.getMaxHealth() * 0.8D && (double)player.getHealth() < (double)player.getMaxHealth() * 0.6D && (double)player.getHealth() < (double)player.getMaxHealth() * 0.3D) {
            	        	 thecolor = (new Color(255, 0, 0, 150)).getRGB();
            	         }

            	         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	         RenderUtil.drawBorderedRect((float)(sr3.getScaledWidth() / 2), (float)(sr3.getScaledHeight() / 2), (float)(sr3.getScaledWidth() / 2 + 150), (float)(sr3.getScaledHeight() / 2 + 60), 1.0F, (new Color(5, 5, 5, 0)).getRGB(), (new Color(5, 6, 4, 100)).getRGB());
            	         font4.drawStringWithShadow(EnumChatFormatting.GRAY + "TargetName: ", (double)((float)(sr3.getScaledWidth() / 2) + 3.0F), (double)((float)(sr3.getScaledHeight() / 2) + 3.0F), 16777215);
            	         FontRenderer var10 = mc.fontRendererObj;
            	         StringBuilder var10001 = (new StringBuilder()).append(EnumChatFormatting.WHITE).append(player.getName()).append("[");
            	         Minecraft var10002 = mc;
            	         var10.drawStringWithShadow(var10001.append((byte)((int)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player))).append("m]").toString(), (float)(sr3.getScaledWidth() / 2) + 3.0F + (float)font2.getStringWidth("TargetName: "), (float)(sr3.getScaledHeight() / 2) + 3.0F, 16777215);
            	         font4.drawStringWithShadow(player.isEntityInsideOpaqueBlock()?EnumChatFormatting.GRAY + "isBlocking: " + EnumChatFormatting.WHITE + "true":EnumChatFormatting.GRAY + "isBlocking: " + EnumChatFormatting.WHITE + "false", (double)((float)(sr3.getScaledWidth() / 2) + 3.0F), (double)((float)(sr3.getScaledHeight() / 2) + 16.0F), -1);
            	         font4.drawStringWithShadow(EnumChatFormatting.GRAY + "HurtTime: " + EnumChatFormatting.WHITE + player.hurtTime, (double)((float)(sr3.getScaledWidth() / 2) + 3.0F), (double)((float)(sr3.getScaledHeight() / 2) + 29.0F), -1);
            	         font4.drawStringWithShadow(player.hurtTime > 0?EnumChatFormatting.GRAY + "isAttacking: " + EnumChatFormatting.WHITE + "true":EnumChatFormatting.GRAY + "isAttacking: " + EnumChatFormatting.WHITE + "false", (double)((float)(sr3.getScaledWidth() / 2) + 6.0F + (float)font2.getStringWidth("HurtTime: " + player.hurtTime)), (double)((float)(sr3.getScaledHeight() / 2) + 29.0F), -1);
            	         font4.drawStringWithShadow(EnumChatFormatting.GRAY + "HP: " + EnumChatFormatting.WHITE + targethp + "/" + (int)player.getMaxHealth(), (double)((float)(sr3.getScaledWidth() / 2) + 3.0F), (double)((float)(sr3.getScaledHeight() / 2) + 42.0F), -1);
            	         font5.drawStringWithShadow(playerhp > targethp?"\u5965\u91cc\u7ed9,\u5e72\u4ed6!":"\u4e09\u5341\u516d\u8ba1,\u8d70\u4e3a\u4e0a\u8ba1!", (float)(sr3.getScaledWidth() / 2) + 17.0F + (float)font2.getStringWidth("HP: " + targethp + "/" + (int)player.getMaxHealth()), (float)(sr3.getScaledHeight() / 2) + 42.0F, -1);
            	         RenderUtil.drawBorderedRect((float)(sr3.getScaledWidth() / 2), sr3.getScaledHeight() / 2 + 58.0, (sr3.getScaledWidth() / 2) + render + 2.0F, sr3.getScaledHeight() / 2 + 60.0, 1.0f, (new Color(1, 1, 1, 0)).getRGB(), thecolor);
            	         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            	         GlStateManager.enableAlpha();
            	         GlStateManager.enableBlend();
            	         GlStateManager.popMatrix();
            	      }
            	}
            	if(this.mode.getValue() == rendermode.Klrui) {
            		Edebug();
            	}

        
            
        }
       

        
    
        
    }
    private void Edebug() {
    	FontRenderer font2 = this.mc.fontRendererObj;
        ScaledResolution res = new ScaledResolution(this.mc);
        ScaledResolution sr2 = new ScaledResolution(this.mc);
        int y2 = 2;
        boolean count = false;
        Color color = new Color(1.0f, 0.75f, 0.0f, 0.45f);
        int x1 = 600;
        int y1 = 355;
        int x2 = 750;
        int y22 = sr2.getScaledHeight() - 50;
        int nametag = y1 + 12;
        double thickness = 0.014999999664723873;
        double xLeft = -20.0;
        double xRight = 20.0;
        double yUp = 27.0;
        double yDown = 130.0;
        double size = 10.0;
        int right = new ScaledResolution(this.mc).getScaledWidth() - new ScaledResolution(this.mc).getScaledWidth() / 2;
        int right2 = new ScaledResolution(this.mc).getScaledWidth() - new ScaledResolution(this.mc).getScaledWidth() / 2 + 30;
        int height = new ScaledResolution(this.mc).getScaledHeight() - 70;
        if (KillAura.target != null) {
            Gui.drawRect((double)right, (double)(height - 50), (double)(right + 130), (double)(height - 90), (int)new Color(0, 0, 0, 130).getRGB());
            font2.drawString(KillAura.target.getName(), right + 30, height - 87, 16777215);
            font2.drawString("HP:" + (int)((EntityLivingBase)KillAura.target).getHealth() + "/" + (int)((EntityLivingBase)KillAura.target).getMaxHealth() + " " + "Hurt:" + (KillAura.target.hurtResistantTime > 0), right + 30, height - 70, new Color(255, 255, 255).getRGB());
            font2.drawString("Coords: " + (int)KillAura.target.posX + " " + (int)KillAura.target.posY + " " + (int)KillAura.target.posZ, right + 30, height - 60, new Color(255, 255, 255).getRGB());
            RenderUtil.drawEntityOnScreen((int)(right + 14), (int)(height - 54), (int)15, (float)2.0f, (float)15.0f, (EntityLivingBase)((EntityLivingBase)KillAura.target));
            //Gui.drawRainbowRectVertical((int)right2, (int)(height - 77), (int)((int)((float)right2 + 95.0f * (float)Math.min((int)right2, ((EntityLivingBase)(KillAura.target)).getHealth() / ((EntityLivingBase)(KillAura.target)).getMaxHealth()))), (int)(height - 73), (int)3, (float)1.0f);
            //Gui.drawRainbowRectVertical((int)right2, (int)(height - 77), (int)(right2 + 95.0f * Math.min(right2, (int)(((EntityLivingBase)KillAura.target).getHealth() / ((int)((EntityLivingBase)KillAura.target).getMaxHealth())))), (int)(height - 73),3, 1);
        }
    }

    
    private void tagrender() {
    	int health = (int)((EntityLivingBase)KillAura.target).getHealth();
	    if (health >= 20.0D) {
		      linehealth = "\247a" + "||||||||||||||||||||";
		    } else if (health > 18.0D) {
		      linehealth = "\247a" + "||||||||||||||||||" + "\247c" + "||";
		    } else if (health > 16.0D) {
		      linehealth = "\247a" + "||||||||||||||||" + "\247c" + "||||";
		    } else if (health > 14.0D) {
		      linehealth = "\247a" + "||||||||||||||" + "\247c" + "||||||";
		    } else if (health > 12.0D) {
		      linehealth = "\247a" + "||||||||||||" + "\247c" + "||||||||";
		    } else if (health > 10.0D) {
		      linehealth = "\247a" + "||||||||||" + "\247c" + "||||||||||";
		    } else if (health > 8.0D) {
		      linehealth = "\247a" + "||||||||" + "\247c" + "||||||||||||";
		    } else if (health > 6.0D) {
		      linehealth = "\247a" + "||||||" + "\247c" + "||||||||||||||";
		    } else if (health > 4.0D) {
		      linehealth = "\247a" + "||||" + "\247c" + "||||||||||||||||";
		    } else if (health > 2.0D) {
		      linehealth = "\247a" + "||" + "\247c" + "||||||||||||||||||";
		    } else if (health > 0.0D) {
		      linehealth = "\247c" + "||||||||||||||||||||";
		    }

	}


    
    
 @EventHandler
	public void onScreenDrawx(EventRender2D er) {
	 if(this.mode.getValue() == rendermode.Mikov){
		ScaledResolution res = new ScaledResolution(this.mc);	 
		int x = res.getScaledWidth() /2 + 10;
		int y = res.getScaledHeight() - 90;
        final EntityLivingBase player = (EntityLivingBase) KillAura.target;
         if (player != null) {

            GlStateManager.pushMatrix();
            RenderUtil.drawRect(x+0.0f, y+0.0f, x+143.0f, y+36.0f, Colors.getColor(0, 150));
            
            mc.fontRendererObj.drawStringWithShadow(player.getName(), x+38.0f, y+2.0f, -1);
       
            BigDecimal bigDecimal = new BigDecimal((double)player.getHealth());
    		bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
    		double HEALTH = bigDecimal.doubleValue();
    		
            BigDecimal DT = new BigDecimal((double)mc.thePlayer.getDistanceToEntity(player));
    		DT = DT.setScale(1, RoundingMode.HALF_UP);
    		double Dis = DT.doubleValue();
    		
            final float health = player.getHealth();
            final float[] fractions = { 0.0f, 0.5f, 1.0f };
            final Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
            final float progress = health / player.getMaxHealth();
            final Color customColor = (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter() : Color.RED;
            double width = (double)mc.fontRendererObj.getStringWidth(player.getName());
            width = PlayerUtil.getIncremental(width, 10.0);
            if (width < 50.0) {
                width = 50.0;
            }
            final double healthLocation = width * progress;
            RenderUtil.drawGradientSideways(x, y+35, x + healthLocation *2.86, y+36, new Color(81,174,255).getRGB(),new Color(40,62,255).getRGB());
            for (int i = 1; i < 10; ++i) {
                final double dThing = width / 10.0 * i;
            }
            String COLOR1;
            if (health > 20.0D) {
               COLOR1 = " \2479";
            } else if (health >= 10.0D) {
               COLOR1 = " \247a";
            } else if (health >= 3.0D) {
               COLOR1 = " \247e";
            } else {
               COLOR1 = " \2474";
            }
            
            GlStateManager.scale(0.5, 0.5, 0.5);
            final String str = "HP: "+ HEALTH + " Dist: " + Dis;
            mc.fontRendererObj.drawStringWithShadow(str, x*2+76.0f, y*2+35.0f, -1);
            final String str2 = String.format("Yaw: %s Pitch: %s ", (int)player.rotationYaw, (int)player.rotationPitch);
            mc.fontRendererObj.drawStringWithShadow(str2, x*2+76.0f, y*2+47.0f, -1);
            final String str3 = String.format("G: %s HURT: %s TE: %s", player.onGround, player.hurtTime, player.ticksExisted);
            mc.fontRendererObj.drawStringWithShadow(str3, x*2+76.0f, y*2+59.0f, -1);
            
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
       
            if(player instanceof EntityPlayer) {
            final List var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy((Iterable)mc.thePlayer.sendQueue.getPlayerInfoMap());
            for (final Object aVar5 : var5) {
                final NetworkPlayerInfo var6 = (NetworkPlayerInfo)aVar5;
                if (mc.theWorld.getPlayerEntityByUUID(var6.getGameProfile().getId()) == player) {
                    mc.getTextureManager().bindTexture(var6.getLocationSkin());
                    Gui.drawScaledCustomSizeModalRect(x+2, y+2, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                    if (((EntityPlayer)player).isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(x+2, y+2, 40.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                    }
                    GlStateManager.bindTexture(0);
                    break;
                }
            }
            
            
            }
            
            GlStateManager.popMatrix();
        }
	 }
	}
 private void renderStuffStatus(ScaledResolution scaledRes) {
		int yOffset = 15;
		for (int slot = 3, xOffset = 0; slot >= 0; slot--) {
			ItemStack stack = mc.thePlayer.inventory.armorItemInSlot(slot);
			GuiIngame gi = new GuiIngame(mc);
			if (stack != null) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glScalef(0.5F, 0.5F, 0.5F);
				mc.fontRendererObj.drawStringWithShadow(stack.getMaxDamage() - stack.getItemDamage() + "", scaledRes.getScaledWidth() + 32 - xOffset * 2 + (stack.getMaxDamage() - stack.getItemDamage() >= 100 ? 4 : (stack.getMaxDamage() - stack.getItemDamage() <= 100 && stack.getMaxDamage() - stack.getItemDamage() >= 10  ? 7 : 11)), scaledRes.getScaledHeight() * 2 - 147 - yOffset + 30, 0xFFFFFF);
				GL11.glScalef(2F, 2F, 2F);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				mc.getRenderItem().renderItemIntoGUI(stack, scaledRes.getScaledWidth() / 2 + 25 - xOffset, scaledRes.getScaledHeight() - 70 - (yOffset / 2) + 15);
				xOffset -= 18;
			}
		}
	}
 	
// @EventHandler
// private void drawMoon(EventRender2D e) {
//	 ScaledResolution res = new ScaledResolution(this.mc);	 
//	 if(KillAura.target != null){
//	 
//	 final float health;
//	 double width;
//	 EntityPlayer entityPlayer;
//	 if (KillAura.target !=null){
//		 entityPlayer = (EntityPlayer) KillAura.target;
//	 }else{
//		 entityPlayer = null;
//	 }
//		 health = ((EntityLivingBase) KillAura.target).getHealth();
//		 width = (double)mc.fontRendererObj.getStringWidth(KillAura.target.getName());
//	 final float progress = health / ((EntityLivingBase) KillAura.target).getMaxHealth();
//	 int x = res.getScaledWidth();
//	 int y = res.getScaledHeight();
//	 final double healthLocation = width * progress;
//	 CFontRenderer font = FontLoaders.kiona16;
//	 String dire = Direction.values()[MathHelper.floor_double(KillAura.target.rotationYaw * 4.0f / 180.0f + 0.5) & 0x7].name();
//	 final EntityLivingBase player = (EntityLivingBase) KillAura.target;
//	 NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfo(player.getName());
//	 int playerPing = playerInfo != null ? playerInfo.getResponseTime() : -1;
//	 FontRenderer font2 = this.mc.fontRendererObj;
//	 if(this.mode.getValue() == rendermode.Moon){
//	 RenderUtil.drawBorderedRect(new ScaledResolution(this.mc).getScaledWidth() - 100, new ScaledResolution(this.mc).getScaledHeight() - 30, new ScaledResolution(this.mc).getScaledWidth() - 340, new ScaledResolution(this.mc).getScaledHeight() - 80, 1.0f, new Color(35, 35, 35, 0).getRGB(), new Color(35, 35, 35, 180).getRGB());
//	 RenderUtil.drawEntityOnScreen(new ScaledResolution(this.mc).getScaledWidth() - 325, new ScaledResolution(this.mc).getScaledHeight() - 45, 15, 2.0f, 15.0f, (EntityLivingBase)KillAura.target);
//	 font2.drawString("Name:"+"\247b"+KillAura.target.getName() + "      " + "\247fHurt:" + "\247c"+(KillAura.target.hurtResistantTime > 0), new ScaledResolution(this.mc).getScaledWidth() -300, new ScaledResolution(this.mc).getScaledHeight() - 75, 16777215);
//	 font2.drawString("Ping:"+ "\247c"+playerPing+ "      " + "\247fDirection:" + dire, new ScaledResolution(this.mc).getScaledWidth() -300, new ScaledResolution(this.mc).getScaledHeight() - 65, 16777215);
//	 if (KillAura.target.getName() != entityPlayer.getName()){
//	 RenderUtil.drawBorderedRect(x/2+150 +30, y -49, x/2+150 + healthLocation *3.86 +30, y - 35, 3.0f, Colors.GREEN.c,Colors.GREEN.c);
//	 }
//	 else{
//		 RenderUtil.drawBorderedRect(x/2+150 +30, y -49, x/2+150 + healthLocation *2.3 +30, y - 35, 3.0f, Colors.GREEN.c,Colors.GREEN.c);
//	 }
//	 font.drawString("\247f" + (int)((EntityLivingBase)KillAura.target).getHealth()*5 +"%", new ScaledResolution(this.mc).getScaledWidth() - 208, new ScaledResolution(this.mc).getScaledHeight() - 43, 1677721);
//	 } 
//	 }
//	 }
 
 public static int[] getFractionIndicies(float[] fractions, float progress) {
     int[] range = new int[2];

     int startPoint;
     for(startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        ;
     }

     if(startPoint >= fractions.length) {
        startPoint = fractions.length - 1;
     }

     range[0] = startPoint - 1;
     range[1] = startPoint;
     return range;
  }

  public static Color blendColors(float[] fractions, Color[] colors, float progress) {
     Color color = null;
     if(fractions == null) {
        throw new IllegalArgumentException("Fractions can\'t be null");
     } else if(colors == null) {
        throw new IllegalArgumentException("Colours can\'t be null");
     } else if(fractions.length == colors.length) {
        int[] indicies = getFractionIndicies(fractions, progress);
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        color = blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
        return color;
     } else {
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
     }
  }

  public static Color blend(Color color1, Color color2, double ratio) {
     float r = (float)ratio;
     float ir = 1.0F - r;
     float[] rgb1 = new float[3];
     float[] rgb2 = new float[3];
     color1.getColorComponents(rgb1);
     color2.getColorComponents(rgb2);
     float red = rgb1[0] * r + rgb2[0] * ir;
     float green = rgb1[1] * r + rgb2[1] * ir;
     float blue = rgb1[2] * r + rgb2[2] * ir;
     if(red < 0.0F) {
        red = 0.0F;
     } else if(red > 255.0F) {
        red = 255.0F;
     }

     if(green < 0.0F) {
        green = 0.0F;
     } else if(green > 255.0F) {
        green = 255.0F;
     }

     if(blue < 0.0F) {
        blue = 0.0F;
     } else if(blue > 255.0F) {
        blue = 255.0F;
     }

     Color color3 = null;

     try {
        color3 = new Color(red, green, blue);
     } catch (IllegalArgumentException var14) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        System.out.println(nf.format((double)red) + "; " + nf.format((double)green) + "; " + nf.format((double)blue));
        var14.printStackTrace();
     }

     return color3;
  }

  private double getIncremental(double val, double inc) {
     double one = 1.0D / inc;
     return (double)Math.round(val * one) / one;
  }

  
  @EventHandler
  public void onScreenDraw(EventRender2D er2) {
      double Dis;
      ScaledResolution res;
      float health;
      String str;
      double width;
      double healthLocation;
      Color[] colors;
      EntityLivingBase player;
      double HEALTH;
      BigDecimal bigDecimal;
      Object str3;
      Color customColor;
      int x2;
      float[] fractions;
      BigDecimal DT;
      int y2;
      String COLOR1;
      float progress;
      CFontRenderer font = FontLoaders.kiona16;
      int heal = 0;
      int input = 0;
      if (mode.getValue() == rendermode.Zeroday && KillAura.target != null) {
          if (KillAura.target.hurtResistantTime == 0) {
              heal = (int)KillAura.target.getHealth();
          }
          if (KillAura.target.hurtResistantTime != 0) {
              input = (int)KillAura.target.getHealth() - heal;
          }
          Gui.drawRect(RenderUtil.width() / 2 + 117, RenderUtil.height() / 2 + 150, RenderUtil.width() / 2 + 280, RenderUtil.height() / 2 + 210, new Color(0, 0, 0, 200).getRGB());
          font.drawStringWithShadow(KillAura.target.getName(), RenderUtil.width() / 2 + 155, RenderUtil.height() / 2 + 155, -1);
          font.drawStringWithShadow(KillAura.target.onGround ? "On Ground | Distance:" + (int)KillAura.target.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer) + " | Hurt:" + KillAura.target.hurtResistantTime : "Off Ground | Distance:" + (int)KillAura.target.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer) + " | Hurt:" + KillAura.target.hurtResistantTime, RenderUtil.width() / 2 + 155, RenderUtil.height() / 2 + 168, -1);
          font.drawStringWithShadow("Damage Output: ", RenderUtil.width() / 2 + 155, RenderUtil.height() / 2 + 177, -1);
          font.drawStringWithShadow("Damage Input: " + input, RenderUtil.width() / 2 + 155, RenderUtil.height() / 2 + 186, -1);
          font.drawStringWithShadow(KillAura.target.getHealth() > Minecraft.getMinecraft().thePlayer.getHealth() ? "You may lose" : (KillAura.target.getHealth() <= Minecraft.getMinecraft().thePlayer.getHealth() ? "You may win" : ""), RenderUtil.width() / 2 + 155, RenderUtil.height() / 2 + 195, -1);
          RenderUtil.drawEntityOnScreen(RenderUtil.width() / 2 + 136, RenderUtil.height() / 2 + 205, 23, 1.0f, 25.0f, KillAura.target);
          if (this.anima <= (double)(KillAura.target.getHealth() * 8.0f)) {
              this.anima += 2.0;
          }
          if (this.anima > (double)(KillAura.target.getHealth() * 8.0f)) {
              this.anima -= 2.0;
          }
          RenderUtil.drawGradientSideways(RenderUtil.width() / 2 + 117, (double)(RenderUtil.height() / 2) + 208.5, (double)(RenderUtil.width() / 2 + 120) + this.anima, RenderUtil.height() / 2 + 210, new Color(255, 0, 0).getRGB(), new Color(255, 255, 0).getRGB());
      }
      if (mode.getValue() == rendermode.Exhibition) {
          res = new ScaledResolution(mc);
          int x22 = res.getScaledWidth() / 2 + 10;
          int y22 = res.getScaledHeight() - 90;
          player = KillAura.target;
          if (player != null) {
              GlStateManager.pushMatrix();
              Gui.drawRect((double)x22 + 0.0, (double)y22 + 0.0, (double)x22 + 113.0, (double)y22 + 36.0, Colors.getColor(0, 150));
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(player.getName(), (float)x22 + 38.0f, (float)y22 + 2.0f, -1);
              bigDecimal = new BigDecimal(player.getHealth());
              bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
              HEALTH = bigDecimal.doubleValue();
              DT = new BigDecimal(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player));
              DT = DT.setScale(1, RoundingMode.HALF_UP);
              Dis = DT.doubleValue();
              health = player.getHealth();
              fractions = new float[]{0.0f, 0.5f, 1.0f};
              colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
              progress = health / player.getMaxHealth();
              customColor = health >= 0.0f ? TargetHUD.blendColors(fractions, colors, progress).brighter() : Color.RED;
              width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName());
              width = this.getIncremental(width, 10.0);
              if (width < 50.0) {
                  width = 50.0;
              }
              if (this.anima < (healthLocation = width * (double)progress) + 1.0) {
                  this.anima += 1.0;
              }
              if (this.anima > healthLocation + 1.0) {
                  this.anima -= 1.0;
              }
              Gui.drawRect((double)x22 + 37.5, (double)y22 + 11.5, (double)x22 + 37.5 + this.anima, (double)y22 + 14.5, customColor.getRGB());
              RenderUtil.rectangleBordered((double)x22 + 37.0, (double)y22 + 11.0, (double)x22 + 39.0 + width, (double)y22 + 15.0, 0.5, Colors.getColor(0, 0), Colors.getColor(0));
              int i2 = 1;
              while (i2 < 10) {
                  double dThing = width / 10.0 * (double)i2;
                  Gui.drawRect((double)x22 + 38.0 + dThing, (double)y22 + 11.0, (double)x22 + 38.0 + dThing + 0.5, (double)y22 + 15.0, Colors.getColor(0));
                  ++i2;
              }
              COLOR1 = (double)health > 20.0 ? " \u00a79" : ((double)health >= 10.0 ? " \u00a7a" : ((double)health >= 3.0 ? " \u00a7e" : " \u00a74"));
              GlStateManager.scale(0.5, 0.5, 0.5);
              String str4 = "HP: " + HEALTH + " Dist: " + Dis;
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str4, (float)(x22 * 2) + 76.0f, (float)(y22 * 2) + 35.0f, -1);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("" + HEALTH, (float)(x22 * 2) + 93.0f, (float)(y22 * 2) + 35.0f, customColor.getRGB());
              String str2 = String.format("Yaw: %s Pitch: %s ", (int)player.rotationYaw, (int)player.rotationPitch);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str2, (float)(x22 * 2) + 76.0f, (float)(y22 * 2) + 47.0f, -1);
              str3 = String.format("G: %s HURT: %s TE: %s", player.onGround, player.hurtTime, player.ticksExisted);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow((String)str3, (float)(x22 * 2) + 76.0f, (float)(y22 * 2) + 59.0f, -1);
              GlStateManager.scale(2.0f, 2.0f, 2.0f);
              GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
              GlStateManager.enableAlpha();
              GlStateManager.enableBlend();
              GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
              if (player instanceof EntityPlayer) {
                  List<NetworkPlayerInfo> var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy(Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap());
                  for (NetworkPlayerInfo aVar5 : var5) {
                      NetworkPlayerInfo var6 = aVar5;
                      if (Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(var6.getGameProfile().getId()) != player) continue;
                      mc.getTextureManager().bindTexture(var6.getLocationSkin());
                      Gui.drawScaledCustomSizeModalRect(x22 + 2, y22 + 2, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                      if (((EntityPlayer)player).isWearing(EnumPlayerModelParts.HAT)) {
                          Gui.drawScaledCustomSizeModalRect(x22 + 2, y22 + 2, 40.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                      }
                      GlStateManager.bindTexture(0);
                      break;
                  }
              }
              GlStateManager.popMatrix();
          }
      }
      if (mode.getValue() == rendermode.Exhibition2) {
          res = new ScaledResolution(mc);
          x2 = res.getScaledWidth() / 2 + 10;
          y2 = res.getScaledHeight() - 90;
          player = KillAura.target;
          if (player != null) {
              GlStateManager.pushMatrix();
              Gui.drawRect((double)x2 + 0.0, (double)y2 - 5.0, (double)x2 + 113.0, (double)y2 + 36.0, Colors.getColor(0, 150));
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(player.getName(), (float)x2 + 38.0f, (float)y2 + 2.0f, -1);
              bigDecimal = new BigDecimal(player.getHealth());
              bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
              HEALTH = bigDecimal.doubleValue();
              DT = new BigDecimal(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player));
              DT = DT.setScale(1, RoundingMode.HALF_UP);
              Dis = DT.doubleValue();
              health = player.getHealth();
              fractions = new float[]{0.0f, 0.5f, 1.0f};
              colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
              progress = health / player.getMaxHealth();
              customColor = health >= 0.0f ? TargetHUD.blendColors(fractions, colors, progress).brighter() : Color.RED;
              width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName());
              width = this.getIncremental(width, 10.0);
              if (width < 50.0) {
                  width = 50.0;
              }
              if (this.anima < (healthLocation = width * (double)progress) + 1.0) {
                  this.anima += 1.0;
              }
              if (this.anima > healthLocation + 1.0) {
                  this.anima -= 1.0;
              }
              Gui.drawRect((double)x2 + 37.5, (double)y2 + 11.5, (double)x2 + 37.5 + this.anima, (double)y2 + 14.5, customColor.getRGB());
              RenderUtil.drawGradientSideways(x2, y2 + 35, (double)x2 + this.anima * 2.22, y2 + 36, customColor.getRGB(), customColor.getRGB());
              RenderUtil.rectangleBordered((double)x2 + 37.0, (double)y2 + 11.0, (double)x2 + 39.0 + width, (double)y2 + 15.0, 0.5, Colors.getColor(0, 0), Colors.getColor(0));
              int i3 = 1;
              while (i3 < 10) {
                  double dThing = width / 10.0 * (double)i3;
                  Gui.drawRect((double)x2 + 38.0 + dThing, (double)y2 + 11.0, (double)x2 + 38.0 + dThing + 0.5, (double)y2 + 15.0, Colors.getColor(0));
                  ++i3;
              }
              COLOR1 = (double)health > 20.0 ? " \u00a79" : ((double)health >= 10.0 ? " \u00a7a" : ((double)health >= 3.0 ? " \u00a7e" : " \u00a74"));
              GlStateManager.scale(0.5, 0.5, 0.5);
              str = "HP: " + HEALTH + " Dist: " + Dis;
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str, (float)(x2 * 2) + 76.0f, (float)(y2 * 2) + 35.0f, -1);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("" + HEALTH, (float)(x2 * 2) + 93.0f, (float)(y2 * 2) + 35.0f, customColor.getRGB());
              String str2 = String.format("Yaw: %s Pitch: %s ", (int)player.rotationYaw, (int)player.rotationPitch);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str2, (float)(x2 * 2) + 76.0f, (float)(y2 * 2) + 47.0f, -1);
              str3 = String.format("G: %s HURT: %s TE: %s", player.onGround, player.hurtTime, player.ticksExisted);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow((String)str3, (float)(x2 * 2) + 76.0f, (float)(y2 * 2) + 59.0f, -1);
              GlStateManager.scale(2.0f, 2.0f, 2.0f);
              GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
              GlStateManager.enableAlpha();
              GlStateManager.enableBlend();
              GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
              GlStateManager.popMatrix();
              RenderUtil.drawEntityOnScreen(x2 + 18, y2 + 34, 16, 1.0f, 15.0f, KillAura.target);
          }
      }
      if (mode.getValue() == rendermode.Stella) {
          res = new ScaledResolution(mc);
          x2 = res.getScaledWidth() / 2 + 10;
          y2 = res.getScaledHeight() - 90;
          player = KillAura.target;
          if (player != null) {
              bigDecimal = new BigDecimal(player.getHealth());
              bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
              HEALTH = bigDecimal.doubleValue();
              DT = new BigDecimal(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player));
              DT = DT.setScale(1, RoundingMode.HALF_UP);
              Dis = DT.doubleValue();
              health = player.getHealth();
              fractions = new float[]{0.0f, 0.5f, 1.0f};
              colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
              progress = health / player.getMaxHealth();
              customColor = health >= 0.0f ? TargetHUD.blendColors(fractions, colors, progress).brighter() : Color.RED;
              width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName());
              if ((width = this.getIncremental(width, 10.0)) < 50.0) {
                  width = 50.0;
              }
              Gui.drawRect((double)x2 + 10.0, (double)y2 - 2.0, (double)x2 + 91.0, (double)y2 + 40.0, Colors.getColor(0, 150));
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(player.getName(), (float)x2 + 38.0f, (float)y2 + 2.0f, -1);
              healthLocation = width * (double)progress;
              if (this.anima < healthLocation + 1.0) {
                  this.anima += 1.0;
              }
              if (this.anima > healthLocation + 1.0) {
                  this.anima -= 1.0;
              }
              Gui.drawRect((double)x2 + 37.5, (double)y2 + 11.5, (double)x2 + 38.0 + 50.0 + 0.5, y2 + 17, new Color(180, 180, 180, 120).getRGB());
              Gui.drawRect((double)x2 + 37.5, (double)y2 + 11.5, (double)x2 + 38.0 + this.anima + 0.5, y2 + 17, customColor.getRGB());
              COLOR1 = (double)health > 20.0 ? " \u00a79" : ((double)health >= 10.0 ? " \u00a7a" : ((double)health >= 3.0 ? " \u00a7e" : " \u00a74"));
              RenderUtil.rectangleBordered(x2 + 10, y2 - 2, x2 + 91, (double)y2 + 40.0, 0.5, Colors.getColor(0, 0), Colors.getColor(255));
              RenderUtil.rectangleBordered(x2 + 10, y2 - 2, x2 + 35, (double)y2 + 40.0, 0.5, Colors.getColor(0, 0), Colors.getColor(255));
              RenderUtil.drawEntityOnScreen(x2 + 22, y2 + 36, 18, 1.0f, 15.0f, KillAura.target);
              str = "HP: " + HEALTH;
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str, (float)x2 + 38.0f, (float)y2 + 20.0f, -1);
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("Ping:" + (!mc.isSingleplayer() ? Integer.valueOf(Minecraft.getMinecraft().getNetHandler().getPlayerInfo(KillAura.target.getUniqueID()).getResponseTime()) : "0"), (float)x2 + 38.0f, (float)y2 + 29.0f, -1);
          }
      }
      if (mode.getValue() == rendermode.SouthSide) {
          res = new ScaledResolution(mc);
          x2 = res.getScaledWidth() / 2 + 10;
          y2 = res.getScaledHeight() - 90;
          player = KillAura.target;
          if (player != null) {
              bigDecimal = new BigDecimal(player.getHealth());
              bigDecimal = bigDecimal.setScale(1, RoundingMode.HALF_UP);
              HEALTH = bigDecimal.doubleValue();
              DT = new BigDecimal(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player));
              DT = DT.setScale(1, RoundingMode.HALF_UP);
              Dis = DT.doubleValue();
              health = player.getHealth();
              fractions = new float[]{0.0f, 0.5f, 1.0f};
              colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
              progress = health / player.getMaxHealth();
              customColor = health >= 0.0f ? TargetHUD.blendColors(fractions, colors, progress).brighter() : Color.RED;
              width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName());
              if ((width = this.getIncremental(width, 10.0)) < 50.0) {
                  width = 50.0;
              }
              if (this.anima < (healthLocation = width * (double)progress * 1.2) + 1.0) {
                  this.anima += 1.0;
              }
              if (this.anima > healthLocation + 1.0) {
                  this.anima -= 1.0;
              }
              RenderUtil.rectangleBordered(x2 - 2, (double)y2 - 2.0, (double)x2 + 63.0 + (double)Minecraft.getMinecraft().fontRendererObj.getStringWidth(player.getName()), (double)y2 + 38.5, 0.5, new Color(53, 56, 61).getRGB(), new Color(210, 210, 210).getRGB());
              Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(player.getName(), (float)x2 + 38.0f, (float)y2 + 4.0f, -1);
              Gui.drawRect((double)x2 + 37.5, y2 + 17, (double)x2 + 38.0 + this.anima + 0.5, y2 + 29, new Color(180, 180, 180, 120).getRGB());
              Gui.drawRect((double)x2 + 37.5, y2 + 17, (double)x2 + 38.0 + this.anima + 0.5, y2 + 29, customColor.getRGB());
              Gui.drawRect((double)x2 + 37.5, y2 + 17, (double)x2 + 38.0 + this.anima + 0.5, y2 + 29, customColor.getRGB());
              RenderUtil.rectangleBordered(x2 + 37, y2 + 17, (double)x2 + 38.0 + this.anima + 0.5, y2 + 29, 0.5, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0).getRGB());
              font.drawStringWithShadow(String.valueOf((int)player.getHealth()) + "/20", (float)x2 + 60.0f, (float)y2 + 18.0f, -1);
              COLOR1 = (double)health > 20.0 ? " \u00a79" : ((double)health >= 10.0 ? " \u00a7a" : ((double)health >= 3.0 ? " \u00a7e" : " \u00a74"));
              if (player instanceof EntityPlayer) {
                  List<NetworkPlayerInfo> var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy(Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap());
                  for (Object aVar5 : var5) {
                      NetworkPlayerInfo var6 = (NetworkPlayerInfo)aVar5;
                      if (Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID(var6.getGameProfile().getId()) != player) continue;
                      mc.getTextureManager().bindTexture(var6.getLocationSkin());
                      Gui.drawScaledCustomSizeModalRect(x2 + 2, y2 + 2, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                      RenderUtil.rectangleBordered(x2 + 2, y2 + 2, x2 + 34, y2 + 34, 0.5, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0).getRGB());
                      if (((EntityPlayer)player).isWearing(EnumPlayerModelParts.HAT)) {
                          Gui.drawScaledCustomSizeModalRect(x2 + 2, y2 + 2, 40.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
                          RenderUtil.rectangleBordered(x2 + 2, y2 + 2, x2 + 34, y2 + 34, 0.5, new Color(0, 0, 0, 0).getRGB(), new Color(0, 0, 0).getRGB());
                      }
                      GlStateManager.bindTexture(0);
                      break;
                  }
              }
          }
      }
  }


    
    
	static enum rendermode {
		Rainbowline,
		HTB,
		Mikov,
		Flat,
		BLC, Klrui,New,Zeroday,Exhibition,Exhibition2,Stella,SouthSide
    }
	public enum Direction {
        S("S", 0), 
        SW("SW", 1), 
        W("W", 2), 
        NW("NW", 3), 
        N("N", 4), 
        NE("NE", 5), 
        E("E", 6), 
        SE("SE", 7);
        private Direction(final String s, final int n) {}
    }

}
