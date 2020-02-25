package me.skidsense.module.collection.visual;

import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;

public class ItemEsp extends Module {
	
	public Option outlinedboundingBox = new Option("Outlined", "Outlined", true);
	
	public ItemEsp() {
		super("Item ESP", new String[]{"ItemESP"}, ModuleType.Visual);
        this.addValues(this.outlinedboundingBox);
	}
	
	@EventHandler
	public void onRender(EventRender3D event) {
		for (Object o : mc.theWorld.loadedEntityList) {
    		if (!(o instanceof EntityItem)) continue;
    		EntityItem item = (EntityItem)o;
 		   	double itemposX = item.posX;
 		   	double x = itemposX - Minecraft.getMinecraft().getRenderManager().renderPosX;
 		   itemposX = item.posY + 0.5D;
 		   	double y = itemposX - RenderManager.renderPosY;
 		   itemposX = item.posZ;
 		   	double z = itemposX - RenderManager.renderPosZ;
 		   	GL11.glEnable(3042);
 		   	GL11.glLineWidth(2.0F);
 		   	GL11.glColor4f(1, 1, 1, .75F);
 		   	GL11.glDisable(3553);
 		   	GL11.glDisable(2929);
 		   	GL11.glDepthMask(false);
            if(((Boolean) this.outlinedboundingBox.getValue()).booleanValue()) {
 	   			RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y-0.05, z - .2D, x + .2D, y - 0.45d, z + .2D));
 	   		}else {
 	   			GL11.glColor4f(1, 1, 1, 0.15f);
 	 	   		RenderUtil.drawBoundingBox(new AxisAlignedBB(x - .2D, y-0.05, z - .2D, x + .2D, y - 0.45d, z + .2D));
 	   		}
 	   		GL11.glEnable(3553);
 	   		GL11.glEnable(2929);
 	   		GL11.glDepthMask(true);
 	   		GL11.glDisable(3042);
    	}
	}
}
