package me.skidsense.hooks.events;


import me.skidsense.hooks.value.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Keir on 21/04/2017.
 * https://github.com/Refreezinq/DarkForge
 */
public class EventBlockRenderSide extends Event {

    private final IBlockAccess world;
    private final BlockPos pos;
    private final EnumFacing side;
    private boolean toRender;

    public EventBlockRenderSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
        this.world = world;
        this.pos = pos;
        this.side = side;
    }

	public IBlockAccess getWorld() {
		return world;
	}

	public BlockPos getPos() {
		return pos;
	}

	public EnumFacing getSide() {
		return side;
	}

	public boolean isToRender() {
		return toRender;
	}

	public void setToRender(boolean toRender) {
		this.toRender = toRender;
	}
}
