package me.skidsense.module.collection.visual;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class ItemEsp extends Mod {
	public final List<Entity> collectedEntities = new ArrayList<Entity>();
	private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private final int color = Color.WHITE.getRGB();
    private final int backgroundColor = new Color(0, 0, 0, 120).getRGB();
    private final int black = Color.BLACK.getRGB();
	public Option<Boolean> tag = new Option<Boolean>("Outlined", "Outlined", true);
	
	public ItemEsp() {
		super("Item ESP", new String[]{"ItemESP"}, ModuleType.Visual);
        //this.addValues(this.outlinedboundingBox);
	}
	
	@Sub
	public void onRender(EventRenderGui event) {
		GL11.glPushMatrix();
        this.collectEntities();
        float partialTicks = event.getPartialTicks();
        ScaledResolution scaledResolution = event.getResolution();
        int scaleFactor = scaledResolution.getScaleFactor();
        double scaling = (double)scaleFactor / Math.pow(scaleFactor, 2.0);
        GL11.glScaled((double)scaling, (double)scaling, (double)scaling);
        int color = this.color;
        int background = this.backgroundColor;
        float scale = 0.65f;
        float upscale = 1.0f / scale;
        FontRenderer fr = mc.fontRendererObj;
        RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        boolean tag = this.tag.getValue();
        List<Entity> collectedEntities = this.collectedEntities;
        int collectedEntitiesSize = collectedEntities.size();
        for (int i = 0; i < collectedEntitiesSize; ++i) {
            ItemStack itemStack;
            Entity entity = collectedEntities.get(i);
            if (!(entity instanceof EntityItem) || !RenderUtil.isInViewFrustrum(entity)) continue;
            double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
            double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
            double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
            double width = (double)entity.width / 1.5;
            double height = (double)entity.height + (entity.isSneaking() ? -0.3 : 0.2);
            AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
            List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
            entityRenderer.setupCameraTransform(partialTicks, 0);
            Vector4d position = null;
            for (Vector3d vector : vectors) {
                vector = this.project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                if (vector == null || !(vector.z >= 0.0) || !(vector.z < 1.0)) continue;
                if (position == null) {
                    position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                }
                position.x = Math.min(vector.x, position.x);
                position.y = Math.min(vector.y, position.y);
                position.z = Math.max(vector.x, position.z);
                position.w = Math.max(vector.y, position.w);
            }
            if (position == null) continue;
            entityRenderer.setupOverlayRendering();
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double endPosY = position.w;
            RenderUtil.rectangleBordered(posX + 0.5, posY + 0.5, endPosX - 0.5, endPosY - 0.5, 0.5, Colors.getColor(0, 0, 0, 0), color);
            RenderUtil.rectangleBordered(posX - 0.5, posY - 0.5, endPosX + 0.5, endPosY + 0.5, 0.5, Colors.getColor(0, 0), Colors.getColor(0, 150));
            RenderUtil.rectangleBordered(posX + 1.5, posY + 1.5, endPosX - 1.5, endPosY - 1.5, 0.5, Colors.getColor(0, 0), Colors.getColor(0, 150));
            if (tag) {
                float scaledHeight = 10.0f;
                String name = entity.getName();
                if (entity instanceof EntityItem) {
                    name = ((EntityItem)entity).getEntityItem().getDisplayName();
                }
                double dif = (endPosX - posX) / 2.0;
                double textWidth = (float)fr.getStringWidth(name) * scale;
                float tagX = (float)((posX + dif - textWidth / 2.0) * (double)upscale);
                float tagY = (float)(posY * (double)upscale) - scaledHeight;
                GL11.glPushMatrix();
                GL11.glScalef((float)scale, (float)scale, (float)scale);
                fr.drawStringWithShadow(name, tagX, tagY, -1);
                GL11.glPopMatrix();
                /*double stackSizetextWidth = (float)fr.getStringWidth(String.valueOf(((EntityItem)entity).getEntityItem().stackSize)) * scale;
                float stackSizetagX = (float)((posX + dif - stackSizetextWidth / 2.0) * (double)upscale);
                float stackSizetagY = (float)(posY * (double)upscale) - scaledHeight - 10;
                GL11.glPushMatrix();
                GL11.glScalef((float)scale, (float)scale, (float)scale);
                fr.drawStringWithShadow(String.valueOf(((EntityItem)entity).getEntityItem().stackSize), stackSizetagX, stackSizetagY, -1);
                GL11.glPopMatrix();*/
            }
            if (!(entity instanceof EntityItem) || !(itemStack = ((EntityItem)entity).getEntityItem()).isItemStackDamageable()) continue;
            int maxDamage = itemStack.getMaxDamage();
            float itemDurability = maxDamage - itemStack.getItemDamage();
            double durabilityWidth = (endPosX - posX) * (double)itemDurability / (double)maxDamage;
            Gui.drawRect(posX - 0.5, endPosY + 1.5, posX - 0.5 + endPosX - posX + 1.0, endPosY + 1.5 + 2.0, background);
            Gui.drawRect(posX, endPosY + 2.0, posX + durabilityWidth, endPosY + 3.0, 0xFFFFFF);
        }
        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        entityRenderer.setupOverlayRendering();
	}
	
	private void collectEntities() {
        this.collectedEntities.clear();
        java.util.List<Entity> playerEntities = mc.theWorld.getLoadedEntityList();
        int playerEntitiesSize = playerEntities.size();
        for (int i = 0; i < playerEntitiesSize; ++i) {
            Entity entity = (Entity)playerEntities.get(i);
            if (!(entity instanceof EntityItem)) continue;
            this.collectedEntities.add(entity);
        }
    }

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat((int)2982, (FloatBuffer)this.modelview);
        GL11.glGetFloat((int)2983, (FloatBuffer)this.projection);
        GL11.glGetInteger((int)2978, (IntBuffer)this.viewport);
        if (GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)this.modelview, (FloatBuffer)this.projection, (IntBuffer)this.viewport, (FloatBuffer)this.vector)) {
            return new Vector3d(this.vector.get(0) / (float)scaleFactor, ((float)Display.getHeight() - this.vector.get(1)) / (float)scaleFactor, this.vector.get(2));
        }
        return null;
    }
}
