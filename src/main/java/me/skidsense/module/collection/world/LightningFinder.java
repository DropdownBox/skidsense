package me.skidsense.module.collection.world;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.ChatUtil;

import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;

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
			        ChatUtil.printChatwithPrefix("Lightning X:" + entityx + " Y:" + entityy + " Z:" + entityz);
				}
		}
	}
}