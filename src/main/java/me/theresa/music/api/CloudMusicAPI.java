package me.theresa.music.api;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import joptsimple.internal.Strings;
//import me.theresa.Client;
import me.theresa.music.MusicMgr;
import me.theresa.music.ui.TrackSlot;
import me.theresa.music.util.Lyric;
import me.theresa.music.util.SongList;
import me.theresa.music.util.Track;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public enum CloudMusicAPI {
	
	INSTANCE;
	
	//public File filePath = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + Client.CLIENT_NAME);
	
	/**
	 * 获取歌单信息
	 */
	public SongList getSongs(String id) {
		String picUrl = "";
		String name = "";
		ResourceLocation res = null;
		
		String jsonResult = request(id);
		JsonParser parser = new JsonParser();
		JsonObject phase1 = (JsonObject) parser.parse(jsonResult);
		
		if(!phase1.get("code").getAsString().contains("200")) {
			System.out.println("\u89e3\u6790\u65f6\u51fa\u73b0\u95ee\u9898\u002c\u8bf7\u68c0\u67e5\u6b4c\u5355\u0049\u0044\u662f\u5426\u6b63\u786e");
			return null;
		}
		
		JsonObject phase2 = phase1.get("result").getAsJsonObject();
		
		try {
			picUrl = phase2.get("coverImgUrl").getAsString();
			name = phase2.get("name").getAsString();
			res = this.getSongListImage(id, picUrl);
		} catch (Exception ex) {
			picUrl = "";
			name = "";
		}
		
		JsonArray phase3 = phase2.get("tracks").getAsJsonArray();
		
		return new SongList(res, name, id, picUrl, new ArrayList<TrackSlot>(), jsonResult);
	}
	
	/**
	 * 歌单内的歌曲获取
	 */
	public ArrayList<TrackSlot> getSongFromSongList(String s) {
		ArrayList<TrackSlot> tracks = new ArrayList<TrackSlot>();
		JsonParser parser = new JsonParser();
		
		JsonObject phase1 = (JsonObject) parser.parse(s);
		JsonObject phase2 = phase1.get("result").getAsJsonObject();
		JsonArray phase3 = phase2.get("tracks").getAsJsonArray();
		
		for(int i = 0; i < phase3.size(); ++i) {
			JsonObject info = phase3.get(i).getAsJsonObject();
			String songName = info.get("name").getAsString();
			String songID = info.get("id").getAsString();
			String imageUrl;
			
			try {
				imageUrl = info.get("album").getAsJsonObject().get("blurPicUrl").getAsString();
			} catch (Exception ex) {
				imageUrl = "";
			}
			
			JsonArray artists = info.get("artists").getAsJsonArray();
			ArrayList<String> artistsName = new ArrayList<String>();
			
			for(int a = 0; a < artists.size(); ++a) {
				String artist = artists.get(a).getAsJsonObject().get("name").getAsString();
				artistsName.add(artist);
			}
			
			tracks.add(new TrackSlot(new Track(songName, Strings.join(artistsName.toArray(new String[] {}), "/"), Long.valueOf(songID), imageUrl, ""), 0));
		}
		return tracks;
	}
	
	/**
	 * 获取搜索列表的歌曲
	 */
	public ArrayList<TrackSlot> getSong(String searchResultJson) {
		ArrayList<TrackSlot> b = new ArrayList<TrackSlot>();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(searchResultJson);
		try {
			
			if(!json.get("code").getAsString().contains("200")) {
				System.out.println("\u89e3\u6790\u65f6\u51fa\u73b0\u95ee\u9898\u002c\u8bf7\u68c0\u67e5\u6b4c\u5355\u0049\u0044\u662f\u5426\u6b63\u786e");
				return null;
			}
			
			//获取Result
			JsonObject result = json.getAsJsonObject("result");
			
			//获取歌单
			JsonArray songs = result.getAsJsonArray("songs");
			
			for(int i = 0; i < songs.size(); ++i) {
				JsonObject obj = songs.get(i).getAsJsonObject();
				//存储歌曲ID
				long songID = obj.get("id").getAsInt();
				//存储歌曲名称
				String songName = String.valueOf(obj.get("name").getAsString());
				
				//存储歌手信息
				JsonArray artists = obj.get("artists").getAsJsonArray();
				ArrayList<String> artistsName = new ArrayList<String>();
				for(int a = 0; a < artists.size(); ++a) {
					String artist = artists.get(a).getAsJsonObject().get("name").getAsString();
					artistsName.add(artist);
				}
				
				//存储歌曲图片 (不存在的)
				String imageUrl = "";
				
				b.add(new TrackSlot(new Track(songName, Strings.join(artistsName.toArray(new String[] {}), "/"), Long.valueOf(songID), imageUrl, ""), 1));
			}
		} catch (Exception ex) {
			
		}
		return b;
	}
	
	/**
	 * 获取每日推荐的歌曲
	 */
	public ArrayList<TrackSlot> getDailySong(String searchResultJson) {
		ArrayList<TrackSlot> b = new ArrayList<TrackSlot>();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(searchResultJson);
		try {
			
			if(!json.get("code").getAsString().contains("200")) {
				System.out.println("发生错误 ");
				System.out.println("\u89e3\u6790\u65f6\u51fa\u73b0\u95ee\u9898\u002c\u8bf7\u68c0\u67e5\u6b4c\u5355\u0049\u0044\u662f\u5426\u6b63\u786e");
				return b;
			}
			
			//获取Result
			JsonArray songs = json.getAsJsonArray("recommend");
			
			for(int i = 0; i < songs.size(); ++i) {
				JsonObject obj = songs.get(i).getAsJsonObject();
				//存储歌曲ID
				long songID = obj.get("id").getAsInt();
				//存储歌曲名称
				String songName = String.valueOf(obj.get("name").getAsString());
				
				//存储歌手信息
				JsonArray artists = obj.get("artists").getAsJsonArray();
				ArrayList<String> artistsName = new ArrayList<String>();
				for(int a = 0; a < artists.size(); ++a) {
					String artist = artists.get(a).getAsJsonObject().get("name").getAsString();
					artistsName.add(artist);
				}
				
				//存储歌曲图片
				JsonObject obj2 = obj.get("album").getAsJsonObject();
				String imageUrl = obj2.get("picUrl").getAsString();
				
				//存储推荐原因
				String reason = obj.get("reason").getAsString();
				b.add(new TrackSlot(new Track(songName, Strings.join(artistsName.toArray(new String[] {}), "/"), Long.valueOf(songID), imageUrl, reason), 2));
			}
		} catch (Exception ex) {
			return b;
		}
		return b;
	}
	
	public ResourceLocation getSongListImage(String id, String url) {
		try {
			ResourceLocation rl2 = new ResourceLocation("songListImage/" + id);
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
	
	public String[] requestLyric(String songID) {
		String requestResult = "";
		String requestUrl = "http://music.163.com/api/song/lyric";
		String requestParam = "os=pc&id=" + songID + "&lv=-1&kv=-1&tv=-1";
		
		String lyric = "";
		String transLyric = "";
		
		try {
			requestResult = sendGet(requestUrl, requestParam);
			JsonParser parser = new JsonParser();
			JsonObject phase1 = (JsonObject) parser.parse(requestResult);
			
			if(!phase1.get("code").getAsString().contains("200")) {
				System.out.println("\u89e3\u6790\u65f6\u51fa\u73b0\u95ee\u9898\u002c\u8bf7\u68c0\u67e5\u6b4c\u66f2\u0049\u0044\u662f\u5426\u6b63\u786e");
				return new String[] {"", ""};
			}
			
			if(phase1.get("nolyric") != null) {
				if(phase1.get("nolyric").getAsBoolean()) {
					return new String[] {"NO LYRIC", "NO LYRIC"};
				}
			}

			if(!phase1.get("lrc").getAsJsonObject().get("lyric").isJsonNull()) {
				lyric = phase1.get("lrc").getAsJsonObject().get("lyric").getAsString();
			} else {
				lyric = "";
			}
			
			try {
				if(!phase1.get("tlyric").getAsJsonObject().get("lyric").isJsonNull()) {
					transLyric = phase1.get("tlyric").getAsJsonObject().get("lyric").getAsString();
				} else {
					transLyric = "";
				}
			} catch (Exception ex) {
				transLyric = "";
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return new String[] {lyric, transLyric};
	}
	
	public ArrayList<Lyric> analyzeLyric(String lyric) {
		ArrayList<Lyric> list = new ArrayList<Lyric>();
		try {
			ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(lyric.getBytes());
			InputStreamReader read = new InputStreamReader(tInputStringStream, MusicMgr.instance.isUtf ? "UTF-8" : "GBK");
			BufferedReader bufferedReader = new BufferedReader(read);
			
			String regex = "\\[([0-9]{2}):([0-9]{2}).([0-9]{1,3})\\]";
			Pattern pattern = Pattern.compile(regex);
			
			String regex2 = "\\[([0-9]{2}):([0-9]{2})\\]";
			Pattern pattern2 = Pattern.compile(regex2);
			
			String lineStr = null;
			while ((lineStr = bufferedReader.readLine()) != null) {
								
				Matcher matcher = pattern.matcher(lineStr);
				
				Matcher matcher2 = pattern2.matcher(lineStr);
				
				if(matcher.find()) {
					String min = matcher.group(1);
					String sec = matcher.group(2);
					String mills = matcher.group(3);
					String text = lineStr.substring(matcher.end());

					list.add(new Lyric(text, strToLong(min, sec, mills)));
					
				} else if(matcher2.find()) {
					String min = matcher2.group(1);
					String sec = matcher2.group(2);
					String text = lineStr.substring(matcher2.end());

					list.add(new Lyric(text, strToLong(min, sec, "000")));
				}
			}
			read.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\u89e3\u6790\u6b4c\u8bcd\u65f6\u53d1\u751f\u9519\u8bef");
		}
		return null;
	}
	
	public long strToLong(String min, String sec, String mill) {
		int minInt = Integer.parseInt(min);
		int secInt = Integer.parseInt(sec);
		int millsInt = Integer.parseInt(mill);
		long times = (minInt * 60 * 1000) + (secInt * 1000) + (millsInt * (mill.length() == 2 ? 10 : 1));
		return times;
	}
	
	public String getDownloadUrl(String id) {
		try {
			return NeteaseAPI.INSTANCE.getRealUrl("120000", id);
		} catch (Exception ex) {
			return "";
		}
	}
	
	public String request(String id) {
		try {
			return sendGet("http://music.163.com/api/playlist/detail", "id=" + id);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public boolean isFileExist(String path, String id) {
		return new File(path + "\\" + id + ".mp3").exists();
	}
	
	public void downLoadSong(String savePath, String id, String realUrl) {
		try {
			this.downLoadFromUrl(realUrl, id + ".mp3", savePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downLoadFromUrl(String urlStr, String fileName, String savePath) {
		try {
			URL url = new URL(urlStr);
						
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3 * 1000);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
			
			if(!MusicMgr.instance.cookies.isEmpty() && MusicMgr.instance.isLoggined) {
				conn.setRequestProperty("Cookie", MusicMgr.instance.cookies);
	        }
			
			InputStream inputStream = conn.getInputStream();
			
			int contentLength = conn.getContentLength();
			
			byte[] getData = readInputStream(inputStream, contentLength);
			File saveDir = new File(savePath);
			
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			
			File file = new File(saveDir + File.separator + fileName);
			FileOutputStream fos = new FileOutputStream(file);
			
			fos.write(getData);
			
			if (fos != null) {
				fos.close();
			}
			
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
			MusicMgr.instance.failedTime += 1;
		}
	}

	public byte[] readInputStream(InputStream inputStream, int contentLength) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		long totalReaded = 0;
		
		while ((len = inputStream.read(buffer)) != -1) {
			
			totalReaded += len;
			long progress = totalReaded * 100 / contentLength;
			MusicMgr.instance.downloadProgress = String.valueOf(progress);
			bos.write(buffer, 0, len);
			
		}
		bos.close();
		return bos.toByteArray();
	}
	
	public String sendGet(String url, String param) throws IOException {
        HttpGet request = new HttpGet(url + "?" + param);
        return send(request);
    }
	
	private String send(HttpRequestBase request) throws IOException {
        String message = "";
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) ...");
        request.setHeader("accept", "*/*");
        request.setHeader("connection", "Keep-Alive");
        
        if(!MusicMgr.instance.cookies.isEmpty()) {
            request.setHeader("Cookie", MusicMgr.instance.cookies);
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(request);
        HttpEntity entity = response.getEntity();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        if (entity != null) {
            long length = entity.getContentLength();
            if (length != -1 && length < 2048) {
                message = EntityUtils.toString(entity);
            } else {
                InputStream in = entity.getContent();
                byte[] data = new byte[4096];
                int count;
                while ((count = in.read(data, 0, 4096)) != -1) {
                    outStream.write(data, 0, count);
                }
                message = new String(outStream.toByteArray(), "UTF-8");
            }
        }
        return message;
    }
}
