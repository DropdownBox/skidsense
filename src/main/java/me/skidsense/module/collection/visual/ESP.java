package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.FriendManager;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.AntiBot;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.Vec3f;

public class ESP
extends Module {
    private ArrayList<Vec3f> points = new ArrayList();
    private Mode<Enum> mode = new Mode("Mode", "Mode", (Enum[])ESPMode.values(), (Enum)ESPMode.Box);
    private Option<Boolean> HEALTH = new Option<Boolean>("Health", "Health", true);
    private Option<Boolean> invis = new Option<Boolean>("Invisible", "Invisible", false);
    private Map<EntityLivingBase, double[]> entityConvertedPointsMap;
    FontRenderer fr;

    public ESP() {
        super("ESP", new String[]{}, ModuleType.Visual);
        this.addValues(mode, this.HEALTH, this.invis);
        int i2 = 0;
        while (i2 < 8) {
            this.points.add(new Vec3f());
            ++i2;
        }
        this.entityConvertedPointsMap = new HashMap<EntityLivingBase, double[]>();
        this.fr = Minecraft.getMinecraft().fontRendererObj;
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }

    @EventHandler
    public void onScreen(EventRender2D eventRender) {
        this.setSuffix(mode.getValue());

    }

    @EventHandler
    public void onRender(EventRender3D event) {
        if (mode.getValue() == ESPMode.Moon) {
            this.doCornerESP2();
        }
        if (mode.getValue() == ESPMode.Box) {
            this.doBoxESP(event);
        }
    }

    @EventHandler
    public void onRender1(EventRender3D event) {
        try {
            this.updatePositions();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @EventHandler
    public void onRender2D(EventRender2D event) {
        GlStateManager.pushMatrix();
        for (Entity entity : this.entityConvertedPointsMap.keySet()) {
            EntityPlayer ent = (EntityPlayer)entity;
            double[] renderPositions = this.entityConvertedPointsMap.get(ent);
            double[] renderPositionsBottom = new double[]{renderPositions[4], renderPositions[5], renderPositions[6]};
            double[] renderPositionsX = new double[]{renderPositions[7], renderPositions[8], renderPositions[9]};
            double[] renderPositionsX2 = new double[]{renderPositions[10], renderPositions[11], renderPositions[12]};
            double[] renderPositionsZ = new double[]{renderPositions[13], renderPositions[14], renderPositions[15]};
            double[] renderPositionsZ2 = new double[]{renderPositions[16], renderPositions[17], renderPositions[18]};
            double[] renderPositionsTop1 = new double[]{renderPositions[19], renderPositions[20], renderPositions[21]};
            double[] renderPositionsTop2 = new double[]{renderPositions[22], renderPositions[23], renderPositions[24]};
            boolean bl2 = renderPositions[3] > 0.0 && renderPositions[3] <= 1.0 && renderPositionsBottom[2] > 0.0 && renderPositionsBottom[2] <= 1.0 && renderPositionsX[2] > 0.0 && renderPositionsX[2] <= 1.0 && renderPositionsX2[2] > 0.0 && renderPositionsX2[2] <= 1.0 && renderPositionsZ[2] > 0.0 && renderPositionsZ[2] <= 1.0 && renderPositionsZ2[2] > 0.0 && renderPositionsZ2[2] <= 1.0 && renderPositionsTop1[2] > 0.0 && renderPositionsTop1[2] <= 1.0 && renderPositionsTop2[2] > 0.0 && renderPositionsTop2[2] <= 1.0;
            boolean shouldRender = bl2;
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            if ((this.invis.getValue().booleanValue() || !ent.isInvisible()) && ent instanceof EntityPlayer && !(ent instanceof EntityPlayerSP)) {
                try {
                    double[] xValues = new double[]{renderPositions[0], renderPositionsBottom[0], renderPositionsX[0], renderPositionsX2[0], renderPositionsZ[0], renderPositionsZ2[0], renderPositionsTop1[0], renderPositionsTop2[0]};
                    double[] yValues = new double[]{renderPositions[1], renderPositionsBottom[1], renderPositionsX[1], renderPositionsX2[1], renderPositionsZ[1], renderPositionsZ2[1], renderPositionsTop1[1], renderPositionsTop2[1]};
                    double x2 = renderPositions[0];
                    double y2 = renderPositions[1];
                    double endx = renderPositionsBottom[0];
                    double endy = renderPositionsBottom[1];
                    double[] array = xValues;
                    int length = array.length;
                    int j2 = 0;
                    while (j2 < length) {
                        double bdubs = array[j2];
                        if (bdubs < x2) {
                            x2 = bdubs;
                        }
                        ++j2;
                    }
                    double[] array2 = xValues;
                    int length2 = array2.length;
                    int k2 = 0;
                    while (k2 < length2) {
                        double bdubs = array2[k2];
                        if (bdubs > endx) {
                            endx = bdubs;
                        }
                        ++k2;
                    }
                    double[] array3 = yValues;
                    int length3 = array3.length;
                    int l2 = 0;
                    while (l2 < length3) {
                        double bdubs = array3[l2];
                        if (bdubs < y2) {
                            y2 = bdubs;
                        }
                        ++l2;
                    }
                    double[] array4 = yValues;
                    int length4 = array4.length;
                    int n2 = 0;
                    while (n2 < length4) {
                        double bdubs = array4[n2];
                        if (bdubs > endy) {
                            endy = bdubs;
                        }
                        ++n2;
                    }
                    double xDiff = (endx - x2) / 4.0;
                    double x2Diff = (endx - x2) / 4.0;
                    double yDiff = xDiff;
                    int color = Colors.getColor(255, 255);
                    color = Teams.isOnSameTeam(ent) ? Colors.getColor(0, 255, 0, 255) : (ent.hurtTime > 0 ? Colors.getColor(255, 0, 0, 255) : (ent.isInvisible() ? Colors.getColor(255, 255, 0, 255) : Colors.getColor(255, 255, 255, 255)));
                    if (mode.getValue() == ESPMode.SkeetBox) {
                        RenderUtil.rectangleBordered(x2 + 0.5, y2 + 0.5, endx - 0.5, endy - 0.5, 1.0, Colors.getColor(0, 0, 0, 0), color);
                        RenderUtil.rectangleBordered(x2 - 0.5, y2 - 0.5, endx + 0.5, endy + 0.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0, 150));
                        RenderUtil.rectangleBordered(x2 + 1.5, y2 + 1.5, endx - 1.5, endy - 1.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0, 150));
                    }
                    if (mode.getValue() == ESPMode.SkeetBox2) {
                        RenderUtil.rectangle(x2 + 0.5, y2 + 0.5, x2 + 1.5, y2 + yDiff + 0.5, color);
                        RenderUtil.rectangle(x2 + 0.5, endy - 0.5, x2 + 1.5, endy - yDiff - 0.5, color);
                        RenderUtil.rectangle(x2 - 0.5, y2 + 0.5, x2 + 0.5, y2 + yDiff + 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + 1.5, y2 + 2.5, x2 + 2.5, y2 + yDiff + 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 - 0.5, y2 + yDiff + 0.5, x2 + 2.5, y2 + yDiff + 1.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 - 0.5, endy - 0.5, x2 + 0.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + 1.5, endy - 2.5, x2 + 2.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 - 0.5, endy - yDiff - 0.5, x2 + 2.5, endy - yDiff - 1.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + 1.0, y2 + 0.5, x2 + x2Diff, y2 + 1.5, color);
                        RenderUtil.rectangle(x2 - 0.5, y2 - 0.5, x2 + x2Diff, y2 + 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + 1.5, y2 + 1.5, x2 + x2Diff, y2 + 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + x2Diff, y2 - 0.5, x2 + x2Diff + 1.0, y2 + 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + 1.0, endy - 0.5, x2 + x2Diff, endy - 1.5, color);
                        RenderUtil.rectangle(x2 - 0.5, endy + 0.5, x2 + x2Diff, endy - 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + 1.5, endy - 1.5, x2 + x2Diff, endy - 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 + x2Diff, endy + 0.5, x2 + x2Diff + 1.0, endy - 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 0.5, y2 + 0.5, endx - 1.5, y2 + yDiff + 0.5, color);
                        RenderUtil.rectangle(endx - 0.5, endy - 0.5, endx - 1.5, endy - yDiff - 0.5, color);
                        RenderUtil.rectangle(endx + 0.5, y2 + 0.5, endx - 0.5, y2 + yDiff + 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 1.5, y2 + 2.5, endx - 2.5, y2 + yDiff + 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx + 0.5, y2 + yDiff + 0.5, endx - 2.5, y2 + yDiff + 1.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx + 0.5, endy - 0.5, endx - 0.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 1.5, endy - 2.5, endx - 2.5, endy - yDiff - 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx + 0.5, endy - yDiff - 0.5, endx - 2.5, endy - yDiff - 1.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 1.0, y2 + 0.5, endx - x2Diff, y2 + 1.5, color);
                        RenderUtil.rectangle(endx + 0.5, y2 - 0.5, endx - x2Diff, y2 + 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 1.5, y2 + 1.5, endx - x2Diff, y2 + 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - x2Diff, y2 - 0.5, endx - x2Diff - 1.0, y2 + 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 1.0, endy - 0.5, endx - x2Diff, endy - 1.5, color);
                        RenderUtil.rectangle(endx + 0.5, endy + 0.5, endx - x2Diff, endy - 0.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - 1.5, endy - 1.5, endx - x2Diff, endy - 2.5, Colors.getColor(0, 150));
                        RenderUtil.rectangle(endx - x2Diff, endy + 0.5, endx - x2Diff - 1.0, endy - 2.5, Colors.getColor(0, 150));
                    }
                    if (this.HEALTH.getValue().booleanValue() && (mode.getValue() == ESPMode.SkeetBox || mode.getValue() == ESPMode.SkeetBox2)) {
                        float health = ent.getHealth();
                        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
                        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                        float progress = health / ent.getMaxHealth();
                        Color customColor = health >= 0.0f ? ESP.blendColors(fractions, colors, progress).brighter() : Color.RED;
                        double difference = y2 - endy + 0.5;
                        double healthLocation = endy + difference * (double)progress;
                        RenderUtil.rectangleBordered(x2 - 6.5, y2 - 0.5, x2 - 2.5, endy, 1.0, Colors.getColor(0, 100), Colors.getColor(0, 150));
                        RenderUtil.rectangle(x2 - 5.5, endy - 1.0, x2 - 3.5, healthLocation, customColor.getRGB());
                        if (- difference > 50.0) {
                            int i2 = 1;
                            while (i2 < 10) {
                                double dThing = difference / 10.0 * (double)i2;
                                RenderUtil.rectangle(x2 - 6.5, endy - 0.5 + dThing, x2 - 2.5, endy - 0.5 + dThing - 1.0, Colors.getColor(0));
                                ++i2;
                            }
                        }
                        if ((int)ESP.getIncremental(progress * 100.0f, 1.0) <= 40) {
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(2.0f, 2.0f, 2.0f);
                            String nigger = String.valueOf((int)ESP.getIncremental(health * 5.0f, 1.0)) + "HP";
                            GlStateManager.popMatrix();
                        }
                    }
                }
                catch (Exception xValues) {
                    // empty catch block
                }
            }
            GlStateManager.popMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        RenderUtil.rectangle(0.0, 0.0, 0.0, 0.0, -1);
    }

    public static double getIncremental(double val, double inc) {
        double one = 1.0 / inc;
        return (double)Math.round(val * one) / one;
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Object color = null;
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
        int[] indicies = ESP.getFractionIndicies(fractions, progress);
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        return ESP.blend(colorRange[0], colorRange[1], 1.0f - weight);
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r2 = (float)ratio;
        float ir2 = 1.0f - r2;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r2 + rgb2[0] * ir2;
        float green = rgb1[1] * r2 + rgb2[1] * ir2;
        float blue = rgb1[2] * r2 + rgb2[2] * ir2;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(String.valueOf(String.valueOf(nf.format(red))) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int[] range = new int[2];
        int startPoint = 0;
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            ++startPoint;
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    private void updatePositions() {
        this.entityConvertedPointsMap.clear();
        float pTicks = ESP.mc.timer.renderPartialTicks;
        for (Entity e2 : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
            double topY;
            EntityPlayer ent;
            if (!(e2 instanceof EntityPlayer) || (ent = (EntityPlayer)e2) == Minecraft.getMinecraft().thePlayer) continue;
            double x2 = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)pTicks - ESP.mc.getRenderManager().viewerPosX + 0.36;
            double y2 = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)pTicks - ESP.mc.getRenderManager().viewerPosY;
            double z2 = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)pTicks - ESP.mc.getRenderManager().viewerPosZ + 0.36;
            y2 = topY = y2 + ((double)ent.height + 0.15);
            double[] convertedPoints = RenderUtil.convertTo2D(x2, y2, z2);
            double[] convertedPoints2 = RenderUtil.convertTo2D(x2 - 0.36, y2, z2 - 0.36);
            double xd2 = 0.0;
            if (convertedPoints2[2] < 0.0 || convertedPoints2[2] >= 1.0) continue;
            x2 = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)pTicks - ESP.mc.getRenderManager().viewerPosX - 0.36;
            z2 = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)pTicks - ESP.mc.getRenderManager().viewerPosZ - 0.36;
            double[] convertedPointsBottom = RenderUtil.convertTo2D(x2, y2, z2);
            y2 = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)pTicks - ESP.mc.getRenderManager().viewerPosY - 0.05;
            double[] convertedPointsx = RenderUtil.convertTo2D(x2, y2, z2);
            x2 = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)pTicks - ESP.mc.getRenderManager().viewerPosX - 0.36;
            z2 = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)pTicks - ESP.mc.getRenderManager().viewerPosZ + 0.36;
            double[] convertedPointsTop1 = RenderUtil.convertTo2D(x2, topY, z2);
            double[] convertedPointsx2 = RenderUtil.convertTo2D(x2, y2, z2);
            x2 = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)pTicks - ESP.mc.getRenderManager().viewerPosX + 0.36;
            z2 = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)pTicks - ESP.mc.getRenderManager().viewerPosZ + 0.36;
            double[] convertedPointsz = RenderUtil.convertTo2D(x2, y2, z2);
            x2 = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)pTicks - ESP.mc.getRenderManager().viewerPosX + 0.36;
            z2 = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)pTicks - ESP.mc.getRenderManager().viewerPosZ - 0.36;
            double[] convertedPointsTop2 = RenderUtil.convertTo2D(x2, topY, z2);
            double[] convertedPointsz2 = RenderUtil.convertTo2D(x2, y2, z2);
            this.entityConvertedPointsMap.put(ent, new double[]{convertedPoints[0], convertedPoints[1], 0.0, convertedPoints[2], convertedPointsBottom[0], convertedPointsBottom[1], convertedPointsBottom[2], convertedPointsx[0], convertedPointsx[1], convertedPointsx[2], convertedPointsx2[0], convertedPointsx2[1], convertedPointsx2[2], convertedPointsz[0], convertedPointsz[1], convertedPointsz[2], convertedPointsz2[0], convertedPointsz2[1], convertedPointsz2[2], convertedPointsTop1[0], convertedPointsTop1[1], convertedPointsTop1[2], convertedPointsTop2[0], convertedPointsTop2[1], convertedPointsTop2[2]});
        }
    }

    private String getColor(int level) {
        if (level == 2) {
            return "\u00a7a";
        }
        if (level == 3) {
            return "\u00a73";
        }
        if (level == 4) {
            return "\u00a74";
        }
        if (level >= 5) {
            return "\u00a76";
        }
        return "\u00a7f";
    }

    public static double[] convertTo2D(double x2, double y2, double z2) {
        double[] arrd;
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float)x2, (float)y2, (float)z2, modelView, projection, viewport, screenCoords);
        if (result) {
            double[] arrd2 = new double[3];
            arrd2[0] = screenCoords.get(0);
            arrd2[1] = (float)Display.getHeight() - screenCoords.get(1);
            arrd = arrd2;
            arrd2[2] = screenCoords.get(2);
        } else {
            arrd = null;
        }
        return arrd;
    }

    public static void rectangleBordered(double x2, double y2, double x1, double y1, double width, int internalColor, int borderColor) {
        ESP.rectangle(x2 + width, y2 + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        ESP.rectangle(x2 + width, y2, x1 - width, y2 + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        ESP.rectangle(x2, y2, x2 + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        ESP.rectangle(x1 - width, y2, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        ESP.rectangle(x2 + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float)(color >> 24 & 255) / 255.0f;
        float var6 = (float)(color >> 16 & 255) / 255.0f;
        float var7 = (float)(color >> 8 & 255) / 255.0f;
        float var8 = (float)(color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void doOther2DESP() {
        for (EntityPlayer entity : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (!this.isValid(entity)) continue;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            float partialTicks = ESP.mc.timer.renderPartialTicks;
            mc.getRenderManager();
            double x2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - RenderManager.renderPosX;
            mc.getRenderManager();
            double y2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - RenderManager.renderPosY;
            mc.getRenderManager();
            double z2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - RenderManager.renderPosZ;
            float DISTANCE = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
            float DISTANCE_SCALE = Math.min(DISTANCE * 0.15f, 0.15f);
            float SCALE = 0.035f;
            float xMid = (float)x2;
            float yMid = (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f);
            float zMid = (float)z2;
            GlStateManager.translate((float)x2, (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z2);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            mc.getRenderManager();
            GlStateManager.rotate(- RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(- SCALE, - SCALE, - (SCALE /= 2.0f));
            Tessellator tesselator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tesselator.getWorldRenderer();
            float HEALTH = entity.getHealth();
            int COLOR = -1;
            COLOR = (double)HEALTH > 20.0 ? -65292 : ((double)HEALTH >= 10.0 ? -16711936 : ((double)HEALTH >= 3.0 ? -23296 : -65536));
            Color gray = new Color(0, 0, 0);
            double thickness = 1.5f + DISTANCE * 0.01f;
            double xLeft = -20.0;
            double xRight = 20.0;
            double yUp = 27.0;
            double yDown = 130.0;
            double size = 10.0;
            Color color = new Color(255, 255, 255);
            if (entity.hurtTime > 0) {
                color = new Color(255, 0, 0);
            } else if (Teams.isOnSameTeam(entity)) {
                color = new Color(0, 255, 0);
            } else {
                entity.isInvisible();
            }
            ESP.drawBorderedRect((float)xLeft, (float)yUp, (float)xRight, (float)yDown, (float)thickness + 0.5f, Colors.BLACK.c, 0);
            ESP.drawBorderedRect((float)xLeft, (float)yUp, (float)xRight, (float)yDown, (float)thickness, color.getRGB(), 0);
            ESP.drawBorderedRect((float)xLeft - 3.0f - DISTANCE * 0.2f, (float)yDown - (float)(yDown - yUp), (float)xLeft - 2.0f, (float)yDown, 0.15f, Colors.BLACK.c, new Color(100, 100, 100).getRGB());
            ESP.drawBorderedRect((float)xLeft - 3.0f - DISTANCE * 0.2f, (float)yDown - (float)(yDown - yUp) * Math.min(1.0f, entity.getHealth() / 20.0f), (float)xLeft - 2.0f, (float)yDown, 0.15f, Colors.BLACK.c, COLOR);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }

    public static void drawBorderedRect(float x2, float y2, float x22, float y22, float l1, int col1, int col2) {
        ESP.drawRect(x2, y2, x22, y22, col2);
        float f2 = (float)(col1 >> 24 & 255) / 255.0f;
        float f1 = (float)(col1 >> 16 & 255) / 255.0f;
        float f22 = (float)(col1 >> 8 & 255) / 255.0f;
        float f3 = (float)(col1 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f22, f3, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y22);
        GL11.glVertex2d(x22, y22);
        GL11.glVertex2d(x22, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x22, y2);
        GL11.glVertex2d(x2, y22);
        GL11.glVertex2d(x22, y22);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawRect(float g2, float h2, float i2, float j2, int col1) {
        float f2 = (float)(col1 >> 24 & 255) / 255.0f;
        float f1 = (float)(col1 >> 16 & 255) / 255.0f;
        float f22 = (float)(col1 >> 8 & 255) / 255.0f;
        float f3 = (float)(col1 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f22, f3, f2);
        GL11.glBegin(7);
        GL11.glVertex2d(i2, h2);
        GL11.glVertex2d(g2, h2);
        GL11.glVertex2d(g2, j2);
        GL11.glVertex2d(i2, j2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    private void doCornerESP(EntityLivingBase entity) {
        Minecraft var10000 = mc;
        Iterator var2 = Minecraft.getMinecraft().theWorld.playerEntities.iterator();
        if (!entity.isInvisible()) {
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            float partialTicks = ESP.mc.timer.renderPartialTicks;
            mc.getRenderManager();
            double x2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - RenderManager.renderPosX;
            mc.getRenderManager();
            double y2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - RenderManager.renderPosY;
            mc.getRenderManager();
            double z2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - RenderManager.renderPosZ;
            var10000 = mc;
            float DISTANCE = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
            float DISTANCE_SCALE = Math.min(DISTANCE * 0.15f, 2.5f);
            float SCALE = 0.035f;
            GlStateManager.translate((float)x2, (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z2);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            mc.getRenderManager();
            GlStateManager.rotate(- RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(- SCALE, - SCALE, - (SCALE /= 2.0f));
            Tessellator tesselator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tesselator.getWorldRenderer();
            Color color = new Color(-13330213);
            if (entity.hurtTime > 0) {
                color = new Color(255, 0, 0);
            }
            double thickness = 2.0f + DISTANCE * 0.08f;
            double xLeft = -30.0;
            double xRight = 30.0;
            double yUp = entity.isSneaking() ? 28.0 : 18.0;
            double yDown = 140.0;
            double size = 10.0;
            ESP.drawVerticalLine(xLeft + size / 2.0, yUp, size / 2.0, thickness, color);
            this.drawHorizontalLine(xLeft, yUp + size - 1.0, size, thickness, color);
            ESP.drawVerticalLine(xRight - size / 2.0, yUp, size / 2.0, thickness, color);
            this.drawHorizontalLine(xRight, yUp + size - 1.0, size, thickness, color);
            ESP.drawVerticalLine(xLeft + size / 2.0, yDown, size / 2.0, thickness, color);
            this.drawHorizontalLine(xLeft, yDown - size + 1.0, size, thickness, color);
            ESP.drawVerticalLine(xRight - size / 2.0, yDown, size / 2.0, thickness, color);
            this.drawHorizontalLine(xRight, yDown - size + 1.0, size, thickness, color);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }

    private void doCornerESP() {
        for (EntityPlayer entity : Minecraft.getMinecraft().theWorld.playerEntities) {
            Minecraft var10001 = mc;
            if (entity == Minecraft.getMinecraft().thePlayer) continue;
            if (!this.isValid(entity)) {
                return;
            }
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            float partialTicks = ESP.mc.timer.renderPartialTicks;
            double var29 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            mc.getRenderManager();
            double x2 = var29 - RenderManager.renderPosX;
            var29 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            mc.getRenderManager();
            double y2 = var29 - RenderManager.renderPosY;
            var29 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            mc.getRenderManager();
            double z2 = var29 - RenderManager.renderPosZ;
            float DISTANCE = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
            float DISTANCE_SCALE = Math.min(DISTANCE * 0.15f, 2.5f);
            float SCALE = 0.035f;
            GlStateManager.translate((float)x2, (float)y2 + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z2);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            mc.getRenderManager();
            GlStateManager.rotate(- RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(- SCALE, - SCALE, - (SCALE /= 2.0f));
            Tessellator tesselator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tesselator.getWorldRenderer();
            Color color = new Color(Colors.WHITE.c);
            if (entity.hurtTime > 0) {
                color = new Color(Colors.BLUE.c);
            } else if (entity == KillAura.target && Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled()) {
                color = new Color(Colors.RED.c);
            }
            Color gray = new Color(0, 0, 0);
            double thickness = 2.0f + DISTANCE * 0.08f;
            double xLeft = -30.0;
            double xRight = 30.0;
            double yUp = 20.0;
            double yDown = 130.0;
            double size = 10.0;
            ESP.drawVerticalLine(xLeft + size / 2.0 - 1.0, yUp + 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xLeft + 1.0, yUp + size, size, thickness, gray);
            ESP.drawVerticalLine(xLeft + size / 2.0 - 1.0, yUp, size / 2.0, thickness, color);
            this.drawHorizontalLine(xLeft, yUp + size, size, thickness, color);
            ESP.drawVerticalLine(xRight - size / 2.0 + 1.0, yUp + 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xRight - 1.0, yUp + size, size, thickness, gray);
            ESP.drawVerticalLine(xRight - size / 2.0 + 1.0, yUp, size / 2.0, thickness, color);
            this.drawHorizontalLine(xRight, yUp + size, size, thickness, color);
            ESP.drawVerticalLine(xLeft + size / 2.0 - 1.0, yDown - 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xLeft + 1.0, yDown - size, size, thickness, gray);
            ESP.drawVerticalLine(xLeft + size / 2.0 - 1.0, yDown, size / 2.0, thickness, color);
            this.drawHorizontalLine(xLeft, yDown - size, size, thickness, color);
            ESP.drawVerticalLine(xRight - size / 2.0 + 1.0, yDown - 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xRight - 1.0, yDown - size, size, thickness, gray);
            ESP.drawVerticalLine(xRight - size / 2.0 + 1.0, yDown, size / 2.0, thickness, color);
            this.drawHorizontalLine(xRight, yDown - size, size, thickness, color);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }

    private void doBoxESP(EventRender3D event) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        for (Object o2 : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (!(o2 instanceof EntityPlayer) || o2 == Minecraft.getMinecraft().thePlayer) continue;
            EntityPlayer ent = (EntityPlayer)o2;
            if (Teams.isOnSameTeam(ent)) {
                RenderUtil.entityESPBox(ent, new Color(0, 255, 0), event);
                continue;
            }
            if (ent.hurtTime > 0) {
                RenderUtil.entityESPBox(ent, new Color(255, 0, 0), event);
                continue;
            }
            if (ent.isInvisible()) continue;
            Client.getModuleManager();
            AntiBot ab2 = (AntiBot)Client.getModuleManager().getModuleByClass(AntiBot.class);
            RenderUtil.entityESPBox(ent, new Color(255, 255, 255), event);
        }
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
	private void doCornerESP2() {
		Iterator var2 = this.mc.theWorld.playerEntities.iterator();

		while (var2.hasNext()) {
			EntityPlayer entity = (EntityPlayer) var2.next();
			if (entity != this.mc.thePlayer) {
				if (!this.isValid(entity)) {
					return;
				}
				GL11.glPushMatrix();
				GL11.glEnable(3042);
				GL11.glDisable(2929);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.enableBlend();
				GL11.glBlendFunc(770, 771);
				GL11.glDisable(3553);
				float partialTicks = this.mc.timer.renderPartialTicks;
				double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks
						- this.mc.getRenderManager().renderPosX;
				double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks
						- this.mc.getRenderManager().renderPosY;
				double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks
						- this.mc.getRenderManager().renderPosZ;
				float DISTANCE = this.mc.thePlayer.getDistanceToEntity(entity);
				float DISTANCE_SCALE = Math.min(DISTANCE * 0.15F, 2.5F);
				float SCALE = 0.035F;
				SCALE /= 2.0F;
				GlStateManager.translate((float) x,
						(float) y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float) z);
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-SCALE, -SCALE, -SCALE);
				Tessellator tesselator = Tessellator.getInstance();
				WorldRenderer worldRenderer = tesselator.getWorldRenderer();
				Color color = new Color(Colors.WHITE.c);
				if (entity.hurtTime > 0) {
					color = new Color(Colors.BLUE.c);
				} else if (KillAura.target!=null && KillAura.target.hurtResistantTime>1 && Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled()) {
					color = new Color(Colors.RED.c);
				}

				Color gray = new Color(0, 0, 0);
				double thickness = (double) (2.0F + DISTANCE * 0.08F);
				double xLeft = -30.0D;
				double xRight = 30.0D;
				double yUp = 20.0D;
				double yDown = 130.0D;
				double size = 10.0D;
				this.drawVerticalLine(xLeft + size / 2.0D-1, yUp + 1.0D, size / 2.0D, thickness, gray);
				this.drawHorizontalLine(xLeft + 1.0D, yUp + size, size, thickness, gray);
				this.drawVerticalLine(xLeft + size / 2.0D-1, yUp, size / 2.0D, thickness, color);
				this.drawHorizontalLine(xLeft, yUp + size, size, thickness, color);
				this.drawVerticalLine(xRight - size / 2.0D +1, yUp + 1.0D, size / 2.0D, thickness, gray);
				this.drawHorizontalLine(xRight-1, yUp + size, size, thickness, gray);
				this.drawVerticalLine(xRight - size / 2.0D+1, yUp, size / 2.0D, thickness, color);
				this.drawHorizontalLine(xRight, yUp + size, size, thickness, color);
				this.drawVerticalLine(xLeft + size / 2.0D-1, yDown -1, size / 2.0D, thickness, gray);
				this.drawHorizontalLine(xLeft + 1.0D, yDown  - size, size, thickness, gray);
				this.drawVerticalLine(xLeft + size / 2.0D-1, yDown, size / 2.0D, thickness, color);
				this.drawHorizontalLine(xLeft, yDown - size, size, thickness, color);
				this.drawVerticalLine(xRight - size / 2.0D+1, yDown - 1.0D, size / 2.0D, thickness, gray);
				this.drawHorizontalLine(xRight - 1.0D, yDown - size , size, thickness, gray);
				this.drawVerticalLine(xRight - size / 2.0D+1, yDown, size / 2.0D, thickness, color);
				this.drawHorizontalLine(xRight, yDown - size, size, thickness, color);
				GL11.glEnable(3553);
				GL11.glEnable(2929);
				GlStateManager.disableBlend();
				GL11.glDisable(3042);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glNormal3f(1.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
			}
		}

	}
    public void renderBox(Entity entity, double r2, double g2, double b2) {
        double x2 = RenderUtil.interpolation(entity.posX, entity.lastTickPosX);
        double y2 = RenderUtil.interpolation(entity.posY, entity.lastTickPosY);
        double z2 = RenderUtil.interpolation(entity.posZ, entity.lastTickPosZ);
        GL11.glPushMatrix();
        RenderUtil.pre();
        GL11.glLineWidth(1.0f);
        GL11.glEnable(2848);
        GL11.glColor3d(r2, g2, b2);
        Minecraft.getMinecraft().getRenderManager();
        Minecraft.getMinecraft().getRenderManager();
        Minecraft.getMinecraft().getRenderManager();
        mc.getRenderManager();
        mc.getRenderManager();
        mc.getRenderManager();
        mc.getRenderManager();
        mc.getRenderManager();
        mc.getRenderManager();
        RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(entity.boundingBox.minX - 0.05 - entity.posX + (entity.posX - RenderManager.renderPosX), entity.boundingBox.minY - entity.posY + (entity.posY - RenderManager.renderPosY), entity.boundingBox.minZ - 0.05 - entity.posZ + (entity.posZ - RenderManager.renderPosZ), entity.boundingBox.maxX + 0.05 - entity.posX + (entity.posX - RenderManager.renderPosX), entity.boundingBox.maxY + 0.1 - entity.posY + (entity.posY - RenderManager.renderPosY), entity.boundingBox.maxZ + 0.05 - entity.posZ + (entity.posZ - RenderManager.renderPosZ)));
        GL11.glDisable(2848);
        RenderUtil.post();
        GL11.glPopMatrix();
    }

    private static void drawVerticalLine(double xPos, double yPos, double xSize, double thickness, Color color) {
        Tessellator tesselator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tesselator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(xPos - xSize, yPos - thickness / 2.0, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos - xSize, yPos + thickness / 2.0, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + xSize, yPos + thickness / 2.0, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + xSize, yPos - thickness / 2.0, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        tesselator.draw();
    }

    private void drawHorizontalLine(double xPos, double yPos, double ySize, double thickness, Color color) {
        Tessellator tesselator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tesselator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(xPos - thickness / 2.0, yPos - ySize, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos - thickness / 2.0, yPos + ySize, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + thickness / 2.0, yPos + ySize, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + thickness / 2.0, yPos - ySize, 0.0).color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).endVertex();
        tesselator.draw();
    }

    private void box(float left, float top, float right, float bottom) {
        GL11.glColor4d(1.0, 1.0, 1.0, 0.5);
        RenderUtil.drawLine(left, top, right, top, 2.0f);
        RenderUtil.drawLine(left, bottom, right, bottom, 2.0f);
        RenderUtil.drawLine(left, top, left, bottom, 2.0f);
        RenderUtil.drawLine(right, top, right, bottom, 2.0f);
        RenderUtil.drawLine(left + 1.0f, top + 1.0f, right - 1.0f, top + 1.0f, 1.0f);
        RenderUtil.drawLine(left + 1.0f, bottom - 1.0f, right - 1.0f, bottom - 1.0f, 1.0f);
        RenderUtil.drawLine(left + 1.0f, top + 1.0f, left + 1.0f, bottom - 1.0f, 1.0f);
        RenderUtil.drawLine(right - 1.0f, top + 1.0f, right - 1.0f, bottom - 1.0f, 1.0f);
        RenderUtil.drawLine(left - 1.0f, top - 1.0f, right + 1.0f, top - 1.0f, 1.0f);
        RenderUtil.drawLine(left - 1.0f, bottom + 1.0f, right + 1.0f, bottom + 1.0f, 1.0f);
        RenderUtil.drawLine(left - 1.0f, top + 1.0f, left - 1.0f, bottom + 1.0f, 1.0f);
        RenderUtil.drawLine(right + 1.0f, top - 1.0f, right + 1.0f, bottom + 1.0f, 1.0f);
    }

    private void name(Entity entity, float left, float top, float right, float bottom) {
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString(FriendManager.isFriend(entity.getName()) ? "\u00a7b" + FriendManager.getAlias(entity.getName()) : entity.getName(), (int)(left + right) / 2, (int)(top - (float)Minecraft.getMinecraft().getMinecraft().fontRendererObj.FONT_HEIGHT - 2.0f + 1.0f), -1);
        if (((EntityPlayer)entity).getCurrentEquippedItem() != null) {
            String stack = ((EntityPlayer)entity).getCurrentEquippedItem().getDisplayName();
            Minecraft.getMinecraft().fontRendererObj.drawCenteredString(stack, (int)(left + right) / 2, (int)bottom, -1);
        }
    }
    
    private int getHealthColor(EntityLivingBase player) {
        float f2 = player.getHealth();
        float f1 = player.getMaxHealth();
        float f22 = Math.max(0.0f, Math.min(f2, f1) / f1);
        return Color.HSBtoRGB(f22 / 3.0f, 1.0f, 1.0f) | -16777216;
    }

    public boolean isValid(EntityLivingBase entity) {
        if (entity == Minecraft.getMinecraft().thePlayer) {
            return false;
        }
        if (entity.getHealth() <= 0.0f) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return true;
        }
        return false;
    }

    public static enum ESPMode {
        Box,
        SkeetBox,
        SkeetBox2,
        Moon;
       
    }

}

