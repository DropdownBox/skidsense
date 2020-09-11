package me.skidsense.management.command.impl;

import java.util.ArrayList;
import java.util.Iterator;

import me.skidsense.Client;
import me.skidsense.management.command.Command;
import me.skidsense.util.ChatUtil;
import net.minecraft.util.EnumChatFormatting;

public class Help extends Command {
   public Help(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      int i = 1;
      if (args == null) {
         ArrayList<String> used = new ArrayList<String>();
         Iterator var4 = Client.instance.commandmanager.getCommands().iterator();

         while(var4.hasNext()) {
            Command command = (Command)var4.next();
            if (!used.contains(command.getName())) {
               used.add(command.getName());
               ChatUtil.printChat(Command.chatPrefix + i + ". " + command.getName() + " - " + command.getDescription());
               ++i;
            }
         }

         ChatUtil.printChat(Command.chatPrefix + "Specify a name of a command for details about it.");
      } else if (args.length > 0) {
         String name = args[0];
         Command command = Client.instance.commandmanager.getCommand(name);
         if (command == null) {
            ChatUtil.printChat(Command.chatPrefix + "Could not find: " + name);
            return;
         }

         ChatUtil.printChat(Command.chatPrefix + command.getName() + ": " + command.getDescription());
         if (command.getUsage() != null) {
            ChatUtil.printChat(command.getUsage());
         }
      }

   }

   public String getUsage() {
      return "Help " + EnumChatFormatting.ITALIC + " [optional] " + EnumChatFormatting.RESET + "<Config>";
   }
}
