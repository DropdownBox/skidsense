package me.skidsense.management;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.io.PrintWriter;
import java.io.FileWriter;
import me.skidsense.Client;
import me.skidsense.SplashProgress;
import me.skidsense.alt.Alt;
import me.skidsense.module.collection.world.AutoL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

import java.io.File;

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
                final PrintWriter printWriter = new PrintWriter(new FileWriter(FileManager.LASTALT));
                printWriter.println();
                printWriter.close();
            }
            else if (FileManager.LASTALT.exists()) {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(FileManager.LASTALT));
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    if (s.contains("\t")) {
                        s = s.replace("\t", "    ");
                    }
                    if (s.contains("    ")) {
                        final String[] parts = s.split("    ");
                        final String[] account = parts[1].split(":");
                        if (account.length == 2) {
                            Client.instance.getAltManager().setLastAlt(new Alt(account[0], account[1], parts[0]));
                        }
                        else {
                            String pw = account[1];
                            for (int i = 2; i < account.length; ++i) {
                                pw = String.valueOf(pw) + ":" + account[i];
                            }
                            Client.instance.getAltManager().setLastAlt(new Alt(account[0], pw, parts[0]));
                        }
                    }
                    else {
                        final String[] account2 = s.split(":");
                        if (account2.length == 1) {
                            Client.instance.getAltManager().setLastAlt(new Alt(account2[0], ""));
                        }
                        else if (account2.length == 2) {
                            Client.instance.getAltManager().setLastAlt(new Alt(account2[0], account2[1]));
                        }
                        else {
                            String pw2 = account2[1];
                            for (int j = 2; j < account2.length; ++j) {
                                pw2 = String.valueOf(pw2) + ":" + account2[j];
                            }
                            Client.instance.getAltManager().setLastAlt(new Alt(account2[0], pw2));
                        }
                    }
                }
                bufferedReader.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }
    
    public static void saveLastAlt() {
        try {
            final PrintWriter printWriter = new PrintWriter(FileManager.LASTALT);
            final Alt alt = Client.instance.getAltManager().getLastAlt();
            if (alt != null) {
                if (alt.getMask().equals("")) {
                    printWriter.println(String.valueOf(alt.getUsername()) + ":" + alt.getPassword());
                }
                else {
                    printWriter.println(String.valueOf(alt.getMask()) + "    " + alt.getUsername() + ":" + alt.getPassword());
                }
            }
            printWriter.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadAlts() {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(FileManager.ALT));
            if (!FileManager.ALT.exists()) {
                final PrintWriter printWriter = new PrintWriter(new FileWriter(FileManager.ALT));
                printWriter.println();
                printWriter.close();
            }
            else if (FileManager.ALT.exists()) {
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    if (s.contains("\t")) {
                        s = s.replace("\t", "    ");
                    }
                    if (s.contains("    ")) {
                        final String[] parts = s.split("    ");
                        final String[] account = parts[1].split(":");
                        if (account.length == 2) {
                            Client.instance.getAltManager();
                            AltManager.getAlts().add(new Alt(account[0], account[1], parts[0]));
                        }
                        else {
                            String pw = account[1];
                            for (int i = 2; i < account.length; ++i) {
                                pw = String.valueOf(pw) + ":" + account[i];
                            }
                            Client.instance.getAltManager();
                            AltManager.getAlts().add(new Alt(account[0], pw, parts[0]));
                        }
                    }
                    else {
                        final String[] account2 = s.split(":");
                        if (account2.length == 1) {
                            Client.instance.getAltManager();
                            AltManager.getAlts().add(new Alt(account2[0], ""));
                        }
                        else if (account2.length == 2) {
                            try {
                                Client.instance.getAltManager();
                                AltManager.getAlts().add(new Alt(account2[0], account2[1]));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            String pw2 = account2[1];
                            for (int j = 2; j < account2.length; ++j) {
                                pw2 = String.valueOf(pw2) + ":" + account2[j];
                            }
                            Client.instance.getAltManager();
                            AltManager.getAlts().add(new Alt(account2[0], pw2));
                        }
                    }
                }
            }
            bufferedReader.close();
        }
        catch (Exception ex) {}
    }
    
    public static void saveAlts() {
        try {
            final PrintWriter printWriter = new PrintWriter(FileManager.ALT);
            Client.instance.getAltManager();
            for (final Alt alt : AltManager.getAlts()) {
                if (alt.getMask().equals("")) {
                    printWriter.println(String.valueOf(alt.getUsername()) + ":" + alt.getPassword());
                }
                else {
                    printWriter.println(String.valueOf(alt.getMask()) + "    " + alt.getUsername() + ":" + alt.getPassword());
                }
            }
            printWriter.close();
        }
        catch (FileNotFoundException e) {
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
            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));//new涓�涓狟ufferedReader瀵硅薄锛屽皢鏂囦欢鍐呭璇诲彇鍒扮紦瀛�
            String s;
            while ((s = bReader.readLine()) != null) {
                //GuiMainMenu.AnnouncementList.add(s);
            }
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
