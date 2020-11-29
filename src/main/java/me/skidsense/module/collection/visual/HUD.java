package me.skidsense.module.collection.visual;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.gui.tabgui.TabMain;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventKey;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.ModManager;
import me.skidsense.management.fontRenderer.TTFFontRenderer;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.ColorCreator;
import me.skidsense.util.Draw;
import me.skidsense.util.MathUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.SpeedCalculator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;

public class HUD
extends Mod {
    public TabGUI tabui;
    public static Mode<arrayPosition> mode = new Mode<arrayPosition>("ArrayPosition", "ArrayPosition", arrayPosition.values(), arrayPosition.TopRight);
    public static Option<Boolean> TABGUI = new Option<Boolean>("TabGui", "TabGui", true);
    public Option<Boolean> info = new Option<Boolean>("Information", "information", true);
    public Option<Boolean> hideRenderModule = new Option<Boolean>("HideRenderModule", "HideRenderModule", false);
    public Option<Boolean> animegirl = new Option<Boolean>("AnimeGirl", "AnimeGirl", false);
    public Option<Boolean> rainbow = new Option<Boolean>("Rainbow", "rainbow", false);
    private final TTFFontRenderer Client_Font = Client.instance.fontManager.comfortaa18;
    private final SpeedCalculator speedc = new SpeedCalculator();
    private TabMain tabGUI;
    
    public HUD() {
        super("HUD", new String[]{"gui"}, ModuleType.Visual);
        this.setEnabled(true);
        this.setRemoved(true);
    }

    @Override
    public void onEnable() {
        tabGUI = new TabMain(2, 12);
        tabGUI.init();
    }

    @Override
    public void onDisable() {
        tabGUI = null;
    }
    
    @Sub
    public final void onUpdate(EventPreUpdate eventPreUpdate) {
    	speedc.update();
    }
    
    @Sub
    private void renderHud(EventRenderGui event) {
        if (!mc.gameSettings.showDebugInfo) {
        	if(!this.isFlux()) {
        		this.drawExusiaiWatermark(event);
        		this.drawExusiaiArrayList(event);
        		this.drawExusiaiTabGui(event);
        	}else {
        		this.drawFluxWatermark();
        		this.drawFluxArrayList();
        		this.drawFluxTabGui();
			}
            if (this.info.getValue().booleanValue()) {
            	String text = (Object)(EnumChatFormatting.GRAY) + "X" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(mc.thePlayer.posX) + " " + (Object)((Object)EnumChatFormatting.GRAY) + "Y" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(mc.thePlayer.posY) + " " + (Object)((Object)EnumChatFormatting.GRAY) + "Z" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(mc.thePlayer.posZ) + (EnumChatFormatting.GRAY) + " FPS: " + (EnumChatFormatting.WHITE) + Minecraft.getDebugFPS();
            	String speedtextString = EnumChatFormatting.GRAY +""+ (MoveUtil.isMoving() ? MathUtil.round(speedc.getCurrentSpeed(), 2) : 0) +EnumChatFormatting.WHITE+ " m / sec";
                Client_Font.drawStringWithShadow(text,(event.getResolution().getScaledWidth() - Client_Font.getWidth(text)) / 2, BossStatus.statusBarTime > 0 ? 20 : 2, -788529153);
                Client_Font.drawStringWithShadow(speedtextString ,(event.getResolution().getScaledWidth() - Client_Font.getWidth(speedtextString)) / 2, BossStatus.statusBarTime > 0 ? 20 + Client_Font.getHeight(speedtextString) : 2 + Client_Font.getHeight(speedtextString), -788529153);
            }
            if(animegirl.getValue()) {
                RenderUtil.drawImage(new ResourceLocation("skidsense/AstolfoTrifasSprite.png"), RenderUtil.width() - 160, RenderUtil.height() - 70, 256, 256);	
            }
            this.drawPotionStatus(event.getResolution());
        }
    }

    @Sub
    public void onKeyPress(EventKey event) {
        if (TABGUI.getValue() && !this.isFlux()) tabGUI.onKeypress(event.getKey());
    }
    
    public void drawExusiaiWatermark(EventRenderGui event) {
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
	}

    public void drawExusiaiTabGui(EventRenderGui event) {
    	if (TABGUI.getValue()) tabGUI.onRender(event.getResolution());
    }
    
    public void drawExusiaiArrayList(EventRenderGui event) {
    	boolean left = mode.getValue() == arrayPosition.TopLeft;
    	int y = left ? (TABGUI.getValue() ? 75 : 12) : 0;
    	List<Mod> modules = new CopyOnWriteArrayList<Mod>();
    	 List<Mod> var21 = Client.getModuleManager().getMods();
         int rainbowTick = 0;
         for(int i = 0; i < var21.size(); ++i) {
        	 Mod module = var21.get(i);
            if (module.isEnabled() || module.translate.getX() != -50.0F) {
               modules.add(module);
            }
            if (!module.isEnabled() || module.wasRemoved()) {
               module.translate.interpolate(left ? -50.0F : (float)event.getResolution().getScaledWidth(), -20.0F, 0.6F);
            }
         }
         modules.sort(Comparator.comparingDouble((o) -> {
            return -MathUtils.getIncremental((double)Client_Font.getWidth(o.getSuffix() != null ? o.getName() + " " + o.getSuffix() : o.getName()), 0.5D);
         }));
         for (int i = 0; i < modules.size(); i++) {
        	 Mod module = (Mod)modules.get(i);
             String suffix = module.getSuffix() != null ? "§7" + module.getSuffix() : "";
             float x = left ? 2.0F : (float)event.getResolution().getScaledWidth() - Client_Font.getWidth(module.getName() + suffix) + 0.5F;
             if (module.isEnabled() && !module.wasRemoved()) {
                module.translate.interpolate(x, (float)y, 0.35F);
             }
             Color rainbow = new Color(Color.HSBtoRGB((float)((double)mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
             Client_Font.drawStringWithShadow(module.getName() + suffix, module.translate.getX(), module.translate.getY(), this.rainbow.getValue() != false ? rainbow.getRGB() : getCategoryColor(module));
             if (module.isEnabled() && !module.wasRemoved()) {
                 if (++rainbowTick > 50) {
                     rainbowTick = 0;
                 }
                y += 9;
             }
         }
    }
    
    public void drawFluxWatermark() {
		Client.instance.fontManager.arial25.drawStringWithShadow("Flux", 2, 3, ColorCreator.createRainbowFromOffset(-6000, 5));
	}
    
    public void drawLBV4Watermark() {
    	FontRenderer fr = mc.fontRendererObj;
        ScaledResolution s = new ScaledResolution(mc);
        GL11.glPushMatrix();
        GL11.glScalef(2, 2, 2);
        boolean left = mode.getValue() == arrayPosition.TopLeft;
        if (left)
        {
            fr.drawStringWithShadow("LBV4" + "§c!", 2, 4, 0xffffff);
        }
        else
        {
            fr.drawStringWithShadow("LBV4" + "§c!", (s.getScaledWidth() - 58) / 2, 4, 0xffffff);
        }

        GL11.glPopMatrix();
	}
    
	public void drawLBV4ArrayList(EventRenderGui eventRenderGui) {
		boolean left = mode.getValue() == arrayPosition.TopLeft;
		ArrayList<Mod> sorted = new ArrayList<Mod>();
		int y = 32;
		for (Mod m : Client.instance.getModuleManager().getMods()) {
            if (!m.isEnabled() || m.wasRemoved()) continue;
            if(m.getType() == ModuleType.Visual) {
            	if(!hideRenderModule.getValue()) {
            		sorted.add(m);
            	}
            }else {
            	sorted.add(m);
			}
            sorted.sort(new Comparator<Mod>()
            {
                public int compare(Mod o1, Mod o2)
                {
                    return Minecraft.getMinecraft().fontRendererObj.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName().replace(" ", "") : String.format("%s %s", o2.getName().replace(" ", ""), o2.getSuffix())) - Minecraft.getMinecraft().fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName().replace(" ", "") : String.format("%s %s", o1.getName().replace(" ", ""), o1.getSuffix()));
                }
            });
            //sorted.sort((o1, o2) -> Minecraft.getMinecraft().fontRendererObj.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName().replace(" ", "") : String.format("%s %s", o2.getName().replace(" ", ""), o2.getSuffix())) - Minecraft.getMinecraft().fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName().replace(" ", "") : String.format("%s %s", o1.getName().replace(" ", ""), o1.getSuffix())));
            String modname = m.getSuffix().isEmpty() ? m.getName().replace(" ", "") : String.format("%s§f%s", m.getName().replace(" ", ""), m.getSuffix());
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(modname, left ? 4 : eventRenderGui.getResolution().getScaledWidth() - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(m.getName()) + 4), y, rainbow.getValue() ? rainbowModuleList(5000, 15 * y) : -1);
            y += 9;
		}
	}
	
	public void drawFluxArrayList() {
		ArrayList<Mod> sorted = new ArrayList<Mod>();
		for (Mod m : Client.instance.getModuleManager().getMods()) {
            if (!m.isEnabled() || m.wasRemoved()) continue;
            if(m.getType() == ModuleType.Visual) {
            	if(!hideRenderModule.getValue()) {
            		sorted.add(m);
            	}
            }else {
            	sorted.add(m);
			}
        }
        sorted.sort(new Comparator<Mod>()
        {
            public int compare(Mod o1, Mod o2)
            {
                return Client.instance.fontManager.arialbold17.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName().replace(" ", "") : String.format("%s %s", o2.getName().replace(" ", ""), o2.getSuffix())) - Client.instance.fontManager.arialbold17.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName().replace(" ", "") : String.format("%s %s", o1.getName().replace(" ", ""), o1.getSuffix()));
            }
        });
        int y = 3;
        int rainbowTick = 0;
            for (Mod m : sorted) {
                String modname = m.getSuffix().isEmpty() ? m.getName().replace(" ", "") : String.format("%s§f%s", m.getName().replace(" ", ""), m.getSuffix());
                float x = RenderUtil.width() - Client.instance.fontManager.arialbold17.getStringWidth(modname);
                Color rainbow = new Color(Color.HSBtoRGB((float)((double)mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                Draw.drawRectangle(RenderUtil.width(), y, RenderUtil.width() - 2, y + 13,
                		rainbow.getRGB());
                Draw.drawRectangle(RenderUtil.width() - 2, y, x - 4.5, y + 13 , new Color(1, 1, 1, 150).getRGB());
                Client.instance.fontManager.arialbold17.drawString(modname, x - 3, y + 3, rainbow.getRGB());
                if (++rainbowTick > 50) {
                    rainbowTick = 0;
                }
                y += 13;
            }
	}
    
	public void drawFluxTabGui() {
		if (!TABGUI.getValue()) return;
		Draw.drawRectangle(3, 18, 58, 104, new Color(0,0,0,150).getRGB());
		Draw.drawRectangle(4, 20, 5.5, 29, new Color(206,89,255,255).getRGB());
		Client.instance.fontManager.arial18.drawStringWithShadow("§fCombat", 7, 20, -1);
		Client.instance.fontManager.arial18.drawStringWithShadow("§7Movement", 7, 32, -1);
		Client.instance.fontManager.arial18.drawStringWithShadow("§7Render", 7, 44, -1);
		Client.instance.fontManager.arial18.drawStringWithShadow("§7Player", 7, 56, -1);
		Client.instance.fontManager.arial18.drawStringWithShadow("§7World", 7, 68, -1);
		Client.instance.fontManager.arial18.drawStringWithShadow("§7Ghost", 7, 80, -1);
		Client.instance.fontManager.arial18.drawStringWithShadow("§7Misc", 7, 92, -1);
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
              name = name + " 2";
           } else if (effect.getAmplifier() == 2) {
              name = name + " 3";
           } else if (effect.getAmplifier() == 3) {
              name = name + " 4";
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
    
    public boolean isFlux() {
    	if("Flux".equalsIgnoreCase(Client.clientName) || "FluxClient".equalsIgnoreCase(Client.clientName)) {
    		return true;
    	}
    	return false;
    }
    
    private int rainbowModuleList(int speed, int offset) {
        float color = (System.currentTimeMillis() + offset) % speed;
        color /= speed;
        return Color.getHSBColor(color, 1f, 1f).getRGB();
    }
    
    public enum arrayPosition {
    	TopRight,
    	TopLeft
    }
}

