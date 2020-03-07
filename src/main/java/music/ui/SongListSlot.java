package music.ui;

import me.theresa.Client;
import music.MusicMgr;
import music.util.SongList;
import me.theresa.utils.ClientUtil;
import me.theresa.utils.Colors;
import me.theresa.utils.RenderUtil;

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
		
		Client.INSTANCE.fontMgr.wqy16.drawString(list.name, x + 24f, y, col);
		RenderUtil.drawImage(list.res, (int) x + 4, (int) y - 3, 16, 16, 1.0f);
	}
	
	public void onCrink() {
		if(MusicMgr.instance.currentSongList != list) {
			MusicMgr.instance.currentSongList = list;
		}
	}
}
