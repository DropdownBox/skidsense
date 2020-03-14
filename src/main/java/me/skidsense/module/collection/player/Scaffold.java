package me.skidsense.module.collection.player;


import java.awt.Color;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import me.skidsense.color.Colors;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
public class Scaffold
        extends Module {
   ItemStack is;
   private BlockData blockData;
   private timeHelper time = new timeHelper();
   private timeHelper delay = new timeHelper();
   private timeHelper timer2 = new timeHelper();
   public static Option<Boolean> tower = new Option<Boolean>("Tower", "Tower",true);
   public static Option<Boolean> movetower = new Option<Boolean>("MoveTower","MoveTower" ,false);
   private Option<Boolean> noSwing = new Option<Boolean>("NoSwing","NoSwing",true);
   private Mode mode = new Mode("Priority", "Priority",Smode.values(), Smode.WatchDog);
   private double olddelay;
   int count, cubeSpoof=-1;
   private BlockPos blockpos;
   private float Disfall;
   private EnumFacing facing;
   private List<Block> blacklisted = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.yellow_flower, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.crafting_table, Blocks.snow_layer, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.cactus, Blocks.lever, Blocks.activator_rail, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.furnace, Blocks.ladder, Blocks.oak_fence, Blocks.redstone_torch, Blocks.iron_trapdoor, Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate, Blocks.dispenser, Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine, Blocks.double_plant, Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin);
   public static List<Block> blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.ender_chest, Blocks.enchanting_table, Blocks.stone_button, Blocks.wooden_button, Blocks.crafting_table, Blocks.beacon);
   private boolean rotated = false;
   private boolean should = false;
   int slot;
   private float animationY2;
   private ItemStack currentlyHolding;
   static final int[] $SwitchMap$net$minecraft$util$EnumFacing = new int[EnumFacing.values().length];

   public Scaffold() {
      super("Scaffold", new String[]{"Scaffold"}, ModuleType.Move);
      this.addValues(mode,tower, movetower,noSwing);
   }
   @EventHandler
   public void onRender2D(EventRender2D event) {
      ScaledResolution res = new ScaledResolution(mc);
      FontRenderer font = mc.fontRendererObj;
      int color = Colors.getColor(195, 0, 0, 255);
      if (this.getBlockCount() >= 64 && 128 > this.getBlockCount()) {
         color = Colors.getColor(195, 195, 0, 255);
      } else if (this.getBlockCount() >= 128) {
         color = Colors.getColor(0, 155, 0, 255);
      }
      GlStateManager.enableBlend();
      font.drawStringWithShadow("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") + 24, res.getScaledHeight() / 2 - 3, color);
      GlStateManager.disableBlend();
   }
   public static void renderChest(BlockPos blockPos, Color white) {
      double d0 = (double)blockPos.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX;
      double d1 = (double)blockPos.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY;
      double d2 = (double)blockPos.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
      GL11.glPushMatrix();
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glLineWidth(1.0F);
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glDepthMask(true);
      GL11.glColor4d(255.0D, 255.0D, 255D, 15.0D);
      RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D));
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDepthMask(true);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glPopMatrix();
   }
   @EventHandler
   public void onPre(EventPreUpdate event) {
      double x = Minecraft.getMinecraft().thePlayer.posX;
      double y = Minecraft.getMinecraft().thePlayer.posY - 1.0;
      double z = Minecraft.getMinecraft().thePlayer.posZ;
      BlockPos underPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
      Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
      BlockPos blockBelow = new BlockPos(x, y, z);
      if (Minecraft.getMinecraft().thePlayer != null) {
         this.blockData = this.getBlockData(blockBelow, blacklistedBlocks);
         if (this.blockData == null) {
            this.blockData = this.getBlockData(blockBelow.offset(EnumFacing.DOWN), blacklistedBlocks);
         }
         if (this.mc.theWorld.getBlockState(blockBelow = new BlockPos(x, y, z)).getBlock() == Blocks.air) {
            if (this.blockData != null) {
               float[] rot = this.getRotationsBlock(BlockData.position, BlockData.face);
               event.pitch = rot[1];
               event.yaw = rot[0];
               mc.thePlayer.renderYawOffset = rot[0];
               mc.thePlayer.rotationYawHead = rot[0];
            }
            if (this.tower.getValue().booleanValue() && this.mc.gameSettings.keyBindJump.pressed) {
               if(this.mode.getValue() == Smode.WatchDog) {
                  if (this.movetower.getValue().booleanValue()) {
                     if (this.mc.gameSettings.keyBindJump.pressed) {
                        if(!mc.thePlayer.isPotionActive(Potion.jump))
                           if (this.isMoving2()) {
                              if (this.isOnGround(0.76) && !this.isOnGround(0.75) && Minecraft.getMinecraft().thePlayer.motionY > 0.23 && Minecraft.getMinecraft().thePlayer.motionY < 0.25) {
                                 Minecraft.getMinecraft().thePlayer.motionY = Math.round(mc.thePlayer.posY) - mc.thePlayer.posY;
                              }
                              if (this.isOnGround(1.0E-4)) {
                                 Minecraft.getMinecraft().thePlayer.motionY = 0.41993956416514;
                                 Minecraft.getMinecraft().thePlayer.motionX *= 0.9;
                                 Minecraft.getMinecraft().thePlayer.motionZ *= 0.9;
                              } else if (Minecraft.getMinecraft().thePlayer.posY >= (double)Math.round(Minecraft.getMinecraft().thePlayer.posY) - 1.0E-4 && Minecraft.getMinecraft().thePlayer.posY <= (double)Math.round(Minecraft.getMinecraft().thePlayer.posY) + 1.0E-4) {
                                 Minecraft.getMinecraft().thePlayer.motionY = 0.0;
                              }
                           } else {
                              Minecraft.getMinecraft().thePlayer.motionX = 0.0;
                              Minecraft.getMinecraft().thePlayer.motionZ = 0.0;
                              Minecraft.getMinecraft().thePlayer.jumpMovementFactor = 0.0f;
                              blockBelow = new BlockPos(x, y, z);
                              if (this.mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air && this.blockData != null) {
                                 Minecraft.getMinecraft().thePlayer.motionY = 0.4195751556457;
                                 Minecraft.getMinecraft().thePlayer.motionX *= 0.75;
                                 Minecraft.getMinecraft().thePlayer.motionZ *= 0.75;
                              }
                           }
                     }
                  } else if (!this.isMoving2() && this.mc.gameSettings.keyBindJump.pressed ) {
                     Minecraft.getMinecraft().thePlayer.motionX = 0.0;
                     Minecraft.getMinecraft().thePlayer.motionZ = 0.0;
                     Minecraft.getMinecraft().thePlayer.jumpMovementFactor = 0.0f;
                     blockBelow = new BlockPos(x, y, z);
                     if (this.mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air && this.blockData != null) {
                        Minecraft.getMinecraft().thePlayer.motionY = 0.4196;
                        Minecraft.getMinecraft().thePlayer.motionX *= 0.75;
                        Minecraft.getMinecraft().thePlayer.motionZ *= 0.75;
                     }
                  }
               }
               else if(this.mode.getValue() == Smode.Normal) {
                  if (isAirBlock(underBlock) && this.blockData != null) {
                     mc.thePlayer.motionY = 0.4196;
                     mc.thePlayer.motionX *= 0.75;
                     mc.thePlayer.motionZ *= 0.75;
                  }
                  if(!MoveUtil.isMoving()){
                     mc.thePlayer.motionX = 0;
                     mc.thePlayer.motionZ = 0;
                  }
               }
            }
         }
      }
      if(this.tower.getValue().booleanValue() && this.mc.gameSettings.keyBindJump.pressed) {
         if(this.mode.getValue() == Smode.CubeCraft) {
            mc.thePlayer.setSprinting(false);
            count ++;
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.jumpMovementFactor = 0;
            if(MoveUtil.isOnGround(2))
               if(count == 1){
                  mc.thePlayer.motionY = 0.41;
               }else{

                  mc.thePlayer.motionY = 0.47;
                  count = 0;
               }
         }
         else if(this.mode.getValue() == Smode.Normal) {
            if (isAirBlock(underBlock) && this.blockData != null) {
               mc.thePlayer.motionY = 0.4196;
               mc.thePlayer.motionX *= 0.75;
               mc.thePlayer.motionZ *= 0.75;
            }
            if(!MoveUtil.isMoving()){
               mc.thePlayer.motionX = 0;
               mc.thePlayer.motionZ = 0;
            }
         }
      }
//	        setSpeed();
   }

   public boolean isOnGround(double height) {
      if (!this.mc.theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0, - height, 0.0)).isEmpty()) {
         return true;
      }
      return false;
   }

   public boolean isMoving2() {
      return Minecraft.getMinecraft().thePlayer.moveForward != 0.0f || Minecraft.getMinecraft().thePlayer.moveStrafing != 0.0f;
   }

   public float[] getRotationsBlock(BlockPos block, EnumFacing face) {
      double x = (double)block.getX() + 0.5 - Minecraft.getMinecraft().thePlayer.posX + (double)face.getFrontOffsetX() / 2.0;
      double z = (double)block.getZ() + 0.5 - Minecraft.getMinecraft().thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0;
      double y = (double)block.getY() + 0.5;
      double d1 = Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() - y;
      double d3 = MathHelper.sqrt_double(x * x + z * z);
      float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
      float pitch = (float)(Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
      if (yaw < 0.0f) {
         yaw += 360.0f;
      }
      return new float[]{yaw, pitch};
   }

   @EventHandler
   public void onSafe(EventPostUpdate event) {
      int i;
      for (i = 36; i < 45; ++i) {

         Item item;
         if (!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((item = (is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack()).getItem()) instanceof ItemBlock) || this.blacklisted.contains(((ItemBlock)item).getBlock()) || ((ItemBlock)item).getBlock().getLocalizedName().toLowerCase().contains("chest") || this.blockData == null) continue;
         int currentItem = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
         Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i - 36));
         Minecraft.getMinecraft().thePlayer.inventory.currentItem = i - 36;
         this.currentlyHolding = this.mc.thePlayer.inventory.getStackInSlot(i - 36);
         Minecraft.getMinecraft().playerController.updateController();
         Minecraft.getMinecraft().playerController.onPlayerRightClick(Minecraft.getMinecraft().thePlayer, this.mc.theWorld, Minecraft.getMinecraft().thePlayer.getHeldItem(), BlockData.position, BlockData.face, new Vec3(BlockData.access$2(this.blockData)).addVector(0.5, 0.5, 0.5).add(new Vec3(BlockData.access$3(this.blockData).getDirectionVec()).scale((float) 0.5)));
         if (this.noSwing.getValue().booleanValue()) {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
         } else {
            Minecraft.getMinecraft().thePlayer.swingItem();
         }
         if(this.mode.getValue() == Smode.CubeCraft){
            if(cubeSpoof != currentItem){

               C09PacketHeldItemChange p = new C09PacketHeldItemChange(currentItem);
               cubeSpoof = currentItem;
               mc.thePlayer.sendQueue.getNetworkManager().sendPacket(p);
               mc.thePlayer.inventory.currentItem = currentItem;
               mc.playerController.updateController();
            }else{
               mc.thePlayer.inventory.currentItem = currentItem;
               mc.playerController.updateController();
            }
         }else{
            mc.thePlayer.inventory.currentItem = currentItem;
            mc.playerController.updateController();
         }
         return;
      }
      if (this.invCheck()) {
         for (i = 9; i < 36; ++i) {
            Item item;
            if (!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock) || this.blacklisted.contains(((ItemBlock)item).getBlock()) || ((ItemBlock)item).getBlock().getLocalizedName().toLowerCase().contains("chest")) continue;
            this.swap(i, 7);
            break;
         }
      }

   }
   public void setSpeed() {
      if(this.mode.getValue() == Smode.CubeCraft)
         mc.thePlayer.onGround = false;
      mc.thePlayer.jumpMovementFactor = 0;
      double forward = mc.thePlayer.movementInput.moveForward;
      double strafe = mc.thePlayer.movementInput.moveStrafe;
      float YAW = mc.thePlayer.rotationYaw;
      double a = (forward * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f)));
      double b = (forward * 0.45 * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * 0.45 * Math.cos(Math.toRadians(YAW + 90.0f)));
      double c = Math.abs((a* b));
      double slow = 1-c*5;
      double speed =0.35 + randomNumber(0.01, -0.05);
      speed *= slow;
      MoveUtil.setMotion(speed);
      mc.thePlayer.setSprinting(false);

   }
   public static double randomNumber(double max, double min) {
      return (Math.random() * (max - min)) + min;
   }
   public static float randomFloat(long seed) {
      seed = System.currentTimeMillis() + seed;
      return 0.3f + (float)new Random(seed).nextInt(70000000) / 1.0E8f + 1.458745E-8f;
   }

   protected void swap(int slot, int hotbarNum) {
      Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, Minecraft.getMinecraft().thePlayer);
   }

   private boolean invCheck() {
      for (int i = 36; i < 45; ++i) {
         Item item;
         if (!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack().getItem()) instanceof ItemBlock) || this.blacklisted.contains(((ItemBlock)item).getBlock())) continue;
         return false;
      }
      return true;
   }

   private double getDoubleRandom(double min, double max) {
      return ThreadLocalRandom.current().nextDouble(min, max);
   }

   private boolean canPlace(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 vec3) {
      if (heldStack.getItem() instanceof ItemBlock) {
         return ((ItemBlock)heldStack.getItem()).canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack);
      }
      return false;
   }

