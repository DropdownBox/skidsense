package me.skidsense.module.collection.visual;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Fullbright extends Mod {
	public Fullbright() {
		super("Full Bright", new String[]{"FullBright"}, ModuleType.Visual);
	}

	@Override
	public void onDisable() {
		mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
	}

	@Sub
	public void onUpdate(EventPreUpdate event) {
		mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 1337, 1));
	}
}
