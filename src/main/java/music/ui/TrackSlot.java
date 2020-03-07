package music.ui;

import java.awt.Color;

import me.skidsense.color.Colors;
import me.skidsense.util.RenderUtil;
import music.MusicMgr;
import music.util.Track;
import me.skidsense.util.ClientUtil;
import net.minecraft.client.Minecraft;

public class TrackSlot {
	
	public Track track;
		
	public int type;
	
	public TrackSlot(Track a, int type) {
		track = a;
		this.type = type;
	}
	
	public void draw(int mouseX, int mouseY, float x, float y) {
		switch(type) {
		case 0:
			RenderUtil.drawRoundedRect(x - 4, y, x + 382, y + 24, ClientUtil.reAlpha(Colors.GREY.c, 0.3f), ClientUtil.reAlpha(Colors.GREY.c, 0.3f));
			Minecraft.getMinecraft().fontRendererObj.drawString(track.getName(), (float) x + 2, y + 2, Colors.BLACK.c);
			Minecraft.getMinecraft().fontRendererObj.drawString(track.getArtists(), (float) x + 2, y + 12, Colors.GREY.c);

			RenderUtil.drawRoundedRect(x + 350, y, x + 382, y + 24, new Color(Colors.GREY.c).brighter().getRGB(), new Color(Colors.GREY.c).brighter().getRGB());
			Minecraft.getMinecraft().fontRendererObj.drawString("播放", x + 358, y + 6, Colors.BLACK.c);
			break;
		case 1:
			RenderUtil.drawRoundedRect(x - 4, y, x + 210, y + 24, ClientUtil.reAlpha(Colors.GREY.c, 0.3f), ClientUtil.reAlpha(Colors.GREY.c, 0.3f));
			Minecraft.getMinecraft().fontRendererObj.drawString(track.getName(), (float) x + 2, y + 2, Colors.BLACK.c);
			Minecraft.getMinecraft().fontRendererObj.drawString(track.getArtists(), (float) x + 2, y + 12, Colors.GREY.c);

			RenderUtil.drawRoundedRect(x + 175, y, x + 210, y + 24, new Color(Colors.GREY.c).brighter().getRGB(), new Color(Colors.GREY.c).brighter().getRGB());
			Minecraft.getMinecraft().fontRendererObj.drawString("播放", x + 184, y + 6, Colors.BLACK.c);
			break;
		case 2:
			RenderUtil.drawRoundedRect(x - 4, y, x + 226, y + 24, ClientUtil.reAlpha(Colors.GREY.c, 0.3f), ClientUtil.reAlpha(Colors.GREY.c, 0.3f));
			Minecraft.getMinecraft().fontRendererObj.drawString(track.getName(), (float) x + 2, y + 2, Colors.BLACK.c);
			Minecraft.getMinecraft().fontRendererObj.drawString(track.b, (float) x + 2, y + 12, Colors.GREY.c);

			RenderUtil.drawRoundedRect(x + 186, y, x + 226, y + 24, new Color(Colors.GREY.c).brighter().getRGB(), new Color(Colors.GREY.c).brighter().getRGB());
			Minecraft.getMinecraft().fontRendererObj.drawString("播放", x + 198, y + 6, Colors.BLACK.c);
			break;
		}
		
	}
	
	public void onCrink() {
		MusicMgr.instance.play(track);
	}
}
