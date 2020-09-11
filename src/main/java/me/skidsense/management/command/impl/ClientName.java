package me.skidsense.management.command.impl;

import me.skidsense.Client;
import me.skidsense.management.command.Command;

public class ClientName extends Command {
   public ClientName(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
       if (args.length != 0) {
           Client.clientName = args[0];
       }
   }
   
   public String getUsage() {
      return null;
   }
}
