package music.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import me.skidsense.color.Colors;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.util.ClientUtil;
import me.skidsense.util.MouseInputHandler;
import me.skidsense.util.RenderUtil;
import music.ui.CustomTextF;
import music.ui.SongListSlot;
import music.ui.TrackSlot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.media.MediaPlayer;

import music.MusicMgr;
import music.api.CloudMusicAPI;
import music.api.NeteaseAPI;
import music.util.SongList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class MusicWindow extends GuiScreen {

	public int x;
	public int y;

	public int x2;
	public int y2;

	public boolean drag = false;

	private MouseInputHandler handler = new MouseInputHandler(0);
	private MouseInputHandler handler2 = new MouseInputHandler(0);

	public ArrayList<SongListSlot> slots = new ArrayList<>();

	public MusicMgr mgr = MusicMgr.instance;

	public int wheelStateTrack;
	public float wheelSmoothTrack;

	public int wheelSearchStateTrack;
	public float wheelSearchSmoothTrack;
	
	public int wheelDailyStateTrack;
	public float wheelDailySmoothTrack;
	
	public int wheelSongListStateTrack;
	public float wheelSongListSmoothTrack;

	public WindowCategory currentWindow = WindowCategory.MUSIC;

	public CustomTextF phoneNum;
	public CustomTextF passwd;

	public CustomTextF searchField;

	public enum WindowCategory {
		MUSIC, USER;
	}

	@Override
	public void initGui() {

		if (!this.slots.isEmpty()) {
			slots.clear();
		}

		for (SongList s : MusicMgr.instance.allSongLists) {
			slots.add(new SongListSlot(s));
		}

		this.phoneNum = new CustomTextF(0, this.fontRendererObj, 0, 0, 100, 20, false);
		this.passwd = new CustomTextF(0, this.fontRendererObj, 0, 0, 100, 20, true);
		this.searchField = new CustomTextF(0, this.fontRendererObj, 0, 0, 170, 20, false);

		super.initGui();
	}

	int mouseWheel = 0;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// 整个窗口
		RenderUtil.drawRoundedRect(x, y + 20, x + 510, y + 335, ClientUtil.reAlpha(Colors.WHITE.c, 0.85f), ClientUtil.reAlpha(Colors.WHITE.c, 0.85f));
		RenderUtil.drawRoundedRect(x, y, x + 510, y + 20, ClientUtil.reAlpha(Colors.AQUA.c, 0.85f), ClientUtil.reAlpha(Colors.AQUA.c, 0.85f));
		RenderUtil.drawRect(x, y + 19, x + 1, y + 20, ClientUtil.reAlpha(Colors.AQUA.c, 0.85f));
		RenderUtil.drawRect(x + 509, y + 19, x + 510, y + 20, ClientUtil.reAlpha(Colors.AQUA.c, 0.85f));
		
		RenderUtil.drawRect(x, y + 20, x + 1, y + 21, ClientUtil.reAlpha(Colors.WHITE.c, 0.85f));
		RenderUtil.drawRect(x + 509, y + 20, x + 510, y + 21, ClientUtil.reAlpha(Colors.WHITE.c, 0.85f));

		if(this.currentWindow == WindowCategory.MUSIC) {
			RenderUtil.drawRect(x + 8, y, x + 44, y + 20, new Color(Colors.AQUA.c).darker().getRGB());
		} else {
			RenderUtil.drawRect(x + 47, y, x + 46 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(mgr.nickname) + 4, y + 20, new Color(Colors.AQUA.c).darker().getRGB());
		}
		
		Minecraft.getMinecraft().fontRendererObj.drawString("我的歌单", (float) x + 10, y + 5, Colors.WHITE.c);

		if (mgr.isLoggined && mgr.user_avatar != null) {
			Minecraft.getMinecraft().fontRendererObj.drawString(mgr.nickname, (float) x + 48, y + 5, Colors.WHITE.c);
		} else {
			mgr.nickname = "登录";
			Minecraft.getMinecraft().fontRendererObj.drawString(mgr.nickname, (float) x + 48, y + 5, Colors.WHITE.c);
		}

		if (RenderUtil.isHovering(mouseX, mouseY, x + 48, y, x + 48 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(mgr.nickname) + 4, y + 20)) {
			if (this.handler.canExcecute()) {
				if (this.currentWindow != WindowCategory.USER) {
					this.currentWindow = WindowCategory.USER;
				}
			}
		}

		if (RenderUtil.isHovering(mouseX, mouseY, x + 8, y, x + 44, y + 20)) {
			if (this.handler.canExcecute()) {
				if (this.currentWindow != WindowCategory.MUSIC) {
					this.currentWindow = WindowCategory.MUSIC;
				}
			}
		}

		if (this.currentWindow == WindowCategory.USER) {
			if (mgr.isLoggined) {
				// 已登录
				//RenderUtil.drawImage(mgr.user_avatar, x + 20, y + 25, 64, 64, 1);

				Minecraft.getMinecraft().fontRendererObj.drawString("欢迎, " + mgr.nickname, x + 86f, y + 25, Colors.BLACK.c);
				Minecraft.getMinecraft().fontRendererObj.drawString("所在城市区域代码: " + mgr.location_code, x + 86f, y + 35f,
						Colors.BLACK.c);
				Minecraft.getMinecraft().fontRendererObj.drawString("关注: " + mgr.follows + " 粉丝: " + mgr.followeds, x + 86f,
						y + 45, Colors.BLACK.c);

				// 歌曲搜索部分
				Minecraft.getMinecraft().fontRendererObj.drawString("Search", x + 20f, y + 92, Colors.BLACK.c);
				
				this.searchField.xPosition = x + 63;
				this.searchField.yPosition = y + 86;
				this.searchField.drawTextBox();
				
				RenderUtil.drawRect(x + 20, y + 90 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, x + 234,
						y + 91 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, Colors.BLACK.c);
				
				if (MusicMgr.instance.searchResult.isEmpty()) {
					Minecraft.getMinecraft().fontRendererObj.drawString("你啥都没搜索", x + 20f, y + 108, Colors.BLACK.c);
				} else {
					this.processSearchScroll(mouseX, mouseY);
					GL11.glEnable(GL11.GL_SCISSOR_TEST);
					RenderUtil.doGlScissor(x + 20, y + 110, 214, 222);
					wheelSearchSmoothTrack = RenderUtil.getAnimationState(wheelSearchSmoothTrack, wheelSearchStateTrack * 20f,
							(Math.max(10, (Math.abs(this.wheelSearchSmoothTrack - (wheelSearchStateTrack * 20))) * 50) * 0.3f));
					float startY1 = y + wheelSearchSmoothTrack + 110;
					for (TrackSlot s : mgr.searchResult) {
						s.draw(mouseX, mouseY, x + 24, startY1);
						if(RenderUtil.isHovering(mouseX, mouseY, x + 24 + 175, startY1, x + 24 + 210, startY1 + 24)) {
							if(this.handler.canExcecute()) {
								if(MusicMgr.instance.lastPlayingTrack == null) {
									MusicMgr.instance.lastPlayingTrack = mgr.searchResult;
								}
								
								s.onCrink();
							}
						}
						startY1 += 26;
					}
					GL11.glDisable(GL11.GL_SCISSOR_TEST);
				}
				
				// 日推
				Minecraft.getMinecraft().fontRendererObj.drawString("Daily", x + 260f, y + 92, Colors.BLACK.c);
				RenderUtil.drawRect(x + 260, y + 90 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, x + 490,
						y + 91 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, Colors.BLACK.c);
				RenderUtil.drawRoundedRect(x + 490 - 40, y + 92, x + 490, y + 105, Colors.AQUA.c, Colors.AQUA.c);
				Minecraft.getMinecraft().fontRendererObj.drawString("Refresh", x + 490f - 35f, y + 94.5f, Colors.WHITE.c);
				if(RenderUtil.isHovering(mouseX, mouseY, x + 490 - 40, y + 92, x + 490, y + 105)) {
					if(handler.canExcecute()) {
						try {
							if(!mgr.dailyList.isEmpty()) {
								mgr.dailyList.clear();
							}
							mgr.dailyList = CloudMusicAPI.INSTANCE.getDailySong(NeteaseAPI.INSTANCE.getDailyRecommend(mgr.cookies));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				if(!mgr.dailyList.isEmpty()) {
					this.processDailyScroll(mouseX, mouseY);
					GL11.glEnable(GL11.GL_SCISSOR_TEST);
					RenderUtil.doGlScissor(x + 260, y + 110, 230, 222);
					wheelDailySmoothTrack = RenderUtil.getAnimationState(wheelDailySmoothTrack, wheelDailyStateTrack * 20f, (Math.max(10, (Math.abs(this.wheelDailySmoothTrack - (wheelDailyStateTrack * 20))) * 50) * 0.3f));
					float c = y + wheelDailySmoothTrack + 110;
					for (TrackSlot s : mgr.dailyList) {
						s.draw(mouseX, mouseY, x + 264f, c);
						if(RenderUtil.isHovering(mouseX, mouseY, x + 264f + 186, c, x + 264f + 226, c + 24)) {
							if(this.handler.canExcecute()) {
								if(MusicMgr.instance.lastPlayingTrack == null) {
									MusicMgr.instance.lastPlayingTrack = mgr.dailyList;
								}
								
								s.onCrink();
							}
						}
						c += 26;
					}
					GL11.glDisable(GL11.GL_SCISSOR_TEST);
				}
			} else {
				// 未登陆
				Minecraft.getMinecraft().fontRendererObj.drawCenteredString("您还没有登录,请先登录", x + (510f / 2) - 5, y + 100,
						Colors.BLACK.c);
				this.phoneNum.xPosition = (int) (x + (510f / 2) - 54);
				this.phoneNum.yPosition = y + 120;

				this.passwd.xPosition = (int) (x + (510f / 2) - 54);
				this.passwd.yPosition = y + 140;

				this.phoneNum.drawTextBox();
				this.passwd.drawTextBox();

				if (this.phoneNum.getText().isEmpty()) {
					Minecraft.getMinecraft().fontRendererObj.drawString("电话号码", (x + (510f / 2) - 54), y + 128, Colors.GREY.c);
				}

				if (this.passwd.getText().isEmpty()) {
					Minecraft.getMinecraft().fontRendererObj.drawString("密码", (x + (510f / 2) - 54), y + 148, Colors.GREY.c);
				}

				int col = this.phoneNum.getText().isEmpty() ? Colors.GREY.c
						: this.passwd.getText().isEmpty() ? Colors.GREY.c : Colors.AQUA.c;

				RenderUtil.drawRoundedRect((x + (510f / 2) - 62), y + 185, (x + (510f / 2) + 54), y + 215, col, col);
				Minecraft.getMinecraft().fontRendererObj.drawString("Login", (x + (510f / 2) - 16), y + 195, Colors.WHITE.c);

				if (!this.phoneNum.getText().isEmpty() && !this.passwd.getText().isEmpty()) {
					if (RenderUtil.isHovering(mouseX, mouseY, (x + (510f / 2) - 62), y + 185, (x + (510f / 2) + 54), y + 215)) {
						if (this.handler.canExcecute()) {
							try {
								this.processJson(NeteaseAPI.INSTANCE.loginWithPhoneNum(phoneNum.getText(), passwd.getText()));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		if (this.currentWindow == WindowCategory.MUSIC) {

			// 状态栏
			RenderUtil.drawRect(x, y + 310, x + 510, y + 311, Colors.GREY.c);
			RenderUtil.drawRect(x + 120, y + 20, x + 121, y + 310, Colors.GREY.c);
			RenderUtil.drawRect(x + 0, y + 33, x + 121, y + 34, Colors.GREY.c);
			RenderUtil.drawRect(x + 0, y + 275, x + 121, y + 276, Colors.GREY.c);

			Minecraft.getMinecraft().fontRendererObj.drawString("保存的歌单 (" + this.slots.size() + ")", x + 2f, y + 21,
					Colors.GREY.c);

			RenderUtil.drawRoundedRect(x + 122 - 26, y + 22, x + 118, y + 32f, Colors.AQUA.c, Colors.AQUA.c);
			Minecraft.getMinecraft().fontRendererObj.drawString("刷新", x + 122f - 23f, y + 21, Colors.WHITE.c);

			if (RenderUtil.isHovering(mouseX, mouseY, x + 122 - 26, y + 22, x + 118, y + 32f)) {
				if (this.handler.canExcecute()) {
					if(mgr.isLoggined) {
						MusicMgr.instance.currentSongList = null;
						this.slots.clear();
						mgr.allSongLists.clear();

						mgr.loadSomeShit();
						
						for (SongList s : MusicMgr.instance.allSongLists) {
							slots.add(new SongListSlot(s));
						}
					} else {
						Notifications.getManager().post("请先登录");
					}
				}
			}
			
			if(mgr.currentTrack != null) {
				if (MusicMgr.instance.trackImage.containsKey(mgr.currentTrack.getId())) {
					GL11.glPushMatrix();
					GL11.glColor4f(1, 1, 1, 1);
					Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(mgr.trackImage.get(mgr.currentTrack.getId()));
					this.drawScaledTexturedModalRect(x + 2, y + 279, 0, 0, 29, 29, 8.8f);
					GL11.glPopMatrix();
				} else if(!mgr.currentTrack.getPicUrl().equals("")) {
					if(mgr.imageThread == null) {
						mgr.imageThread = new Thread() {
							@Override
							public void run() {
								mgr.getTrackImage(mgr.currentTrack);
								mgr.imageThread = null;
							}
						};
						mgr.imageThread.start();
					}
				}
				
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				RenderUtil.doGlScissor(x, y + 275, 121, 32);
				Minecraft.getMinecraft().fontRendererObj.drawString(mgr.currentTrack.getName(), x + 34f, y + 282, Colors.BLACK.c);
				Minecraft.getMinecraft().fontRendererObj.drawString(mgr.currentTrack.getArtists(), x + 34f, y + 295, Colors.GREY.c);
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				GL11.glPopMatrix();
				
			}
			
			if (!this.slots.isEmpty()) {
				this.processSongListScroll(mouseX, mouseY);
				wheelSongListSmoothTrack = RenderUtil.getAnimationState(wheelSongListSmoothTrack, wheelSongListStateTrack * 20f,
						(Math.max(10, (Math.abs(this.wheelSongListSmoothTrack - (wheelSongListStateTrack * 20))) * 50) * 0.3f));
				
				float startY = y + wheelSongListSmoothTrack + 42;

				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				RenderUtil.doGlScissor(x, y + 34, 120, 241);
				for (SongListSlot s : slots) {
					s.draw(mouseX, mouseY, x, startY);

					if (RenderUtil.isHovering(mouseX, mouseY, x, startY - 4, x + 120, startY + 16)) {
						if(this.handler.canExcecute()) {
							
							if(RenderUtil.isHovering(mouseX, mouseY, x, y + 34, x + 120, y + 275)) {
								s.onCrink();
							}
							
							this.wheelStateTrack = 0;
						}
					}

					startY += 24;
				}
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
			}

			if (MusicMgr.instance.currentSongList != null) {

				SongList lists = MusicMgr.instance.currentSongList;

				wheelSmoothTrack = RenderUtil.getAnimationState(wheelSmoothTrack, wheelStateTrack * 20f,
						(Math.max(10, (Math.abs(this.wheelSmoothTrack - (wheelStateTrack * 20))) * 50) * 0.3f));
				
				float startY1 = y + wheelSmoothTrack + 22;
				
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				RenderUtil.doGlScissor(x + 121, y + 22, 387, 286);
				if(!lists.songs.isEmpty()) {
					this.processScroll(mouseX, mouseY);
					for (TrackSlot s : lists.songs) {

						if (startY1 < y + 320 && startY1 > y - 10) {

							s.draw(mouseX, mouseY, x + 126, startY1);
							
							if (RenderUtil.isHovering(mouseX, mouseY, x + 126 + 350, startY1, x + 126 + 382, startY1 + 24) && this.handler.canExcecute()) {
								if(RenderUtil.isHovering(mouseX, mouseY, x + 122, y + 22, x + 121 + 387, y + 22 + 286)) {
									s.onCrink();

									if (MusicMgr.instance.lastPlayingTrack == null
											|| MusicMgr.instance.lastPlayingTrack != lists.songs) {
										MusicMgr.instance.lastPlayingTrack = lists.songs;
									}
								}
							}

						}

						startY1 += 26;
					}
				} else {
					Minecraft.getMinecraft().fontRendererObj.drawString("为防止请求太过频繁,请手动获取歌单内的歌曲", x + 232f, y + 120, Colors.BLACK.c);
					if(RenderUtil.isHovering(mouseX, mouseY, x + 232f, y + 132, x + 232 + Minecraft.getMinecraft().fontRendererObj.getStringWidth("为防止请求太过频繁,请手动获取歌单内的歌曲"), y + 152)) {
						RenderUtil.drawRoundedRect(x + 232f, y + 132, x + 232 + Minecraft.getMinecraft().fontRendererObj.getStringWidth("为防止请求太过频繁,请手动获取歌单内的歌曲"), y + 152, new Color(Colors.AQUA.c).darker().getRGB(), new Color(Colors.AQUA.c).darker().getRGB());
						if(this.handler.canExcecute()) {
							try {
								for(int i = 0; i < mgr.allSongLists.size(); ++i) {
									SongList s = mgr.allSongLists.get(i);
									if(s == mgr.currentSongList) {
										s.songs = CloudMusicAPI.INSTANCE.getSongFromSongList(s.jsonStorage);
										mgr.currentSongList = s;
										s.jsonStorage = "";
										break;
									}
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} else {
						RenderUtil.drawRoundedRect(x + 232f, y + 132, x + 232 + Minecraft.getMinecraft().fontRendererObj.getStringWidth("为防止请求太过频繁,请手动获取歌单内的歌曲"), y + 152, Colors.AQUA.c, Colors.AQUA.c);
					}
					Minecraft.getMinecraft().fontRendererObj.drawString("获取", x + 305f, y + 136, Colors.WHITE.c);
				}

				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				GL11.glPopMatrix();
			}
			
			// 暂停/播放 按钮
			if(RenderUtil.isHovering(mouseX, mouseY, x + 4, y + 312, x + 26, y + 334)) {
				RenderUtil.circle(x + 15, y + 323, 10, new Color(Colors.AQUA.c).darker().getRGB());
				
				if(this.handler.canExcecute() && mgr.mediaPlayer != null) {
					if(mgr.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
						mgr.mediaPlayer.pause();
					} else {
						mgr.mediaPlayer.play();
					}
				}
			} else {
				RenderUtil.circle(x + 15, y + 323, 10, Colors.AQUA.c);
			}
			if(mgr.mediaPlayer != null) {
				Minecraft.getMinecraft().fontRendererObj.drawString(mgr.mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ? "| |" : "|>", x + 12f, y + 317, Colors.WHITE.c);
			} else {
				Minecraft.getMinecraft().fontRendererObj.drawString("|>", x + 12f, y + 317, Colors.WHITE.c);
			}
			
			// 下一首 按钮
			if(RenderUtil.isHovering(mouseX, mouseY, x + 31, y + 314, x + 49, y + 332)) {
				RenderUtil.circle(x + 40, y + 323, 8, new Color(Colors.AQUA.c).darker().getRGB());
				if(this.handler.canExcecute() && mgr.currentTrack != null && mgr.mediaPlayer != null) {
					mgr.next();
				}
			} else {
				RenderUtil.circle(x + 40, y + 323, 8, Colors.AQUA.c);
			}
			Minecraft.getMinecraft().fontRendererObj.drawString(">", x + 38f, y + 317, Colors.WHITE.c);
			
			//进度条
			double progress = 0;
			double progress2 = 0;
			if(mgr.mediaPlayer != null) {
				progress = MusicMgr.instance.mediaPlayer.getCurrentTime().toSeconds();
				progress2 = MusicMgr.instance.mediaPlayer.getStopTime().toSeconds();
				Minecraft.getMinecraft().fontRendererObj.drawString(mgr.formatSeconds((int)progress), x + 60f, y + 314f, Colors.BLACK.c);
				Minecraft.getMinecraft().fontRendererObj.drawString(mgr.formatSeconds((int)progress2), x + 420f - Minecraft.getMinecraft().fontRendererObj.getStringWidth(mgr.formatSeconds((int)progress2)), y + 314f, Colors.BLACK.c);
			}
			
			RenderUtil.drawRoundedRect(x + 60f, y + 322, x + 420, y + 326, Colors.GREY.c, Colors.GREY.c);
			
			if(mgr.mediaPlayer != null) {
				int fuck = (int) ((progress / progress2) * 1000);
				if(fuck < 10) {
					fuck = 10;
				}
				RenderUtil.drawRoundedRect(x + 60f, y + 322, x + 60 + (0.36f * fuck), y + 326, Colors.AQUA.c, Colors.AQUA.c);
			}
			
			//歌词显示
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("词", x + 440f, y + 318, mgr.displayLyric == false ? Colors.GREY.c : Colors.AQUA.c);
			if(RenderUtil.isHovering(mouseX, mouseY, x + 440f, y + 320, x + 448f, y + 328)) {
				 if(this.handler.canExcecute()) {
					 mgr.displayLyric = !mgr.displayLyric;
				 }
			}
			
			//歌词编码
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(mgr.isUtf ? "U" : "G", x + 460f, y + 318, Colors.GREY.c);
			if(RenderUtil.isHovering(mouseX, mouseY, x + 460f, y + 320, x + 468f, y + 328)) {
				if(this.handler.canExcecute()) {
					mgr.isUtf = !mgr.isUtf;
					Notifications.getManager().post("重新播放歌曲使其生效");
				}
			}
			//单曲循环
			Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("R", x + 480f, y + 318, mgr.loop ? Colors.AQUA.c : Colors.GREY.c);
			if(RenderUtil.isHovering(mouseX, mouseY, x + 480f, y + 320, x + 488f, y + 328)) {
				if(this.handler2.canExcecute()) {
					mgr.loop = !mgr.loop;
					if(mgr.mediaPlayer != null) {
						if(mgr.loop) {
							mgr.mediaPlayer.setOnEndOfMedia(new Runnable() {
								@Override
								public void run() {
									if(MusicMgr.instance.loop) {
				                		MusicMgr.instance.play(MusicMgr.instance.currentTrack);
				                	} else {
				                		MusicMgr.instance.next();
				                	}
								}
							});
						} else {
							mgr.mediaPlayer.setOnEndOfMedia(new Runnable() {
								@Override
								public void run() {
									MusicMgr.instance.next();
								}
							});
						}
					}
				}
			}
		}
		
		mouseWheel = Mouse.getDWheel();
		this.moveWindow(mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	public void drawScaledTexturedModalRect(double x, double y, double textureX, double textureY, double width,
			double height, float scale) {
		float f = 0.00390625F * scale;
		float f1 = 0.00390625F * scale;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos((double) (x + 0), (double) (y + height), (double) this.zLevel)
				.tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + height) * f1)).endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + height), (double) this.zLevel)
				.tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1))
				.endVertex();
		worldrenderer.pos((double) (x + width), (double) (y + 0), (double) this.zLevel)
				.tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		worldrenderer.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel)
				.tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
		tessellator.draw();
	}

	public void processScroll(int mouseX, int mouseY) {
		if (RenderUtil.isHovering(mouseX, mouseY, x + 121, y + 20, x + 121 + 389, y + 310)) {
			if (mouseWheel > 0) {
				if (wheelStateTrack < 0)
					wheelStateTrack++;
			} else if (mouseWheel < 0) {
				if (wheelStateTrack * 10 > MusicMgr.instance.currentSongList.songs.size() * -25f)
					wheelStateTrack--;
			}
		}
	}

	public void processSearchScroll(int mouseX, int mouseY) {
		if (RenderUtil.isHovering(mouseX, mouseY, x + 20, y + 110, x + 20 + 160, y + 110 + 222)) {
			if (mouseWheel > 0) {
				if (wheelSearchStateTrack < 0)
					wheelSearchStateTrack++;
			} else if (mouseWheel < 0) {
				if (wheelSearchStateTrack * 10 > MusicMgr.instance.searchResult.size() * -25f)
					wheelSearchStateTrack--;
			}
		}
	}
	
	public void processDailyScroll(int mouseX, int mouseY) {
		if (RenderUtil.isHovering(mouseX, mouseY, x + 260f, y + 110, x + 490, y + 332)) {
			if (mouseWheel > 0) {
				if (wheelDailyStateTrack < 0)
					wheelDailyStateTrack++;
			} else if (mouseWheel < 0) {
				if (wheelDailyStateTrack * 10 > MusicMgr.instance.dailyList.size() * -25f)
					wheelDailyStateTrack--;
			}
		}
	}
	
	public void processSongListScroll(int mouseX, int mouseY) {
		if (RenderUtil.isHovering(mouseX, mouseY, x, y + 34, x + 120, y + 275)) {
			if (mouseWheel > 0) {
				if (wheelSongListStateTrack < 0)
					wheelSongListStateTrack++;
			} else if (mouseWheel < 0) {
				if (wheelSongListStateTrack * 10 > MusicMgr.instance.allSongLists.size() * -25f)
					wheelSongListStateTrack--;
			}
		}
	}

	public void processJson(String str) {
		JsonParser parser = new JsonParser();

		try {
			// 解析传递进来的Json
			JsonObject one = (JsonObject) parser.parse(str);

			// 解析到个人资料分类
			JsonObject two = (JsonObject) one.get("profile").getAsJsonObject();
			// 下载用户头像
			mgr.user_avatar = mgr.getImage("user_avatar", two.get("avatarUrl").getAsString());
			// 获取用户名称
			mgr.nickname = two.get("nickname").getAsString();
			// 获取所在城市代码
			mgr.location_code = String.valueOf(two.get("city").getAsInt());
			// 获取关注数
			mgr.follows = String.valueOf(two.get("follows").getAsInt());
			// 获取粉丝数
			mgr.followeds = String.valueOf(two.get("followeds").getAsInt());
			//获取用户ID
			mgr.uid = two.get("userId").getAsString();

			// 解析cookies
			mgr.cookies = one.get("cookie").getAsString();

			mgr.isLoggined = true;
		} catch (Exception ex) {
			System.out.println(str);
			ex.printStackTrace();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.phoneNum.textboxKeyTyped(typedChar, keyCode);
		this.passwd.textboxKeyTyped(typedChar, keyCode);
		this.searchField.textboxKeyTyped(typedChar, keyCode);

		switch (keyCode) {
		case Keyboard.KEY_RETURN:
			if (this.currentWindow == WindowCategory.USER) {
				if(mgr.isLoggined) {
					if(!this.searchField.getText().isEmpty()) {
						try {
							mgr.searchResult = CloudMusicAPI.INSTANCE.getSong(NeteaseAPI.INSTANCE.searchSong(this.searchField.getText(), 40, 0, 1));
						} catch (Exception e) {
							e.printStackTrace();
						}
						this.searchField.setText("");
					}
				}
			}
			break;
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.phoneNum.mouseClicked(mouseX, mouseY, mouseButton);
		this.passwd.mouseClicked(mouseX, mouseY, mouseButton);
		this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	public void moveWindow(int mouseX, int mouseY) {
		
		if(RenderUtil.isHovering(mouseX, mouseY, x + 8, y, x + 44, y + 20) || RenderUtil.isHovering(mouseX, mouseY, x + 47, y, x + 46 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(mgr.nickname) + 4, y + 20)) {
			return;
		}

		if (RenderUtil.isHovering(mouseX, mouseY, x, y, x + 510, y + 20) && this.handler.canExcecute()) {

			this.drag = true;
			this.x2 = (int) (mouseX - this.x);
			this.y2 = (int) (mouseY - this.y);
		}

		if (this.drag) {
			this.x = mouseX - this.x2;
			this.y = mouseY - this.y2;
		}

		if (!Mouse.isButtonDown(0)) {
			this.drag = false;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
