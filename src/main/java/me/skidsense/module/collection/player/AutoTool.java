package me.skidsense.module.collection.player;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventTick;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.BlockUtil;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {
	public AutoTool() {
		super("Auto Tool", new String[] {"AutoTool"},ModuleType.Player);
    }

	@EventHandler
	    public void onEvent(EventTick event) {
	        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
	            return;
	        }
	        if (mc.objectMouseOver == null) {
	            return;
	        }
	        BlockPos pos = mc.objectMouseOver.getBlockPos();
	        if (pos == null) {
	            return;
	        }
	        BlockUtil.updateTool(pos);
	    }
	}