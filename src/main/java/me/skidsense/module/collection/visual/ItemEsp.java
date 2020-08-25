package me.skidsense.module.collection.visual;

import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class ItemEsp extends Mod {
	
	public Option outlinedboundingBox = new Option("Outlined", "Outlined", true);
	
	public ItemEsp() {
		super("Item ESP", new String[]{"ItemESP"}, ModuleType.Visual);
        //this.addValues(this.outlinedboundingBox);
	}
	
	@Sub
	public void onRender(EventRender3D event) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDepthMask(false);
		GL11.glLineWidth(1.0F);

		GL11.glTranslated(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

		for (Object o : mc.theWorld.loadedEntityList) {
			if (o instanceof EntityItem) {
				EntityItem entityItem = (EntityItem) o;

				double posX = entityItem.lastTickPosX + (entityItem.posX - entityItem.lastTickPosX) * event.getPartialTicks();
				double posY = entityItem.lastTickPosY + (entityItem.posY - entityItem.lastTickPosY) * event.getPartialTicks();
				double posZ = entityItem.lastTickPosZ + (entityItem.posZ - entityItem.lastTickPosZ) * event.getPartialTicks();

				GL11.glColor4d(1, 1, 1, 0.2);

				if (entityItem.getEntityItem() != null) {
					ItemStack stack = entityItem.getEntityItem();
					Color color = new Color(stack.getItem().getColorFromItemStack(stack, 1));

					GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), 0.2);
				}

				AxisAlignedBB boundingBox = new AxisAlignedBB(posX - 0.25, posY, posZ - 0.25,
						posX + 0.25, posY + 0.5, posZ + 0.25);

				RenderUtil.drawBoundingBox(boundingBox);
				RenderUtil.drawOutlinedBoundingBox(boundingBox);
			}
		}

		GL11.glTranslated(mc.getRenderManager().viewerPosX, mc.getRenderManager().viewerPosY, mc.getRenderManager().viewerPosZ);

		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glPopMatrix();
	}
}
