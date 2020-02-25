package me.skidsense.notifications;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Notification {

	public static void SendNotification(String text) {
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatFormatting.RED + "[Notifier] " + ChatFormatting.WHITE + text));
	}

}
