package me.theresa.music.util;

public class Lyric {
	public String text;
	public long time;
	
	public Lyric(String text, long time) {
		this.text = text;
		this.time = time;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
