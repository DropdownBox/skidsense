package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.util.ArrayList;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.management.fontRenderer.TTFFontRenderer;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public class StaffList extends Mod {
	
	private int lastAdmins;
    private final ArrayList<String> admins = new ArrayList<String>();
    
	public StaffList() {
		super("Staff List", new String[] {"StaffList"}, ModuleType.Visual);
	}

	@Override
	public void onEnable() {
		admins.add("Name1");
		admins.add("Name2");
		admins.add("Name3");
		admins.add("Name4");
		super.onEnable();
	}
	
	@Sub
	public void on2DEvent(EventRenderGui eventRenderGui) {
		ScaledResolution sr = new ScaledResolution(mc);
        TTFFontRenderer font = Client.instance.fontManager.tahomabold11;
        int size = 80;
        float xOffset = (sr.getScaledWidth() / 2F) - (size / 2F);
        float yOffset = 25;
        float Y = 0;

        if (!(mc.gameSettings.keyBindPlayerList.isKeyDown())) {
        	String adminString = "";
            RenderUtil.rectangleBordered(xOffset + 2.0f, yOffset + 2.0f, xOffset + size - 2.0f, yOffset + (size / 6F) + 3.0f + ((font.getHeight(adminString) +7f) * this.admins.size()), 0.5, Colors.getColor(90), Colors.getColor(0));
            RenderUtil.rectangleBordered(xOffset + 3.0f, yOffset + 3.0f, xOffset + size - 3.0f, yOffset + (size / 6F) + 2.0f + ((font.getHeight(adminString) + 7f) * this.admins.size()), 0.5, Colors.getColor(27), Colors.getColor(61));
            RenderUtil.drawRect(xOffset + 4.0f, yOffset + font.getHeight(adminString) + 5.5f, xOffset + size - 4.0f, yOffset + font.getHeight(adminString) + 37f, Colors.getColor(0));
            font.drawStringWithShadow("TargetList", xOffset + 6.75f, yOffset + font.getHeight(adminString) + 2.8f, -1);
            for (final String admin2 : this.admins) {
            	adminString = admin2;
                font.drawStringWithShadow(admin2, xOffset + 7.0f, yOffset + font.getHeight(admin2) + Y + 3, new Color(255, 60, 60, 255).getRGB());
                Y += font.getHeight(admin2) + 2;
            }
        }
	}
}
