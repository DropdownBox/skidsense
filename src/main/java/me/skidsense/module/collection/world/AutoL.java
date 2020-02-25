
package me.skidsense.module.collection.world;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventChat;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class AutoL
extends Module
{
TimerUtil delay = new TimerUtil();
public static Mode mode;
public static ArrayList<String> AbuseText = new ArrayList<>();

public AutoL()
{
  super("AutoL", new String[] { "LLL,LLLLL" }, ModuleType.Player);
  setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
  mode = new Mode("LMode", "lmode", Lmode.values(), Lmode.Normal);
  addValues(new Value[] { mode});
}

@EventHandler
private void handleRequest(EventChat e)
{
  setSuffix(mode.getValue());
  Random r = new Random();
  if ((KillAura.target != null) && 
    (mode.getValue() == Lmode.Normal) && 
    (e.getMessage().contains(Minecraft.getMinecraft().thePlayer.getName())) && (e.getMessage().contains(KillAura.target.getName())) && (this.delay.isDelayComplete(100L)))
  {
    e.setCancelled(false);
      if (KillAura.target != null)
      {
    	  String[] knm = {"qwq","poi","QAQ","QWQ","qaq","awa","(๑>؂<๑）","(⑉• •⑉)‥♡","ヾ(●´∇｀●)ﾉ","POI~","(｡•́︿•̀｡)","poi~","-3-"};
    	  Minecraft.getMinecraft().thePlayer.sendChatMessage("[skidsense] "+knm[random.nextInt(knm.length)]+" "+String.format(AbuseText.get(random.nextInt(AbuseText.size())),KillAura.target.getName())+" buy skidsense client to skidsense，pub");
        //Minecraft.thePlayer.sendChatMessage(KillAura.target.getName()+"，buy skidsense client to skidsense，pub");
    	  Minecraft.getMinecraft().thePlayer.sendChatMessage("/wdr " + KillAura.target.getName() + " ka fly speed reach ac bhop");
      }
  }
}

@EventHandler
private void Antimama(EventPacketRecieve e) {
    if (e.getPacket() instanceof S27PacketExplosion) {
    	Client.notif("检测到S27 Cancelled");
        e.setCancelled(true);
}
}
public void onEnable() {
	
}
public static void OH() {
	 try {
			URL realUrl = new URL("https://kody.cf/suckkid/fuckkodybitchkidss.txt");
			URLConnection connection = realUrl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36");
			connection.setConnectTimeout(28000);
			connection.setReadTimeout(28000);
			connection.connect();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));//new一个BufferedReader对象，将文件内容读取到缓存
	        String HWIDList;
	        while ((HWIDList =bReader.readLine()) != null) {
	        	AbuseText.add(HWIDList);
	        }
	        bReader.close();
		      List<String> HWID = AbuseText;
			} catch (Exception e) {
			}
		}
}
enum Lmode {
	Normal;
}


