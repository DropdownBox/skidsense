package me.skidsense.module.collection.visual;

import java.awt.Color;

import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class InvViewer extends Mod {
	public Numbers<Double> posX = new Numbers<Double>("PosX", "PosX", 0.0, 0.0, 1000.0, 1.0);
    public Numbers<Double> posY = new Numbers<Double>("PosY", "PosY", 10.0, 0.0, 1000.0, 1.0);
    public InvViewer() {
        super("Inventory Viewer", new String[] { "InventoryViewer","InvViewer"}, ModuleType.Visual);
    }
    
    @Sub
    public void on2DRender(EventRenderGui e) {
    	Color fillWithOpacity = new Color(180,180,180,180);
    	Color outlineWithOpacity = new Color(250,200,200,255);
    	//shaded box
        Gui.drawRect(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 161, posY.getValue() + 55, fillWithOpacity.getRGB());
        //top
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 162, posY.getValue() + 1, outlineWithOpacity.getRGB());
        //bottom
        Gui.drawRect(posX.getValue(), posY.getValue() + 55, posX.getValue() + 162, posY.getValue() + 56, outlineWithOpacity.getRGB());
        //left
        Gui.drawRect(posX.getValue(), posY.getValue(), posX.getValue() + 1, posY.getValue() + 56, outlineWithOpacity.getRGB());
        //right
        Gui.drawRect(posX.getValue() + 161, posY.getValue(), posX.getValue() + 162, posY.getValue() + 56, outlineWithOpacity.getRGB());

        float yOffset = posY.getValue().floatValue();
        float boxWidth = 165;
        float boxHeight = 99;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtil.rectangleBordered((double)(posX.getValue() - 3) - 0.5D, (double)(posY.getValue() - 3) - 0.3D, (double)(posX.getValue() + boxWidth) + 0.5D, (double)(posY.getValue() + 59) + 0.3D, 0.5D, Colors.getColor(60), Colors.getColor(10));
        RenderUtil.rectangleBordered((double)(posX.getValue() - 3) + 0.5D, (double)(posY.getValue() - 3) + 0.6D, (double)(posX.getValue() + boxWidth) - 0.5D, (double)(posY.getValue() + 59) - 0.6D, 1.3D, Colors.getColor(60), Colors.getColor(40));
        RenderUtil.rectangleBordered((double)(posX.getValue() - 3) + 2.5D, (double)(posY.getValue() - 3) + 2.5D, (double)(posX.getValue() + boxWidth) - 2.5D, (double)(posY.getValue() + 59) - 2.5D, 0.5D, Colors.getColor(22), Colors.getColor(12));
        RenderUtil.drawGradientSideways((double)(posX.getValue()), (double)(posY.getValue() - 0.5), (double)(posX.getValue() + boxWidth - 2.5), (double)(posY.getValue()), Colors.getColor(55, 177, 218), Colors.getColor(204, 77, 198));
        RenderUtil.rectangle((double)(posX.getValue()), (double)(posY.getValue()), (double)(posX.getValue() + boxWidth - 2.5), (double)(posY.getValue()), Colors.getColor(0, 110));
        RenderUtil.rectangleBordered(posX.getValue(), posY.getValue(), posX.getValue() + 162, posY.getValue() + 56, 0.3D, Colors.getColor(48), Colors.getColor(10));
        RenderUtil.rectangle(posX.getValue() + 1, posY.getValue() + 1, posX.getValue() + 161, posY.getValue() + 55, Colors.getColor(17));
        //RenderUtil.rectangle((double)((float)(sr.getScaledWidth() / 2 - boxWidth + 6) + 4.5F), (double)(yOffset / 4 + boxHeight + 8), (double)(sr.getScaledWidth() / 2 - boxWidth + 35), (double)(yOffset / 4 + boxHeight + 9), Colors.getColor(17));
        //items
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        ItemStack[] items = Minecraft.getMinecraft().thePlayer.inventory.mainInventory;
        for (int size = items.length, item = 9; item < size; ++item) {
            final int slotX = (int) (posX.getValue() + (item) % 9 * 18);
            final int slotY = (int) (posY.getValue() + 2 + (item / 9 - 1) * 18);
            mc.getRenderItem().renderItemAndEffectIntoGUI(items[item], slotX + 1, slotY);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, items[item], slotX, slotY);
        }
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
    }
}
