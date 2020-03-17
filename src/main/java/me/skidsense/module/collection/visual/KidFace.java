package me.skidsense.module.collection.visual;

 import net.minecraft.client.renderer.GlStateManager;
 import net.minecraft.client.renderer.entity.RenderManager;
 import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
 import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

public class KidFace
extends Mod {
    public Mode<Enum> mode = new Mode("Mode","Mode", EmojiMode.values(), EmojiMode.SunZheng);

    public KidFace() {
        super("Kid Face", new String[] {"KidFace"}, ModuleType.Visual);
        //this.addValues(mode);
        //this.setRemoved(true);
    }

    private boolean isValid(EntityLivingBase entity) {
	    return entity instanceof EntityVillager || entity instanceof EntityPlayer && entity.getHealth() >= 0.0f && entity != mc.thePlayer;
    }

    @Sub
    public void onpre(EventPreUpdate event) {
    		this.setSuffix(this.mode.getValue());
    }
    
    @Sub
    public void onRender(EventRender3D event) {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (!this.isValid(entity)) continue;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            float partialTicks = mc.timer.renderPartialTicks;
            //this.mc.getRenderManager();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks - mc.getRenderManager().renderPosX;
            //this.mc.getRenderManager();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks - mc.getRenderManager().renderPosY;
            //this.mc.getRenderManager();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().renderPosZ;
            float SCALE = 0.035f;
            GlStateManager.translate((float)x, (float)y + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float)z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            //this.mc.getRenderManager();
            GlStateManager.rotate(- mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(- SCALE, - SCALE, - (SCALE /= 2.0f));
            double xLeft = -20.0;
            double yUp = 27.0;
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            switch (this.mode.getValue().toString()) {
			case "SunZheng":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/yaoer.png"), (int)xLeft + 9, (int)yUp - 20, 20, 25);
				break;
			case "FanYangXiao":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/ganga.png"), (int)xLeft + 7, (int)yUp - 28, 27, 32);
				break;
			case "SkidSenseDev":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/zhangchengyu.png"), (int)xLeft + 7, (int)yUp - 28, 27, 32);
				break;
			case "Hanxi":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/hanxi.png"), (int)xLeft + 7, (int)yUp - 28, 27, 32);
				break;
			case "LiangNuoYan":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/taijun.png"), (int)xLeft + 7, (int)yUp - 28, 27, 32);
				break;
			case "Jesus191":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/jesus191.png"), (int)xLeft + 7, (int)yUp - 22, 27, 32);
				break;
			case "Haze":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/haze.png"), (int)xLeft + 7, (int)yUp - 22, 26, 25);
				break;
			case "KoreaFish":
                RenderUtil.drawImage(new ResourceLocation("skidsense/face/KoreaFish.png"), (int)xLeft + 7, (int)yUp - 25, 27, 30);
				break;
				case "Kyaru":
				    RenderUtil.drawImage(new ResourceLocation("skidsense/face/Kyaru.png"), (int)xLeft + 7, (int)yUp - 25, 27, 33);
				break;
			}
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
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
        Kyaru,
    	FanYangXiao;
    }
}
