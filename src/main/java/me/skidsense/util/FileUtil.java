package me.skidsense.util;

import java.io.*;
import java.util.*;

import me.skidsense.Client;
import me.skidsense.management.FileManager;
import net.minecraft.client.Minecraft;

public final class FileUtil {
    public static FileUtil instance = new FileUtil();
    
	public void inputStreamToFile(InputStream inputStream, String targetFilePath) {
	      File file = new File(targetFilePath);

	      try {
	         OutputStream os = new FileOutputStream(file);
	         int bytesRead;
	         byte[] buffer = new byte[8192];

	         while((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
	            os.write(buffer, 0, bytesRead);
	         }

	         os.flush();
	         os.close();
	         inputStream.close();
	      } catch (Exception var7) {
	         var7.printStackTrace();
	      }

	   }
	
    public static List<String> read(final File inputFile) {

        final List<String> readContent = new ArrayList<String>();
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
            String str;
            while ((str = in.readLine()) != null) {
                readContent.add(str);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readContent;
    }

    public static void write(final File outputFile, final List<String> writeContent, final boolean overrideContent) {
        try {
            final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
            for (final String outputLine : writeContent) {
                out.write(String.valueOf(outputLine) + System.getProperty("line.separator"));
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File getConfigDir() {
        final File file = new File(Minecraft.getMinecraft().mcDataDir, FileManager.name);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
    private static File getConfigFolder() {
        final File Sigma = new File(Minecraft.getMinecraft().mcDataDir, FileManager.name);
        final File file = new File(Sigma, "Configs");
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
    public static File getConfig(final String name){
    	final File file = new File(getConfigFolder(), String.format("%s.txt", name));
        if (!file.exists()) {
            return null;
        }
        return file;
    }
    public static File getConfigFile(final String name) {
        final File file = new File(getConfigDir(), String.format("%s.txt", name));
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}

