package me.skidsense.module.collection.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

class BlockData {
    public BlockPos position;
    public EnumFacing face;

    public BlockData(BlockPos position, EnumFacing face) {
        this.position = position;
        this.face = face;
    }
}

