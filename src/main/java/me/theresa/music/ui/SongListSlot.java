package me.theresa.music.ui;

import me.skidsense.color.Colors;
import me.skidsense.util.ClientUtil;
import me.skidsense.util.RenderUtil;
import me.theresa.music.util.SongList;
import me.theresa.music.MusicMgr;
import net.minecraft.client.Minecraft;

public class SongListSlot {
	public SongList list;
	
	public SongListSlot(SongList a) {
			this.list = a;
	}
	
	public void draw(int mouseX, int mouseY, float x, float y) {
		
		int col = MusicMgr.instance.currentSongList == list ? Colors.BLACK.c : Colors.GREY.c;
		
		if(MusicMgr.instance.currentSongList == list) {
			RenderUtil.drawRect(x - 4, y - 6, x + 2, y + 16, Colors.AQUA.c);
			RenderUtil.drawRect(x + 2, y - 6, x + 120, y + 16, ClientUtil.reAlpha(Colors.GREY.c, 0.4f));
		}

		Minecraft.getMinecraft().fontRendererObj.drawString(list.name, x + 24f, y, col);
		//RenderUtil.drawImage(list.res, (int) x + 4, (int) y - 3, 16, 16, 1.0f);
	}
	
	public void onCrink() {
		if(MusicMgr.instance.currentSongList != list) {
			MusicMgr.instance.currentSongList = list;
		}
	}
}
