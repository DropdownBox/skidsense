package music.util;

import java.util.ArrayList;

import music.ui.TrackSlot;
import net.minecraft.util.ResourceLocation;

public class SongList {

	public String name;
	public String url;
	public String songListID;
	public ArrayList<TrackSlot> songs;
	public ResourceLocation res;
	public String jsonStorage;

	public SongList(ResourceLocation a, String b, String c, String d, ArrayList<TrackSlot> e, String f) {
		this.res = a;
		this.name = b;
		this.songListID = c;
		this.url = d;
		this.songs = e;
		this.jsonStorage = f;
	}
	
	public String getName() {
		return name;
	}

	public ArrayList<TrackSlot> getSongs() {
		return songs;
	}

}
