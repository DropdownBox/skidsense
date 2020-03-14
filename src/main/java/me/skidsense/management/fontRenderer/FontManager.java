package me.skidsense.management.fontRenderer;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class FontManager {
	private HashMap<String, HashMap<Float, FontRenderer>> fonts = new HashMap<>();
	//private HashMap<String, HashMap<Float, ChFontRenderer>> chFonts = new HashMap();
	public FontRenderer verdana12;
	public FontRenderer verdana14;
	public FontRenderer verdana16;
	public FontRenderer verdana17;
	public FontRenderer verdana20;
	public FontRenderer sigmaarr;
	public FontRenderer zeroarr;
	public FontRenderer comfortaa18;
	public FontRenderer comfortaa216;
	public FontRenderer comfortaa14;
	public FontRenderer comfortaa20;
	public FontRenderer comfortaa34;
	public FontRenderer roboto19;
	public FontRenderer roboto20;
	public FontRenderer tahomabold13;
	public FontRenderer sansation16;
	public FontRenderer sansation18;
	public FontRenderer sansation14;
	public FontRenderer sansation28;
	public FontRenderer notoSans25;
	public FontRenderer notoSans30;
	public FontRenderer jbmono25;
	public FontRenderer jbmono30;
    
	
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
		notoSans25 = this.getFont("notosansCN", 25f,".otf",true);
		notoSans30 = this.getFont("notosansCN", 30f,".otf",true);
		jbmono25 = this.getFont("jetbrainsmono", 25f);
		jbmono30 = this.getFont("jetbrainsmono", 30f);
	}


	public FontRenderer getFont(String name, float size) {

        return getFont(name,size,".ttf",false);
    }

    public FontRenderer getFont(String name, float size, String format,boolean cn) {
        FontRenderer fontRenderer = null;
        try {
            if (this.fonts.containsKey(name) && this.fonts.get(name).containsKey(size)) {
                return this.fonts.get(name).get(size);
            }
            InputStream inputStream = this.getClass().getResourceAsStream("/fonts/" + name + format);
            Font font;
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            if(cn)
            	fontRenderer = new ChineseFontRenderer(font.deriveFont(size));
            else
	            fontRenderer = new UnicodeFontRenderer(font.deriveFont(size));
            fontRenderer.setUnicodeFlag(true);
            fontRenderer.setBidiFlag(Minecraft.getMinecraft().mcLanguageManager.isCurrentLanguageBidirectional());
            HashMap<Float, FontRenderer> map = new HashMap<>();
            if (this.fonts.containsKey(name)) {
                map.putAll(this.fonts.get(name));
            }
            map.put(size, fontRenderer);
            this.fonts.put(name, map);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return fontRenderer;
    }
}
