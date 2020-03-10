
package me.skidsense.module.collection.world;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventChat;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import java.awt.Color;
import java.util.Random;

import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S45PacketTitle;

public class AutoL extends Module {
	private StringBuilder SBL = new StringBuilder();
	private Random RD =new Random(System.currentTimeMillis());
	private String[] knm = {"qwq","poi","QAQ","QWQ","qaq","awa","(๑>؂<๑）","(⑉• •⑉)‥♡","ヾ(●´∇｀●)ﾉ","POI~","(｡•́︿•̀｡)","poi~","-3-"};

	public AutoL() {
		super("Auto L", new String[]{"AutoL"}, ModuleType.Player);
		setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
	}

	@EventHandler
	private void onLChat(EventChat e) {
		if(KillAura.target != null){
			if(e.getMessage().contains(Minecraft.getMinecraft().thePlayer.getName()) && e.getMessage().contains(KillAura.target.getName())){
				SBL.append("[skidsense]");
				SBL.append(" ");
				SBL.append(knm[RD.nextInt(knm.length)]);
				SBL.append(" ");
				SBL.append(KillAura.target.getName());
				SBL.append("你好废物");
				ChatUtil.sendChat_NoFilter(SBL.toString());
			}
		}
	}
}

