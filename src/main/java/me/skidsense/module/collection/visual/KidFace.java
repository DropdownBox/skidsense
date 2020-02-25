package me.skidsense.module.collection.visual;

 import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

public class KidFace
extends Module {
    public Mode<Enum> mode = new Mode("Mode","Mode",(Enum[])EmojiMode.values(),(Enum)EmojiMode.SunZheng);

    public KidFace() {
        super("Kid Face", new String[] {"KidFace"}, ModuleType.Visual);
        this.addValues(mode);
        //this.setRemoved(true);
    }

    private boolean isValid(EntityLivingBase entity) {
        if (entity instanceof EntityVillager ||entity instanceof EntityPlayer && entity.getHealth() >= 0.0f && entity != mc.thePlayer) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onpre(EventPreUpdate event) {
    		this.setSuffix(this.mode.getValue());
    }
    
    @EventHandler
    public void onRender(EventRender3D event) {
        for (EntityPlayer entity : this.mc.theWorld.playerEntities) {
            if (!this.isValid((EntityLivingBase)entity)) continue;
            GL11.glPushMatrix();
            GL11.glEnable((int)3042);
            GL11.glDisable((int)2929);
            GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glDisable((int)3553);
            float partialTicks = this.mc.timer.renderPartialTicks;
            this.mc.getRenderManager();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - RenderManager.renderPosX;
            this.mc.getRenderManager();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - RenderManager.renderPosY;
            this.mc.getRenderManager();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - RenderManager.renderPosZ;
            float SCALE = 0.035f;
            GlStateManager.translate((float)((float)x), (float)((float)y + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f)), (float)((float)z));
            GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
            this.mc.getRenderManager();
            GlStateManager.rotate((float)(- RenderManager.playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glScalef((float)(- SCALE), (float)(- SCALE), (float)(- (SCALE /= 2.0f)));
            double xLeft = -20.0;
            double yUp = 27.0;
            GL11.glEnable((int)3553);
            GL11.glEnable((int)2929);
            GlStateManager.disableBlend();
            GL11.glDisable((int)3042);
            switch (this.mode.getValue().toString()) {
			case "SunZheng":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/yaoer.png"), (int)((int)xLeft + 9), (int)((int)yUp - 20), (int)20, (int)25);
				break;
			case "FanYangXiao":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/ganga.png"), (int)((int)xLeft + 7), (int)((int)yUp - 28), (int)27, (int)32);
				break;
			case "SkidSenseDev":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/zhangchengyu.png"), (int)((int)xLeft + 7), (int)((int)yUp - 28), (int)27, (int)32);
				break;
			case "Hanxi":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/hanxi.png"), (int)((int)xLeft + 7), (int)((int)yUp - 28), (int)27, (int)32);
				break;
			case "LiangNuoYan":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/taijun.png"), (int)((int)xLeft + 7), (int)((int)yUp - 28), (int)27, (int)32);
				break;
			case "Jesus191":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/jesus191.png"), (int)((int)xLeft + 7), (int)((int)yUp - 22), (int)27, (int)32);
				break;
			case "Haze":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/haze.png"), (int)((int)xLeft + 7), (int)((int)yUp - 22), (int)26, (int)25);
				break;
			case "KoreaFish":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/KoreaFish.png"), (int)((int)xLeft + 7), (int)((int)yUp - 25), (int)27, (int)30);
				break;
			}
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glNormal3f((float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
        }
    }
    
    enum EmojiMode{
    	Hanxi,
    	Jesus191,
    	SkidSenseDev,
    	SunZheng,
    	Haze,
    	KoreaFish,
    	LiangNuoYan,
    	FanYangXiao;
    }
}
