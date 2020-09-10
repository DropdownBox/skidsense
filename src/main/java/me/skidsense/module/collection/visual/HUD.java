package me.skidsense.module.collection.visual;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.FriendManager;
import me.skidsense.management.ModManager;
import me.skidsense.management.fontRenderer.TTFFontRenderer;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
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
extends Mod {
    public TabGUI tabui;
    private Option<Boolean> info = new Option<Boolean>("Information", "information", true);
    private Option<Boolean> rainbow = new Option<Boolean>("Rainbow", "rainbow", false);
    public static boolean shouldMove;
    private final TTFFontRenderer Client_Font = Client.fontManager.comfortaa18;
    private String[] directions = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public HUD() {
        super("HUD", new String[]{"gui"}, ModuleType.Visual);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        this.setEnabled(true);
        this.setRemoved(true);
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
            Client_Font.drawStringWithShadow(first, 4, (float)2, new Color(220,1,5).getRGB());
            Client_Font.drawStringWithShadow(second, Client_Font.getStringWidth(first)+2, (float)2, new Color(255,255,255).getRGB());
            Client_Font.drawStringWithShadow("#001", Client_Font.getStringWidth(Client.clientName)+5, 2, new Color(180,180,180).getRGB());
            ArrayList<Mod> sorted = new ArrayList<Mod>();
			for (Mod m : ModManager.getMods()) {
                if (!m.isEnabled() || m.wasRemoved()) continue;
                sorted.add(m);
            }
                sorted.sort((o1, o2) -> Client_Font.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - Client_Font.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix())));
            int y = 0;
            int rainbowTick = 0;
                for (Mod m : sorted) {
                    if (!m.isEnabled()) {
                        m.translate.interpolate((float)event.getResolution().getScaledWidth(), -20.0F, 0.6F);
                     }
                    name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s\2477%s", m.getName(), m.getSuffix());
                    float x = RenderUtil.width() - Client_Font.getStringWidth(name);
                    Color rainbow = new Color(Color.HSBtoRGB((float)((double)this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double)rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                    if (m.isEnabled()) {
                       m.translate.interpolate(x, (float)y, 0.40F);
                    }
                    Client_Font.drawStringWithShadow(name, m.translate.getX() - 0.1f, m.translate.getY(), this.rainbow.getValue() != false ? rainbow.getRGB() : m.getColor());
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    y += 9;
                }
            String text = (Object)((Object)EnumChatFormatting.GRAY) + "X" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(this.mc.thePlayer.posX) + " " + (Object)((Object)EnumChatFormatting.GRAY) + "Y" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(this.mc.thePlayer.posY) + " " + (Object)((Object)EnumChatFormatting.GRAY) + "Z" + (Object)((Object)EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(this.mc.thePlayer.posZ);
                int ychat;
                int n = ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 25 : 10;
                if (this.info.getValue().booleanValue()) {
                    Client_Font.drawStringWithShadow(text, 4.0f, new ScaledResolution(this.mc).getScaledHeight() - ychat, new Color(11, 12, 17).getRGB());
                    Client_Font.drawStringWithShadow((Object)((Object)EnumChatFormatting.GRAY) + "FPS: " + (Object)((Object)EnumChatFormatting.WHITE) + Minecraft.getDebugFPS(), 2.0f, shouldMove ? 90 : 75, -1);
                    this.drawPotionStatus(new ScaledResolution(this.mc));
                    direction = this.directions[RotationUtil.wrapAngleToDirection(this.mc.thePlayer.rotationYaw, this.directions.length)];
                    Client_Font.drawStringWithShadow("[" + direction + "]", Client_Font.getStringWidth(String.valueOf("ETB") + " " + 0.6 + 2), shouldMove ? 15 : 2, new Color(102, 172, 255).getRGB());
                }
        }
    }

    private void drawPotionStatus(ScaledResolution sr) {
        int y = 0;
        for (PotionEffect effect : this.mc.thePlayer.getActivePotionEffects()) {
            int ychat;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName(), new Object[0]);
            switch (effect.getAmplifier()) {
                case 1: {
                    PType = String.valueOf(PType) + " II";
                    break;
                }
                case 2: {
                    PType = String.valueOf(PType) + " III";
                    break;
                }
                case 3: {
                    PType = String.valueOf(PType) + " IV";
                    break;
                }
            }
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = String.valueOf(PType) + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = String.valueOf(PType) + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = String.valueOf(PType) + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
            }
            int n = ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 5 : -10;
                Client_Font.drawStringWithShadow(PType, sr.getScaledWidth() - Client_Font.getStringWidth(PType) - 2, sr.getScaledHeight() - Client_Font.getHeight(PType) + y - 12 - ychat, potion.getLiquidColor());
            y -= 10;
        }
    }
}

