package me.skidsense.management.fontRenderer;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontManager {
	private HashMap<String, HashMap<Float, UnicodeFontRenderer>> fonts = new HashMap();
	private HashMap<String, HashMap<Float, ChFontRenderer>> chFonts = new HashMap();
	public UnicodeFontRenderer verdana12;
	public UnicodeFontRenderer verdana14;
	public UnicodeFontRenderer verdana16;
	public UnicodeFontRenderer verdana17;
	public UnicodeFontRenderer verdana20;
	public UnicodeFontRenderer sigmaarr;
	public UnicodeFontRenderer zeroarr;
	public UnicodeFontRenderer comfortaa18;
	public UnicodeFontRenderer comfortaa216;
	public UnicodeFontRenderer comfortaa14;
	public UnicodeFontRenderer comfortaa20;
	public UnicodeFontRenderer comfortaa34;
	public UnicodeFontRenderer roboto19;
	public UnicodeFontRenderer roboto20;
	public UnicodeFontRenderer tahomabold13;
	public UnicodeFontRenderer sansation16;
	public UnicodeFontRenderer sansation18;
	public UnicodeFontRenderer sansation14;
	public UnicodeFontRenderer sansation28;
	public ChFontRenderer notoSans25;
	public ChFontRenderer notoSans30;
    
	
	public FontManager() {
		verdana12 = this.getFont("verdana", 12f);
		verdana14 = this.getFont("verdana", 14f);
		verdana16 = this.getFont("verdana", 16f);
		verdana17 = this.getFont("verdana", 17f);
		verdana20 = this.getFont("verdana", 20f);
		sigmaarr = this.getFont("sigma", 8f);
		zeroarr = this.getFont("comfortaa", 17f);
		comfortaa18 = this.getFont("comfortaa", 18f);
		comfortaa216 = this.getFont("comfortaa2", 18f);
		comfortaa14 = this.getFont("comfortaa", 14f);
		comfortaa20 = this.getFont("comfortaa", 20f);
		comfortaa34 = this.getFont("comfortaa", 34f);
		roboto19 = this.getFont("roboto", 19f);
		roboto20 = this.getFont("roboto", 20f);
		tahomabold13 = this.getFont("tahomabold", 13f);
		sansation18 = this.getFont("sansation", 18f);
		sansation14 = this.getFont("sansation", 14f);
		sansation28 = this.getFont("sansation", 28f);
		sansation16 = this.getFont("sansation", 16f);
		notoSans25 = this.getChFont("notosans", 25f);
		notoSans30 = this.getChFont("notosans", 30f);
	}

	public ChFontRenderer getChFont(String name, float size) {
		ChFontRenderer chFont = null;
		try {
			if (this.chFonts.containsKey(name) && this.chFonts.get(name).containsKey(Float.valueOf(size))) {
				return this.chFonts.get(name).get(size);
			}
			//InputStream inputStream = this.getClass().getResourceAsStream("fonts/" + name + ".ttf");
			InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("skidsense/fonts/" + name + ".ttf")).getInputStream();
			Font font = null;
			font = Font.createFont(0, inputStream);
			chFont = new ChFontRenderer(font.deriveFont(size));
			chFont.setUnicodeFlag(true);
			chFont.setBidiFlag(Minecraft.getMinecraft().mcLanguageManager.isCurrentLanguageBidirectional());
			HashMap<Float, ChFontRenderer> fontRendererHashMap = new HashMap<>();
			if (this.chFonts.containsKey(name)) {
				fontRendererHashMap.putAll(this.chFonts.get(name));
			}
			fontRendererHashMap.put(size, chFont);
			this.chFonts.put(name, fontRendererHashMap);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return chFont;
	}
	public UnicodeFontRenderer getFont(String name, float size) {
        UnicodeFontRenderer unicodeFont = null;
        try {
            if (this.fonts.containsKey(name) && this.fonts.get(name).containsKey(Float.valueOf(size))) {
                return this.fonts.get(name).get(Float.valueOf(size));
            }
            //InputStream inputStream = this.getClass().getResourceAsStream("fonts/" + name + ".ttf");
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("skidsense/fonts/" + name + ".ttf")).getInputStream();
            Font font = null;
            font = Font.createFont(0, inputStream);
            unicodeFont = new UnicodeFontRenderer(font.deriveFont(size));
            unicodeFont.setUnicodeFlag(true);
            unicodeFont.setBidiFlag(Minecraft.getMinecraft().mcLanguageManager.isCurrentLanguageBidirectional());
            HashMap<Float, UnicodeFontRenderer> map = new HashMap<Float, UnicodeFontRenderer>();
            if (this.fonts.containsKey(name)) {
                map.putAll(this.fonts.get(name));
            }
            map.put(size, unicodeFont);
            this.fonts.put(name, map);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return unicodeFont;
    }

    public UnicodeFontRenderer getFont(String name, float size, boolean b) {
        UnicodeFontRenderer unicodeFont = null;
        try {
            if (this.fonts.containsKey(name) && this.fonts.get(name).containsKey(Float.valueOf(size))) {
                return this.fonts.get(name).get(Float.valueOf(size));
            }
            InputStream inputStream = this.getClass().getResourceAsStream("fonts/" + name + ".otf");
            Font font = null;
            font = Font.createFont(0, inputStream);
            unicodeFont = new UnicodeFontRenderer(font.deriveFont(size));
            unicodeFont.setUnicodeFlag(true);
            unicodeFont.setBidiFlag(Minecraft.getMinecraft().mcLanguageManager.isCurrentLanguageBidirectional());
            HashMap<Float, UnicodeFontRenderer> map = new HashMap<Float, UnicodeFontRenderer>();
            if (this.fonts.containsKey(name)) {
                map.putAll((Map)this.fonts.get(name));
            }
            map.put(Float.valueOf(size), unicodeFont);
            this.fonts.put(name, map);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return unicodeFont;
    }
}
