package me.skidsense.module.collection.player;


import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRenderGui;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Scaffold extends Mod {

   private List<Block> invalid = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
           Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars,
           Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore,
           Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt,
           Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore,
           Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate,
           Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily,
           Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace,
           Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
   private BlockData blockData;

   public boolean isPlaceTick = false;
   int currentSlot;
   int slot;
   private double startY;
   public TimerUtil towerTimer = new TimerUtil();
   public Mode<ScaffoldMode> RotationMode = new Mode<ScaffoldMode>("RotationMode", "RotationMode", ScaffoldMode.values(),ScaffoldMode.Hypixel);
   public Option<Boolean> KeepRotation = new Option<Boolean>("KeepRotation", "KeepRotation", true);
   public Option<Boolean> tower = new Option<>("Tower", "Tower", true);
   public Option<Boolean> towermove = new Option<>("Towermove", "Towermove", true);
   public Option<Boolean> swing = new Option<>("Swing", "Swing", false);
   public Option<Boolean> keepY = new Option<>("KeepY", "KeepY", false);
   public Option<Boolean> down = new Option<Boolean>("Downwards", "Downwards", true);


   public Scaffold() {
      super("Scaffold", new String[]{"ScaffoldWalk"}, ModuleType.Player);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.isPlaceTick = false;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.thePlayer != null) {
         startY = mc.thePlayer.posY;
      }
   }

   @Sub
   public void onPreUpdate(EventPreUpdate e) {
      slot = this.getSlot();
      this.setSuffix(RotationMode.getValue());
      this.isPlaceTick = KeepRotation.getValue() ? blockData != null && slot != -1 : blockData != null && slot != -1 && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, -1, 0)).getBlock() == Blocks.air;
      if (getBlockCount() > 0 && slot == -1) {
         for (int i = 9; i < 36; ++i) {
            Item item;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    || !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                    .getItem()) instanceof ItemBlock)
                    || this.invalid.contains(((ItemBlock) item).getBlock())
                    || ((ItemBlock) item).getBlock().getLocalizedName().toLowerCase().contains("chest"))
               continue;
            this.swap(i, 7);
            break;
         }
         return;
      }
	  if(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && !mc.gameSettings.keyBindJump.isKeyDown() && down.getValue() && mc.thePlayer.onGround) {
		  KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
	  }
	  this.blockData = this.getBlockData();
      if (this.blockData == null) {
         return;
      }


      // tower and towermove
      if (mc.gameSettings.keyBindJump.isKeyDown() && tower.getValue() && (this.towermove.getValue() || !MoveUtil.isMoving()) && !mc.thePlayer.isPotionActive(Potion.jump)) {
    	  tower();
      }

      if (this.isPlaceTick) {
         float yaw = e.yaw;
         if (this.RotationMode.getValue() == ScaffoldMode.Hypixel) {
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
         } else if (this.RotationMode.getValue() == ScaffoldMode.NCP) {
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
         e.setPitch(85);
         e.setYaw(yaw);
         //Minecraft.getMinecraft().thePlayer.renderArmYaw = e.getYaw();
         Minecraft.getMinecraft().thePlayer.rotationYawHead = e.getYaw();
         Minecraft.getMinecraft().thePlayer.renderYawOffset = e.getYaw();
         //Minecraft.getMinecraft().thePlayer.renderArmPitch = e.getPitch();
      }
   }

   @Sub
   public void onPostUpdate(EventPostUpdate e){
      currentSlot = mc.thePlayer.inventory.currentItem;
      if(this.blockData != null){
         mc.thePlayer.inventory.currentItem = slot;
         if (this.getPlaceBlock(this.blockData.getPosition(), this.blockData.getFacing())) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
         }
         mc.thePlayer.inventory.currentItem = currentSlot;
      }
   }
   
   @Sub
   public void onRender(EventRenderGui e) {
       GlStateManager.enableBlend();
       RenderUtil.drawOutlinedString(String.valueOf(this.getBlockCount()), e.getResolution().getScaledWidth() / 2 - Scaffold.mc.fontRendererObj.getStringWidth(this.getBlockCount() + "") / 2, e.getResolution().getScaledHeight() / 2 + 10, getBlockColor(this.getBlockCount()));
       GlStateManager.disableBlend();
   }
   
   private int getBlockColor(int count) {
       float f = count;
       float f1 = 64;
       float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
       return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
   }
   
   protected void swap(int slot, int hotbarNum) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
              mc.thePlayer);
   }

   private boolean getPlaceBlock(final BlockPos pos, final EnumFacing facing) {
      Vec3i data = this.blockData.getFacing().getDirectionVec();
      if(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock() == Blocks.air) {
         if (getBlockCount() > 0 && slot != -1 && mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, facing, new Vec3(this.blockData.getPosition()).addVector(0.5, 0.5, 0.5).add(new Vec3(data.getX() * 0.5, data.getY() * 0.5, data.getZ() * 0.5)))) {
            if (this.swing.getValue()) {
               mc.thePlayer.swingItem();
            } else {
               mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
            return true;
         }
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
      if (!this.down.getValue() && this.keepY.getValue() && !tower) {
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

   public static int getEmptySlotInHotbar() {
      for (int i = 0; i < 9; i++) {
         if (mc.thePlayer.inventory.mainInventory[i] == null)
            return i;
      }
      return -1;
   }

   private boolean isValid(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemBlock) {
         boolean isBad = false;

         ItemBlock block = (ItemBlock) itemStack.getItem();
         for (int i = 0; i < this.invalid.size(); i++) {
            if (block.getBlock().equals(this.invalid.get(i))) {
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

   public void tower() {
       double var38 = Minecraft.getMinecraft().thePlayer.posY - 1.0D;
       BlockPos underPos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, var38, Minecraft.getMinecraft().thePlayer.posZ);
       Block underBlock = Minecraft.getMinecraft().theWorld.getBlockState(underPos).getBlock();
       BlockData data = this.getBlockData();
       double var29;
       if (!mc.gameSettings.keyBindJump.isKeyDown()) {
           if (towermove.getValue() && PlayerUtil.isMoving2()) {
               if (MoveUtil.isOnGround(0.76D) && !MoveUtil.isOnGround(0.75D)) {
                   if (Minecraft.getMinecraft().thePlayer.motionY > 0.23D) {
                       if (Minecraft.getMinecraft().thePlayer.motionY < 0.25D) {
                           var29 = (double) Math.round(Minecraft.getMinecraft().thePlayer.posY);
                           Minecraft.getMinecraft().thePlayer.motionY = var29 - Minecraft.getMinecraft().thePlayer.posY;
                       }
                   }
               }

               if (!MoveUtil.isOnGround(1.0E-4D)) {
                   if (Minecraft.getMinecraft().thePlayer.motionY > 0.1D) {
                       if (Minecraft.getMinecraft().thePlayer.posY >= (double) Math.round(Minecraft.getMinecraft().thePlayer.posY) - 1.0E-4D) {
                           if (Minecraft.getMinecraft().thePlayer.posY <= (double) Math.round(Minecraft.getMinecraft().thePlayer.posY) + 1.0E-4D) {
                               Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
                           }
                       }
                   }
               }
           }
       } else if (PlayerUtil.isMoving2()) {
           if (MoveUtil.isOnGround(0.76D) && !MoveUtil.isOnGround(0.75D)) {
               if (Minecraft.getMinecraft().thePlayer.motionY > 0.23D) {
                   if (Minecraft.getMinecraft().thePlayer.motionY < 0.25D) {
                       var29 = (double) Math.round(Minecraft.getMinecraft().thePlayer.posY);
                       Minecraft.getMinecraft().thePlayer.motionY = var29 - Minecraft.getMinecraft().thePlayer.posY;
                   }
               }
           }

           if (MoveUtil.isOnGround(1.0E-4D)) {
               Minecraft.getMinecraft().thePlayer.motionY = 0.42D;
               Minecraft.getMinecraft().thePlayer.motionX *= 0.9D;
               Minecraft.getMinecraft().thePlayer.motionZ *= 0.9D;
           } else {
               if (Minecraft.getMinecraft().thePlayer.posY >= (double) Math.round(Minecraft.getMinecraft().thePlayer.posY) - 1.0E-4D) {
                   if (Minecraft.getMinecraft().thePlayer.posY <= (double) Math.round(Minecraft.getMinecraft().thePlayer.posY) + 1.0E-4D) {
                       Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
                   }
               }
           }
       } else {
           Minecraft.getMinecraft().thePlayer.motionX = 0.0D;
           Minecraft.getMinecraft().thePlayer.motionZ = 0.0D;
           Minecraft.getMinecraft().thePlayer.jumpMovementFactor = 0.0F;
           if (this.isAirBlock(underBlock) && data != null) {
               Minecraft.getMinecraft().thePlayer.motionY = 0.4196D;
               Minecraft.getMinecraft().thePlayer.motionX *= 0.75D;
               Minecraft.getMinecraft().thePlayer.motionZ *= 0.75D;
           }
       }

   }
   
   public boolean isAirBlock(Block block) {
       return block.getMaterial().isReplaceable() && (!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125D);
   }
   
   public static class BlockData {
      private final BlockPos blockPos;
      private final EnumFacing enumFacing;

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
   
   public enum ScaffoldMode {
	      Hypixel, NCP
   }

}

