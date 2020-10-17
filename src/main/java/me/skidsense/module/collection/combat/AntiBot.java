package me.skidsense.module.collection.combat;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Mod {
	public Mode<Enum> mode = new Mode("Mode", "Mode", (Enum[]) AntiMode.values(), (Enum) AntiMode.WatchDog);
	public static ArrayList<EntityPlayer> nigbot = new ArrayList<>();
	public static ArrayList<EntityPlayer> whitepig = new ArrayList<>();

	public AntiBot() {
		super("Anti Bot", new String[]{"AntiBot"},ModuleType.Fight);
		//this.addValues(this.mode);
	}

	@Sub
	public void onUpdate(EventPreUpdate event) {
		this.setSuffix(this.mode.getValue());
		if (mode.getValue() == AntiMode.WatchDog) {
			for (Object entities : mc.theWorld.loadedEntityList) {
				if (entities instanceof EntityPlayer) {
					EntityPlayer entity = (EntityPlayer) entities;
					if (entity != mc.thePlayer) {
						if (mc.thePlayer.getDistanceToEntity(entity) < 20) {
							if (!entity.getDisplayName().getFormattedText().contains("§c")
									|| entity.getDisplayName().getFormattedText().toLowerCase().contains("[npc]")
									|| entity.getDisplayName().getFormattedText().toLowerCase().contains("§r")) {
								nigbot.add(entity);
								whitepig.add(entity);
							}
						}
					}
					if (nigbot.contains(entity)) {
						nigbot.remove(entity);
					}
				}
			}
			for (Entity entity : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
				if (entity instanceof EntityPlayer) {
					EntityPlayer ent;
					EntityPlayer entityPlayer = ent = (EntityPlayer) entity;
					if (entityPlayer == Minecraft.getMinecraft().thePlayer) {
						continue;
					}
					if (!ent.isInvisible() || ent.ticksExisted <= 105) {
						continue;
					}

					ent.setInvisible(false);
					Minecraft.getMinecraft().theWorld.removeEntity(ent);
				}
			}
		}
	}

	public boolean isInGodMode(Entity entity) {
		if (this.isEnabled()) {
			if (this.mode.getValue() == AntiMode.WatchDog) {
				if (entity.ticksExisted <= 25) {
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}

	public boolean isServerBot(Entity entity) {
		if (this.isEnabled()) {
			if (this.mode.getValue() == AntiMode.WatchDog) {
				if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible()
						&& !entity.getDisplayName().getFormattedText().toLowerCase().contains("[npc]")) {
					if(isInGodMode(entity))
					{
						return true;
					}
					return false;
				}
				return true;
			}

			if (this.mode.getValue() == AntiMode.Mineplex) {
				for (Object object : this.mc.theWorld.playerEntities) {
					EntityPlayer entityPlayer = (EntityPlayer) object;
					if (entityPlayer == null || entityPlayer == this.mc.thePlayer
							|| !entityPlayer.getName().startsWith("Body #") && entityPlayer.getMaxHealth() != 20.0f)
						continue;
					return true;
				}
			}
		}
		return false;
	}

	static enum AntiMode {
		WatchDog, Mineplex;
	}

	@Override
	public void onEnable() {
		nigbot.clear();
	}

	@Override
	public void onDisable() {
		nigbot.clear();
	}
}
