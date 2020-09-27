package me.skidsense.module.collection.visual;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Projectiles extends Mod{

	public Projectiles() {
		super("Projectiles", new String[]{"Projectiles"}, ModuleType.Visual);
	}

	@Sub
	public void onRender3D(EventRender3D eventRender3D) {
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Minecraft.getMinecraft().entityRenderer.orientCamera(eventRender3D.getPartialTicks());
        GL11.glTranslated(-Minecraft.getMinecraft().getRenderManager().renderPosX, -Minecraft.getMinecraft().getRenderManager().renderPosY, -Minecraft.getMinecraft().getRenderManager().renderPosZ);

        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && isThrowable(Minecraft.getMinecraft().thePlayer.getHeldItem().getItem())) {
            double x = player.lastTickPosX
                    + (player.posX - player.lastTickPosX)
                    * (double) eventRender3D.getPartialTicks()
                    - (double) (MathHelper.cos((float) Math.toRadians((double) player.rotationYaw)) * 0.16F);
            double y = player.lastTickPosY
                    + (player.posY - player.lastTickPosY) * (double) eventRender3D.getPartialTicks()
                    + (double) player.getEyeHeight() - 0.100149011612D;
            double z = player.lastTickPosZ
                    + (player.posZ - player.lastTickPosZ)
                    * (double) eventRender3D.getPartialTicks()
                    - (double) (MathHelper.sin((float) Math.toRadians((double) player.rotationYaw)) * 0.16F);
            float con = 1.0F;
            if (!(player.inventory.getCurrentItem().getItem() instanceof ItemBow)) {
                con = 0.4F;
            }

            double motionX = (double) (-MathHelper.sin((float) Math.toRadians((double) player.rotationYaw))
                    * MathHelper.cos((float) Math.toRadians((double) player.rotationPitch)) * con);
            double motionZ = (double) (MathHelper.cos((float) Math.toRadians((double) player.rotationYaw))
                    * MathHelper.cos((float) Math.toRadians((double) player.rotationPitch)) * con);
            double motionY = (double) (-MathHelper.sin((float) Math.toRadians((double) player.rotationPitch)) * con);
            double ssum = Math.sqrt(motionX * motionX
                    + motionY * motionY + motionZ
                    * motionZ);

            motionX /= ssum;
            motionY /= ssum;
            motionZ /= ssum;

            GL11.glColor4d(1.0f, 0f, 0f, .5);

            if (player.inventory.getCurrentItem().getItem() instanceof ItemBow) {
                float pow = (float) (72000 - player.getItemInUseCount()) / 20.0F;
                pow = (pow * pow + pow * 2.0F) / 3.0F;

                if (pow > 1.0F) {
                    pow = 1.0F;
                }

                if (pow <= 0.1F) {
                    pow = 1.0F;
                }

                pow *= 2.0F;
                pow *= 1.5F;
                motionX *= (double) pow;
                motionY *= (double) pow;
                motionZ *= (double) pow;
            } else {
                motionX *= 1.5D;
                motionY *= 1.5D;
                motionZ *= 1.5D;
            }

            GL11.glPushMatrix();
            enableDefaults();
            GL11.glLineWidth(1.5F);

            GL11.glBegin(GL11.GL_LINE_STRIP);
            double gravity = this.getGravity(player.inventory.getCurrentItem().getItem());

            for (int q = 0; q < 1000; ++q) {
                double rx = x * 1.0D;
                double ry = y * 1.0D;
                double rz = z * 1.0D;

                GL11.glColor3d(1, 0, 0);
                GL11.glVertex3d(rx, ry, rz);

                x += motionX;
                y += motionY;
                z += motionZ;
                motionX *= 0.99D;
                motionY *= 0.99D;
                motionZ *= 0.99D;
                motionY -= gravity;
            }

            GL11.glEnd();

            GL11.glPopMatrix();
            disableDefaults();
        }
        GL11.glTranslated(Minecraft.getMinecraft().getRenderManager().renderPosX, Minecraft.getMinecraft().getRenderManager().renderPosY, Minecraft.getMinecraft().getRenderManager().renderPosZ);
        GL11.glPopMatrix();

	}
	
	private final List<double[]> positions = new ArrayList<double[]>();
	
	@Sub
	public void onRender3DArrowESP(EventRender3D eventRender3D) {
		for (Object e2 : mc.theWorld.loadedEntityList) {
			Entity e = (Entity)e2;
			if (!(e instanceof EntityArrow)) {
				continue;
			}
			EntityArrow arrow = (EntityArrow) e;
			if (arrow.inGround) {
				continue;
			}
			
			positions.add(new double[] { arrow.getPosition().getX(), arrow.getPosition().getY(), arrow.getPosition().getZ() });

			final double viewerPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
	        final double viewerPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
	        final double viewerPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
	        final double line_lenght = 0.25;
	        final ArrayList<BlockPos> dontRender = new ArrayList<BlockPos>();
	            final BlockPos pos = arrow.getPosition();
	            if (dontRender.contains(pos)) {
	                continue;
	            }
	            final double x = -(viewerPosX - pos.getX()) + 0.5;
	            final double y = -(viewerPosY - pos.getY()) + 0.5;
	            final double z = -(viewerPosZ - pos.getZ()) + 0.5;
	            GL11.glPushMatrix();
	            GL11.glBlendFunc(770, 771);
	            GL11.glEnable(3042);
	            GL11.glLineWidth(1.0f);
	            GL11.glDisable(3553);
	            GL11.glDisable(2929);
	            GL11.glDepthMask(true);
	            GL11.glColor3f(255.0f, 255.0f, 1.0f);
	            GL11.glBegin(1);
	        //    for (double[] position : positions) {
	         
	       
	            GL11.glVertex3d(x - line_lenght, y, z);
	            GL11.glVertex3d(x + line_lenght, y, z);
	            GL11.glVertex3d(x, y + line_lenght, z);
	            GL11.glVertex3d(x, y - line_lenght, z);
	            GL11.glVertex3d(x, y, z + line_lenght);
	            GL11.glVertex3d(x, y, z - line_lenght);
	     //       }
	            GL11.glEnd();
	            GL11.glEnable(3553);
	            GL11.glEnable(2929);
	            GL11.glDepthMask(true);
	            GL11.glDisable(3042);
	            GL11.glPopMatrix();
	            
	            double posX = arrow.posX;
                double posY = arrow.posY;
                double posZ = arrow.posZ;
                double motionX = arrow.motionX;
                double motionY = arrow.motionY;
                double motionZ = arrow.motionZ;
                MovingObjectPosition landingPosition2 = null;
                boolean hasLanded2 = false;
                Projectiles.enableRender3D(true);
                this.setColor(3196666);
                GL11.glLineWidth(2.0f);
                GL11.glBegin(3);
                int limit2 = 0;
                while (!hasLanded2 && limit2 < 300) {
                    Vec3 posBefore2 = new Vec3(posX, posY, posZ);
                    Vec3 posAfter2 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                    landingPosition2 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(posBefore2, posAfter2, false, true, false);
                    if (landingPosition2 != null) {
                        hasLanded2 = true;
                    }
                    if ((Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(posX += motionX, posY += motionY, posZ += motionZ)).getBlock()).getMaterial() == Material.water) {
                        motionX *= 0.6;
                        motionY *= 0.6;
                        motionZ *= 0.6;
                    } else {
                        motionX *= 0.99;
                        motionY *= 0.99;
                        motionZ *= 0.99;
                    }
                    motionY -= 0.05000000074505806;
                    mc.getRenderManager();
                    mc.getRenderManager();
                    mc.getRenderManager();
                    GL11.glVertex3d(posX - Minecraft.getMinecraft().getRenderManager().renderPosX, posY - Minecraft.getMinecraft().getRenderManager().renderPosY, posZ - Minecraft.getMinecraft().getRenderManager().renderPosZ);
                    ++limit2;
                }
                GL11.glEnd();
                Projectiles.disableRender3D(true);
		}
	}

    public void drawBox(AxisAlignedBB bb2) {
        GL11.glBegin(7);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.minZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.maxX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.minZ);
        GL11.glVertex3d(bb2.minX, bb2.minY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.maxZ);
        GL11.glVertex3d(bb2.minX, bb2.maxY, bb2.minZ);
        GL11.glEnd();
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
        }
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDisable(2848);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    private void enableDefaults() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2929);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glDepthMask(false);
    }

    private void disableDefaults() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(GL13.GL_MULTISAMPLE);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private double getGravity(Item item) {
        return item instanceof ItemBow ? 0.05D : 0.03D;
    }

    private boolean isThrowable(Item item) {
        return item instanceof ItemBow || item instanceof ItemSnowball
                || item instanceof ItemEgg || item instanceof ItemEnderPearl;
    }
}
