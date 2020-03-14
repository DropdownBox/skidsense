package me.skidsense.management;

import java.io.*;
import java.util.*;

import java.net.URL;
import java.net.URLConnection;

import me.skidsense.Client;
import me.skidsense.SplashProgress;
import me.skidsense.alt.Alt;
import me.skidsense.module.collection.world.AutoL;
import me.skidsense.util.EncryptionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

import java.util.stream.Collectors;

public class FileManager
{
    public final static String name = "skidsense";
    private static File dir;
    private static final File ALT;
    private static final File LASTALT;
    private static final File hwid;
    
    static {
        final File mcDataDir = Minecraft.getMinecraft().mcDataDir;
        Client.instance.getClass();
        FileManager.dir = new File(mcDataDir, FileManager.name);
        ALT = getConfigFile("Alts");
        LASTALT = getConfigFile("LastAlt");
        hwid = getConfigFileEncrypt("Data");
    }
    
    public FileManager() {
        super();
    }
    
    public static void loadLastAlt() {
        try {
            if (!FileManager.LASTALT.exists()) {
                LASTALT.createNewFile();
                final PrintWriter printWriter = new PrintWriter(new FileWriter(FileManager.LASTALT));
                printWriter.println();
                printWriter.close();
            }
            else if (FileManager.LASTALT.exists()) {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(FileManager.LASTALT));
                String decrypted = EncryptionUtil.decrypt(bufferedReader.lines().collect(Collectors.joining("\n")));
                String[] de;
                if (decrypted != null) {
                    de = decrypted.split("\n")[0].split(":");
                    if(de.length == 2)
                        AltManager.lastAlt = new Alt(de[0],de[1]);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveLastAlt() {
        try {

            final BufferedWriter printWriter = new BufferedWriter(new FileWriter(FileManager.ALT));
            //Client.instance.getAltManager();
            final Alt alt = AltManager.lastAlt;
            String s = "\n";
            if(alt != null)
                s = String.format("%s:%s\n", alt.getUsername(), alt.getPassword());
            printWriter.write(Objects.requireNonNull(EncryptionUtil.encrypt(s)));
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadAlts() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(FileManager.ALT));
            if (!FileManager.ALT.exists()) {
                FileManager.ALT.createNewFile();
                final BufferedWriter printWriter = new BufferedWriter(new FileWriter(FileManager.ALT));
                printWriter.newLine();
                printWriter.close();
            }
            else if (FileManager.ALT.exists()) {
               String s = bufferedReader.lines().collect(Collectors.joining("\n"));
               s = EncryptionUtil.decrypt(s);
                String[] strings = new String[0];
                if (s != null) {
                    strings = s.split("\n");
                }
                for (int i = 0; i < strings.length; i++) {
                   String[] strings1 = strings[i].split(":");
                   AltManager.alts.add(new Alt(strings1[0],strings1[1]));
               }

            }
            bufferedReader.close();
        }
        catch (Exception ex) {}
    }
    
    public static void saveAlts() {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            final BufferedWriter printWriter = new BufferedWriter(new FileWriter(FileManager.ALT));
            //Client.instance.getAltManager();
            for (final Alt alt : AltManager.getAlts()) {
                stringBuffer.append(String.format("%s:%s\n", alt.getUsername(), alt.getPassword()));
            }
            printWriter.write(Objects.requireNonNull(EncryptionUtil.encrypt(stringBuffer.toString())));
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static File getConfigFile(final String name) {
        final File file = new File(FileManager.dir, String.format("%s.txt", name));
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException ex) {}
        }
        return file;
    }
    
    public static File getConfigFileEncrypt(final String name) {
        final File file = new File(FileManager.dir, String.format("%s.kody", name));
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException ex) {}
        }
        return file;
    }
    
    public static void init() {
    	SplashProgress.setProgress(7, "Initializing FileManager...");
        if (!FileManager.dir.exists()) {
            FileManager.dir.mkdir();
        }
        loadLastAlt();
        loadAlts();
        getAnnouncement();
    }
    
    public static void getAnnouncement() {
        try {
            URL realUrl = new URL("https://kody.cf/suckkid/nivialc.txt");
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36");
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.connect();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //while ((s = bReader.readLine()) != null) {
                //GuiMainMenu.AnnouncementList.add(s);
            //}
            bReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static List<String> read(final String file) {
        final List<String> out = new ArrayList<String>();
        try {
            if (!FileManager.dir.exists()) {
                FileManager.dir.mkdir();
            }
            final File f = new File(FileManager.dir, file);
            if (!f.exists()) {
                f.createNewFile();
            }
            Throwable t = null;
            try {
                final FileInputStream fis = new FileInputStream(f);
                try {
                    final InputStreamReader isr = new InputStreamReader(fis);
                    try {
                        final BufferedReader br = new BufferedReader(isr);
                        try {
                            String line = "";
                            while ((line = br.readLine()) != null) {
                                out.add(line);
                            }
                        }
                        finally {
                            if (br != null) {
                                br.close();
                            }
                        }
                        if (isr != null) {
                            isr.close();
                        }
                    }
                    finally {
                        if (t == null) {
                            final Throwable t2 = null;
                            t = t2;
                        }
                        else {
                            final Throwable t2 = null;
                            if (t != t2) {
                                t.addSuppressed(t2);
                            }
                        }
                        if (isr != null) {
                            isr.close();
                        }
                    }
                    if (fis != null) {
                        fis.close();
                        return out;
                    }
                }
                finally {
                    if (t == null) {
                        final Throwable t3 = null;
                        t = t3;
                    }
                    else {
                        final Throwable t3 = null;
                        if (t != t3) {
                            t.addSuppressed(t3);
                        }
                    }
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable t4 = null;
                    t = t4;
                }
                else {
                    final Throwable t4 = null;
                    if (t != t4) {
                        t.addSuppressed(t4);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
    
    public static void save(final String file, final String content, final boolean append) {
        try {
            final File f = new File(FileManager.dir, file);
            if (!f.exists()) {
                f.createNewFile();
            }
            Throwable t = null;
            try {
                final FileWriter writer = new FileWriter(f, append);
                try {
                    writer.write(content);
                }
                finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable t2 = null;
                    t = t2;
                }
                else {
                    final Throwable t2 = null;
                    if (t != t2) {
                        t.addSuppressed(t2);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
