package me.skidsense.management.command.impl;

import java.util.Iterator;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.management.command.Command;
import me.skidsense.util.ChatUtil;
import net.minecraft.util.Vec3;

public class Waypoint extends Command {
   public Waypoint(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      if (args == null) {
         this.printUsage();
      } else if (args.length > 1) {
         if (!args[0].equalsIgnoreCase("d") && !args[0].equalsIgnoreCase("del")) {
            if (args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("add")) {
               int color;
               if (args.length == 2) {
                  if (!Client.instance.waypointManager.containsName(args[1])) {
                     color = Colors.getColor((int)(255.0D * Math.random()), (int)(255.0D * Math.random()), (int)(255.0D * Math.random()));
                     Client.instance.waypointManager.createWaypoint(args[1], new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0D, this.mc.thePlayer.posZ), color, this.mc.isSingleplayer() ? "SinglePlayer" : this.mc.getCurrentServerData().serverIP);
                     ChatUtil.printChatwithPrefix("§7Waypoint §c" + args[1] + "§7 has been successfully created.");
                  } else {
                     ChatUtil.printChatwithPrefix("§7Waypoint §c" + args[1] + "§7 already exists.");
                     this.printUsage();
                  }
               } else if (args.length == 5) {
                  if (!Client.instance.waypointManager.containsName(args[1])) {
                     color = Colors.getColor((int)(255.0D * Math.random()), (int)(255.0D * Math.random()), (int)(255.0D * Math.random()));
                     Client.instance.waypointManager.createWaypoint(args[1], new Vec3((double)Integer.parseInt(args[2]), (double)Integer.parseInt(args[3]), (double)Integer.parseInt(args[4])), color, this.mc.getCurrentServerData().serverIP);
                     ChatUtil.printChatwithPrefix("§7Waypoint §c" + args[1] + " §7has been successfully created.");
                  } else {
                     ChatUtil.printChatwithPrefix("§7Waypoint §c" + args[1] + " §7already exists.");
                     this.printUsage();
                  }
               } else {
                  this.printUsage();
               }
            }
         } else if (args.length == 2) {
            Iterator var2 = Client.instance.waypointManager.getWaypoints().iterator();

            me.skidsense.management.waypoints.Waypoint waypoint;
            do {
               if (!var2.hasNext()) {
                  ChatUtil.printChatwithPrefix("§7No Waypoint under the name §c" + args[1] + "§7 was found.");
                  return;
               }

               waypoint = (me.skidsense.management.waypoints.Waypoint)var2.next();
            } while(!waypoint.getName().equalsIgnoreCase(args[1]));

            Client.instance.waypointManager.deleteWaypoint(waypoint);
            ChatUtil.printChatwithPrefix("§7Waypoint §c" + args[1] + "§7 has been removed.");
         } else {
            this.printUsage();
         }
      } else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("clear")) {
    	  ChatUtil.printChatwithPrefix("§7Waypoint removed.");
    	  Client.instance.waypointManager.clearWaypoint();
         
      }else {
    	  this.printUsage();
	}
   }

   public String getUsage() {
      return "add/del <name> or add <name> <x> <y> <z>";
   }
}
