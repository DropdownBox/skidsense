package me.skidsense.module.collection.visual;

import com.google.common.collect.Lists;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.events.EventRenderBlock;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Xray
        extends Module {
    public static List<Integer> KEY_IDS = Lists.newArrayList(10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154);
    private ArrayList<BlockPos> toRender = new ArrayList();
    private ArrayList<BlockPos> rsPosToRender = new ArrayList();
    private ArrayList<BlockPos> ironPosToRender = new ArrayList();
    private ArrayList<BlockPos> diaPosToRender = new ArrayList();
    private ArrayList<BlockPos> godPosToRender = new ArrayList();
    private ArrayList<BlockPos> coalPosToRender = new ArrayList();
    public static ArrayList<Integer> blockID = new ArrayList();
    private Numbers<Double> blockLimit = new Numbers<Double>("BlockLimit", "BlockLimit", 0.0, 0.0, 1000.0, 5.0);
    private Numbers<Double> range = new Numbers<Double>("Range", "Range", 0.0, 0.0, 1000.0, 5.0);
    public static Option<Boolean> gold = new Option<Boolean> ("Gold", "Gold", true);
    public static Option<Boolean> dia = new Option<Boolean> ("Diamond", "Diamond", true);
    public static Option<Boolean> rs = new Option<Boolean> ("Redstone", "Redstone", true);
    public static Option<Boolean> iron = new Option<Boolean> ("Iron", "Iron", true);
    public static Option<Boolean> coal = new Option<Boolean> ("Coal", "Coal", true);
    public Xray() {
        super("Xray", new String[]{}, ModuleType.Visual);
        addValues(blockLimit,range,dia,gold,rs,iron,coal);
    }
    @Override
    public void onEnable(){
        mc.renderGlobal.loadRenderers();
    }
    @Override
    public void onDisable(){
        mc.renderGlobal.loadRenderers();
    }
    boolean canRender(BlockPos blockPos){
        BlockPos[] addon = new BlockPos[]{
                //** there is only 6 pos legal in hypixel now...
                new BlockPos(0,0,1),
                new BlockPos(0,0,-1),
                new BlockPos(1,0,0),
                new BlockPos(-1,0,0),
                new BlockPos(0,1,0),
                new BlockPos(0,-1,0)
        };
        int i = 0;
        while(i <addon.length){
            int addPosX = addon[i].getX();
            int addPosY = addon[i].getY();
            int addPosZ = addon[i].getZ();
            Block checkBlock = mc.theWorld.getBlockState(blockPos.add(addPosX,addPosY,addPosZ)).getBlock();
            if(checkBlock instanceof BlockAir || checkBlock instanceof BlockLiquid)
                return true;
            i++;
        }
        return false;
    }
    @EventHandler
    public void onRenderBlock(EventRenderBlock event){
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if(gold.getValue().booleanValue()&&getDistanceToPos(pos)<=range.getValue()&&godPosToRender.size()<=blockLimit.getValue()&&!godPosToRender.contains(pos)&&Block.getIdFromBlock(event.getBlock())==14&&canRender(pos)){
            godPosToRender.add(pos);
        }
        if(dia.getValue().booleanValue()&&getDistanceToPos(pos)<=range.getValue()&&godPosToRender.size()<=blockLimit.getValue()&&!diaPosToRender.contains(pos)&&Block.getIdFromBlock(event.getBlock())==56&&canRender(pos)){
            diaPosToRender.add(pos);
        }
        if(rs.getValue().booleanValue()&&getDistanceToPos(pos)<=range.getValue()&&godPosToRender.size()<=blockLimit.getValue()&&!rsPosToRender.contains(pos)&&(Block.getIdFromBlock(event.getBlock())==73||Block.getIdFromBlock(event.getBlock())==74)&&canRender(pos)){
            rsPosToRender.add(pos);
        }
        if(iron.getValue().booleanValue()&&getDistanceToPos(pos)<=range.getValue()&&godPosToRender.size()<=blockLimit.getValue()&&!ironPosToRender.contains(pos)&&Block.getIdFromBlock(event.getBlock())==15&&canRender(pos)){
            ironPosToRender.add(pos);
        }
        if(coal.getValue().booleanValue()&&getDistanceToPos(pos)<=range.getValue()&&godPosToRender.size()<=blockLimit.getValue()&&!coalPosToRender.contains(pos)&&Block.getIdFromBlock(event.getBlock())==16&&canRender(pos)){
            coalPosToRender.add(pos);
        }
        int z = 0;
        while (z < this.godPosToRender.size()) {
            BlockPos pos_1 = this.godPosToRender.get(z);
            int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
            if (!canRender(pos_1)||getDistanceToPos(pos_1)>range.getValue()||id!=14)
                this.godPosToRender.remove(z);

            ++z;
        }
        int h = 0;
        while (h < this.coalPosToRender.size()) {
            BlockPos pos_1 = this.coalPosToRender.get(h);
            int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
            if (!canRender(pos_1)||getDistanceToPos(pos_1)>range.getValue()||id!=16)
                this.coalPosToRender.remove(h);
            
            ++h;
        }
        int a = 0;
        while (a < this.ironPosToRender.size()) {
            BlockPos pos_1 = this.ironPosToRender.get(a);
            int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
            if (!canRender(pos_1)||getDistanceToPos(pos_1)>range.getValue()||id!=15)
                this.ironPosToRender.remove(a);

            ++a;
        }
        int b = 0;
        while (b < this.rsPosToRender.size()) {
            BlockPos pos_1 = this.rsPosToRender.get(b);
            int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
            if (!canRender(pos_1)||getDistanceToPos(pos_1)>range.getValue()||id!=73&&id!=74)
                this.rsPosToRender.remove(b);

            ++b;
        }
        int c = 0;
        while (c < this.diaPosToRender.size()) {
            BlockPos pos_1 = this.diaPosToRender.get(c);
            int id = Block.getIdFromBlock(this.mc.theWorld.getBlockState(pos_1).getBlock());
            if (!canRender(pos_1)||getDistanceToPos(pos_1)>range.getValue()||id!=56) {
                this.diaPosToRender.remove(c);
            }
            ++c;
        }    
    }
    
    @EventHandler
    public void onRender(EventRender3D event) {
        for (BlockPos pos : this.godPosToRender) {
            this.renderBlock(pos, Color.ORANGE,0.2f);
        }
        for (BlockPos pos : this.coalPosToRender) {
            this.renderBlock(pos, Color.BLACK,0.2f);
        }
        for (BlockPos pos : this.rsPosToRender) {
            this.renderBlock(pos, Color.RED,0.2f);
        }
        for (BlockPos pos : this.ironPosToRender) {
            this.renderBlock(pos, Color.GRAY,0.2f);
        }
        for (BlockPos pos : this.diaPosToRender) {
            this.renderBlock(pos, Color.CYAN,0.2f);
        }
    }
    public float getDistanceToPos(BlockPos blockPos)
    {
        float f = (float)(blockPos.getX() - mc.thePlayer.posX);
        float f1 = (float)(blockPos.getY() - mc.thePlayer.posY);
        float f2 = (float)(blockPos.getZ() - mc.thePlayer.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }
    public float getYawToPos(BlockPos pos) {
        double pX = mc.thePlayer.posX;
        double pZ = mc.thePlayer.posY;
        double eX = pos.getX()+0.5;
        double eZ = pos.getZ()+0.5;
        double dX = pX - eX;
        double dZ = pZ - eZ;
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        return (float) yaw;
    }
    private void renderBlock(BlockPos pos,Color color,float alpha) {
        double x = (double)pos.getX() - mc.getRenderManager().renderPosX;
        double y = (double)pos.getY() - mc.getRenderManager().renderPosY;
        double z = (double)pos.getZ() - mc.getRenderManager().renderPosZ;
        RenderUtil.drawSolidBlockESP(x, y, z, color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
	
    public static boolean containsID(int id) {
        return blockID.contains(id);
    }
}