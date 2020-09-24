package me.skidsense.management.command.impl;

import java.util.Iterator;

import me.skidsense.Client;
import me.skidsense.management.command.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class Target extends Command {
   public Target(String[] names, String description) {
      super(names, description);
   }

   public void fire(String[] args) {
      if (args != null) {
         if (args.length > 0) {
        	 Iterator<Entity> loadedentity = mc.theWorld.getLoadedEntityList().iterator();
     		while(loadedentity.hasNext()) {
     			Object o = loadedentity.next();
     			if (o instanceof EntityLivingBase) {
     				EntityLivingBase entityLivingBase = (EntityLivingBase)o;
     				if (entityLivingBase.getName().equalsIgnoreCase(args[0]) || entityLivingBase.getName().equals(args[0]))
     					Client.instance.viptarget = entityLivingBase;
     			}
     		}
         }

      }
   }

   public String getUsage() {
      return "Target <Name>";
   }
}
