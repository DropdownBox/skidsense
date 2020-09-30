package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.util.ArrayList;

import me.skidsense.gui.util.GuiUtil;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
		int x = event.getResolution().getScaledWidth() / 2 + 20;
		int y = event.getResolution().getScaledHeight() / 2 - 30;
		if (KillAura.target != null) {
			EntityLivingBase player = KillAura.target;
			RenderUtil.pre();
			GlStateManager.pushMatrix();

			// BaseRect(black)
			GuiUtil.drawBorderedRect(x + 0.7f, y, x + 149.7f, y + 60,1, new Color(0, 0, 0, 130).getRGB(), new Color(0, 0, 0, 180).getRGB());

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
		}
    }
}
