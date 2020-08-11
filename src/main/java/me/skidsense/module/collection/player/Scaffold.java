package me.skidsense.module.collection.player;


import me.skidsense.color.Colors;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.value.Event;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.util.BlockUtil;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlaceInfo;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Scaffold extends Mod {

   private List<Block> badBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
   private BlockData blockData;
   public static boolean isPlaceTick = false;
   private double startY;
   public TimerUtil towerTimer = new TimerUtil();

   public Mode<Enum> mode = new Mode<Enum>("Mode", "Mode", ScaffoldMode.values(), ScaffoldMode.Hypixel);
   public static Option<Boolean> swing = new Option("Swing", "Swing", false);
   public static Option<Boolean> keeprots = new Option("KeepRotation", "KeepRotation", true);
   public static Option<Boolean> tower = new Option("Tower", "Tower", true);
   public static Option<Boolean> towermove = new Option("TowerMove", "TowerMove", true);
   public static Option<Boolean> down = new Option("Downwards", "Downwards", false);
   public static Option<Boolean> keepy = new Option("KeepY","KeepY",false);


   public Scaffold() {
      super("Scaffold", new String[]{}, ModuleType.Player);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.isPlaceTick = false;
      towerTimer.reset();
   }

   enum ScaffoldMode{
      Hypixel,Ncp
   }



   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.thePlayer != null) {
         startY = mc.thePlayer.posY;
      }
   }

   @Sub
   public void onEvent(EventPreUpdate e) {
      //if (e instanceof EventPreUpdate) {
         this.setSuffix(this.mode.getValue());
         int slot = this.getSlot();
            this.isPlaceTick = keeprots.getValue() ? blockData != null && slot != -1 : blockData != null && slot != -1 && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() == Blocks.air;
            if (slot == -1) {
               this.moveBlocksToHotbar();
               return;
            }
            this.blockData = this.getBlockData();
            if (this.blockData == null) {
               return;
            }

            // tower and towermove
//            if(mc.gameSettings.keyBindJump.isKeyDown() && tower.getValue() && (this.towermove.getValue() || !MoveUtil.isMoving()) && !mc.thePlayer.isPotionActive(Potion.jump)) {
//               if(towerTimer.hasReached(130)) {
//                  mc.thePlayer.jump();
//                  if (MoveUtil.isMoving()) {
//                     MoveUtil.setMotion(e, (MoveUtil.getBaseMoveSpeed() / 1.25));
//                  }
//                  towerTimer.reset();
//               } else if (towerTimer.getTime() >= 120) {
//                  mc.thePlayer.motionY = 0;
//               }
//            } else {
//               towerTimer.reset();
//            }

            if (this.isPlaceTick) {
               float yaw = e.yaw;
               boolean random = MoveUtil.isMoving();
               if (this.mode.getValue() == ScaffoldMode.Hypixel) {
                  // float speed = (float) ThreadLocalRandom.current().nextDouble(2, 3);
                  float targetYaw = 0;
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("north")) {
                     targetYaw = 0;
                  }
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("south")) {
                     targetYaw = 180;
                  }
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("west")) {
                     targetYaw = -90;
                  }
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("east")) {
                     targetYaw = 90;
                  }

                  float yawDifference = e.getYaw() - targetYaw;
                  yaw = e.getYaw() - (yawDifference / 3);
               } else if (this.mode.getValue() == ScaffoldMode.Ncp) {
                  yaw = 0;
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("north")) {
                     yaw = 0;
                  }
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("south")) {
                     yaw = 180;
                  }
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("west")) {
                     yaw = -90;
                  }
                  if (this.blockData.getFacing().getName().equalsIgnoreCase("east")) {
                     yaw = 90;
                  }
               }
               e.pitch = 85;
               e.yaw = yaw;
            }
          else if (slot != -1 && this.blockData != null) {
            final int currentSlot = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = slot;
            if (this.getPlaceBlock(this.blockData.getPosition(), this.blockData.getFacing())) {
               mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
            }
            mc.thePlayer.inventory.currentItem = currentSlot;
         }
   }

   @Sub
   public void onTower(EventMove e){
      if(mc.gameSettings.keyBindJump.isKeyDown() && tower.getValue() && (this.towermove.getValue() || !MoveUtil.isMoving()) && !mc.thePlayer.isPotionActive(Potion.jump)) {
         if(towerTimer.hasReached(130)) {
            mc.thePlayer.jump();
            if (MoveUtil.isMoving()) {
               MoveUtil.setMotion(e, (MoveUtil.getBaseMoveSpeed() / 1.25));
            }
            towerTimer.reset();
         } else if (towerTimer.getTime() >= 120) {
            mc.thePlayer.motionY = 0;
         }
      } else {
         towerTimer.reset();
      }
   }

   private boolean getPlaceBlock(final BlockPos pos, final EnumFacing facing) {
      final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      Vec3i data = this.blockData.getFacing().getDirectionVec();
      if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, new Vec3(this.blockData.getPosition()).addVector(0.5, 0.5, 0.5).add(new Vec3(data.getX() * 0.5, data.getY() * 0.5, data.getZ() * 0.5)))) {
         if(this.swing.getValue()) {
            mc.thePlayer.swingItem();
         } else {
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
         }
         return true;
      }
      return false;
   }

   private BlockData getBlockData() {
      final EnumFacing[] invert = { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST };
      double yValue = 0;
      if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !mc.gameSettings.keyBindJump.isKeyDown() && down.getValue() && mc.thePlayer.onGround) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
         yValue -= 1;
      }
      BlockPos playerpos = new BlockPos(mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0, yValue, 0);

      boolean tower = !this.towermove.getValue() && this.tower.getValue() && !MoveUtil.isMoving();
      if (!this.down.getValue() && this.keepy.getValue() && !tower) {
         playerpos = new BlockPos(new Vec3(mc.thePlayer.getPositionVector().xCoord, this.startY, mc.thePlayer.getPositionVector().zCoord)).offset(EnumFacing.DOWN);
      } else {
         this.startY = mc.thePlayer.posY;
      }
      List<EnumFacing> facingVals = Arrays.asList(EnumFacing.values());
      for (int i = 0; i < facingVals.size(); ++i) {
         if (mc.theWorld.getBlockState(playerpos.offset(facingVals.get(i))).getBlock().getMaterial() != Material.air) {
            return new BlockData(playerpos.offset(facingVals.get(i)), invert[facingVals.get(i).ordinal()]);
         }
      }
      final BlockPos[] addons = {
              new BlockPos(-1, 0, 0),
              new BlockPos(1, 0, 0),
              new BlockPos(0, 0, -1),
              new BlockPos(0, 0, 1)};
      for (int length2 = addons.length, j = 0; j < length2; ++j) {
         final BlockPos offsetPos = playerpos.add(addons[j].getX(), 0, addons[j].getZ());
         if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
            for (int k = 0; k < EnumFacing.values().length; ++k) {
               if (mc.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).getBlock().getMaterial() != Material.air) {
                  return new BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal()]);
               }
            }
         }
      }
      return null;
   }

   private int getSlot() {
      for (int k = 0; k < 9; ++k) {
         final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
         if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
            return k;
         }
      }
      return -1;
   }

   private void moveBlocksToHotbar() {
      boolean added = false;
      if (BlockUtil.getEmptyHotbarSlot() != -1) {
         for (int k = 0; k < mc.thePlayer.inventory.mainInventory.length; ++k) {
            if (k > 8 && !added) {
               final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
               if (itemStack != null && this.isValid(itemStack)) {
                  ItemUtils.shiftClick(k);
                  added = true;
               }
            }
         }
      }
   }

   public static boolean roi() {
      int v = 0;
      for(v = 0; v < 9; ++v) {
         tsn v = krd.aqo.xlh.xfm[v];
         if (v != null) {
            ++v;
         }
      }

      if (v == 8) {
         return true;
      } else {
         return false;
      }
   }



   private boolean isValid(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemBlock) {
         boolean isBad = false;

         ItemBlock block = (ItemBlock) itemStack.getItem();
         for (int i = 0; i < this.badBlocks.size(); i++) {
            if (block.getBlock().equals(this.badBlocks.get(i))) {
               isBad = true;
            }
         }

         return !isBad;
      }
      return false;
   }

   private int getBlockCount() {
      int count = 0;
      for (int k = 0; k < mc.thePlayer.inventory.mainInventory.length; ++k) {
         final ItemStack itemStack = mc.thePlayer.inventory.mainInventory[k];
         if (itemStack != null && this.isValid(itemStack) && itemStack.stackSize >= 1) {
            count += itemStack.stackSize;
         }
      }
      return count;
   }

   private class BlockData {
      private BlockPos blockPos;
      private EnumFacing enumFacing;

      private BlockData(final BlockPos blockPos, final EnumFacing enumFacing) {
         this.blockPos = blockPos;
         this.enumFacing = enumFacing;
      }

      private EnumFacing getFacing() {
         return this.enumFacing;
      }

      private BlockPos getPosition() {
         return this.blockPos;
      }
   }
}
