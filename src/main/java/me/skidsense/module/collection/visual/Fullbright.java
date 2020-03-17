package me.skidsense.module.collection.visual;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;

public class Fullbright extends Mod {
	public int alphag = 1;
	public Fullbright() {
		super("Full Bright", new String[]{"FullBright"}, ModuleType.Visual);
        this.addValues();
	}

	@Override
	public void onDisable() {
		mc.gameSettings.gammaSetting = (float) 1;
	}

	@Sub
	public void onUpdate(EventPreUpdate event) {
		if(alphag < 15 && alphag > 0) {
			alphag++;
		}
		mc.gameSettings.gammaSetting = alphag;
	}
}
