package me.skidsense.module.collection.move;

import org.lwjgl.input.Keyboard;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

public class InvMove extends Mod {

	public InvMove() {
		super("Gui Move", new String[] {"GuiMove"}, ModuleType.Move);
	}
	
	@Sub
	public void onUpdate(EventPreUpdate event) {
		if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
			KeyBinding[] key = { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump };
			KeyBinding[] array;
			for (int length = (array = key).length, i = 0; i < length; ++i) {
				KeyBinding b = array[i];
				KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
			}
		}
	}
}
