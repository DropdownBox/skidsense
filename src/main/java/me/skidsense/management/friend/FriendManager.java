package me.skidsense.management.friend;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.skidsense.util.FileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;

public class FriendManager {
   private static final File FRIEND_DIR = FileUtils.getConfigFile("Friends");
   public static ArrayList friendsList = new ArrayList();

   public static void start() {
      load();
      save();
   }

   public static void addFriend(String name, String alias) {
      friendsList.add(new Friend(name, alias));
      save();
   }

   public static String getAlias(String name) {
      String alias = null;
      Iterator var2 = friendsList.iterator();

      while(var2.hasNext()) {
         Friend friend = (Friend)var2.next();
         if (friend.name.equalsIgnoreCase(StringUtils.stripControlCodes(name))) {
            alias = friend.alias;
            break;
         }
      }

      return alias;
   }

   public static void removeFriend(String name) {
      Iterator var1 = friendsList.iterator();

      while(var1.hasNext()) {
         Friend friend = (Friend)var1.next();
         if (friend.name.equalsIgnoreCase(name)) {
            friendsList.remove(friend);
            break;
         }
      }

      save();
   }

   public static boolean isFriend(String name) {
      boolean isFriend = false;
      Iterator var2 = friendsList.iterator();

      while(var2.hasNext()) {
         Friend friend = (Friend)var2.next();
         if (friend.name.equalsIgnoreCase(StringUtils.stripControlCodes(name))) {
            isFriend = true;
            break;
         }
      }

      if (Minecraft.getMinecraft().thePlayer.getGameProfile().getName() == name) {
         isFriend = true;
      }

      return isFriend;
   }

   public static void load() {
      friendsList.clear();
      List fileContent = FileUtils.read(FRIEND_DIR);
      Iterator var1 = fileContent.iterator();

      while(var1.hasNext()) {
         String line = (String)var1.next();

         try {
            String[] split = line.split(":");
            String name = split[0];
            String alias = split[1];
            addFriend(name, alias);
         } catch (Exception var6) {
            ;
         }
      }

   }

   public static void save() {
      List fileContent = new ArrayList();
      Iterator var1 = friendsList.iterator();

      while(var1.hasNext()) {
         Friend friend = (Friend)var1.next();
         fileContent.add(String.format("%s:%s", friend.name, friend.alias));
      }

      FileUtils.write(FRIEND_DIR, fileContent, true);
   }
}
