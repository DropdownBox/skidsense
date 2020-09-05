package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;

import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;

public class ESP extends Mod {
	public Mode mode = new Mode("Mode", "Mode", esp2dmode.values(), esp2dmode.Off);
	public Option<Boolean> teamBasedColors = new Option("TeamBasedColors", "TeamBasedColors", false);
	public Option<Boolean> tags = new Option("Tags", "Tags", false);
	public Option<Boolean> healthNumber = new Option("HealthNumber", "HealthNumber", false);
	public Option<Boolean> healthBar = new Option("HealthBar", "HealthBar", true);
	public Option<Boolean> localPlayer = new Option("LocalPlayer", "LocalPlayer", true);
	public Numbers<Double> width = new Numbers("Width", "Width", 0.5, 0.1, 1.0, 0.1);
	public List<EntityPlayer> collectedEntities = new ArrayList<>();
	private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
	private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
	private static final Map<EntityPlayer, float[][]> modelRotations = new HashMap<EntityPlayer, float[][]>();

	private final int black = new Color(0, 0, 0, 150).getRGB();

	public ESP() {
		super("ESP", new String[] { "ESP2D" }, ModuleType.Visual);
		this.removed = true;
	}

	enum esp2dmode {
		Box, Off,
	}

	public static int[] getFractionIndicies(float[] fractions, float progress) {
		int[] range = new int[2];

		int startPoint = 0;
		while (startPoint < fractions.length && fractions[startPoint] <= progress) {
			startPoint++;
		}

		if (startPoint >= fractions.length) {
			startPoint = fractions.length - 1;
		}

		range[0] = startPoint - 1;
		range[1] = startPoint;

		return range;
	}

	public static Color blend(Color color1, Color color2, double ratio) {
		float r = (float) ratio;
		float ir = (float) 1.0 - r;

		float rgb1[] = new float[3];
		float rgb2[] = new float[3];

		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);

		float red = rgb1[0] * r + rgb2[0] * ir;
		float green = rgb1[1] * r + rgb2[1] * ir;
		float blue = rgb1[2] * r + rgb2[2] * ir;

		if (red < 0) {
			red = 0;
		} else if (red > 255) {
			red = 255;
		}
		if (green < 0) {
			green = 0;
		} else if (green > 255) {
			green = 255;
		}
		if (blue < 0) {
			blue = 0;
		} else if (blue > 255) {
			blue = 255;
		}

