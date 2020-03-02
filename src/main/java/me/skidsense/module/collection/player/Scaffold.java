package me.skidsense.module.collection.player;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

import me.skidsense.color.Colors;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender2D;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.PlayerUtil;
import me.skidsense.util.RenderUtil;

public class Scaffold extends Module {
   public static boolean firstdown = true;
   public static boolean safewalk;
   public static Option tower = new Option("Tower", "Tower", Boolean.valueOf(true));
   public static Option silent = new Option("Silent", "Silent", Boolean.valueOf(true));
   public static Option nosprint = new Option("NoSprint", "NoSprint", Boolean.valueOf(false));
   public static Option swingItem = new Option("Swing", "Swing", Boolean.valueOf(true));
   public static Option towermove = new Option("TowerMove", "TowerMove", Boolean.valueOf(false));
   public static Option pick = new Option("BlockPick", "BlockPick", Boolean.valueOf(true));
   public static Option safe = new Option("SafeWalk", "SafeWalk", Boolean.valueOf(false));
   public static Option down = new Option("Down", "Down", Boolean.valueOf(true));
   private Boolean SprintKeyDown;
   public BlockData blockdata;
   private float Disfall;
   private Boolean autodis;
   private List blacklist;
   private int width = 0;
   private Scaffold.BlockCache blockCache;
   private int currentItem;
   private static List blacklistedBlocks = Arrays.asList(new Block[]{Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence});

   public static List getBlacklistedBlocks() {
      return blacklistedBlocks;
   }
   //TODO scaffold 狗ban了，需要修复
   public Scaffold() {
      super("Scaffold Walk", new String[]{"Scaffold", "ScaffoldWalk", "airwalk"}, ModuleType.Player);
      this.addValues(new Value[]{tower, silent, nosprint, swingItem, towermove, safe, down, pick});
      this.currentItem = 0;
      this.setColor((new Color(244, 119, 194)).getRGB());
   }

   public void onEnable() {
      super.onEnable();
   }

   public void onDisable() {
      super.onDisable();
   }

