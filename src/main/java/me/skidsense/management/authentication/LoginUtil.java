package me.skidsense.management.authentication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.skidsense.util.FileUtils;

public class LoginUtil {
   private static final File LOGIN = FileUtils.getConfigFile("Data");

   public static void saveLogin(String encryptedUsername, String encryptedPassword) {
      List<String> fileContent = new ArrayList<String>();
      fileContent.add(encryptedUsername);
      fileContent.add(encryptedPassword);
      FileUtils.write(LOGIN, fileContent, true);
   }

   public static List<?> getLoginInformation() {
      return FileUtils.read(LOGIN);
   }
}
