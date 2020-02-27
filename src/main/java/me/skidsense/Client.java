package me.skidsense;

import java.util.ArrayList;

import me.skidsense.hooks.value.Value;
import me.skidsense.management.AltManager;
import me.skidsense.management.CommandManager;
import me.skidsense.management.FileManager;
import me.skidsense.management.FriendManager;
import me.skidsense.management.ModuleManager;
import me.skidsense.management.fontRenderer.FontManager;
import me.skidsense.module.Module;
import me.skidsense.module.collection.visual.TabGUI;
import me.skidsense.util.ChatUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class Client {

	public static Minecraft mc = Minecraft.getMinecraft();
	public static String clientName = "Exusiai";
	public static Client instance = new Client();
	private static ModuleManager modulemanager;
	private CommandManager commandmanager;
	private AltManager altmanager;
	private FriendManager friendmanager;
	public static FontManager fontMgr;
	public static FontManager fontManager;
	private TabGUI tabui;
	public static ResourceLocation CLIENT_CAPE = new ResourceLocation("skidsense/cape.png");
	public static final ArrayList<ResourceLocation> gifLocations = new ArrayList<ResourceLocation>();

	public void initiate() {
		fontManager = fontMgr = new FontManager();
		this.commandmanager = new CommandManager();
		this.commandmanager.init();
		this.friendmanager = new FriendManager();
		this.friendmanager.init();
		this.tabui = new TabGUI();
		this.tabui.init();
		this.modulemanager = new ModuleManager();
		this.modulemanager.init();
		this.altmanager = new AltManager();
		AltManager.init();
		AltManager.setupAlts();
		FileManager.init();
	}

	public static ModuleManager getModuleManager() {
		return modulemanager;
	}

	public CommandManager getCommandManager() {
		return this.commandmanager;
	}

	public AltManager getAltManager() {
		return this.altmanager;
	}

	public void shutDown() {
		String values = "";
		instance.getModuleManager();
		for (Module m : ModuleManager.getModules()) {
			for (Value v : m.getValues()) {
				values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
			}
		}
		FileManager.save("Values.txt", values, false);
		String enabled = "";
		instance.getModuleManager();
		for (Module m : ModuleManager.getModules()) {
			if (!m.isEnabled()) continue;
			enabled = String.valueOf(enabled) + String.format("%s%s", m.getName(), System.lineSeparator());
		}
		FileManager.save("Enabled.txt", enabled, false);
	}


	public static void LoadGif() {
		for (int i = 0; i < 273; i++) {
			gifLocations.add(new ResourceLocation("skidsense/gif/bitchkody " + String.valueOf(i) + ".png"));
		}
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

	public static void sendMessageOLD(String msg) {
		Object[] arrobject = new Object[2];
		arrobject[0] = (Object) ((Object) EnumChatFormatting.BLUE) + "skidsense" + (Object) ((Object) EnumChatFormatting.GRAY) + ": ";
		arrobject[1] = msg;
		mc.thePlayer.addChatMessage(new ChatComponentText(String.format("%s%s", arrobject)));
	}

	public static void sendMessage(String message) {
		new ChatUtil.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
	}

	public static void sendMessageWithoutPrefix(String message) {
		new ChatUtil.ChatMessageBuilder(false, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
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

}
