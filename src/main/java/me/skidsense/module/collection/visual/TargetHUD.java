package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.skidsense.Client;
import me.skidsense.color.ColorManager;
import me.skidsense.color.Colors;
import me.skidsense.gui.util.GuiUtil;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.MathUtil;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
public class TargetHUD extends Mod {
	
	float astolfoHelathAnim = 0f;
	
    public TargetHUD() {
        super("Target Info", new String[]{"TargetInfo"}, ModuleType.Visual);
    }
    
	@Override
	public void onEnable() {
		astolfoHelathAnim = 0f;
		super.onEnable();
	}
	
    @Sub
    public final void onRenderGui(EventRenderGui event) {
        float xOff = 80 - 2.5F;
        float yOff = 80 + 10.0F;
        float height = 50;
        RenderUtil.rectangleBordered((double)xOff, (double)(yOff - 6.0F), (double)(xOff + 110.0F), (double)(yOff + height), 0.5D, Colors.getColor(0, 0), Colors.getColor(10, 255));
        RenderUtil.rectangleBordered((double)xOff + 0.5D, (double)yOff - 5.5D, (double)(xOff + 110.0F) - 0.5D, (double)(yOff + height) - 0.5D, 0.5D, Colors.getColor(0, 0), Colors.getColor(48, 255));
        RenderUtil.rectangle((double)(xOff + 1.0F), (double)(yOff - 5.0F), (double)(xOff + 109.0F), (double)(yOff + height - 1.0F), Colors.getColor(17, 255));
        RenderUtil.rectangle((double)(xOff + 5.0F), (double)(yOff - 6.0F), (double)(xOff + Client.instance.fontManager.tahomabold11.getWidth("TargetInfo") + 5.0F), (double)(yOff - 4.0F), Colors.getColor(17, 255));
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        List var5 = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap());
        Iterator var17 = var5.iterator();

