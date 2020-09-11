package me.skidsense.management.command.impl;

import me.skidsense.management.command.Command;
import net.minecraft.client.Minecraft;

public class Clear extends Command {
   public Clear(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
   }

   public String getUsage() {
      return "Clear";
   }
}
