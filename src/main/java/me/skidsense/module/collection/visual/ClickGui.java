package me.skidsense.module.collection.visual;

import me.skidsense.hooks.value.Mode;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.visual.clickgui.LAC.ClickUI;
import me.skidsense.module.collection.visual.clickgui.Skidsense.ClickGUI;
import me.theresa.music.ui.MusicWindow;

public class ClickGui extends Module {
    public Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])renderMode.values(), (Enum)renderMode.skidsense);

	public ClickGui() {
		super("Click GUI", new String[] { "ClickGUI","ClickHUD" }, ModuleType.Visual);
	this.addValues(mode);
		
	}

	@Override
	public void onEnable() {
		switch (this.mode.getValue().toString()) {
		case "skidsense":
			this.mc.displayGuiScreen(new ClickGUI());
			break;
		case "LAC":
			this.mc.displayGuiScreen(new ClickUI());
			break;
			case "test":
				this.mc.displayGuiScreen(new MusicWindow());
				break;
		}
		
		this.setEnabled(false);
	}
	
	public static enum renderMode {
		skidsense,
		test,
		LAC;
	}
}
