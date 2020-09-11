package me.skidsense.management.command.impl;

import me.skidsense.Client;
import me.skidsense.management.command.Command;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.util.ChatUtil;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class Bind extends Command {
   public Bind(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      if (args == null) {
         this.printUsage();
      } else {
    	  if (args.length >= 2) {
              Mod m = Client.getModuleManager().getAlias(args[0]);
              if (m != null) {
                  int k = Keyboard.getKeyIndex((String)args[1].toUpperCase());
                  m.setKey(k);
                  Object[] arrobject = new Object[2];
                  arrobject[0] = m.getName();
                  arrobject[1] = k == 0 ? "none" : args[1].toUpperCase();
                  //Notifications.getManager().post(String.format("> Bound %s to %s", arrobject));
                  ChatUtil.printChat(Command.chatPrefix + String.format("Set %s to %s",arrobject));
                  //Client.sendMessage(String.format("> Bound %s to %s", arrobject));
              } else {
                  //Notifications.getManager().post("> Invalid module name, double check spelling.");
                  ChatUtil.printChat(Command.chatPrefix + "Invalid module name");
              	//Client.sendMessage("> Invalid module name, double check spelling.");
              }
          }
      }
   }

   public String getUsage() {
      return ".bind <Module> <Key>";
   }
}
