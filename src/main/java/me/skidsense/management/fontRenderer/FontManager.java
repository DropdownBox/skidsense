package me.skidsense.management.fontRenderer;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class FontManager {
	private HashMap<String, HashMap<Float, FontRenderer>> fonts = new HashMap<>();
	//private HashMap<String, HashMap<Float, ChFontRenderer>> chFonts = new HashMap();
	public TTFFontRenderer arial18;
	public TTFFontRenderer arial20;
	public TTFFontRenderer arial25;
	public TTFFontRenderer arialbold17;
	public TTFFontRenderer tahoma10;
	public TTFFontRenderer verdana10;
	public FontRenderer verdana12;
	public FontRenderer verdana14;
	public FontRenderer verdana16;
	public FontRenderer verdana17;
	public FontRenderer verdana20;
	public FontRenderer sigmaarr;
	public FontRenderer zeroarr;
	public TTFFontRenderer comfortaa18;
	public TTFFontRenderer comfortaa16;
	public TTFFontRenderer comfortaa14;
	public TTFFontRenderer comfortaa20;
	public TTFFontRenderer comfortaa34;
	public FontRenderer roboto17;
	public TTFFontRenderer roboto18;
	public FontRenderer roboto19;
	public TTFFontRenderer roboto20;
	public FontRenderer tahomabold13;
	public FontRenderer sansation16;
	public FontRenderer sansation18;
	public FontRenderer sansation14;
	public FontRenderer sansation28;
    public FontRenderer kiona16;
	public FontRenderer kiona18;
	
	public FontManager() {
		arial18 = new TTFFontRenderer(new Font("Arial", 0, 18), true);
		arial20 = new TTFFontRenderer(new Font("Arial", 0, 20), true);
		arial25 = new TTFFontRenderer(new Font("Arial", 0, 25), true);
		arialbold17 = new TTFFontRenderer(new Font("Arial Bold",0, 17), true);
		tahoma10 = new TTFFontRenderer(new Font("Tahoma", 0, 10), true);
		verdana10 = new TTFFontRenderer(new Font("Verdana", 0, 10), true);
		verdana12 = this.getFont("verdana", 12f);
		verdana14 = this.getFont("verdana", 14f);
		verdana16 = this.getFont("verdana", 16f);
		verdana17 = this.getFont("verdana", 17f);
		verdana20 = this.getFont("verdana", 20f);
		sigmaarr = this.getFont("sigma", 8f);
		zeroarr = this.getFont("comfortaa", 17f);
		comfortaa14 = new TTFFontRenderer(new Font("Comfortaa", 0, 14), true);
		comfortaa16 =  new TTFFontRenderer(new Font("Comfortaa", 0, 16), true);
		comfortaa18 = new TTFFontRenderer(new Font("Comfortaa", 0, 18), true);
		comfortaa20 = new TTFFontRenderer(new Font("Comfortaa", 0, 20), true);
		comfortaa34 =new TTFFontRenderer(new Font("Comfortaa", 0, 34), true);
		roboto17 = this.getFont("roboto", 17f);
		roboto18 = new TTFFontRenderer(new Font("Roboto", 0, 18), true);
		roboto19 = this.getFont("roboto", 19f);
		roboto20 = new TTFFontRenderer(new Font("Roboto", 0, 20), true);
		tahomabold13 = this.getFont("tahomabold", 13f);
		sansation18 = this.getFont("sansation", 18f);
		sansation14 = this.getFont("sansation", 14f);
		sansation28 = this.getFont("sansation", 28f);
		sansation16 = this.getFont("sansation", 16f);
		kiona16 = this.getFont("Kiona-Regular", 16f);
		kiona18 = this.getFont("Kiona-Regular", 18f);
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