        while(var17.hasNext()) {
           Object aVar5 = var17.next();
           NetworkPlayerInfo var24 = (NetworkPlayerInfo)aVar5;
           if (mc.theWorld.getPlayerEntityByUUID(var24.getGameProfile().getId()) == Minecraft.getMinecraft().thePlayer) {
              mc.getTextureManager().bindTexture(var24.getLocationSkin());
              Gui.drawScaledCustomSizeModalRect((int)xOff + 130, (int)xOff + 150, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
              if (Minecraft.getMinecraft().thePlayer.isWearing(EnumPlayerModelParts.HAT)) {
                 Gui.drawScaledCustomSizeModalRect((int)xOff, (int)xOff, 40.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
              }

              GlStateManager.bindTexture(0);
              break;
           }
        }

        GlStateManager.popMatrix();
        Client.instance.fontManager.tahomabold11.drawStringWithShadow("TargetInfo", xOff + 5, yOff - 8, -1);
        Client.instance.fontManager.tahoma10.drawStringWithShadow(Minecraft.getMinecraft().thePlayer.getName(), xOff + 20, yOff + 6, -1);
        //Client.instance.fontManager.tahomabold11.drawStringWithShadow(String.valueOf(Minecraft.getMinecraft().thePlayer.getHealth()) + " HP", xOff + 65, yOff - 1, -1);
        RenderUtil.drawGradientRect(xOff + 45, yOff + 6, xOff + 100, yOff + 10, new Color(140, 10, 10, 255).getRGB(), new Color(255, 50, 50, 255).getRGB());
        drawcheckbox("OnGround", xOff, yOff, 5, 18, 100, 100, Minecraft.getMinecraft().thePlayer.onGround);
		GlStateManager.pushMatrix();
		// Player Armor
		EntityPlayer entityplayer = (EntityPlayer) Minecraft.getMinecraft().thePlayer;
		ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
		int split = (int) (xOff - 13);
		int y2 = (int) (yOff + 30);
		int index = 3;
		while (index >= 0) {
			ItemStack armer = entityplayer.inventory.armorInventory[index];
			if (armer != null) {
				stuff.add(armer);
			}
			--index;
		}
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            stuff.add(mc.thePlayer.getCurrentEquippedItem());
        }
		for (ItemStack errything : stuff) {
			RenderUtil.rectangleBordered((double)split + 36, (double)y2 - 1, (double)split + 20, (double)y2 + 17, 1.0, new Color(0,0,0).getRGB(), new Color(255,255,255).getRGB());
			if (Minecraft.getMinecraft().theWorld != null) {
				RenderHelper.enableGUIStandardItemLighting();
				split += 20;
			}
			GlStateManager.disableAlpha();
			GlStateManager.clear(256);
			mc.getRenderItem().zLevel = -150.0f;
			mc.getRenderItem().renderItemAndEffectIntoGUI(errything, split, y2);
			mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, errything, split, y2);
			mc.getRenderItem().zLevel = 0.0f;
			GlStateManager.disableBlend();
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.disableDepth();
			GlStateManager.disableLighting();
			GlStateManager.enableDepth();
			GlStateManager.scale(2.0f, 2.0f, 2.0f);
			GlStateManager.enableAlpha();
		}
		GlStateManager.popMatrix();
        /*int x = event.getResolution().getScaledWidth() / 2 + 20;
		int y = event.getResolution().getScaledHeight() / 2 - 30;
		if (KillAura.target != null) {
			EntityLivingBase player = KillAura.target;
			RenderUtil.pre();
			GlStateManager.pushMatrix();

			// BaseRect(black)
			Gui.drawRect(x + 0.7f, y, x + 149.7f, y + 60, new Color(0, 0, 0, 110).getRGB());

			// health color math
			float health = player.getHealth();
			float health2;
			float[] fractions = new float[] { 0.0f, 0.2f, 0.7f };
			Color[] colors = new Color[] { Color.RED, Color.YELLOW, Color.GREEN };
			float progress = health / player.getMaxHealth();
			Color customColor = health >= 0.0f ? ESP.blendColors(fractions, colors, progress).brighter()
					: Color.RED;

			customColor = customColor.darker();

			// Player name
			mc.fontRendererObj.drawStringWithShadow(player.getName(), x + 37, y + 8, -1);

			// Player Health

			GlStateManager.scale(1.5f, 1.5f, 1.5f);
			mc.fontRendererObj.drawStringWithShadow((int) player.getHealth() + " ❤", (x + 37) / 1.5f,
					(y + 20) / 1.5f, customColor.getRGB());

			// health rect animation
			double wdnmd = 150D;
			if ((double) this.astolfoHelathAnim < wdnmd
					* (double) (health2 = player.getHealth() / player.getMaxHealth())) {
				if (wdnmd * (double) health2 - (double) this.astolfoHelathAnim < 1.0) {
					this.astolfoHelathAnim = (float) (wdnmd * (double) health2);
				}
				this.astolfoHelathAnim = (float) ((double) this.astolfoHelathAnim + 4D);
			}
			if (wdnmd * (double) health2 - (double) this.astolfoHelathAnim > 1.0) {
				this.astolfoHelathAnim = (float) (wdnmd * (double) health2);
			}
			this.astolfoHelathAnim = (float) ((double) this.astolfoHelathAnim - 4D);
			if (astolfoHelathAnim < 0) {
				astolfoHelathAnim = 0;
			}

			// health rect base
			Gui.drawRect((x + 2.985f) / 1.5f, (y + 55) / 1.5f, (x + 148) / 1.5f, (y + 58) / 1.5f,
					new Color(customColor.getRed(), customColor.getGreen(), customColor.getBlue(), 100).getRGB());

			// health rect main
			Gui.drawRect((x + 2.985f) / 1.5f, (y + 55) / 1.5f, (x + 2 + (astolfoHelathAnim)) / 1.5f,
					(y + 58) / 1.5f, customColor.getRGB());

			GlStateManager.popMatrix();
			RenderUtil.post();

			// Player Model
			GlStateManager.color(1.0f, 1.0f, 1.0f);
			RenderUtil.drawEntityOnScreen(x + 18,
					(int) (y + (player.isSneaking() ? 38
							: ((player instanceof EntityPlayer)
									? (((EntityPlayer) player).inventory.armorInventory.length == 0 ? 44 : 46)
									: 46))
							+ (player.getEyeHeight() / 0.3f)),
					24, player.rotationYaw, player.rotationPitch, player);

			if (player instanceof EntityPlayer) {
				GlStateManager.pushMatrix();
				// Player Armor
				EntityPlayer entityplayer = (EntityPlayer) player;
				ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
				int split = x + 20;
				int y2 = y + 35;
				int index = 3;
				while (index >= 0) {
					ItemStack armer = entityplayer.inventory.armorInventory[index];
					if (armer != null) {
						stuff.add(armer);
					}
					--index;
				}
		        if (mc.thePlayer.getCurrentEquippedItem() != null) {
		            stuff.add(mc.thePlayer.getCurrentEquippedItem());
		        }
				for (ItemStack errything : stuff) {
					if (Minecraft.getMinecraft().theWorld != null) {
						RenderHelper.enableGUIStandardItemLighting();
						split += 20;
					}
					GlStateManager.disableAlpha();
					GlStateManager.clear(256);
					mc.getRenderItem().zLevel = -150.0f;
					mc.getRenderItem().renderItemAndEffectIntoGUI(errything, split, y2);
					mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, errything, split, y2);
					mc.getRenderItem().zLevel = 0.0f;
					GlStateManager.disableBlend();
					GlStateManager.scale(0.5, 0.5, 0.5);
					GlStateManager.disableDepth();
					GlStateManager.disableLighting();
					GlStateManager.enableDepth();
					GlStateManager.scale(2.0f, 2.0f, 2.0f);
					GlStateManager.enableAlpha();
				}
				GlStateManager.popMatrix();
			}
		}*/
    }
    
    public void drawcheckbox(String name , float xOff , float yOff , float x ,float y , float p2 ,float p3 ,boolean enabled) {
        GlStateManager.pushMatrix();
        String xd = name.charAt(0) + name.toLowerCase().substring(1);
        Client.instance.fontManager.tahomabold11.drawStringWithShadow(xd, x + 7.5F + xOff, y + 1.0F + yOff, Colors.getColor(220, 255));
        RenderUtil.rectangle((double)(x + xOff) + 0.6D, (double)(y + yOff) + 0.6D, (double)(x + 6.0F + xOff) + -0.6D, (double)(y + 6.0F + yOff) + -0.6D, Colors.getColor(10, 255));
        RenderUtil.drawGradient((double)(x + xOff + 1.0F), (double)(y + yOff + 1.0F), (double)(x + 6.0F + xOff + -1.0F), (double)(y + 6.0F + yOff + -1.0F), Colors.getColor(76), Colors.getColor(51, 255));
        boolean hovering = p2 >= x + xOff && p3 >= y + yOff && p2 <= x + 35.0F + xOff && p3 <= y + 6.0F + yOff;
        if (enabled) {
        	RenderUtil.drawGradient((double)(x + xOff + 1.0F), (double)(y + yOff + 1.0F), (double)(x + xOff + 5.0F), (double)(y + yOff + 5.0F), Colors.getColor(ColorManager.hudColor.red, ColorManager.hudColor.green, ColorManager.hudColor.blue, 255), Colors.getColor(ColorManager.hudColor.red, ColorManager.hudColor.green, ColorManager.hudColor.blue, 120));
        }

        if (hovering && !enabled) {
        	RenderUtil.rectangle((double)(x + xOff + 1.0F), (double)(y + yOff + 1.0F), (double)(x + xOff + 5.0F), (double)(y + yOff + 5.0F), Colors.getColor(255, 40));
        }

        GlStateManager.popMatrix();
	}
}
