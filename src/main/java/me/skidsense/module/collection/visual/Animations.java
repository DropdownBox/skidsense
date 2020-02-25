package me.skidsense.module.collection.visual;

import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;

public class Animations extends Module{
    public static Mode<Enum> mode = new Mode("Mode", "mode", (Enum[])renderMode.values(), (Enum)renderMode.Vanilla);
    public static Option<Boolean> smooth = new Option<Boolean>("Smooth", "Smooth", false);
    public static Option<Boolean> Eliminates = new Option<Boolean>("Eliminates", "Eliminates", false);
	public Animations() {
		super("Animations", new String[] {"BlockHitanimations"}, ModuleType.Visual);
		this.addValues(this.mode,this.smooth,Eliminates);
		this.setEnabled(true);
		this.setRemoved(true);
	}
	public static enum renderMode {
		Vanilla,
		Slide,
		Luna,
		Avatar,
		Jigsaw,
		Remix,
		Sigma,
		Astro;
	}
}
