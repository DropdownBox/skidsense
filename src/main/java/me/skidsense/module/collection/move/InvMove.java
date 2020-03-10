package me.skidsense.module.collection.move;

import me.skidsense.util.MoveUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Keyboard;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

public class InvMove extends Module{

	public InvMove() {
		super("Inv Move", new String[] {"InvMove"}, ModuleType.Move);
	}
	
	@EventHandler
	public void onUpdate(EventPreUpdate event) {
		if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiChat)) {
			KeyBinding[] key = { this.mc.gameSettings.keyBindForward, this.mc.gameSettings.keyBindBack, this.mc.gameSettings.keyBindLeft, this.mc.gameSettings.keyBindRight, this.mc.gameSettings.keyBindSprint, this.mc.gameSettings.keyBindJump };
			KeyBinding[] array;
			for (int length = (array = key).length, i = 0; i < length; ++i) {
				KeyBinding b = array[i];
				KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
			}
			Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C09PacketHeldItemChange(8));
			if(MoveUtil.isMoving()){
				Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C09PacketHeldItemChange(Minecraft.getMinecraft().thePlayer.inventory.currentItem));
			}
		}
	}
}