   @EventHandler
   public void onRender2D(EventRender2D event) {
	   ScaledResolution res = new ScaledResolution(mc);
	   FontRenderer font = mc.fontRendererObj;
       int color = Colors.getColor(255, 0, 0, 150);
       if (this.getBlockCount() >= 64 && 128 > this.getBlockCount()) {
           color = Colors.getColor(255, 255, 0, 150);
       } else if (this.getBlockCount() >= 128) {
           color = Colors.getColor(0, 255, 0, 150);
       }
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2-1, res.getScaledHeight() / 2 - 16, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2+1, res.getScaledHeight() / 2 - 16, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2-1, res.getScaledHeight() / 2 - 14, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2+1, res.getScaledHeight() / 2 - 14, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2-1, res.getScaledHeight() / 2 - 15, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2+1, res.getScaledHeight() / 2 - 15, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2, res.getScaledHeight() / 2 - 14, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2, res.getScaledHeight() / 2 - 16, new Color(0,0,0,150).getRGB());
       font.drawString("" + this.getBlockCount(), res.getScaledWidth() / 2 - font.getStringWidth(this.getBlockCount() + "") / 2, res.getScaledHeight() / 2 - 15, color);
  }

   @EventHandler
   private void onUpdate(EventPreUpdate event) {
         this.setSuffix("LACPro");

      if(((Boolean)nosprint.getValue()).booleanValue()) {
         Minecraft.getMinecraft().thePlayer.setSprinting(false);
      }

      if(this.getBlockSlot() != -1) {
         this.blockCache = this.grab();
         if(((Boolean)tower.getValue()).booleanValue() && (((Boolean)towermove.getValue()).booleanValue() || !isMoving2())) {
            this.tower();
      }
         double var7 = Minecraft.getMinecraft().thePlayer.posY - 1.0D;
         BlockPos underPos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, var7, Minecraft.getMinecraft().thePlayer.posZ);
         BlockData data = this.getBlockData(underPos);
         if(data != null) {
        	 float[] rot = getRotationsBlock(data.position, data.face);
             event.setYaw(rot[0]);
             event.setPitch(rot[1]);
             mc.thePlayer.rotationYawHead = rot[0];
    	     mc.thePlayer.renderYawOffset = rot[0];
         }
      }

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

   private boolean invCheck() {
      for(int i = 36; i < 45; ++i) {
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            Item item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
            if(item instanceof ItemBlock && isValid(item)) {
               return false;
            }
         }
      }

      return true;
   }

   public int getBlockCount() {
      int blockCount = 0;

      for(int i = 0; i < 45; ++i) {
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if(is.getItem() instanceof ItemBlock && isValid(item)) {
               blockCount += is.stackSize;
            }
         }
      }

      return blockCount;
   }

   private static boolean isValid(Item item) {
      if(!(item instanceof ItemBlock)) {
         return false;
      } else {
         ItemBlock iBlock = (ItemBlock)item;
         Block block = iBlock.getBlock();
         return !blacklistedBlocks.contains(block);
      }
   }

   public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
      double x = (double)pos.getX() + 0.5D;
      double y = (double)pos.getY() + 0.5D;
      double z = (double)pos.getZ() + 0.5D;
      x = x + (double)face.getFrontOffsetX() / 2.0D;
      z = z + (double)face.getFrontOffsetZ() / 2.0D;
      y = y + (double)face.getFrontOffsetY() / 2.0D;
      if(face != EnumFacing.UP && face != EnumFacing.DOWN) {
         y += randomNumber(0.3D, -0.3D);
      } else {
         x += randomNumber(0.3D, -0.3D);
         z += randomNumber(0.3D, -0.3D);
      }

      if(face == EnumFacing.WEST || face == EnumFacing.EAST) {
         z += randomNumber(0.3D, -0.3D);
      }

      if(face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
         x += randomNumber(0.3D, -0.3D);
      }

      return new Vec3(x, y, z);
   }

   public static double randomNumber(double max, double min) {
      return Math.random() * (max - min) + min;
   }

   @EventHandler
   private void onPostUpdate(EventPostUpdate event) {
      this.getBestBlocks();
      if(this.blockCache != null) {
         if(((Boolean)swingItem.getValue()).booleanValue()) {
            Minecraft.getMinecraft().thePlayer.swingItem();
         } else {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
         }

         int currentSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
         int slot = getBlockSlot();
         if(slot == -1) {
            this.blockCache = null;
            return;
         }

         Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
         double var8 = Minecraft.getMinecraft().thePlayer.posY - 1.0D;
         BlockPos underPos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, var8, Minecraft.getMinecraft().thePlayer.posZ);
         Block underBlock = Minecraft.getMinecraft().theWorld.getBlockState(underPos).getBlock();
         BlockData data = this.getBlockData(underPos);
         if(this.placeBlock(data)) {
            if(((Boolean)silent.getValue()).booleanValue()) {
               Minecraft.getMinecraft().thePlayer.inventory.currentItem = currentSlot;
               Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
            }

            this.blockCache = null;
         }
      }

   }

   private boolean isPosSolid(BlockPos pos) {
      Block block = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
      return (block.getMaterial().isSolid() || !block.isTranslucent() || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
   }

   private boolean CanDownPut() {
      if(((Boolean)down.getValue()).booleanValue()) {
         if(mc.gameSettings.keyBindSprint.isKeyDown()) {
            if(Minecraft.getMinecraft().thePlayer.onGround) {
               return true;
            }
         }
      }

      return false;
   }

   private BlockData getBlockData(BlockPos pos) {
      if(this.isPosSolid(pos.add(0, -1, 0))) {
         return new BlockData(pos.add(0, -1, 0), this.CanDownPut()?EnumFacing.DOWN:EnumFacing.UP);
      } else if(this.isPosSolid(pos.add(-1, 0, 0))) {
         return new BlockData(pos.add(-1, 0, 0), this.CanDownPut()?EnumFacing.DOWN:EnumFacing.EAST);
      } else if(this.isPosSolid(pos.add(1, 0, 0))) {
         return new BlockData(pos.add(1, 0, 0), this.CanDownPut()?EnumFacing.DOWN:EnumFacing.WEST);
      } else if(this.isPosSolid(pos.add(0, 0, 1))) {
         return new BlockData(pos.add(0, 0, 1), this.CanDownPut()?EnumFacing.DOWN:EnumFacing.NORTH);
      } else if(this.isPosSolid(pos.add(0, 0, -1))) {
         return new BlockData(pos.add(0, 0, -1), this.CanDownPut()?EnumFacing.DOWN:EnumFacing.SOUTH);
      } else {
         BlockPos pos1 = pos.add(-1, 0, 0);
         if(this.isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
         } else if(this.isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
         } else if(this.isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
         } else if(this.isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
         } else if(this.isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
         } else {
            BlockPos pos2 = pos.add(1, 0, 0);
            if(this.isPosSolid(pos2.add(0, -1, 0))) {
               return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
            } else if(this.isPosSolid(pos2.add(-1, 0, 0))) {
               return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
            } else if(this.isPosSolid(pos2.add(1, 0, 0))) {
               return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
            } else if(this.isPosSolid(pos2.add(0, 0, 1))) {
               return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
            } else if(this.isPosSolid(pos2.add(0, 0, -1))) {
               return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
            } else {
               BlockPos pos3 = pos.add(0, 0, 1);
               if(this.isPosSolid(pos3.add(0, -1, 0))) {
                  return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
               } else if(this.isPosSolid(pos3.add(-1, 0, 0))) {
                  return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
               } else if(this.isPosSolid(pos3.add(1, 0, 0))) {
                  return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
               } else if(this.isPosSolid(pos3.add(0, 0, 1))) {
                  return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
               } else if(this.isPosSolid(pos3.add(0, 0, -1))) {
                  return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
               } else {
                  BlockPos pos4 = pos.add(0, 0, -1);
                  if(this.isPosSolid(pos4.add(0, -1, 0))) {
                     return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
                  } else if(this.isPosSolid(pos4.add(-1, 0, 0))) {
                     return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
                  } else if(this.isPosSolid(pos4.add(1, 0, 0))) {
                     return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
                  } else if(this.isPosSolid(pos4.add(0, 0, 1))) {
                     return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
                  } else if(this.isPosSolid(pos4.add(0, 0, -1))) {
                     return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
                  } else {
                     BlockPos pos19 = pos.add(-2, 0, 0);
                     if(this.isPosSolid(pos1.add(0, -1, 0))) {
                        return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
                     } else if(this.isPosSolid(pos1.add(-1, 0, 0))) {
                        return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
                     } else if(this.isPosSolid(pos1.add(1, 0, 0))) {
                        return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
                     } else if(this.isPosSolid(pos1.add(0, 0, 1))) {
                        return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
                     } else if(this.isPosSolid(pos1.add(0, 0, -1))) {
                        return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
                     } else {
                        BlockPos pos29 = pos.add(2, 0, 0);
                        if(this.isPosSolid(pos2.add(0, -1, 0))) {
                           return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
                        } else if(this.isPosSolid(pos2.add(-1, 0, 0))) {
                           return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
                        } else if(this.isPosSolid(pos2.add(1, 0, 0))) {
                           return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
                        } else if(this.isPosSolid(pos2.add(0, 0, 1))) {
                           return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
                        } else if(this.isPosSolid(pos2.add(0, 0, -1))) {
                           return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
                        } else {
                           BlockPos pos39 = pos.add(0, 0, 2);
                           if(this.isPosSolid(pos3.add(0, -1, 0))) {
                              return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
                           } else if(this.isPosSolid(pos3.add(-1, 0, 0))) {
                              return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
                           } else if(this.isPosSolid(pos3.add(1, 0, 0))) {
                              return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
                           } else if(this.isPosSolid(pos3.add(0, 0, 1))) {
                              return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
                           } else if(this.isPosSolid(pos3.add(0, 0, -1))) {
                              return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
                           } else {
                              BlockPos pos49 = pos.add(0, 0, -2);
                              if(this.isPosSolid(pos4.add(0, -1, 0))) {
                                 return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
                              } else if(this.isPosSolid(pos4.add(-1, 0, 0))) {
                                 return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
                              } else if(this.isPosSolid(pos4.add(1, 0, 0))) {
                                 return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
                              } else if(this.isPosSolid(pos4.add(0, 0, 1))) {
                                 return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
                              } else if(this.isPosSolid(pos4.add(0, 0, -1))) {
                                 return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
                              } else {
                                 BlockPos pos5 = pos.add(0, -1, 0);
                                 if(this.isPosSolid(pos5.add(0, -1, 0))) {
                                    return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
                                 } else if(this.isPosSolid(pos5.add(-1, 0, 0))) {
                                    return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
                                 } else if(this.isPosSolid(pos5.add(1, 0, 0))) {
                                    return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
                                 } else if(this.isPosSolid(pos5.add(0, 0, 1))) {
                                    return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
                                 } else if(this.isPosSolid(pos5.add(0, 0, -1))) {
                                    return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
                                 } else {
                                    BlockPos pos6 = pos5.add(1, 0, 0);
                                    if(this.isPosSolid(pos6.add(0, -1, 0))) {
                                       return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
                                    } else if(this.isPosSolid(pos6.add(-1, 0, 0))) {
                                       return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
                                    } else if(this.isPosSolid(pos6.add(1, 0, 0))) {
                                       return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
                                    } else if(this.isPosSolid(pos6.add(0, 0, 1))) {
                                       return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
                                    } else if(this.isPosSolid(pos6.add(0, 0, -1))) {
                                       return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
                                    } else {
                                       BlockPos pos7 = pos5.add(-1, 0, 0);
                                       if(this.isPosSolid(pos7.add(0, -1, 0))) {
                                          return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
                                       } else if(this.isPosSolid(pos7.add(-1, 0, 0))) {
                                          return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
                                       } else if(this.isPosSolid(pos7.add(1, 0, 0))) {
                                          return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
                                       } else if(this.isPosSolid(pos7.add(0, 0, 1))) {
                                          return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
                                       } else if(this.isPosSolid(pos7.add(0, 0, -1))) {
                                          return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
                                       } else {
                                          BlockPos pos8 = pos5.add(0, 0, 1);
                                          if(this.isPosSolid(pos8.add(0, -1, 0))) {
                                             return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
                                          } else if(this.isPosSolid(pos8.add(-1, 0, 0))) {
                                             return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
                                          } else if(this.isPosSolid(pos8.add(1, 0, 0))) {
                                             return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
                                          } else if(this.isPosSolid(pos8.add(0, 0, 1))) {
                                             return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
                                          } else if(this.isPosSolid(pos8.add(0, 0, -1))) {
                                             return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
                                          } else {
                                             BlockPos pos9 = pos5.add(0, 0, -1);
                                             return this.isPosSolid(pos9.add(0, -1, 0))?new BlockData(pos9.add(0, -1, 0), EnumFacing.UP):(this.isPosSolid(pos9.add(-1, 0, 0))?new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST):(this.isPosSolid(pos9.add(1, 0, 0))?new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST):(this.isPosSolid(pos9.add(0, 0, 1))?new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH):(this.isPosSolid(pos9.add(0, 0, -1))?new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH):null))));
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void tower() {
      double var38 = Minecraft.getMinecraft().thePlayer.posY - 1.0D;
      BlockPos underPos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, var38, Minecraft.getMinecraft().thePlayer.posZ);
      Block underBlock = Minecraft.getMinecraft().theWorld.getBlockState(underPos).getBlock();
      BlockData data = this.getBlockData(underPos);
      if(!mc.gameSettings.keyBindJump.isKeyDown()) {
         if(((Boolean)towermove.getValue()).booleanValue() && isMoving2()) {
            if(MoveUtil.isOnGround(0.76D) && !MoveUtil.isOnGround(0.75D)) {
               if(Minecraft.getMinecraft().thePlayer.motionY > 0.23D) {
                  if(Minecraft.getMinecraft().thePlayer.motionY < 0.25D) {
                     EntityPlayerSP var24 = Minecraft.getMinecraft().thePlayer;
                     Minecraft var32 = mc;
                     double var33 = (double)Math.round(Minecraft.getMinecraft().thePlayer.posY);
                     var24.motionY = var33 - Minecraft.getMinecraft().thePlayer.posY;
                  }
               }
            }

            if(!MoveUtil.isOnGround(1.0E-4D)) {
               
               if(Minecraft.getMinecraft().thePlayer.motionY > 0.1D) {
                  
                  Minecraft var34 = mc;
                  if(Minecraft.getMinecraft().thePlayer.posY >= (double)Math.round(Minecraft.getMinecraft().thePlayer.posY) - 1.0E-4D) {
                     
                     var34 = mc;
                     if(Minecraft.getMinecraft().thePlayer.posY <= (double)Math.round(Minecraft.getMinecraft().thePlayer.posY) + 1.0E-4D) {
                        
                        Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
                     }
                  }
               }
            }
         }

      } else {
         if(isMoving2()) {
            if(MoveUtil.isOnGround(0.76D) && !MoveUtil.isOnGround(0.75D)) {
               
               if(Minecraft.getMinecraft().thePlayer.motionY > 0.23D) {
                  
                  if(Minecraft.getMinecraft().thePlayer.motionY < 0.25D) {
                     
                     EntityPlayerSP var8 = Minecraft.getMinecraft().thePlayer;
                     
                     double var29 = (double)Math.round(Minecraft.getMinecraft().thePlayer.posY);

                     var8.motionY = var29 - Minecraft.getMinecraft().thePlayer.posY;
                  }
               }
            }

            if(MoveUtil.isOnGround(1.0E-4D)) {
               
               Minecraft.getMinecraft().thePlayer.motionY = 0.42D;
               
               Minecraft.getMinecraft().thePlayer.motionX *= 0.9D;
               
               Minecraft.getMinecraft().thePlayer.motionZ *= 0.9D;
            } else {
               

               if(Minecraft.getMinecraft().thePlayer.posY >= (double)Math.round(Minecraft.getMinecraft().thePlayer.posY) - 1.0E-4D) {
                  

                  if(Minecraft.getMinecraft().thePlayer.posY <= (double)Math.round(Minecraft.getMinecraft().thePlayer.posY) + 1.0E-4D) {
                     
                     Minecraft.getMinecraft().thePlayer.motionY = 0.0D;
                  }
               }
            }
         } else {
            
            Minecraft.getMinecraft().thePlayer.motionX = 0.0D;
            
            Minecraft.getMinecraft().thePlayer.motionZ = 0.0D;
            
            Minecraft.getMinecraft().thePlayer.jumpMovementFactor = 0.0F;
            if(this.isAirBlock(underBlock) && data != null) {
               
               Minecraft.getMinecraft().thePlayer.motionY = 0.4196D;
               
               Minecraft.getMinecraft().thePlayer.motionX *= 0.75D;
               
               Minecraft.getMinecraft().thePlayer.motionZ *= 0.75D;
            }
         }

      }
   }

   public static boolean isOnGround(double height) {
	   return !Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
   }

   public boolean isAirBlock(Block block) {
      return block.getMaterial().isReplaceable()?!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125D:false;
   }

   public static boolean isMoving2() {

      if(Minecraft.getMinecraft().thePlayer.moveForward == 0.0F) {
 
         if(Minecraft.getMinecraft().thePlayer.moveStrafing == 0.0F) {
            return false;
         }
      }

      return true;
   }

   private boolean placeBlock(BlockData data) {
      Minecraft var10001 = mc;
      Minecraft var10002 = mc;
      Minecraft var10003 = mc;
      if(Minecraft.getMinecraft().playerController.onPlayerRightClick(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem(), data.position, data.face, getVec3(data.position, data.face))) {
         Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
         return true;
      } else {
         return false;
      }
   }

   private Vec3 grabPosition(BlockPos position, EnumFacing facing) {
      Vec3 offset = new Vec3((double)facing.getDirectionVec().getX() / 2.0D, (double)facing.getDirectionVec().getY() / 2.0D, (double)facing.getDirectionVec().getZ() / 2.0D);
      Vec3 point = new Vec3((double)position.getX() + 0.5D, (double)position.getY() + 0.5D, (double)position.getZ() + 0.5D);
      return point.add(offset);
   }

   private Scaffold.BlockCache grab() {
      EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
      BlockPos position = (new BlockPos(Minecraft.getMinecraft().thePlayer.getPositionVector())).offset(EnumFacing.DOWN);
      if(!(Minecraft.getMinecraft().theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
         return null;
      } else {
         for(EnumFacing offsets : EnumFacing.values()) {
            BlockPos offset1 = position.offset(offsets);
            Minecraft.getMinecraft().theWorld.getBlockState(offset1);
            if(!(Minecraft.getMinecraft().theWorld.getBlockState(offset1).getBlock() instanceof BlockAir)) {
               return new Scaffold.BlockCache(this, offset1, invert[offsets.ordinal()], (Scaffold.BlockCache)null);
            }
         }

         BlockPos[] var16 = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0)};

         for(BlockPos var17 : var16) {
            BlockPos offsetPos = position.add(var17.getX(), 0, var17.getZ());
            Minecraft.getMinecraft().theWorld.getBlockState(offsetPos);
            if(Minecraft.getMinecraft().theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
               for(EnumFacing facing2 : EnumFacing.values()) {
                  BlockPos offset2 = offsetPos.offset(facing2);
                  Minecraft.getMinecraft().theWorld.getBlockState(offset2);
                  if(!(Minecraft.getMinecraft().theWorld.getBlockState(offset2).getBlock() instanceof BlockAir)) {
                     return new Scaffold.BlockCache(this, offset2, invert[facing2.ordinal()], (Scaffold.BlockCache)null);
                  }
               }
            }
         }

         return null;
      }
   }

   public void getBestBlocks() {
      if(((Boolean)pick.getValue()).booleanValue()) {
         new ItemStack(Item.getItemById(261));
         int bestInvSlot = this.getBiggestBlockSlotInv();
         int bestHotbarSlot = this.getBiggestBlockSlotHotbar();
         int bestSlot = this.getBiggestBlockSlotHotbar() > 0?this.getBiggestBlockSlotHotbar():this.getBiggestBlockSlotInv();
         int spoofSlot = 42;
         if(bestHotbarSlot > 0 && bestInvSlot > 0 && Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(bestInvSlot).getHasStack() && Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getHasStack() && Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getStack().stackSize < Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(bestInvSlot).getStack().stackSize) {
            bestSlot = bestInvSlot;
         }

         if(this.hotbarContainBlock()) {
            for(int a = 36; a < 45; ++a) {
               if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                  Item item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(a).getStack().getItem();
                  if(item instanceof ItemBlock && isValid(item)) {
                     spoofSlot = a;
                     break;
                  }
               }
            }
         } else {
            for(int a = 36; a < 45; ++a) {
               if(!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                  spoofSlot = a;
                  break;
               }
            }
         }

         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(spoofSlot).slotNumber != bestSlot) {
            this.swap(bestSlot, spoofSlot - 36);
            Minecraft.getMinecraft().playerController.updateController();
         }
      } else if(this.invCheck()) {
         ItemStack is = new ItemStack(Item.getItemById(261));

         for(int i = 9; i < 36; ++i) {
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               Item item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
               int count = 0;
               if(item instanceof ItemBlock && isValid(item)) {
                  for(int a = 36; a < 45; ++a) {
                     Container var10000 = Minecraft.getMinecraft().thePlayer.inventoryContainer;
                     if(Container.canAddItemToSlot(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(a), is, true)) {
                        this.swap(i, a - 36);
                        ++count;
                        break;
                     }
                  }

                  if(count == 0) {
                     this.swap(i, 7);
                  }
                  break;
               }
            }
         }
      }

   }

   protected void swap(int slot, int hotbarNum) {
	   Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2, Minecraft.getMinecraft().thePlayer);
   }

   public int getBiggestBlockSlotInv() {
      int slot = -1;
      int size = 0;
      if(getBlockCount() == 0) {
         return -1;
      } else {
         for(int i = 9; i < 36; ++i) {
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               Item item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
               if(item instanceof ItemBlock && isValid(item) && is.stackSize > size) {
                  size = is.stackSize;
                  slot = i;
               }
            }
         }

         return slot;
      }
   }

   public int getBiggestBlockSlotHotbar() {
      int slot = -1;
      int size = 0;
      if(getBlockCount() == 0) {
         return -1;
      } else {
         for(int i = 36; i < 45; ++i) {
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               Item item = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
               if(item instanceof ItemBlock && isValid(item) && is.stackSize > size) {
                  size = is.stackSize;
                  slot = i;
               }
            }
         }

         return slot;
      }
   }

   private boolean hotbarContainBlock() {
      int i = 36;

      while(i < 45) {
         try {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemBlock && isValid(stack.getItem())) {
               return true;
            }

            ++i;
         } catch (Exception var3) {
            ;
         }
      }

      return false;
   }

   private int getBlockSlot() {
      for(int i2 = 0; i2 < 9; ++i2) {
         ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i2];
         if(itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            return i2;
         }
      }

      return -1;
   }

   static class BlockCache {
      private BlockPos position;
      private EnumFacing facing;
      final Scaffold this$0;

      private BlockCache(Scaffold var1, BlockPos position, EnumFacing facing) {
         this.this$0 = var1;
         this.position = position;
         this.facing = facing;
      }

      private BlockPos getPosition() {
         return this.position;
      }

      private EnumFacing getFacing() {
         return this.facing;
      }

      static BlockPos access$0(Scaffold.BlockCache var0) {
         return var0.getPosition();
      }

      static EnumFacing access$1(Scaffold.BlockCache var0) {
         return var0.getFacing();
      }

      static BlockPos access$2(Scaffold.BlockCache var0) {
         return var0.position;
      }

      static EnumFacing access$3(Scaffold.BlockCache var0) {
         return var0.facing;
      }

      BlockCache(Scaffold var1, BlockPos var2, EnumFacing var3, Scaffold.BlockCache var4) {
         this(var1, var2, var3);
      }
   }
}
