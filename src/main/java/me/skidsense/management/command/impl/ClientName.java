package me.skidsense.management.command.impl;

import me.skidsense.Client;
import me.skidsense.management.command.Command;

public class ClientName extends Command {
   public ClientName(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
       if (args.length != 0) {
    	   String nameString =new String();
    	   for (int i = 0; i < args.length; i++) {
    		   if(i == 0) {
    			   nameString += args[i];
    		   }else {
    			   nameString += " " + args[i];
    		   }
    	   }
           Client.clientName = nameString;
           nameString =new String("");
       }
   }
   
   public String getUsage() {
      return null;
   }
}
