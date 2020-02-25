package me.skidsense.module.collection.visual;

import java.util.Iterator;
import java.awt.Color;
import java.util.List;
import javax.vecmath.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.GLUtils;
import me.skidsense.util.RenderUtil;

public class ChestESP
extends Module {
    public ChestESP() {
        super("Chest ESP", new String[]{"chesthack"}, ModuleType.Visual);
        this.setColor(new Color(90, 209, 165).getRGB());
    }

    @EventHandler
    public void onRender(EventRender3D eventRender) {
        Iterator var3;
    	var3 = this.mc.theWorld.loadedTileEntityList.iterator();

    	for (int xd = 0; xd < mc.theWorld.loadedTileEntityList.size(); xd++) {
			TileEntity b = mc.theWorld.loadedTileEntityList.get(xd);

			if (b instanceof TileEntityChest) {
	            TileEntityLockable storage = (TileEntityLockable)b;
	            this.drawESPOnStorage(storage, storage.getPos().getX(), storage.getPos().getY(), storage.getPos().getZ());
			}
			if (b instanceof TileEntityEnderChest) {
				drawBox(((TileEntityEnderChest) b).getPos(), 3);
			}
		}
    }
    
    public void drawESPOnStorage(TileEntityLockable storage, double x, double y, double z) {
        assert (!storage.isLocked());
        TileEntityChest chest = (TileEntityChest)storage;
        Vec3 vec = new Vec3(0.0, 0.0, 0.0);
        Vec3 vec2 = new Vec3(0.0, 0.0, 0.0);
        if (chest.adjacentChestZNeg != null) {
            vec = new Vec3(x + 0.0625, y, z - 0.9375);
            vec2 = new Vec3(x + 0.9375, y + 0.875, z + 0.9375);
        } else if (chest.adjacentChestXNeg != null) {
            vec = new Vec3(x + 0.9375, y, z + 0.0625);
            vec2 = new Vec3(x - 0.9375, y + 0.875, z + 0.9375);
        } else if (chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null && chest.adjacentChestXPos == null && chest.adjacentChestZPos == null) {
            vec = new Vec3(x + 0.0625, y, z + 0.0625);
            vec2 = new Vec3(x + 0.9375, y + 0.875, z + 0.9375);
        } else {
            return;
        }
        GL11.glPushMatrix();
        RenderUtil.pre3D();
        ChestESP.mc.entityRenderer.setupCameraTransform(ChestESP.mc.timer.renderPartialTicks, 2);
        if (chest.getChestType() == 1) {
            GL11.glColor4d((double)0.7, (double)0.1, (double)0.1, (double)0.5);
        } else {
            GL11.glColor4d((double)0.7, (double)0.4, (double)0.0, (double)0.5);
        }
        RenderUtil.drawBoundingBox(new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY, vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX, vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        RenderUtil.post3D();
        GL11.glPopMatrix();
    }

    public void drawESP(double x, double y, double z, double r, double g, double b) {
        GL11.glPushMatrix();
        GLUtils.setGLCap(3042, true);
        GL11.glBlendFunc((int)770, (int)771);
        GLUtils.setGLCap(2896, false);
        GLUtils.setGLCap(3553, false);
        GLUtils.setGLCap(2848, true);
        GLUtils.setGLCap(2929, false);
        GL11.glDepthMask((boolean)false);
        AxisAlignedBB boundingBox = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        GL11.glColor4d((double)r, (double)g, (double)b, (double)0.14000000059604645);
        RenderUtil.drawBoundingBox(boundingBox.contract(0.025, 0.025, 0.025));
        GL11.glColor4d((double)r, (double)g, (double)b, (double)0.33000001311302185);
        GL11.glLineWidth((float)1.0f);
        RenderUtil.drawOutlinedBoundingBox(boundingBox);
        GL11.glLineWidth((float)1.4f);
        RenderUtil.drawLines(boundingBox);
        GL11.glLineWidth((float)2.0f);
        GLUtils.revertAllCaps();
        GL11.glDepthMask((boolean)true);
        GL11.glPopMatrix();
    }
    
    public static void drawBox(BlockPos blockPos, int mode) {
        double x =
                blockPos.getX()
                		- Minecraft.getMinecraft().getRenderManager().renderPosX;
        double y =
                blockPos.getY()
                        - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double z =
                blockPos.getZ()
                        - Minecraft.getMinecraft().getRenderManager().renderPosZ;


        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        if (mode == 0) {//Nuker
            GL11.glColor4d(0.33, 0.0f, 0.025, 0.22f);
        } else if (mode == 1) {//Chest
            GL11.glColor4d(0.12, 0, 0.98, 0.20);
        } else if (mode == 2) {//Hopper
            GL11.glColor4d(0, 0.98, 0.50, 0.20);
        } else if (mode == 3) {//EnderChest
            GL11.glColor4d(0, 0.0f, 0.25, 0.20F);
        } else if (mode == 4) {//Dispenser/Dropper
            GL11.glColor4d(0, 0.0f, 0.25, 0.20F);
        } else if (mode == 5) {//Furnace
            GL11.glColor4d(0, 0.0f, 0.25, 0.20F);
        } else if (mode == 6) {//Spawners
            GL11.glColor4d(0, 0.0f, 0.05, 0.20F);
        } else if (mode == 7) {//Scaffold
            GL11.glColor4d(0.4, 0, 0, 0.20);
        } else if (mode == 1337) {
            GL11.glColor4d(3, 0, 1, 0.22);
        } else
            GL11.glColor4d(1, 0.0f, 0.05, 0.20F);
        drawBoundingBox(new AxisAlignedBB(x, y, z,
                x + 1.0, y + 1.0, z + 1.0));
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }
    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).tex(0, 0).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).tex(0, 0).endVertex();
        tessellator.draw();
    }
    }
