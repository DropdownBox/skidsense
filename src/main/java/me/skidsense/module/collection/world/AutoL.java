
package me.skidsense.module.collection.world;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventChatRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class AutoL extends Mod {
	public static ArrayList<String> fuckTextArrayList = new ArrayList<String>();
	private Random RD =new Random(System.currentTimeMillis());
	private String[] knm = {"qwq","poi","QAQ","QWQ","qaq","awa","(๑>؂<๑）","(⑉• •⑉)‥♡","ヾ(●´∇｀●)ﾉ","POI~","(｡•́︿•̀｡)","poi~","-3-"};
	private EntityLivingBase kids;

	public AutoL() {
		super("Auto L", new String[]{"AutoL"}, ModuleType.Player);
		setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
	}

	@Sub
	private void onEventUpdate(EventPreUpdate e){
		if(KillAura.target != null){
			kids = KillAura.target;
		}
	}

	@Sub
	private void onLChat(EventChatRecieve e) {
		if(kids != null){
			if(e.getMessage().contains(mc.thePlayer.getName()) && e.getMessage().contains(kids.getName())){
				{
					if (kids.getName().toLowerCase().contains("tw")) {
						ChatUtil.sendChat_NoFilter(String.format("我就在台北，你他媽的坐捷运來打我啊？,%s", kids.getName()));
					} else {
						RD.nextInt(knm.length);
						Minecraft.getMinecraft().getNetHandler().sendpacketNoEvent(new C01PacketChatMessage(String.format(fuckTextArrayList.get(new Random(System.nanoTime()).nextInt(fuckTextArrayList.size())), kids.getName())));
					}
					kids = null;
				}
			}
		}
	}
}

