package me.skidsense.module.collection.world;

import me.skidsense.Client;
import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.ChatUtil;

import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.Vec3;

public class LightningFinder extends Mod {

	public LightningFinder() {
		super("Lightning Finder", new String[] {"LightningFinder"}, ModuleType.World);
	}

	@Sub
	public void onPacketRecieve(EventPacketRecieve eventPacketRecieve) {
		if(eventPacketRecieve.getPacket() instanceof S2CPacketSpawnGlobalEntity) {
			S2CPacketSpawnGlobalEntity s2c = (S2CPacketSpawnGlobalEntity) eventPacketRecieve.getPacket();
				if(s2c.func_149053_g() == 1) {
					int entityx = (int)((double)s2c.func_149051_d() / 32.0D);
			        int entityy = (int)((double)s2c.func_149050_e() / 32.0D);
			        int entityz = (int)((double)s2c.func_149049_f() / 32.0D);
			        ChatUtil.printChatwithPrefix("Lightning X:" + entityx + " Y:" + entityy + " Z:" + entityz+" , Waypoint Created");
			        int color = Colors.getColor((int)(255.0D * Math.random()), (int)(255.0D * Math.random()), (int)(255.0D * Math.random()));
			        Client.instance.waypointManager.createWaypoint("Lightning " + entityx + " " + entityy + " " + entityz, new Vec3(entityx, entityy + 1.0D, entityz), color, this.mc.isSingleplayer() ? "SinglePlayer" : this.mc.getCurrentServerData().serverIP);
				}
		}
	}
}