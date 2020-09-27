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
	public TTFFontRenderer comfortaa17;
	public TTFFontRenderer comfortaa18;
	public TTFFontRenderer comfortaa16;
	public TTFFontRenderer comfortaa14;
	public TTFFontRenderer comfortaa20;
	public TTFFontRenderer comfortaa34;
	public FontRenderer tahomabold13;
	
	public FontManager() {
		arial18 = new TTFFontRenderer(new Font("Arial", 0, 18), true);
		arial20 = new TTFFontRenderer(new Font("Arial", 0, 20), true);
		arial25 = new TTFFontRenderer(new Font("Arial", 0, 25), true);
		arialbold17 = new TTFFontRenderer(new Font("Arial Bold",0, 17), true);
		tahoma10 = new TTFFontRenderer(new Font("Tahoma", 0, 10), true);
		verdana10 = new TTFFontRenderer(new Font("Verdana", 0, 10), true);
		comfortaa17 = new TTFFontRenderer(new Font("Comfortaa", 0, 17), true);
		comfortaa14 = new TTFFontRenderer(new Font("Comfortaa", 0, 14), true);
		comfortaa16 =  new TTFFontRenderer(new Font("Comfortaa", 0, 16), true);
		comfortaa18 = new TTFFontRenderer(new Font("Comfortaa", 0, 18), true);
		comfortaa20 = new TTFFontRenderer(new Font("Comfortaa", 0, 20), true);
		comfortaa34 =new TTFFontRenderer(new Font("Comfortaa", 0, 34), true);
		tahomabold13 = this.getFont("tahomabold", 13f);
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
            InputStream inputStream = this.getClass().getResourceAsStream("/assets/minecraft/" + name + format);
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
