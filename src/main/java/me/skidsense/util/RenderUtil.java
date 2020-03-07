/*
 * Decompiled with CFR 0.136.
 */
package me.skidsense.util;

import me.skidsense.Client;
import me.skidsense.hooks.events.EventRender3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.tessellate.Tessellation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.newdawn.slick.opengl.Texture;
import shadersmod.client.Shaders;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;


public class RenderUtil {
    private static final Frustum frustum = new Frustum();
	public static final Tessellation tessellator;
	private static final List<Integer> csBuffer;
	private static final Consumer<Integer> ENABLE_CLIENT_STATE;
	private static final Consumer<Integer> DISABLE_CLIENT_STATE;
	public static float delta;
	public static Object Existance_60;

	static {
		tessellator = Tessellation.createExpanding(4, 1.0f, 2.0f);
		csBuffer = new ArrayList<Integer>();
		ENABLE_CLIENT_STATE = GL11::glEnableClientState;
		DISABLE_CLIENT_STATE = GL11::glEnableClientState;
	}
	
    public static double interpolate(double newPos, double oldPos) {
        return oldPos + (newPos - oldPos) * (double)Client.mc.timer.renderPartialTicks;
    }
    public static boolean isHovering(float mouseX,float mouseY,float boxX,float boxY,float boxX1,float boxY1){
	    return (mouseX >= boxX && mouseX <= boxX1 && mouseY >= boxY && mouseY <= boxY1) || (mouseX <= boxX && mouseX >= boxX1 && mouseY <= boxY && mouseY >= boxY1);
    }
    public static int getRandomRGB(double min, double max, float alpha) {
        return new Color((float)MathUtil.randomDouble(min, max), (float)MathUtil.randomDouble(min, max), (float)MathUtil.randomDouble(min, max), alpha).getRGB();
    }
    
    public static int withTransparency(int rgb, float alpha) {
        float r2 = (float)(rgb >> 16 & 255) / 255.0f;
        float g2 = (float)(rgb >> 8 & 255) / 255.0f;
        float b2 = (float)(rgb >> 0 & 255) / 255.0f;
        return new Color(r2, g2, b2, alpha).getRGB();
    }

    public static int getHexRGB(int hex) {
        return -16777216 | hex;
    }

