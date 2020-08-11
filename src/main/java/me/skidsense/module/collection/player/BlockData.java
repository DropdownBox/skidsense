package me.skidsense.module.collection.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

class BlockData {
    public BlockPos pos;
    public EnumFacing face;

    public BlockData(BlockPos pos, EnumFacing face) {
        this.pos = pos;
        this.face = face;
    }

    public String toString() {
        return "Pos:" + this.pos.getX() + ", " + this.pos.getY() + ", " + this.pos.getZ() + ". Face:" + (Object)this.face;
    }
}

