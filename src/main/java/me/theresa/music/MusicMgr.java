package me.theresa.music;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.management.notifications.Notifications;

import me.theresa.music.api.CloudMusicAPI;
import me.theresa.music.api.NeteaseAPI;
import me.theresa.music.ui.TrackSlot;
import me.theresa.music.util.Lyric;
import me.theresa.music.util.SongList;
import me.theresa.music.util.Track;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class MusicMgr {

	public static MusicMgr instance;
	public MediaPlayer mediaPlayer = null;

	public Track currentTrack = null;
	public Thread loadingThread = null;

	public SongList currentSongList = null;
	public ArrayList<SongList> allSongLists = new ArrayList<SongList>();

	public HashMap<Long, ResourceLocation> songListImage = new HashMap<>();
	public HashMap<Long, ResourceLocation> trackImage = new HashMap<>();

	//private String fileDir;

	public ArrayList<TrackSlot> lastPlayingTrack = null;

	public boolean showLyric = false;
	public boolean showTranslateLyric = false;

	public File musicFolder;
	public File imageFolder;

	public int failedTime = 0;

	public String downloadProgress = "0";
	
	//搜索结果的存储
	public ArrayList<TrackSlot> searchResult = new ArrayList<TrackSlot>();

	// TODO 歌词存储部分
	public int index = 1;
	public int index2 = 1;
	public String currentLyric = "";
	public String currentTranslateLyric = "";
	public ArrayList<Lyric> lyric = new ArrayList<Lyric>();
	public ArrayList<Lyric> translateLyric = new ArrayList<Lyric>();

	// TODO 用户存储部分
	public boolean isLoggined = false;
	
	//用户名
	public String nickname = "";
	
	//用户ID
	public String uid = "";
	
	//用户头像和背景
	public ResourceLocation user_avatar = null;
	public ResourceLocation user_background = null;
	//用户所在城市代码
	public String location_code = "";
	//Cookies (饼干 ^_^)
	public String cookies = "";
	
	//关注数
	public String follows = "";
	//粉丝数
	public String followeds = "";
	
	
	//TODO 专辑图片获取线程
	public Thread imageThread;
	
	//日推
	public ArrayList<TrackSlot> dailyList = new ArrayList<TrackSlot>();
	
	//循环播放
	public boolean loop = false;
	
	//歌词解码
	public boolean isUtf = false;
	
	//歌词显示
	public boolean displayLyric = true;
	
	public MusicMgr() {
		instance = this;

		musicFolder = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.toString()) + File.separator
				+ "skidsense" + File.separator + "me/theresa/music");
		if (!musicFolder.exists()) {
			musicFolder.mkdirs();
		}
		
		imageFolder = new File(String.valueOf(Minecraft.getMinecraft().mcDataDir.toString()) + File.separator
				+ "skidsense" + File.separator + "image");
		if (!imageFolder.exists()) {
			imageFolder.mkdirs();
		}

		new JFXPanel();

	}

	public void loadSomeShit() {
		if(this.isLoggined) {
			try {
				String result = NeteaseAPI.INSTANCE.getPlaylist(this.cookies, this.uid, 30, 0);
				//解析Json
				JsonParser parser = new JsonParser();
				JsonObject obj = (JsonObject) parser.parse(result);
				//解析到playlist分类
				JsonArray one = obj.get("playlist").getAsJsonArray();
				
				//解析歌单
				for(int i = 0; i < one.size(); ++i) {
					JsonObject two = one.get(i).getAsJsonObject();
					this.allSongLists.add(CloudMusicAPI.INSTANCE.getSongs(two.get("id").getAsString()));
				}

			} catch (Exception e) {}
		} else {
			Notifications.getManager().post("MusicPlayer "+"请先登录账号");
		}
	}

	public void next() {
		if (currentSongList != null) {
			// TODO Play
			if (this.currentTrack == null) {
				currentSongList.getSongs().get(0);
			} else {
				if(this.lastPlayingTrack == null) {
					this.play(this.currentTrack);
				} else {
					shit();
				}
			}
		} else {
			if(this.lastPlayingTrack != null) {
				shit();
			}
		}
	}

	public void shit() {
		boolean playNext = false;
		for (TrackSlot a : lastPlayingTrack) {
			if (playNext) {
				play(a.track);
				return;
			} else if (a.track.getId() == currentTrack.getId()) {
				playNext = true;
			}
		}
	}

	public ResourceLocation getImage(String name, String url) {
		try {
			ResourceLocation rl2 = new ResourceLocation(name);
			IImageBuffer iib2 = new IImageBuffer() {

				public BufferedImage parseUserSkin(BufferedImage a) {
					return a;
				}

				@Override
				public void skinAvailable() {
				}
			};
			ThreadDownloadImageData textureArt2 = new ThreadDownloadImageData(null, url, (ResourceLocation) null, iib2);
			Minecraft.getMinecraft().getTextureManager().loadTexture(rl2, textureArt2);
			return rl2;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void getTrackImage(Track track) {
		try {	
			
			if(this.trackImage.containsKey(track.getId())) {
	    		return;
	    	}
			
			ResourceLocation rl2 = new ResourceLocation("trackImage/" + track.getId());
			
			if(!new File(this.imageFolder.getAbsolutePath() + File.separator + track.getId()).exists()) {
				CloudMusicAPI.INSTANCE.downLoadFromUrl(track.getPicUrl(), String.valueOf(track.getId()), this.imageFolder.getAbsolutePath());
			}
			
			IImageBuffer iib2 = new IImageBuffer() {

				public BufferedImage parseUserSkin(BufferedImage a) {
					return a;
				}

				@Override
				public void skinAvailable() {
					MusicMgr.instance.trackImage.put(track.getId(), rl2);
				}
			};
			ThreadDownloadImageData textureArt2 = new ThreadDownloadImageData(new File(this.imageFolder.getAbsolutePath() + File.separator + track.getId()), null, (ResourceLocation) null, iib2);
			Minecraft.getMinecraft().getTextureManager().loadTexture(rl2, textureArt2);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void play(Track track) {
		showLyric = false;
		showTranslateLyric = false;
		index = 1;
		index2 = 1;

		if (loadingThread != null) {
			loadingThread.interrupt();
		}
		
		this.currentTrack = track;
		
		if (failedTime > 10) {
			failedTime = 0;
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[ERROR] 无法获取缓存地址,请检查网络是否有问题或此歌曲是否为VIP歌曲."));
			return;
		}

		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
			} catch (Exception ex) {}
		}
		
		this.currentLyric = "";
		this.currentTranslateLyric = "";

		File file = new File(musicFolder, track.getId() + ".mp3");

		if (!CloudMusicAPI.INSTANCE.isFileExist(musicFolder.getAbsolutePath(), String.valueOf(track.getId()))) {
			loadingThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						CloudMusicAPI.INSTANCE.downLoadSong(musicFolder.getAbsolutePath(),
								String.valueOf(track.getId()),
								CloudMusicAPI.INSTANCE.getDownloadUrl(String.valueOf(track.getId())));
						File tempFile = new File(musicFolder, track.getId() + ".mp3");
						play(track);
						loadingThread = null;
					} catch (Exception ex) {

					}
				}
			});

			loadingThread.start();
		} else {

			try {
				String[] lyrics = CloudMusicAPI.INSTANCE.requestLyric(String.valueOf(track.getId()));

				if (!lyric.isEmpty()) {
					lyric.clear();
				}

				if (!translateLyric.isEmpty()) {
					translateLyric.clear();
				}

				if (lyrics[0] != "") {
					if (lyrics[0].equalsIgnoreCase("NO LYRIC")) {
						currentLyric = currentTrack.getName() + " - " + currentTrack.getArtists();
					} else {
						MusicMgr.instance.lyric = CloudMusicAPI.INSTANCE.analyzeLyric(lyrics[0]);
					}
				} else {
					currentLyric = "(\u53d1\u751f\u9519\u8bef\u6216\u6b4c\u66f2\u4e0d\u5305\u542b\u6b4c\u8bcd)";
					MusicMgr.instance.lyric.clear();
				}

				if (lyrics[1] != "") {

					if (lyrics[1].equalsIgnoreCase("NO LYRIC")) {
						currentTranslateLyric = "(\u7eaf\u97f3\u4e50\u002c\u8bf7\u6b23\u8d4f)";
					} else {
						MusicMgr.instance.translateLyric = CloudMusicAPI.INSTANCE.analyzeLyric(lyrics[1]);
					}

				} else {
					currentTranslateLyric = "(\u83b7\u53d6\u65f6\u51fa\u73b0\u9519\u8bef\u6216\u8bd1\u6587\u4e0d\u5b58\u5728)";
					MusicMgr.instance.translateLyric.clear();
				}

			} catch (Exception ex) {
				lyric.clear();
				translateLyric.clear();

				currentLyric = currentTrack.getName() + " - " + currentTrack.getArtists();
				currentTranslateLyric = "(获取歌词时出现错误)";
			}	

			Media hit = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.setVolume(1.0);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                	if(MusicMgr.instance.loop) {
                		MusicMgr.instance.play(MusicMgr.instance.currentTrack);
                	} else {
                		MusicMgr.instance.next();
                	}
                }
            });
			failedTime = 0;
			
			// Make Lyric Thread ReLaunch
			showLyric = true;
			showTranslateLyric = true;
			
			// TODO 歌词
		}
	}

	boolean mark = false;
	boolean mark2 = false;

	@EventHandler
	public void updateLyric(EventPreUpdate event) {
		if (mediaPlayer != null) {
			// TODO 翻译显示部分
			long time = (long) (mediaPlayer.getCurrentTime().toMillis());
			if (showTranslateLyric && !this.translateLyric.isEmpty()) {

				if ((index - 1) > translateLyric.size()) {
					return;
				}

				try {
					if (time <= translateLyric.get(index).getTime()) {
						if (!mark2) {
							this.currentTranslateLyric = translateLyric.get(index - 1).getText();
						}
						mark2 = true;
					} else {
						index++;
						mark2 = false;
					}
				} catch (Exception ex) {
					this.currentTranslateLyric = "";
				}
			}

			// TODO 原文显示部分
			if (showLyric && !this.lyric.isEmpty()) {

				if ((index2 - 1) > lyric.size()) {
					return;
				}

				try {
					if (time <= lyric.get(index2).getTime()) {
						if (!mark) {
							this.currentLyric = lyric.get(index2 - 1).getText();
							if (translateLyric.isEmpty()) {
								try {
									this.currentTranslateLyric = lyric.get(index2).getText();
								} catch (Exception ex) {
								}
							}
						}
						mark = true;
					} else {
						index2++;
						mark = false;
					}
				} catch (Exception ex) {

				}
			}
		}

	}

	public String formatSeconds(int seconds) {
		String rstl = "";
		int mins = seconds / 60;
		if (mins < 10) {
			rstl += "0";
		}
		rstl += mins + ":";
		seconds %= 60;
		if (seconds < 10) {
			rstl += "0";
		}
		rstl += seconds;
		return rstl;
	}
}
