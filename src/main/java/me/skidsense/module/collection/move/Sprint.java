package me.skidsense.module.collection.move;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPacketRecieve;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import java.awt.*;

public class Sprint
        extends Mod {
    private Option<Boolean> omni = new Option<Boolean>("Omni", "Omni", true);
    private Option<Boolean> keepsprint = new Option<Boolean>("KeepSprint", "keepsprint", true);

    public Sprint() {
        super("Auto Sprint", new String[]{"run", "sprint", "autosprint"}, ModuleType.Move);
        this.setColor(new Color(158, 205, 125).getRGB());
        //this.addValues(this.omni,keepsprint);
    }

    @Sub
    private void onUpdate(EventPreUpdate event) {

        mc.thePlayer.setSprinting(this.canSprint());

    }

    private boolean canSprint() {
        return !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && (omni.getValue() ? MoveUtil.isMoving() : mc.thePlayer.moveForward > 0);
    }

    @Sub
    public void onEvent(EventPacketRecieve e) {
        if (keepsprint.getValue()) {
            try {
                if (e.getPacket() instanceof C0BPacketEntityAction) {
                    C0BPacketEntityAction packet = (C0BPacketEntityAction) e.getPacket();
                    if (packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        e.setCancelled(true);
                    }
                }
            } catch (ClassCastException ignored) {
            }
        }

    }
}

