package me.skidsense.management.authentication;

import java.io.File;
import java.security.Key;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;

import net.minecraft.util.ResourceLocation;

public class Crypto {
   public static String encrypt(Key key, String text) throws Exception {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(1, key);
      byte[] encrypted = cipher.doFinal(text.getBytes());
      byte[] encryptedValue = Base64.encodeBase64(encrypted);
      return new String(encryptedValue);
   }

   public static String decrypt(Key key, String text) throws Exception {
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(2, key);
      byte[] decodedBytes = Base64.decodeBase64(text.getBytes());
      byte[] original = cipher.doFinal(decodedBytes);
      return new String(original);
   }

   public static String decryptPrivate(String str) {
      try {
         return decrypt(AuthenticationUtil.getDecrypt(), str);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String decryptPublic(String str) {
      try {
         return decrypt(AuthenticationUtil.getSecret(), str);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String decryptPrivateNew(String str) {
      try {
         return decrypt(AuthenticationUtil.getDecryptNew(), str);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String decryptPublicNew(String str) {
      try {
         return decrypt(AuthenticationUtil.getSecretNew(), str);
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static byte[] getUserKey(int size) {
      byte[] ret = new byte[size];

      for(int i = 0; i < size; ++i) {
         String yourMom = new String(new byte[]{77 , 97 , 82 , 103 , 69 , 108 , 101 , 75 , 69 , 89 , 70 , 108 , 117 , 120 , 67 , 108 , 105 , 101 , 110 , 116 , 81 , 117 , 105 , 99 , 107 , 108 , 121 , 68 , 101 , 98 , 117 , 103 , 65 , 122 , 117 , 114 , 101 , 119 , 97 , 114 , 101 , 76 , 66 , 86 , 52 , 83 , 107 , 105 , 108 , 108 , 83 , 108 , 111 , 119 , 108 , 121 , 101 , 90 , 104 , 90 , 88 , 116 , 84 , 79 , 79 , 72 , 38 , 56 , 83 , 111 , 101 , 50 , 73 , 118 , 80 , 116 , 105 , 116 , 68 , 109 , 49 , 56 , 84 , 115 , 84 , 76 , 111 , 111 , 101 , 33 , 50 , 117 , 75 , 88 , 102 , 87 , 55 , 78 , 77 , 55 , 57 , 54 , 69 , 118 , 53 , 115 , 101 , 52 , 36 , 97 , 89 , 109 , 78 , 33 , 65 , 122 , 64 , 83 , 72 , 38});
         ret[i] = (byte)(yourMom.split("(?<=\\G.{4})")[i].hashCode() % 256);
      }

      return ret;
   }

   public static byte[] getUserKeySet(int size) {
      byte[] ret = new byte[size];

      for(int i = 0; i < size; ++i) {
         String str = new String(new byte[]{107 , 97 , 119 , 97 , 105 , 105 , 64 , 118 , 97 , 116 , 105 , 46 , 99 , 99 , 76 , 66 , 86 , 52 , 83 , 107 , 105 , 108 , 108 , 65 , 122 , 117 , 114 , 101 , 119 , 97 , 33 , 54 , 113 , 84 , 103 , 70 , 84 , 70 , 86 , 110 , 106 , 114 , 119 , 80 , 112 , 72 , 68 , 74 , 90 , 116 , 50 , 70 , 97 , 68 , 103 , 66 , 51 , 108 , 101 , 121 , 71 , 72 , 79 , 33 , 35 , 102 , 53 , 64 , 74 , 122 , 85 , 38 , 66 , 110 , 90 , 81 , 119 , 88 , 88 , 115 , 117 , 65 , 112 , 122 , 97 , 81 , 97 , 53 , 74 , 35 , 84 , 86 , 114 , 119 , 114 , 101 , 104 , 97 , 102 , 100 , 115 , 103 , 101 , 103 , 100 , 102 , 104 , 103 , 102 , 100 , 104 , 102 , 100});
         ret[i] = (byte)(str.split("(?<=\\G.{4})")[i].hashCode() % 256);
      }

      return ret;
   }

   public static byte[] getUserKeyOLD(int size) {
      byte[] ret = new byte[size];

      for(int i = 0; i < size; ++i) {
         String yourMom = new String(new byte[]{70 , 114 , 82 , 87 , 36 , 83 , 102 , 99 , 64 , 69 , 66 , 111 , 54 , 53 , 84 , 73 , 76 , 38 , 107 , 107 , 79 , 113 , 107 , 99 , 101 , 57 , 121 , 81 , 118 , 52 , 80 , 68 , 85 , 52 , 89 , 111 , 82 , 56 , 65 , 86 , 116 , 51 , 86 , 55 , 49 , 117 , 49 , 73 , 85 , 67 , 101 , 114 , 94 , 76 , 54 , 89 , 90 , 50 , 118 , 107 , 88 , 79 , 64 , 79 , 103 , 54 , 33 , 103 , 81 , 66 , 35 , 74 , 112 , 52 , 114 , 75 , 37 , 49 , 122 , 53 , 99 , 99 , 64 , 110 , 64 , 111 , 51 , 77 , 109 , 56 , 102 , 117 , 66 , 101 , 117 , 94 , 74 , 89});
         ret[i] = (byte)(yourMom.split("(?<=\\G.{4})")[i].hashCode() % 256);
      }

      return ret;
   }

   public static byte[] getUserKeySetOLD(int size) {
      byte[] ret = new byte[size];

      for(int i = 0; i < size; ++i) {
         String str = new String(new byte[]{65, 52, 51, 115, 49, 65, 83, 68, 97, 45, 97, 115, 100, 97, 51, 50, 61, 50, 61, 51, 102, 115, 102, 50, 52, 97, 83, 65, 68, 65, 109, 79, 80, 43, 45, 97, 69, 122, 120, 49, 65, 83, 68, 77, 83, 43, 115, 97, 115, 100, 100, 97, 48, 45, 97, 57, 97, 117, 106, 115, 100, 48, 97, 45, 115, 97, 100, 48, 57, 97, 115, 95, 65, 83, 65, 83, 68, 45, 97, 100, 48, 45, 97, 102, 107, 97, 115, 102, 45, 75, 70, 95, 97, 48, 65, 115, 45, 48, 100, 95, 74, 95, 95, 111, 111, 112, 53, 49, 119, 57, 49, 50});
         ret[i] = (byte)(str.split("(?<=\\G.{4})")[i].hashCode() % 256);
      }

      return ret;
   }

   public static Crypto.EnumOS getOSType() {
      String var0 = System.getProperty("os.name").toLowerCase();
      return var0.contains("win") ? Crypto.EnumOS.WINDOWS : (var0.contains("mac") ? Crypto.EnumOS.OSX : (var0.contains("solaris") ? Crypto.EnumOS.SOLARIS : (var0.contains("sunos") ? Crypto.EnumOS.SOLARIS : (var0.contains("linux") ? Crypto.EnumOS.LINUX : (var0.contains("unix") ? Crypto.EnumOS.LINUX : Crypto.EnumOS.UNKNOWN)))));
   }

   public static enum EnumOS {
      LINUX("LINUX", 0),
      SOLARIS("SOLARIS", 1),
      WINDOWS("WINDOWS", 2),
      OSX("OSX", 3),
      UNKNOWN("UNKNOWN", 4);

      private static final Crypto.EnumOS[] $VALUES = new Crypto.EnumOS[]{LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN};

      private EnumOS(String p_i1357_1_, int p_i1357_2_) {
      }
   }
}
