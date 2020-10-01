package me.skidsense.module.collection.player;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPacketSend;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.ChatUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.TimerUtil;
import me.skidsense.util.pathfinding.CustomVec3;
import me.skidsense.util.pathfinding.PathfindingUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Teleport extends Mod {
	
	private final TimerUtil timer = new TimerUtil();
	private CustomVec3 target;
	private int stage;
	boolean tp;
	
	public Teleport() {
		super("Teleport", new String[] {"Teleport"}, ModuleType.Player);
	}

	@Override
	public void onEnable() {
		if (mc.thePlayer == null)
			return;
		if (Client.instance.viptarget == null) {
			return;	
		}
		this.stage = 0;
		tp = false;
			mc.getNetHandler().sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 0.17, mc.thePlayer.posZ, true));
			mc.getNetHandler().sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
					mc.thePlayer.posY + 0.06, mc.thePlayer.posZ, true));
			mc.thePlayer.stepHeight = 0.0f;
			mc.thePlayer.motionX = 0.0;
			mc.thePlayer.motionZ = 0.0;
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		EntityPlayerSP player = mc.thePlayer;
		mc.timer.timerSpeed = 1.0f;
		player.stepHeight = 0.625f;
		player.motionX = 0.0;
		player.motionZ = 0.0;
		super.onDisable();
	}
	
	@Sub
	public void onPacketSend(EventPacketSend eventPacketSend) {
		if (this.stage == 1 && !this.timer.delay(6000L) || true && !tp && eventPacketSend.getPacket() instanceof C03PacketPlayer) {
			eventPacketSend.setCancelled(true);
		}
	}
	
	@Sub
	public void onPacketRecieve(EventPacketRecieve eventPacketRecieve) {
		if (eventPacketRecieve.getPacket() instanceof S08PacketPlayerPosLook) {
			if (!this.tp) {
				this.tp = true;
			}
		}
	}
	
	@Sub
	public void onUpdate(EventPreUpdate eventPreUpdate) {
		if (tp) {
			this.setEnabled(false);
			mc.getNetHandler().sendpacketNoEvent(new C0CPacketInput(0.0f, 0.0f, true, true));
			double lastY = mc.thePlayer.posY, downY = 0;
			for (CustomVec3 vec3 : PathfindingUtils.computePath(
					new CustomVec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
					new CustomVec3(0, 180, 0))) {
				if (vec3.getY() < lastY) {
					downY += (lastY - vec3.getY());
				}
				if (downY > 2.5) {
					downY = 0;
					mc.getNetHandler().sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),
							vec3.getY(), vec3.getZ(), true));
				} else {
					mc.getNetHandler().sendpacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),
							vec3.getY(), vec3.getZ(), false));
				}
				lastY = vec3.getY();
			}
			ChatUtil.printChatwithPrefix("Teleported");
			mc.thePlayer.setPosition(this.target.getX(), this.target.getY(), this.target.getZ());
		}
	}
	
	@Sub
	public void onMove(EventMove eventMove) {
		MoveUtil.setSpeed(0.0);
		mc.thePlayer.motionY = 0.0;
		eventMove.setY(0);
	}
}
