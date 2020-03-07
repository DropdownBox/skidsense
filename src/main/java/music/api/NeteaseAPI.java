package music.api;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public enum NeteaseAPI {
	INSTANCE;

	private String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
	private String nonce = "0CoJUm6Qyw8W8jud";
	private String pubKey = "010001";
	private String ivpara = "0102030405060708";

	public String loginUrl = "https://music.163.com/weapi/login/cellphone";
	public String realUrl = "https://music.163.com/weapi/song/enhance/player/url";
	public String searchUrl = "https://music.163.com/weapi/search/get";
	public String playlistUrl = "https://music.163.com/weapi/user/playlist";
	public String recoomendUrl = "https://music.163.com/weapi/v2/discovery/recommend/songs";
	
	/**
	 * 搜索歌曲,以下是type变量的参数 -> 
	 * 1 - 单曲/10 - 专辑/100 - 歌手/1014 - 视频/1000 - 歌单/1006 - 歌词/1009 - 主播电台/1002 - 用户
	 * @param songName 关键词
	 * @param limit 搜索数量
	 * @param offset 分页偏移值
	 * @param type 类型
	 * @return Json 搜索结果
	 */
	public String searchSong(String songName, int limit, int offset, int type) throws Exception {
		return this.getData(searchUrl, this.getSearchJson(songName, limit, offset, type));
	}
	
	/**
	 * 获取每日推荐
	 * @param cookie 传入Cookie
	 * @return Json 结果
	 * @throws Exception
	 */
	public String getDailyRecommend(String cookie) throws Exception {
		return this.getDataWithCookie(this.recoomendUrl, this.getDailyRecoomendJson(true, 0, 30), cookie);
	}
	
	/**
	 * 获取指定用户的所有歌单
	 * @param cookie 传入Cookie
	 * @param uid 用户ID
	 * @param limit 数量
	 * @param offset 分页偏移值
	 * @return Json 此账号收藏的所有歌单
	 */
	public String getPlaylist(String cookie, String uid, int limit, int offset) throws Exception {
		return this.getDataWithCookie(this.playlistUrl, this.getPlayListJson(uid, limit, offset), cookie);
	}
	
	/**
	 * 使用手机号码登录到网易云音乐
	 * @param phoneNum 电话号码
	 * @param passwd 密码
	 * @return Json 登录结果
	 */
	public String loginWithPhoneNum(String phoneNum, String passwd) throws Exception {
		return this.getData(loginUrl, this.getLoginJson(phoneNum, passwd));
	}
	
	/**
	 * 获取歌曲真实地址
	 * @param kbps 码率
	 * @param id 歌曲ID
	 * @return Json 解析结果
	 */
	public String getRealUrl(String kbps, String id) throws Exception {
		String result = this.getData(this.realUrl, this.getRealUrlJson(id, kbps));
		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject) parser.parse(result);
		JsonArray arr = obj.get("data").getAsJsonArray();
		return arr.get(0).getAsJsonObject().get("url").getAsString();
	}
	
	public String getDailyRecoomendJson(boolean total, int offset, int limit) {
		JsonObject obj = new JsonObject();
		obj.addProperty("total", total);
		obj.addProperty("offset", offset);
		obj.addProperty("limit", limit);
		return obj.toString();
	}
	
	public String getPlayListJson(String userID, int limit, int offset) {
		JsonObject obj = new JsonObject();
		obj.addProperty("uid", userID);
		obj.addProperty("offset", offset);
		obj.addProperty("limit", limit);
		return obj.toString();
	}

	public String getSearchJson(String songName, int limit, int offset, int type) {
		JsonObject obj = new JsonObject();
		obj.addProperty("s", songName);
		obj.addProperty("limit", limit);
		obj.addProperty("offset", offset);
		obj.addProperty("type", type);
		return obj.toString();
	}

	public String getRealUrlJson(String id, String kbps) {
		JsonObject obj = new JsonObject();
		obj.addProperty("br", Integer.valueOf(kbps));
		obj.addProperty("ids", "[" + id + "]");
		return obj.toString();
	}

	public String getData(String url, String content) throws Exception {
		HashMap<String, Object> data = new HashMap<String, Object>();
		String secKey = this.createSecretKey(16);
		String params = aesEncrypt(aesEncrypt(content, nonce), secKey);
		String encSecKey = rsaEncrypt(secKey);
		return this.postParams(url, params, encSecKey);
	}
	
	public String getDataWithCookie(String url, String content, String cookie) throws Exception {
		HashMap<String, Object> data = new HashMap<String, Object>();
		String secKey = this.createSecretKey(16);
		String params = aesEncrypt(aesEncrypt(content, nonce), secKey);
		String encSecKey = rsaEncrypt(secKey);
		return this.postParamsWithCookie(url, params, encSecKey, cookie);
	}

	// 加密
	public String aesEncrypt(String content, String sKey) throws Exception {
		byte[] encryptedBytes;
		byte[] byteContent = content.getBytes("UTF-8");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKeySpec = new SecretKeySpec(sKey.getBytes(), "AES");
		IvParameterSpec iv = new IvParameterSpec(ivpara.getBytes());
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
		encryptedBytes = cipher.doFinal(byteContent);
		return new String(new Base64().encode(encryptedBytes), "UTF-8");
	}

	public String rsaEncrypt(String secKey) {
		secKey = new StringBuffer(secKey).reverse().toString();
		String secKeyHex = stringToHexString(secKey);
		BigInteger biText = new BigInteger(secKeyHex, 16);
		BigInteger biEx = new BigInteger(pubKey, 16);
		BigInteger biMod = new BigInteger(modulus, 16);
		BigInteger bigInteger = biText.modPow(biEx, biMod);
		return zfill(bigInteger.toString(16), 256);
	}

	public static String stringToHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	public static String zfill(String str, int size) {
		while (str.length() < size)
			str = "0" + str;
		return str;
	}

	public String createSecretKey(int length) {
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public String getLoginJson(String phoneNum, String passwd) {
		passwd = DigestUtils.md5Hex(passwd.getBytes());

		JsonObject json = new JsonObject();
		json.addProperty("phone", phoneNum);
		json.addProperty("password", passwd);
		json.addProperty("rememberLogin", true);

		return json.toString();
	}

	public String postParams(String url, String params, String encSecKey) {
		// 存储服务器返回的Cookie
		CookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

		String entityStr = null;

		CloseableHttpResponse response = null;

		try {
			HttpPost httpPost = new HttpPost(url);

			List<NameValuePair> list = new LinkedList<>();
			BasicNameValuePair param = new BasicNameValuePair("params", params);
			BasicNameValuePair secKey = new BasicNameValuePair("encSecKey", encSecKey);
			list.add(secKey);
			list.add(param);

			UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
			httpPost.setEntity(entityParam);
			httpPost.setConfig(RequestConfig.custom().setConnectTimeout(10000).build());

			// 浏览器表示
			httpPost.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0");
			httpPost.addHeader("Accept", "*/*");
			httpPost.setHeader("Accept-Encoding", "gzip");
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Host", "music.163.com");
			httpPost.addHeader("Referer", "https://music.163.com");
			httpPost.addHeader("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3");
			httpPost.addHeader("DNT", "1");
			httpPost.addHeader("Pragma", "no-cache");
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			entityStr = EntityUtils.toString(entity, "UTF-8");

			String MUSIC_U = "";
			String CSRF = "";
			String remember_me = "";

			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.size() > 0) {
				try {
					// Cookie处理
					for (Cookie c : cookies) {
						if (c.getName().contains("MUSIC_U")) {
							MUSIC_U = c.getValue();
						}

						if (c.getName().contains("__csrf")) {
							CSRF = c.getValue();
						}

						if (c.getName().contains("__remember_me")) {
							remember_me = c.getValue();
						}
					}

					JsonParser parser = new JsonParser();
					JsonObject array = (JsonObject) parser.parse(entityStr);
					StringBuilder sb = new StringBuilder();
					String ntes_nuid = createRandomKey(32);
					sb.append("_ntes_nuid=" + ntes_nuid + ";");
					sb.append("__remember_me=" + remember_me + ";");
					sb.append("MUSIC_U=" + MUSIC_U + ";");
					sb.append("__csrf=" + CSRF + ";");
					array.addProperty("cookie", sb.toString());
					entityStr = array.toString();
				} catch (Exception ex) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return entityStr;
	}

	public String postParamsWithCookie(String url, String params, String encSecKey, String cookie) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String entityStr = null;
		CloseableHttpResponse response = null;

		try {
			HttpPost httpPost = new HttpPost(url);

			List<NameValuePair> list = new LinkedList<>();
			BasicNameValuePair param = new BasicNameValuePair("params", params);
			BasicNameValuePair secKey = new BasicNameValuePair("encSecKey", encSecKey);
			list.add(secKey);
			list.add(param);

			UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
			httpPost.setEntity(entityParam);
			httpPost.setConfig(RequestConfig.custom().setConnectTimeout(10000).build());

			// 浏览器表示
			httpPost.addHeader("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0");
			httpPost.addHeader("Accept", "*/*");
			httpPost.setHeader("Accept-Encoding", "gzip");
			httpPost.addHeader("Cache-Control", "no-cache");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Host", "music.163.com");
			httpPost.addHeader("Referer", "https://music.163.com");
			httpPost.addHeader("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3");
			httpPost.addHeader("DNT", "1");
			httpPost.addHeader("Pragma", "no-cache");
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.addHeader("Cookie", cookie);

			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			entityStr = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entityStr;
	}

	public String createRandomKey(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

}
