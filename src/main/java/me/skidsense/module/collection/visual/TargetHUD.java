package me.skidsense.module.collection.visual;

import java.awt.Color;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
public class TargetHUD extends Mod {
	
    public TargetHUD() {
        super("Target Info", new String[]{"TargetInfo"}, ModuleType.Fight);
    }
    
    @Sub
    public final void onRenderGui(EventRenderGui event) {
    	FontRenderer fontRendererObj = this.mc.fontRendererObj;
        if (KillAura.target != null) {
            RenderUtil.drawBordered((float)(new ScaledResolution(this.mc).getScaledWidth() - 75), (double)(new ScaledResolution(this.mc).getScaledHeight() - 65), (double)(new ScaledResolution(this.mc).getScaledWidth() - 179), (double)(new ScaledResolution(this.mc).getScaledHeight() - 130), (float)1.0f, (int)new Color(35, 35, 35, 0).getRGB(), (int)new Color(35, 35, 35, 180).getRGB());
            fontRendererObj.drawString(KillAura.target.getName(), new ScaledResolution(this.mc).getScaledWidth() - 163, new ScaledResolution(this.mc).getScaledHeight() - 140, new Color(235, 0, 120, 200).getRGB());
            fontRendererObj.drawString("\u00a7aHP: \u00a7c" + (int)((EntityLivingBase)KillAura.target).getHealth() + "/" + (int)((EntityLivingBase)KillAura.target).getMaxHealth(), (float)(new ScaledResolution(this.mc).getScaledWidth() - 118), (float)(new ScaledResolution(this.mc).getScaledHeight() - 116), 16777215);
            fontRendererObj.drawString("\u00a7bHurt: " + (KillAura.target.hurtResistantTime > 0), (float)(new ScaledResolution(this.mc).getScaledWidth() - 118), (float)(new ScaledResolution(this.mc).getScaledHeight() - 108), 16777215);
            fontRendererObj.drawString("\u00a7eReach: " + (int)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(KillAura.target), (float)(new ScaledResolution(this.mc).getScaledWidth() - 118), (float)(new ScaledResolution(this.mc).getScaledHeight() - 94), 16777215);
            fontRendererObj.drawString("X: " + (int)KillAura.target.posX, (float)(new ScaledResolution(this.mc).getScaledWidth() - 118), (float)(new ScaledResolution(this.mc).getScaledHeight() - 87), new Color(235, 0, 120, 200).getRGB());
            fontRendererObj.drawString("Y: " + (int)KillAura.target.posY, (float)(new ScaledResolution(this.mc).getScaledWidth() - 118), (float)(new ScaledResolution(this.mc).getScaledHeight() - 80), new Color(235, 0, 120, 200).getRGB());
            fontRendererObj.drawString("Z:" + (int)KillAura.target.posZ, (float)(new ScaledResolution(this.mc).getScaledWidth() - 118), (float)(new ScaledResolution(this.mc).getScaledHeight() - 73), new Color(235, 0, 120, 200).getRGB());
            RenderUtil.drawEntityOnScreen((int)(new ScaledResolution(this.mc).getScaledWidth() - 148), (int)(new ScaledResolution(this.mc).getScaledHeight() - 66), (int)30, (float)2.0f, (float)15.0f, (EntityLivingBase)((EntityLivingBase)KillAura.target));
        }
    }
}
