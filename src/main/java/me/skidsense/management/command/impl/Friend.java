package me.skidsense.management.command.impl;

import me.skidsense.management.command.Command;
import me.skidsense.management.friend.FriendManager;
import me.skidsense.util.ChatUtil;
import net.minecraft.util.EnumChatFormatting;

public class Friend extends Command {
   public Friend(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      if (args != null && args.length >= 2) {
         try {
            if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("a")) {
               if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("d")) {
                  if (FriendManager.isFriend(args[1])) {
                     FriendManager.removeFriend(args[1]);
                     ChatUtil.printChat(Command.chatPrefix + "Removed friend: " + args[1]);
                  } else {
                     ChatUtil.printChat(Command.chatPrefix + args[1] + " is not your friend.");
                  }
               }
            } else {
               if (FriendManager.isFriend(args[1])) {
                  ChatUtil.printChat(Command.chatPrefix + args[1] + " is already your friend.");
               }

               FriendManager.removeFriend(args[1]);
               FriendManager.addFriend(args[1], args.length == 3 ? args[2] : args[1]);
               ChatUtil.printChat(Command.chatPrefix + "Added " + args[1]);
            }
         } catch (NullPointerException var3) {
            this.printUsage();
         }

      } else {
         this.printUsage();
      }
   }

   public String getUsage() {
      return "friend <add/del> " + EnumChatFormatting.RESET + "<name> " + EnumChatFormatting.RESET + "<alias>";
   }
}
