package me.skidsense.module.collection.visual;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.ModManager;
import me.skidsense.management.fontRenderer.TTFFontRenderer;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.SpeedCalculator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;

public class HUD
extends Mod {
    public TabGUI tabui;
    public static Mode<Enum> mode = new Mode("ArrayPosition", "ArrayPosition", (Enum[])arrayPosition.values(), (Enum)arrayPosition.TopRight);
    public static Option<Boolean> TABGUI = new Option<Boolean>("TabGui", "TabGui", true);
    private Option<Boolean> info = new Option<Boolean>("Information", "information", true);
    private Option<Boolean> hideRenderModule = new Option<Boolean>("HideRenderModule", "HideRenderModule", false);
    private Option<Boolean> animegirl = new Option<Boolean>("AnimeGirl", "AnimeGirl", false);
    private Option<Boolean> rainbow = new Option<Boolean>("Rainbow", "rainbow", false);
    public static boolean shouldMove;
    private final TTFFontRenderer Client_Font = Client.instance.fontManager.comfortaa18;
    private final SpeedCalculator speedc = new SpeedCalculator();

    public HUD() {
        super("HUD", new String[]{"gui"}, ModuleType.Visual);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        this.setEnabled(true);
        this.setRemoved(true);
    }

    @Sub
    public final void onUpdate(EventPreUpdate eventPreUpdate) {
    	speedc.update();
    }
    
    @Sub
    private void renderHud(EventRenderGui event) {
        if (!this.mc.gameSettings.showDebugInfo) {
            String name;
            String direction;
            HUD.shouldMove = false;
	        String[] a;
	        String first;
	        String second;
            if((a = Client.clientName.split("\\|")).length > 2){
            	first = a[0];
            	second = a[1];
            } else {
				try {
					first = Client.clientName.substring(0, 1);
				} catch (IndexOutOfBoundsException e){
					first = "";
				}
            	try {
		            second = Client.clientName.substring(1);
	            } catch (IndexOutOfBoundsException e){
            		second = "";
	            }
            }
            Client_Font.drawStringWithShadow(first, 2, (float)2, new Color(220,1,5).getRGB());
            Client_Font.drawStringWithShadow(second, Client_Font.getStringWidth(first), (float)2, new Color(255,255,255).getRGB());
            Client_Font.drawStringWithShadow("#001", Client_Font.getStringWidth(Client.clientName)+4, 2, new Color(180,180,180).getRGB());
            ArrayList<Mod> sorted = new ArrayList<Mod>();
            boolean left = mode.getValue() == arrayPosition.TopLeft;
			for (Mod m : ModManager.getMods()) {
                if (!m.isEnabled() || m.wasRemoved()) continue;
                if(m.getType() == ModuleType.Visual) {
                	if(!hideRenderModule.getValue()) {
                		sorted.add(m);
                	}
                }else {
                	sorted.add(m);
				}
            }
                sorted.sort((o1, o2) -> Client_Font.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - Client_Font.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix())));
            int y = left ? (TABGUI.getValue() ? 75 : 12) : 0;
            int rainbowTick = 0;
                for (Mod m : sorted) {
                    if (!m.isEnabled()) {
                        m.translate.interpolate((float)event.getResolution().getScaledWidth(), -20.0F, 0.6F);
                     }
                    name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s\2477%s", m.getName(), m.getSuffix());
                    float x = left ? 2.0F :RenderUtil.width() - Client_Font.getStringWidth(name);
                    Color rainbow = new Color(Color.HSBtoRGB((float)((double)this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                    if (m.isEnabled()) {
                       m.translate.interpolate(x, (float)y, 0.40F);
                    }
                    Client_Font.drawStringWithShadow(name, m.translate.getX() - 0.1f, m.translate.getY(), this.rainbow.getValue() != false ? rainbow.getRGB() : getCategoryColor(m));
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    y += 9;
                }
            String text = (Object)(EnumChatFormatting.GRAY) + "X" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(this.mc.thePlayer.posX) + " " + (Object)((Object)EnumChatFormatting.GRAY) + "Y" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(this.mc.thePlayer.posY) + " " + (Object)((Object)EnumChatFormatting.GRAY) + "Z" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(this.mc.thePlayer.posZ) + (EnumChatFormatting.GRAY) + " FPS: " + (EnumChatFormatting.WHITE) + Minecraft.getDebugFPS();
                if (this.info.getValue().booleanValue()) {
                    Client_Font.drawStringWithShadow(text,(event.getResolution().getScaledWidth() - Client_Font.getWidth(text)) / 2, BossStatus.statusBarTime > 0 ? 20 : 2, -788529153);
                    String speedtextString = EnumChatFormatting.GRAY +""+ (MoveUtil.isMoving() ? MathUtil.round(speedc.getCurrentSpeed(), 2) : 0) +EnumChatFormatting.WHITE+ " m / sec";
                    Client_Font.drawStringWithShadow(speedtextString ,(event.getResolution().getScaledWidth() - Client_Font.getWidth(speedtextString)) / 2, BossStatus.statusBarTime > 0 ? 20 + Client_Font.getHeight(speedtextString) : 2 + Client_Font.getHeight(speedtextString), -788529153);
                }
                if(animegirl.getValue()) {
                    RenderUtil.drawImage(new ResourceLocation("skidsense/AstolfoTrifasSprite.png"), RenderUtil.width() - 160, RenderUtil.height() - 70, 256, 256);	
                }
                this.drawPotionStatus(event.getResolution());
        }
    }

    private void drawPotionStatus(ScaledResolution sr) {
        TTFFontRenderer font = Client.instance.fontManager.comfortaa18;
        List<PotionEffect> potions = new ArrayList<PotionEffect>();
        Iterator<PotionEffect> var3 = mc.thePlayer.getActivePotionEffects().iterator();

        while(var3.hasNext()) {
           Object o = var3.next();
           potions.add((PotionEffect)o);
        }

        potions.sort(Comparator.comparingDouble((effectx) -> {
           return (double)(-font.getWidth(I18n.format(Potion.potionTypes[effectx.getPotionID()].getName())));
        }));
        float pY = mc.currentScreen != null && mc.currentScreen instanceof GuiChat ? -25.0F : -2.0F;

        for(Iterator<PotionEffect> var11 = potions.iterator(); var11.hasNext(); pY -= 9.0F) {
           PotionEffect effect = (PotionEffect)var11.next();
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

           if (effect.getDuration() < 600 && effect.getDuration() > 300) {
              PType = PType + "\2476 " + Potion.getDurationString(effect);
           } else if (effect.getDuration() < 300) {
              PType = PType + "\247c " + Potion.getDurationString(effect);
           } else if (effect.getDuration() > 600) {
              PType = PType + "\2477 " + Potion.getDurationString(effect);
           }

           Color c = new Color(potion.getLiquidColor());
           font.drawStringWithShadow(name, (float)sr.getScaledWidth() - font.getWidth(name + PType), (float)(sr.getScaledHeight() - 9) + pY, Colors.getColor(c.getRed(), c.getGreen(), c.getBlue()));
           font.drawStringWithShadow(PType, (float)sr.getScaledWidth() - font.getWidth(PType), (float)(sr.getScaledHeight() - 9) + pY, -1);
        }
    }
    
    public int getCategoryColor(Mod m){
    	switch (m.getType()){
    	case Fight:
    		return Colors.getColor(150, 53, 46);
    	case Visual:
    		return Colors.getColor(65, 118, 146);
    	case World:
    		return Colors.getColor(65, 155, 103);
    	case Move:
    		return Colors.getColor(200, 121, 0);  		
    	case Player:
    		return Colors.getColor(65, 134, 45);
    	}
    	return 0;
    }
    
    public enum arrayPosition {
    	TopRight,
    	TopLeft
    }
}