		Color color = null;
		try {
			color = new Color(red, green, blue);
		} catch (IllegalArgumentException exp) {
			NumberFormat nf = NumberFormat.getNumberInstance();
			System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
			exp.printStackTrace();
		}
		return color;
	}

	public static Color blendColors(float[] fractions, Color[] colors, float progress) {
		Color color = null;
		if (fractions != null) {
			if (colors != null) {
				if (fractions.length == colors.length) {
					int[] indicies = getFractionIndicies(fractions, progress);

					if (indicies[0] < 0 || indicies[0] >= fractions.length || indicies[1] < 0
							|| indicies[1] >= fractions.length) {
						return colors[0];
					}
					float[] range = new float[] { fractions[indicies[0]], fractions[indicies[1]] };
					Color[] colorRange = new Color[] { colors[indicies[0]], colors[indicies[1]] };

					float max = range[1] - range[0];
					float value = progress - range[0];
					float weight = value / max;

					color = blend(colorRange[0], colorRange[1], 1f - weight);
				} else {
					throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
				}
			} else {
				throw new IllegalArgumentException("Colours can't be null");
			}
		} else {
			throw new IllegalArgumentException("Fractions can't be null");
		}
		return color;
	}

	@Sub
	public void onRender2D(EventRenderGui event) {
		GL11.glPushMatrix();
		this.collectedEntities.clear();
		this.collectEntities();
		double boxWidth = this.width.getValue().doubleValue();
		double scaling = event.getResolution().getScaleFactor()
				/ Math.pow(event.getResolution().getScaleFactor(), 2.0);
		GlStateManager.scale(scaling, scaling, scaling);
		for (EntityPlayer entity : collectedEntities) {
			if (isValid(entity) && RenderUtil.isInViewFrustrum(entity)) {
				double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, event.getPartialTicks());
				double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, event.getPartialTicks());
				double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, event.getPartialTicks());
				double width = entity.width / 1.5;
				double height = entity.height + (entity.isSneaking() ? -0.3 : 0.2);
				AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
				List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
						new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
						new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
						new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
						new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
				mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
				Vector4d position = null;
				for (Vector3d vector : vectors) {
					vector = project2D(event.getResolution(), vector.x - mc.getRenderManager().viewerPosX,
							vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
					if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
						if (position == null) {
							position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
						}
						position.x = Math.min(vector.x, position.x);
						position.y = Math.min(vector.y, position.y);
						position.z = Math.max(vector.x, position.z);
						position.w = Math.max(vector.y, position.w);
					}
				}
				mc.entityRenderer.setupOverlayRendering();
				if (position != null) {
					double posX = position.x;
					double posY = position.y;
					double endPosX = position.z;
					double endPosY = position.w;
					int color2 = Colors.getColor(200, 200, 200);
					if (teamBasedColors.getValue())
						color2 = Colors.getColor(235, 60, 60);
					if (Teams.isOnSameTeam((Entity) entity)) {
						color2 = Colors.getColor(0, 150, 0, 255);
					} else if (entity.isInvisible()) {
						color2 = Colors.getColor(200, 200, 0, 255);
					} else {
						color2 = Colors.getColor(255, 70, 70, 255);
					}
					if (mode.getValue() == esp2dmode.Box) {
						// Left
						RenderUtil.drawRect(posX - 1, posY, posX + boxWidth, endPosY + .5,
								Colors.getColor(0, 0, 0, 250));
						// Top
						RenderUtil.drawRect(posX - 1, posY - .5, endPosX + .5, posY + .5 + boxWidth,
								Colors.getColor(0, 0, 0, 250));
						// Right
						RenderUtil.drawRect(endPosX - .5 - boxWidth, posY, endPosX + .5, endPosY + .5,
								Colors.getColor(0, 0, 0, 250));
						// Bottom
						RenderUtil.drawRect(posX - 1, endPosY - boxWidth - .5, endPosX + .5, endPosY + .5,
								Colors.getColor(0, 0, 0, 250));

						// Left
						RenderUtil.drawRect(posX - .5, posY, posX + boxWidth - .5, endPosY, color2);
						// Bottom
						RenderUtil.drawRect(posX, endPosY - boxWidth, endPosX, endPosY, color2);
						// Top
						RenderUtil.drawRect(posX - .5, posY, endPosX, posY + boxWidth, color2);
						// Right
						RenderUtil.drawRect(endPosX - boxWidth, posY, endPosX, endPosY, color2);
					}
					if (tags.getValue()) {
						double dif = (endPosX - posX) / 2;
						String colorCode = Teams.isOnSameTeam(entity) ? "\247a" : "\247c";
						mc.fontRendererObj.drawStringWithShadow(colorCode + entity.getName(),
								(float) (posX + dif) - (mc.fontRendererObj.getStringWidth(entity.getName()) / 2),
								(float) ((posY - (9 / 1.5f * 2.0f)) + 1.0f), color2);
					}
					double armorstrength = 0;
					EntityPlayer player = (EntityPlayer) entity;
					for (int index = 3; index >= 0; index--) {
						ItemStack stack = player.inventory.armorInventory[index];
						if (stack != null) {
							armorstrength += getArmorStrength(stack);
						}
					}
					if (armorstrength > 0.0f) {
						double offset = posY - endPosY;
						double percentoffset = offset / 40;
						double finalnumber = percentoffset * armorstrength * 2;
						RenderUtil.drawRect(endPosX + 1.5f, posY - 0.5f, endPosX + 3f, endPosY + 0.5f, black);
						RenderUtil.drawRect(endPosX + 2f, endPosY + finalnumber, endPosX + 2.5f, endPosY,
								new Color(200, 200, 200).getRGB());
					}
					if (healthBar.getValue() || healthNumber.getValue()) {
						double hpPercentage = entity.getHealth() / entity.getMaxHealth();
						if (hpPercentage > 1)
							hpPercentage = 1;
						else if (hpPercentage < 0)
							hpPercentage = 0;

						float health = entity.getHealth();

						double hpHeight = (endPosY - posY) * hpPercentage;

						double difference = posY - endPosY + 0.5;

						if (health > 0 && healthBar.getValue()) {
							RenderUtil.drawOutline(posX - 3.5, posY - .5, 1.5, (endPosY - posY) + 1, 1, black);
							float healthHeight = (float) ((endPosY - y) * (((EntityLivingBase) entity).getHealth()
									/ ((EntityLivingBase) entity).getMaxHealth()));
							float[] fractions = new float[] { 0f, 0.5f, 1f };
							Color[] colors = new Color[] { Color.RED, Color.YELLOW, Color.GREEN };
							float progress = (health * 5) * 0.01f;
							Color customColor = blendColors(fractions, colors, progress).brighter();
							RenderUtil.drawRect(posX - 3, endPosY - hpHeight, posX - 2.5, endPosY,
									customColor.getRGB());
						}
						if (healthNumber.getValue()) {
							Client.fontManager.zeroarr.drawStringWithShadow((int) (hpPercentage * 100) + "%",
									(float) (posX - 13), (float) (endPosY - hpHeight - 2),
									Colors.getColor(255, 255, 255, 255));
						}
					}
				}
			}
		}
		GL11.glPopMatrix();
		GlStateManager.enableBlend();
		mc.entityRenderer.setupOverlayRendering();

	}

	public static void updateModel(EntityPlayer player, ModelPlayer model) {
		modelRotations.put(player, new float[][] {
				{ model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ },
				{ model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY,
						model.bipedRightArm.rotateAngleZ },
				{ model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ },
				{ model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY,
						model.bipedRightLeg.rotateAngleZ },
				{ model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY,
						model.bipedLeftLeg.rotateAngleZ } });
	}

	private void collectEntities() {
		for (EntityPlayer entity : mc.theWorld.playerEntities) {
			if (isValid(entity))
				collectedEntities.add(entity);
		}
	}

	private double getArmorStrength(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ItemArmor)) {
			return -1.0;
		}
		float damageReduction = ((ItemArmor) itemStack.getItem()).damageReduceAmount;
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
		return damageReduction;
	}

	private int getHealthColor(EntityLivingBase player) {
		float f = player.getHealth();
		float f1 = player.getMaxHealth();
		float f2 = Math.max(0.0f, Math.min(f, f1) / f1);
		return Color.HSBtoRGB(f2 / 3.0f, 1.0f, 1.0f) | -16777216;
	}

	private Vector3d project2D(ScaledResolution scaledResolution, double x, double y, double z) {
		GL11.glGetFloat(2982, modelview);
		GL11.glGetFloat(2983, projection);
		GL11.glGetInteger(2978, viewport);
		if (GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, vector)) {
			return new Vector3d(vector.get(0) / scaledResolution.getScaleFactor(),
					(Display.getHeight() - vector.get(1)) / scaledResolution.getScaleFactor(), vector.get(2));
		}
		return null;
	}

	private boolean isValid(EntityPlayer entityLivingBase) {
		return ((!localPlayer.getValue() || entityLivingBase != mc.thePlayer)) && !entityLivingBase.isDead
				&& !entityLivingBase.isInvisible();
	}
}