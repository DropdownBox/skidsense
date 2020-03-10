package me.skidsense.module.collection.visual;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArmorStatus extends Module{
    public ArmorStatus() {
        super("Armor Status", new String[] {"ArmorStatus"}, ModuleType.Visual);
        this.setRemoved(true);
    }

    @EventHandler
    public void onRender(EventRenderGui e) {
        GL11.glPushMatrix();
        List stuff = new ArrayList();
        boolean onwater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
        int split = -3;

        ItemStack errything;
        for(int index = 3; index >= 0; --index) {
            errything = mc.thePlayer.inventory.armorInventory[index];
            if (errything != null) {
                stuff.add(errything);
            }
        }

        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            stuff.add(mc.thePlayer.getCurrentEquippedItem());
        }

        Iterator var8 = stuff.iterator();

        while(var8.hasNext()) {
            errything = (ItemStack)var8.next();
            if (mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                split += 16;
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            mc.getRenderItem().zLevel = -150.0F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(errything, split + e.getResolution().getScaledWidth() / 2 - 4, e.getResolution().getScaledHeight() - (onwater ? 65 : 55));
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, errything, split + e.getResolution().getScaledWidth() / 2 - 4, e.getResolution().getScaledHeight() - (onwater ? 65 : 55));
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            errything.getEnchantmentTagList();
        }

        GL11.glPopMatrix();
    }
}