//    private void setBlockAndFacing(BlockPos bp) {
//        if (this.mc.theWorld.getBlockState(bp.add(0, -1, 0)).getBlock() != Blocks.air) {
//            this.blockpos = bp.add(0, -1, 0);
//            this.facing = EnumFacing.UP;
//        } else if (this.mc.theWorld.getBlockState(bp.add(-1, 0, 0)).getBlock() != Blocks.air) {
//            this.blockpos = bp.add(-1, 0, 0);
//            this.facing = EnumFacing.EAST;
//        } else if (this.mc.theWorld.getBlockState(bp.add(1, 0, 0)).getBlock() != Blocks.air) {
//            this.blockpos = bp.add(1, 0, 0);
//            this.facing = EnumFacing.WEST;
//        } else if (this.mc.theWorld.getBlockState(bp.add(0, 0, -1)).getBlock() != Blocks.air) {
//            this.blockpos = bp.add(0, 0, -1);
//            this.facing = EnumFacing.SOUTH;
//        } else if (this.mc.theWorld.getBlockState(bp.add(0, 0, 1)).getBlock() != Blocks.air) {
//            this.blockpos = bp.add(0, 0, 1);
//            this.facing = EnumFacing.NORTH;
//        } else {
//            bp = null;
//            this.facing = null;
//        }
//    }

   private int getBlockCount() {
      int blockCount = 0;
      for (int i = 0; i < 45; ++i) {
         if (!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
         ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
         Item item = is.getItem();
         if (!(is.getItem() instanceof ItemBlock) || this.blacklisted.contains(((ItemBlock)item).getBlock())) continue;
         blockCount += is.stackSize;
      }
      return blockCount;
   }

   private int getBlockSlot() {
      for (int i = 36; i < 45; ++i) {
         ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
         if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock) || itemStack.stackSize <= 0 || this.blacklisted.stream().anyMatch(e -> e.equals(((ItemBlock)itemStack.getItem()).getBlock()))) continue;
         return i - 36;
      }
      return -1;
   }

   private BlockData getBlockData(BlockPos pos, List list) {
      Disfall = Minecraft.getMinecraft().thePlayer.fallDistance;
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
         return new BlockData(pos.add(0, -1, 0), EnumFacing.UP, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {

         return new BlockData(pos.add(-1, 0, 0), Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Minecraft.getMinecraft().thePlayer.onGround && Minecraft.getMinecraft().thePlayer.fallDistance == 0.0f && this.mc.theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.0, Minecraft.getMinecraft().thePlayer.posZ)).getBlock() == Blocks.air ? EnumFacing.DOWN : EnumFacing.EAST,  this.blockData );
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
         return new BlockData(pos.add(1, 0, 0), Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Minecraft.getMinecraft().thePlayer.onGround && Minecraft.getMinecraft().thePlayer.fallDistance == 0.0f && this.mc.theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.0, Minecraft.getMinecraft().thePlayer.posZ)).getBlock() == Blocks.air ? EnumFacing.DOWN : EnumFacing.WEST, this.blockData );
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
         return new BlockData(pos.add(0, 0, -1), Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Minecraft.getMinecraft().thePlayer.onGround && Minecraft.getMinecraft().thePlayer.fallDistance == 0.0f && this.mc.theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.0, Minecraft.getMinecraft().thePlayer.posZ)).getBlock() == Blocks.air ? EnumFacing.DOWN : EnumFacing.SOUTH, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
         return new BlockData(pos.add(0, 0, 1), Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Minecraft.getMinecraft().thePlayer.onGround && Minecraft.getMinecraft().thePlayer.fallDistance == 0.0f && this.mc.theWorld.getBlockState(new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1.0, Minecraft.getMinecraft().thePlayer.posZ)).getBlock() == Blocks.air ? EnumFacing.DOWN : EnumFacing.NORTH, this.blockData);
      }
      BlockPos add = pos.add(-1, 0, 0);
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add.add(-1, 0, 0)).getBlock())) {
         return new BlockData(add.add(-1, 0, 0), EnumFacing.EAST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add.add(1, 0, 0)).getBlock())) {
         return new BlockData(add.add(1, 0, 0), EnumFacing.WEST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add.add(0, 0, -1)).getBlock())) {
         return new BlockData(add.add(0, 0, -1), EnumFacing.SOUTH, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add.add(0, 0, 1)).getBlock())) {
         return new BlockData(add.add(0, 0, 1), EnumFacing.NORTH, this.blockData);
      }
      BlockPos add2 = pos.add(1, 0, 0);
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock())) {
         return new BlockData(add2.add(-1, 0, 0), EnumFacing.EAST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add2.add(1, 0, 0)).getBlock())) {
         return new BlockData(add2.add(1, 0, 0), EnumFacing.WEST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add2.add(0, 0, -1)).getBlock())) {
         return new BlockData(add2.add(0, 0, -1), EnumFacing.SOUTH, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add2.add(0, 0, 1)).getBlock())) {
         return new BlockData(add2.add(0, 0, 1), EnumFacing.NORTH, this.blockData);
      }
      BlockPos add3 = pos.add(0, 0, -1);
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock())) {
         return new BlockData(add3.add(-1, 0, 0), EnumFacing.EAST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add3.add(1, 0, 0)).getBlock())) {
         return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add3.add(0, 0, -1)).getBlock())) {
         return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add3.add(0, 0, 1)).getBlock())) {
         return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH, this.blockData);
      }
      BlockPos add4 = pos.add(0, 0, 1);
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock())) {
         return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add4.add(1, 0, 0)).getBlock())) {
         return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add4.add(0, 0, -1)).getBlock())) {
         return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH, this.blockData);
      }
      if (!blacklistedBlocks.contains(this.mc.theWorld.getBlockState(add4.add(0, 0, 1)).getBlock())) {
         return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH, this.blockData);
      }
      return null;
   }

   public boolean isAirBlock(Block block) {
      return block.getMaterial().isReplaceable() && (!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125);
   }

   public Vec3 getBlockSide(BlockPos pos, EnumFacing face) {
      if (face == EnumFacing.NORTH) {
         return new Vec3(pos.getX(), pos.getY(), (double)pos.getZ() - 0.5);
      }
      if (face == EnumFacing.EAST) {
         return new Vec3((double)pos.getX() + 0.5, pos.getY(), pos.getZ());
      }
      if (face == EnumFacing.SOUTH) {
         return new Vec3(pos.getX(), pos.getY(), (double)pos.getZ() + 0.5);
      }
      if (face == EnumFacing.WEST) {
         return new Vec3((double)pos.getX() - 0.5, pos.getY(), pos.getZ());
      }
      return new Vec3(pos.getX(), pos.getY(), pos.getZ());
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.timer2.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(Minecraft.getMinecraft().thePlayer.inventory.currentItem));
      mc.timer.timerSpeed = 1F;
   }


   public class timeHelper {
      private long prevMS = 0L;

      public boolean delay(float milliSec) {
         return (float)(this.getTime() - this.prevMS) >= milliSec;
      }

      public void reset() {
         this.prevMS = this.getTime();
      }

      public long getTime() {
         return System.nanoTime() / 1000000L;
      }

      public long getDifference() {
         return this.getTime() - this.prevMS;
      }

      public void setDifference(long difference) {
         this.prevMS = this.getTime() - difference;
      }
   }

   private static class BlockData {
      public static BlockPos position;
      public static EnumFacing face;

      public BlockData(BlockPos position, EnumFacing face, BlockData blockData) {
         BlockData.position = position;
         BlockData.face = face;
      }

      private BlockPos getPosition() {
         return position;
      }

      private EnumFacing getFacing() {
         return face;
      }

      static BlockPos access$0(BlockData var0) {
         return var0.getPosition();
      }

      static EnumFacing access$1(BlockData var0) {
         return var0.getFacing();
      }

      static BlockPos access$2(BlockData var0) {
         return position;
      }

      static EnumFacing access$3(BlockData var0) {
         return face;
      }
   }

   public static enum Smode{
      Normal,
      WatchDog,
      CubeCraft,

   }

}

