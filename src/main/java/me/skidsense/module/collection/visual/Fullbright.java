package me.skidsense.module.collection.visual;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class Fullbright extends Module {
	public int alphag = 1;
	public Fullbright() {
		super("Full Bright", new String[]{"FullBright"}, ModuleType.Visual);
        this.addValues();
	}

	@Override
	public void onDisable() {
		mc.gameSettings.gammaSetting = (float) 1;
	}

	@EventHandler
	public void onUpdate(EventPreUpdate event) {
		if(alphag < 15 && alphag > 0) {
			alphag++;
		}
		mc.gameSettings.gammaSetting = alphag;
	}
}