    public static Color rainbow(long time, float count, float fade) {
        float hue = ((float)time + (1.0F + count) * 2.0E8F) /(500 * 1.0E9F) % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue,1.0F, 1.0F)).intValue()), 16);
        Color c = new Color((int)color);
        return new Color((float)c.getRed() / 255.0F * fade, (float)c.getGreen() / 255.0F * fade, (float)c.getBlue() / 255.0F * fade, (float)c.getAlpha() / 255.0F);
    }
    public static void pre() {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public static void post() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0, 1.0, 1.0);
    }
    public static void drawRoundedRect(float x, float y, float x2, float y2, final float round, final int color) {
        x += (float)(round / 2.0f + 0.5);
        y += (float)(round / 2.0f + 0.5);
        x2 -= (float)(round / 2.0f + 0.5);
        y2 -= (float)(round / 2.0f + 0.5);
        Gui.drawRect((int)x, (int)y, (int)x2, (int)y2, color);
        circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
        circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
        circle(x + round / 2.0f, y + round / 2.0f, round, color);
        circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
        Gui.drawRect((int)(x - round / 2.0f - 0.5f), (int)(y + round / 2.0f), (int)x2, (int)(y2 - round / 2.0f), color);
        Gui.drawRect((int)x, (int)(y + round / 2.0f), (int)(x2 + round / 2.0f + 0.5f), (int)(y2 - round / 2.0f), color);
        Gui.drawRect((int)(x + round / 2.0f), (int)(y - round / 2.0f - 0.5f), (int)(x2 - round / 2.0f), (int)(y2 - round / 2.0f), color);
        Gui.drawRect((int)(x + round / 2.0f), (int)y, (int)(x2 - round / 2.0f), (int)(y2 + round / 2.0f + 0.5f), color);
    }
    public static Color reAlpha(Color cIn, float alpha){
        return new Color(cIn.getRed()/255f,cIn.getGreen()/255f,cIn.getBlue()/255f,cIn.getAlpha() / 255f * alpha);
    }
    public static void drawRoundedRect(float x, float y, float x2, float y2, final float round, final Color color) {
        x += (float)(round / 2.0f + 0.5);
        y += (float)(round / 2.0f + 0.5);
        x2 -= (float)(round / 2.0f + 0.5);
        y2 -= (float)(round / 2.0f + 0.5);
        Gui.drawRect((int)x, (int)y, (int)x2, (int)y2, color.getRGB());
        circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
        circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
        circle(x + round / 2.0f, y + round / 2.0f, round, color);
        circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
        Gui.drawRect((int)(x - round / 2.0f - 0.5f), (int)(y + round / 2.0f), (int)x2, (int)(y2 - round / 2.0f), color.getRGB());
        Gui.drawRect((int)x, (int)(y + round / 2.0f), (int)(x2 + round / 2.0f + 0.5f), (int)(y2 - round / 2.0f), color.getRGB());
        Gui.drawRect((int)(x + round / 2.0f), (int)(y - round / 2.0f - 0.5f), (int)(x2 - round / 2.0f), (int)(y2 - round / 2.0f), color.getRGB());
        Gui.drawRect((int)(x + round / 2.0f), (int)y, (int)(x2 - round / 2.0f), (int)(y2 + round / 2.0f + 0.5f), color.getRGB());
    }
    public static void circle(final float x, final float y, final float radius, final int fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }
    
    public static void circle(final float x, final float y, final float radius, final Color fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }
    
    public static void arc(final float x, final float y, final float start, final float end, final float radius, final int color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }
    
    public static void arc(final float x, final float y, final float start, final float end, final float radius, final Color color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }
    
    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final int color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final float var11 = (color >> 24 & 0xFF) / 255.0f;
        final float var12 = (color >> 16 & 0xFF) / 255.0f;
        final float var13 = (color >> 8 & 0xFF) / 255.0f;
        final float var14 = (color & 0xFF) / 255.0f;
        final Tessellator var15 = Tessellator.getInstance();
        final WorldRenderer var16 = var15.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var12, var13, var14, var11);
        if (var11 > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float)Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float)Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float)Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float)Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawWolframEntityESP(EntityLivingBase entity, int rgb, double posX, double posY, double posZ) {
        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotatef(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        setColor(rgb);
        enableGL3D(1.0F);
        Cylinder c = new Cylinder();
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        c.setDrawStyle(100011);
        c.draw(0.5F, 0.5F, entity.height + 0.1F, 18, 1);
        disableGL3D();
        GL11.glPopMatrix();
     }
    
    public static void enableGL3D(float lineWidth) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        Shaders.disableLightmap();
        Shaders.disableFog();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(lineWidth);
     }
    
    public static void setColor(int colorHex) {
        float alpha = (float)(colorHex >> 24 & 255) / 255.0F;
        float red = (float)(colorHex >> 16 & 255) / 255.0F;
        float green = (float)(colorHex >> 8 & 255) / 255.0F;
        float blue = (float)(colorHex & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha == 0.0F ? 1.0F : alpha);
     }
    
    public static void disableGL3D() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
     }
    
    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final Color color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final Tessellator var9 = Tessellator.getInstance();
        final WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        if (color.getAlpha() > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float)Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float)Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float)Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float)Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBordered(double x2, double y2, double width, double height, double length, int innerColor, int outerColor) {
        Gui.drawRect(x2, y2, x2 + width, y2 + height, innerColor);
        Gui.drawRect(x2 - length, y2, x2, y2 + height, outerColor);
        Gui.drawRect(x2 - length, y2 - length, x2 + width, y2, outerColor);
        Gui.drawRect(x2 + width, y2 - length, x2 + width + length, y2 + height + length, outerColor);
        Gui.drawRect(x2 - length, y2 + height, x2 + width, y2 + height + length, outerColor);
    }
    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    
	public static void rectTexture(float x, float y, float w, float h, Texture texture, int color) {
		if (texture == null) {
			return;
		}
		GlStateManager.color(0, 0, 0);
		GL11.glColor4f(0, 0, 0, 0);

		x = Math.round(x);
		w = Math.round(w);
		y = Math.round(y);
		h = Math.round(h);
		
		float var11 = (float)(color >> 24 & 255) / 255.0F;
        float var6 = (float)(color >> 16 & 255) / 255.0F;
        float var7 = (float)(color >> 8 & 255) / 255.0F;
        float var8 = (float)(color & 255) / 255.0F;
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		
		float tw = (w/texture.getTextureWidth())/(w/texture.getImageWidth());
		float th = (h/texture.getTextureHeight())/(h/texture.getImageHeight());
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(0, th);
			GL11.glVertex2f(x, y+h);
			GL11.glTexCoord2f(tw, th);
			GL11.glVertex2f(x+w, y+h);
			GL11.glTexCoord2f(tw, 0);
			GL11.glVertex2f(x+w, y);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
	
	public static void rectTexture(float x, float y, float w, float h, Texture texture) {
		if (texture == null) {
			return;
		}
		GlStateManager.color(0, 0, 0);
		GL11.glColor4f(0, 0, 0, 0);

		x = Math.round(x);
		w = Math.round(w);
		y = Math.round(y);
		h = Math.round(h);
		

        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		
		float tw = (w/texture.getTextureWidth())/(w/texture.getImageWidth());
		float th = (h/texture.getTextureHeight())/(h/texture.getImageHeight());
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(0, th);
			GL11.glVertex2f(x, y+h);
			GL11.glTexCoord2f(tw, th);
			GL11.glVertex2f(x+w, y+h);
			GL11.glTexCoord2f(tw, 0);
			GL11.glVertex2f(x+w, y);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
	
    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, Color color) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getRed() / 255.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    public static void drawBordered1(double x2, double y2, double width, double height, double length, int innerColor, int outerColor) {
        Gui.drawRect(x2, y2, x2 + width, y2 + height, innerColor);
        Gui.drawRect(x2, y2, x2, y2, outerColor);
    }

    public static boolean isInFrustumView(Entity ent) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        double x2 = RenderUtil.interpolate(current.posX, current.lastTickPosX);
        double y2 = RenderUtil.interpolate(current.posY, current.lastTickPosY);
        double z2 = RenderUtil.interpolate(current.posZ, current.lastTickPosZ);
        frustum.setPosition(x2, y2, z2);
        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public static final ScaledResolution getScaledRes() {
        ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
        return scaledRes;
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
        float var11 = (float) (color >> 24 & 255) / 255.0f;
        float var6 = (float) (color >> 16 & 255) / 255.0f;
        float var7 = (float) (color >> 8 & 255) / 255.0f;
        float var8 = (float) (color & 255) / 255.0f;
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

    public static void disableLighting() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void drawGradientRect(float x2, float y2, float x1, float y1, int topColor, int bottomColor) {
        RenderUtil.enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        RenderUtil.glColor(topColor);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x1, y1);
        RenderUtil.glColor(bottomColor);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        RenderUtil.disableGL2D();
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 255) / 255.0f;
        float red = (float)(hex >> 16 & 255) / 255.0f;
        float green = (float)(hex >> 8 & 255) / 255.0f;
        float blue = (float)(hex & 255) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawGradientBordere(float x2, float y2, float x1, float y1, float lineWidth, int border, int bottom, int top) {
        RenderUtil.enableGL2D();
        RenderUtil.drawGradientRect(x2, y2, x1, y1, top, bottom);
        RenderUtil.glColor(border);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        RenderUtil.disableGL2D();
    }

    public static void drawBorderedRect(float x2, float y2, float x22, float y22, float l1, int col1, int col2) {
        RenderUtil.drawRect(x2, y2, x22, y22, col2);
        float f2 = (float)(col1 >> 24 & 255) / 255.0f;
        float f22 = (float)(col1 >> 16 & 255) / 255.0f;
        float f3 = (float)(col1 >> 8 & 255) / 255.0f;
        float f4 = (float)(col1 & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(f22, f3, f4, f2);
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
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawRect(float g2, float h2, float i2, float j2, int col1) {
        float f2 = (float)(col1 >> 24 & 255) / 255.0f;
        float f22 = (float)(col1 >> 16 & 255) / 255.0f;
        float f3 = (float)(col1 >> 8 & 255) / 255.0f;
        float f4 = (float)(col1 & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(f22, f3, f4, f2);
        GL11.glBegin(7);
        GL11.glVertex2d(i2, h2);
        GL11.glVertex2d(g2, h2);
        GL11.glVertex2d(g2, j2);
        GL11.glVertex2d(i2, j2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }
    public static void glColor(Color color){
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }
    public static void drawRect(float g2, float h2, float i2, float j2, Color col1) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        glColor(col1);
        GL11.glBegin(7);
        GL11.glVertex2d(i2, h2);
        GL11.glVertex2d(g2, h2);
        GL11.glVertex2d(g2, j2);
        GL11.glVertex2d(i2, j2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void Color(int color) {
        float f = (float)(color >> 24 & 255) / 255.0f;
        float f2 = (float)(color >> 16 & 255) / 255.0f;
        float f3 = (float)(color >> 8 & 255) / 255.0f;
        float f4 = (float)(color & 255) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static void drawRect(double x1, double y1, double x2, double y2, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        Color(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }
    
	public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f2 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        float f4 = (float) (col2 >> 24 & 255) / 255.0f;
        float f5 = (float) (col2 >> 16 & 255) / 255.0f;
        float f6 = (float) (col2 >> 8 & 255) / 255.0f;
        float f7 = (float) (col2 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        drawRect(left - (!borderIncludedInBounds ? borderWidth : 0), top - (!borderIncludedInBounds ? borderWidth : 0), right + (!borderIncludedInBounds ? borderWidth : 0), bottom + (!borderIncludedInBounds ? borderWidth : 0), borderColor);
        drawRect(left + (borderIncludedInBounds ? borderWidth : 0), top + (borderIncludedInBounds ? borderWidth : 0), right - ((borderIncludedInBounds ? borderWidth : 0)), bottom - ((borderIncludedInBounds ? borderWidth : 0)), insideColor);
    }

    // 你这是什么傻逼FernFlower解出来的RenderUtil在这里耀武扬威呢不用GL11的常数你让我维护你妈呢
    public static void drawImage(int x, int y, int width, int height, ResourceLocation image) {
        GL11.glDisable(GL_DEPTH_TEST);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public static void drawLines(AxisAlignedBB boundingBox) {
        GL11.glPushMatrix();
        GL11.glBegin(2);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void drawHLine(float par1, float par2, float par3, int par4) {
        if (par2 < par1) {
            float var5 = par1;
            par1 = par2;
            par2 = var5;
        }
        RenderUtil.drawRect(par1, par3, par2 + 1.0f, par3 + 1.0f, par4);
    }

	public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        BBDrawA(aa, tessellator, worldRenderer);
    }

    private static void BBDrawA(AxisAlignedBB aa, Tessellator tessellator, WorldRenderer worldRenderer) {
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        BBDraw3(aa, worldRenderer);
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        BBDraw2(aa, worldRenderer);
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        BBDraw1(aa, tessellator, worldRenderer);
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        BBDraw1(aa, tessellator, worldRenderer);
        worldRenderer.endVertex();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.endVertex();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        BBDraw2(aa, worldRenderer);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.endVertex();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        BBDraw3(aa, worldRenderer);
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.endVertex();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.endVertex();
        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.endVertex();
    }

    private static void BBDraw3(AxisAlignedBB aa, WorldRenderer worldRenderer) {
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
    }

    private static void BBDraw2(AxisAlignedBB aa, WorldRenderer worldRenderer) {
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
    }

    private static void BBDraw1(AxisAlignedBB aa, Tessellator tessellator, WorldRenderer worldRenderer) {
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }

    public static void drawEntityOnScreen(int p_147046_0_, int p_147046_1_, int p_147046_2_, float p_147046_3_, float p_147046_4_, EntityLivingBase p_147046_5_) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(p_147046_0_, p_147046_1_, 40.0f);
        GlStateManager.scale(-p_147046_2_, p_147046_2_, p_147046_2_);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float var6 = p_147046_5_.renderYawOffset;
        float var7 = p_147046_5_.rotationYaw;
        float var8 = p_147046_5_.rotationPitch;
        float var9 = p_147046_5_.prevRotationYawHead;
        float var10 = p_147046_5_.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((- (float)Math.atan(p_147046_4_ / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        p_147046_5_.renderYawOffset = (float)Math.atan(p_147046_3_ / 40.0f) * -14.0f;
        p_147046_5_.rotationYaw = (float)Math.atan(p_147046_3_ / 40.0f) * -14.0f;
        p_147046_5_.rotationPitch = (- (float)Math.atan(p_147046_4_ / 40.0f)) * 15.0f;
        p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
        p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager var11 = Minecraft.getMinecraft().getRenderManager();
        var11.setPlayerViewY(180.0f);
        var11.setRenderShadow(false);
        var11.renderEntityWithPosYaw(p_147046_5_, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        var11.setRenderShadow(true);
        p_147046_5_.renderYawOffset = var6;
        p_147046_5_.rotationYaw = var7;
        p_147046_5_.rotationPitch = var8;
        p_147046_5_.prevRotationYawHead = var9;
        p_147046_5_.rotationYawHead = var10;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

	public static void drawroundrect(double x,double y,double x2,double y2,int color) {
		Gui.drawRect(x, y,x2, y2, color);
		Gui.drawRect(x, y,x2+1.5, y-1, color);
		Gui.drawRect(x, y2,x2+1.5, y2+1, color);
		Gui.drawRect(x2+1, y,x2+0.5, y-1, color);
		Gui.drawRect(x2+1, y2,x2+0.5, y2+1, color);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRoundRect(double d, double e, double g, double h, int color)
    {
        drawRect(d + 1, e, g - 1, h, color);
        drawRect(d, e + 1, d + 1, h - 1, color);
        drawRect(d + 1, e + 1, d + 0.5, e + 0.5, color);
        drawRect(d + 1, e + 1, d + 0.5, e + 0.5, color);
        drawRect(g - 1, e + 1, g - 0.5, e + 0.5, color);
        drawRect(g - 1, e + 1, g, h - 1, color);
        drawRect(d + 1, h - 1, d + 0.5, h - 0.5, color);
        drawRect(g - 1, h - 1, g - 0.5, h - 0.5, color);
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
                                     float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        glInit(red, green, blue, alpha);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void rectangleBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawOutline(double x, double y, double width, double height, double lineWidth, int color) {
    	RenderUtil.drawRect(x, y, x + width, y + lineWidth, color);
    	RenderUtil.drawRect(x, y, x + lineWidth, y + height, color);
    	RenderUtil.drawRect(x, y + height - lineWidth, x + width, y + height, color);
    	RenderUtil.drawRect(x + width - lineWidth, y, x + width, y + height, color);
    }
    
    public static void drawBorderedRect(float x2, double d, float x22, double e, float l1, int col1, int col2) {
        RenderUtil.drawRect(x2, d, x22, e, col2);
        float f2 = (float)(col1 >> 24 & 255) / 255.0f;
        float f22 = (float)(col1 >> 16 & 255) / 255.0f;
        float f3 = (float)(col1 >> 8 & 255) / 255.0f;
        float f4 = (float)(col1 & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(f22, f3, f4, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, d);
        GL11.glVertex2d(x2, e);
        GL11.glVertex2d(x22, e);
        GL11.glVertex2d(x22, d);
        GL11.glVertex2d(x2, d);
        GL11.glVertex2d(x22, d);
        GL11.glVertex2d(x2, e);
        GL11.glVertex2d(x22, e);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return RenderUtil.isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustum.setPosition(current.posX, current.posY, current.posZ);
        return frustum.isBoundingBoxInFrustum(bb);
    }

    public static void drawHorizontalLine(float x, float y, float x1, float thickness, int color) {
        RenderUtil.drawRect2(x, y, x1, y + thickness, color);
    }

    public static void drawRect2(double x, double y, double x2, double y2, int color) {
        RenderUtil.drawRect(x, y, x2, y2, color);
    }

    public static void drawVerticalLine(float x, float y, float y1, float thickness, int color) {
        RenderUtil.drawRect2(x, y, x + thickness, y1, color);
    }

    public static void drawHollowBox(float x, float y, float x1, float y1, float thickness, int color) {
        RenderUtil.drawHorizontalLine(x, y, x1, thickness, color);
        RenderUtil.drawHorizontalLine(x, y1, x1, thickness, color);
        RenderUtil.drawVerticalLine(x, y, y1, thickness, color);
        RenderUtil.drawVerticalLine(x1 - thickness, y, y1, thickness, color);
    }

	public static double[] convertTo2D(double x, double y, double z) {
        double[] arrd;
        java.nio.FloatBuffer screenCoords = org.lwjgl.BufferUtils.createFloatBuffer(3);
        java.nio.IntBuffer viewport = org.lwjgl.BufferUtils.createIntBuffer(16);
        java.nio.FloatBuffer modelView = org.lwjgl.BufferUtils.createFloatBuffer(16);
        java.nio.FloatBuffer projection = org.lwjgl.BufferUtils.createFloatBuffer(16);
        org.lwjgl.opengl.GL11.glGetFloat(2982, modelView);
        org.lwjgl.opengl.GL11.glGetFloat(2983, projection);
        org.lwjgl.opengl.GL11.glGetInteger(2978, viewport);
        boolean result = org.lwjgl.util.glu.GLU.gluProject((float) x, (float) y,
                (float) z, modelView, projection,
                viewport, screenCoords);
        if (result) {
            double[] arrd2 = new double[3];
            arrd2[0] = screenCoords.get(0);
            arrd2[1] = (float) org.lwjgl.opengl.Display.getHeight() - screenCoords.get(1);
            arrd = arrd2;
            arrd2[2] = screenCoords.get(2);
        } else {
            arrd = null;
        }
        return arrd;
	}

	public static void entityESPBox(Entity e, Color color, EventRender3D event) {
        double posX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) event.getPartialTicks()
                - RenderManager.renderPosX;
        double posY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) event.getPartialTicks()
                - RenderManager.renderPosY;
        double posZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) event.getPartialTicks()
                - RenderManager.renderPosZ;
        AxisAlignedBB box = AxisAlignedBB.fromBounds(posX - (double) e.width, posY, posZ - (double) e.width,
                posX + (double) e.width, posY + (double) e.height + 0.2, posZ + (double) e.width);
        if (e instanceof EntityLivingBase) {
            box = AxisAlignedBB.fromBounds(posX - (double) e.width + 0.2, posY, posZ - (double) e.width + 0.2,
                    posX + (double) e.width - 0.2, posY + (double) e.height + (e.isSneaking() ? 0.02 : 0.2),
                    posZ + (double) e.width - 0.2);
        }
        GL11.glLineWidth(3.0f);
        GL11.glColor4f(0f, 0f, 0f, 1f);
        RenderUtil.drawOutlinedBoundingBox(box);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f,
                (float) color.getBlue() / 255.0f, 1f);
        RenderUtil.drawOutlinedBoundingBox(box);
    }

    // TODO 你这是什么傻逼FernFlower解出来的RenderUtil在这里耀武扬威呢不用GL11的常数你让我维护你妈呢
    public static void drawSolidBlockESP(double x, double y, double z, float red, float green, float blue, float alpha) {
        glInit(red, green, blue, alpha);
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT,GL_NICEST);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glDisable(2848);
        glDisable(GL_POLYGON_SMOOTH);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static double interpolation(final double newPos, final double oldPos) {
        return oldPos + (newPos - oldPos) * Minecraft.getMinecraft().timer.renderPartialTicks;
    }

    public static void drawLine(final Vec2f start, final Vec2f end, final float width) {
        drawLine(start.getX(), start.getY(), end.getX(), end.getY(), width);
    }

    public static void drawLine(final Vec3f start, final Vec3f end, final float width) {
        drawLine((float) start.getX(), (float) start.getY(), (float) start.getZ(), (float) end.getX(),
				(float) end.getY(), (float) end.getZ(), width);
	}

	public static void drawLine(final float x, final float y, final float x1, final float y1, final float width) {
		drawLine(x, y, 0.0f, x1, y1, 0.0f, width);
	}

	public static void drawLine(final float x, final float y, final float z, final float x1, final float y1,
			final float z1, final float width) {
		GL11.glLineWidth(width);
		setupRender(true);
		setupClientState(GLClientState.VERTEX, true);
		RenderUtil.tessellator.addVertex(x, y, z).addVertex(x1, y1, z1).draw(3);
		setupClientState(GLClientState.VERTEX, false);
		setupRender(false);
	}

	public static void setupClientState(final GLClientState state, final boolean enabled) {
		RenderUtil.csBuffer.clear();
		if (state.ordinal() > 0) {
			RenderUtil.csBuffer.add(state.getCap());
		}
		RenderUtil.csBuffer.add(32884);
		RenderUtil.csBuffer.forEach(enabled ? RenderUtil.ENABLE_CLIENT_STATE : RenderUtil.DISABLE_CLIENT_STATE);
	}

	public static void setupRender(final boolean start) {
		if (start) {
			GlStateManager.enableBlend();
			GL11.glEnable(2848);
			GlStateManager.disableDepth();
			GlStateManager.disableTexture2D();
			GlStateManager.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
        }
        GlStateManager.depthMask(!start);
    }

    // TODO 你这是什么傻逼FernFlower解出来的RenderUtil在这里耀武扬威呢不用GL11的常数你让我维护你妈呢
    private static void glInit(float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static float getAnimationState(float animation, float finalState, float speed) {
        float add = (float)((double)delta * speed);
        animation = animation < finalState ? (animation + (double)add < finalState ? (animation += (double)add) : finalState) : (animation - (double)add > finalState ? (animation -= (double)add) : finalState);
        return animation;
    }

    public static double getAnimationState(double animation, double finalState, double speed) {
        float add = (float)((double)delta * speed);
        animation = animation < finalState ? (animation + (double)add < finalState ? (animation += (double)add) : finalState) : (animation - (double)add > finalState ? (animation -= (double)add) : finalState);
        return animation;
    }

    public static class R3DUtils {
        public static void startDrawing() {
            GL11.glEnable(3042);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            Client.mc.entityRenderer.setupCameraTransform(Client.mc.timer.renderPartialTicks, 0);
        }

        public static void stopDrawing() {
            GL11.glDisable(3042);
            GL11.glEnable(3553);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
            GL11.glEnable(2929);
        }

        public static void drawOutlinedBox(AxisAlignedBB box2) {
            if (box2 == null) {
                return;
            }
            Client.mc.entityRenderer.setupCameraTransform(Client.mc.timer.renderPartialTicks, 0);
            GL11.glBegin(3);
            GL11.glVertex3d(box2.minX, box2.minY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.minY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.minY, box2.maxZ);
            GL11.glVertex3d(box2.minX, box2.minY, box2.maxZ);
            GL11.glVertex3d(box2.minX, box2.minY, box2.minZ);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex3d(box2.minX, box2.maxY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.maxY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.maxY, box2.maxZ);
            GL11.glVertex3d(box2.minX, box2.maxY, box2.maxZ);
            GL11.glVertex3d(box2.minX, box2.maxY, box2.minZ);
            GL11.glEnd();
            GL11.glBegin(1);
            GL11.glVertex3d(box2.minX, box2.minY, box2.minZ);
            GL11.glVertex3d(box2.minX, box2.maxY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.minY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.maxY, box2.minZ);
            GL11.glVertex3d(box2.maxX, box2.minY, box2.maxZ);
            GL11.glVertex3d(box2.maxX, box2.maxY, box2.maxZ);
            GL11.glVertex3d(box2.minX, box2.minY, box2.maxZ);
            GL11.glVertex3d(box2.minX, box2.maxY, box2.maxZ);
            GL11.glEnd();
        }


        public static void drawImage(int x, int y, int width, int height, ResourceLocation image) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glDepthMask(false);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(image);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glEnable(2929);
        }

        public static void drawblock(final double a, final double a2, final double a3, final int a4, final int a5, final float a6) {
            final float a7 = (a4 >> 24 & 0xFF) / 255.0f;
            final float a8 = (a4 >> 16 & 0xFF) / 255.0f;
            final float a9 = (a4 >> 8 & 0xFF) / 255.0f;
            final float a10 = (a4 & 0xFF) / 255.0f;
            final float a11 = (a5 >> 24 & 0xFF) / 255.0f;
            final float a12 = (a5 >> 16 & 0xFF) / 255.0f;
            final float a13 = (a5 >> 8 & 0xFF) / 255.0f;
            final float a14 = (a5 & 0xFF) / 255.0f;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(a8, a9, a10, a7);
            drawOutlinedBoundingBox(new AxisAlignedBB(a, a2, a3, a + 1.0, a2 + 1.0, a3 + 1.0));
            GL11.glLineWidth(a6);
            GL11.glColor4f(a12, a13, a14, a11);
            drawOutlinedBoundingBox(new AxisAlignedBB(a, a2, a3, a + 1.0, a2 + 1.0, a3 + 1.0));
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }

        public static void drawOutlinedBoundingBox(final AxisAlignedBB aa) {
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            BBDrawA(aa, tessellator, worldRenderer);
        }
    }
}

