package me.theresa.music.util;

public class Track {
	private long id;
	private String name;
	private String artists;
	private String picUrl;
	public String b;

	/**
	 *
	 * @param b 用于日推显示来源
	 */
	public Track(String name, String artists, long id, String picUrl, String b) {
		this.id = id;
		this.name = name;
		this.artists = artists;
		this.picUrl = picUrl;
		this.b = b;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getArtists() {
		return artists;
	}

	public String getPicUrl() {
		return picUrl;
	}
}
