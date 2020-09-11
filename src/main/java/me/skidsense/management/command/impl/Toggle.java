package me.skidsense.management.command.impl;

import me.skidsense.Client;
import me.skidsense.management.command.Command;
import me.skidsense.module.Mod;
import me.skidsense.util.ChatUtil;

public class Toggle extends Command {
   public Toggle(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      if (args == null) {
         this.printUsage();
      } else {
         Mod module = null;
         if (args.length > 0) {
            module = Client.getModuleManager().getModuleByName(args[0]);
         }

         if (module == null) {
            this.printUsage();
         } else {
            if (args.length == 1) {
               module.setEnabled(!module.isEnabled());
               ChatUtil.printChat(Command.chatPrefix + module.getName() + " has been" + (module.isEnabled() ? "§a Enabled." : "§c Disabled."));
            }

         }
      }
   }

   public String getUsage() {
      return "toggle <module name>";
   }
}
