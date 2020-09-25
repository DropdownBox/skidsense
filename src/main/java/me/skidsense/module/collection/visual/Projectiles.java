package me.skidsense.module.collection.visual;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
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
		double renderPosX = mc.thePlayer.lastTickPosX
				+ (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * eventRender3D.getPartialTicks();
		double renderPosY = mc.thePlayer.lastTickPosY
				+ (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * eventRender3D.getPartialTicks();
		double renderPosZ = mc.thePlayer.lastTickPosZ
				+ (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * eventRender3D.getPartialTicks();
		Minecraft.getMinecraft().entityRenderer.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 2);
		ItemStack stack;
		Item item;

		if (mc.thePlayer.getHeldItem() != null && mc.gameSettings.thirdPersonView == 0) {
			if (!isValidPotion(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getItem())
					&& mc.thePlayer.getHeldItem().getItem() != Items.experience_bottle
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSnowball)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl)
					&& !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEgg))
				return;
			stack = mc.thePlayer.getHeldItem();
			item = mc.thePlayer.getHeldItem().getItem();
		} else
			return;
		if ((mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && (!mc.thePlayer.isUsingItem()))
			return;
		double posX = renderPosX - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		double posY = renderPosY + mc.thePlayer.getEyeHeight() - 0.1000000014901161D;
		double posZ = renderPosZ - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;

		double motionX = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
				* (item instanceof ItemBow ? 1.0D : 0.4D);
		double motionY = -MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
				* (item instanceof ItemBow ? 1.0D : 0.4D);
		double motionZ = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
				* MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI)
				* (item instanceof ItemBow ? 1.0D : 0.4D);

		int var6 = 72000 - mc.thePlayer.getItemInUseCount();
		float power = (float) (var6 / 20.0F);
		power = (power * power + power * 2.0F) / 3.0F;

		if (power < 0.1D)
			return;

		if (power > 1.0F)
			power = 1.0F;

		float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

		motionX /= distance;
		motionY /= distance;
		motionZ /= distance;

		float pow = (item instanceof ItemBow ? power * 2.0F
				: isValidPotion(stack, item) ? 0.325F
						: item instanceof ItemFishingRod ? 1.25F
								: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.9F : 1.0F);

		motionX *= pow * (item instanceof ItemFishingRod ? 0.75F
				: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F);
		motionY *= pow * (item instanceof ItemFishingRod ? 0.75F
				: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F);
		motionZ *= pow * (item instanceof ItemFishingRod ? 0.75F
				: mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle ? 0.75F : 1.5F);

		RenderUtil.startDrawing();
		if (power > 0.6F) {
			GlStateManager.color(0.0F, 1.0F, 0.0F, 0.15F);
		} else if (power > 0.3F) {
			GlStateManager.color(0.8F, 0.5F, 0.0F, 0.15F);
		} else {
			GlStateManager.color(1.0F, 0.0F, 0.0F, 0.15F);
		}
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.begin(3, DefaultVertexFormats.POSITION);
		if (!(item instanceof ItemBow))
			GL11.glColor4d(0, 0.9, 1, 0.75);
		renderer.pos(posX - renderPosX, posY + 0.01 - renderPosY, posZ - renderPosZ);

		float size = (float) (item instanceof ItemBow ? 0.3D : 0.25D);
		boolean hasLanded = false;
		Entity landingOnEntity = null;
		MovingObjectPosition landingPosition = null;
		while (!hasLanded && posY > 0.0D) {
			Vec3 present = new Vec3(posX, posY, posZ);
			Vec3 future = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

			MovingObjectPosition possibleLandingStrip = mc.theWorld.rayTraceBlocks(present, future, false, true, false);
			if (possibleLandingStrip != null
					&& possibleLandingStrip.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
				landingPosition = possibleLandingStrip;
				hasLanded = true;
			}

			AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size,
					posZ + size);
			List<Entity> entities = getEntitiesWithinAABB(
					arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			for (Object entity : entities) {
				Entity boundingBox = (Entity) entity;
				if ((boundingBox.canBeCollidedWith()) && (boundingBox != mc.thePlayer)) {
					float var11 = 0.3F;
					AxisAlignedBB var12 = boundingBox.getEntityBoundingBox().expand(var11, var11, var11);
					MovingObjectPosition possibleEntityLanding = var12.calculateIntercept(present, future);
					if (possibleEntityLanding != null) {
						hasLanded = true;
						landingOnEntity = boundingBox;
						landingPosition = possibleEntityLanding;
					}
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			float motionAdjustment = 0.99F;
			motionX *= motionAdjustment;
			motionY *= motionAdjustment;
			motionZ *= motionAdjustment;
			motionY -= (item instanceof ItemBow ? 0.05D : 0.03D);
			renderer.pos(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
		}
		tessellator.draw();

		if (landingPosition != null && landingPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			GlStateManager.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);

			int side = landingPosition.sideHit.getIndex();

			if (side == 2) {
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			} else if (side == 3) {
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			} else if (side == 4) {
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			} else if (side == 5) {
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			}

			Cylinder c = new Cylinder();

			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			c.setDrawStyle(GLU.GLU_LINE);

			if (landingOnEntity != null) {
				GL11.glColor4d(0.9, 1, 0, 0.15);
			}
			if (!(item instanceof ItemBow))
				GL11.glColor4d(0, 0.9, 1, 1);
			c.draw(0.5F, 0.5F, 0.0F, 4, 1);
			if (!(item instanceof ItemBow)) {
				GL11.glColor4d(0, 0.9, 1, 0.15);
				c.draw(0.0f, 0.5F, 0.0F, 4, 100);
			} else {
				c.draw(0.0f, 0.5F, 0.0F, 4, 27);
			}
		}
		RenderUtil.stopDrawing();
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
	
	private boolean isValidPotion(ItemStack stack, Item item) {
		if (item != null && item instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) item;
			if (!ItemPotion.isSplash(stack.getItemDamage()))
				return false;

			if (potion.getEffects(stack) != null) {
				return true;
			}
		}
		return false;
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

    @Override
    public void setColor(int colorHex) {
        float alpha = (float)(colorHex >> 24 & 255) / 255.0f;
        float red = (float)(colorHex >> 16 & 255) / 255.0f;
        float green = (float)(colorHex >> 8 & 255) / 255.0f;
        float blue = (float)(colorHex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha == 0.0f ? 1.0f : alpha);
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
    
	private List<Entity> getEntitiesWithinAABB(AxisAlignedBB bb) {
		ArrayList<Entity> list = new ArrayList<Entity>();
		int chunkMinX = MathHelper.floor_double((bb.minX - 2.0D) / 16.0D);
		int chunkMaxX = MathHelper.floor_double((bb.maxX + 2.0D) / 16.0D);
		int chunkMinZ = MathHelper.floor_double((bb.minZ - 2.0D) / 16.0D);
		int chunkMaxZ = MathHelper.floor_double((bb.maxZ + 2.0D) / 16.0D);
		for (int x = chunkMinX; x <= chunkMaxX; x++) {
			for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
				if (mc.theWorld.getChunkProvider().chunkExists(x, z)) {
					mc.theWorld.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(mc.thePlayer, bb, list, null);
				}
			}
		}
		return list;
	}
}
