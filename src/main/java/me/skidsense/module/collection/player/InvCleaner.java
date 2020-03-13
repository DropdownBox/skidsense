package me.skidsense.module.collection.player;

import java.util.ArrayList;

import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.management.ModuleManager;
import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.AutoArmor;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class InvCleaner extends Module {
   private Numbers BlockCap = new Numbers("BlockCap", "BlockCap", Double.valueOf(128.0D), Double.valueOf(-1.0D), Double.valueOf(256.0D), Double.valueOf(8.0D));
   private Numbers Delay = new Numbers("Delay", "Delay", Double.valueOf(1.0D), Double.valueOf(0.0D), Double.valueOf(10.0D), Double.valueOf(1.0D));
   private Option Food = new Option("Food", "Food", Boolean.valueOf(true));
   private Option sort = new Option("sort", "sort", Boolean.valueOf(true));
   private Option Archery = new Option("Archery", "Archery", Boolean.valueOf(true));
   private Option Sword = new Option("Sword", "Sword", Boolean.valueOf(true));
   private Mode Mode = new Mode("Mode", "Mode", EMode.values(), EMode.Basic);
   private Option InvCleaner = new Option("InvCleaner", "InvCleaner", Boolean.valueOf(true));
   private Option OpenInv = new Option("OpenInv", "OpenInv", Boolean.valueOf(true));
   private Option UHC = new Option("UHC", "UHC", Boolean.valueOf(false));
   public static int weaponSlot = 36;
   public static int pickaxeSlot = 37;
   public static int axeSlot = 38;
   public static int shovelSlot = 39;
   TimerUtil timer = new TimerUtil();
   ArrayList whitelistedItems = new ArrayList();

   public InvCleaner() {
      super("Inv Cleaner", new String[]{"InvCleaner"}, ModuleType.Player);
      this.addValues(new Value[]{this.BlockCap, this.Delay, OpenInv,this.Food, this.Archery, this.Sword, this.Mode, this.InvCleaner, this.sort, this.UHC});
   }

   public void onEnable() {
      super.onEnable();
   }

   @EventHandler
   public void onEvent(EventPreUpdate event) {
      InvCleaner i3 = (InvCleaner)Client.getModuleManager().getModuleByName("Inv Cleaner");
      long delay = ((Double)this.Delay.getValue()).longValue() * 50L;
      long Adelay = ((Double)Delay.getValue()).longValue() * 50L;
      if(this.timer.check((float)Adelay) && i3.isEnabled() && (i3.Mode.getValue() != OpenInv.getValue() || mc.currentScreen instanceof GuiInventory) && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
         this.getBestArmor();
      }

      if(i3.isEnabled()) {
         for(int type = 1; type < 5; ++type) {
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getStack();
               if(!isBestArmor(is, type)) {
                  return;
               }
            } else if(this.invContainsType(type - 1)) {
               return;
            }
         }
      }

      if(this.Mode.getValue() != EMode.OpenInv || mc.currentScreen instanceof GuiInventory) {
         if(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if(this.timer.check((float)delay) && weaponSlot >= 36) {
               Minecraft var14 = mc;
               if(!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
                  this.getBestWeapon(weaponSlot);
               } else {
                  Minecraft var10001 = mc;
                  if(!this.isBestWeapon(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) {
                     this.getBestWeapon(weaponSlot);
                  }
               }
            }

            if(((Boolean)this.sort.getValue()).booleanValue()) {
               if(this.timer.check((float)delay) && pickaxeSlot >= 36) {
                  this.getBestPickaxe(pickaxeSlot);
               }

               if(this.timer.check((float)delay) && shovelSlot >= 36) {
                  this.getBestShovel(shovelSlot);
               }

               if(this.timer.check((float)delay) && axeSlot >= 36) {
                  this.getBestAxe(axeSlot);
               }
            }

            if(this.timer.check((float)delay) && ((Boolean)this.InvCleaner.getValue()).booleanValue()) {
               for(int i = 9; i < 45; ++i) {
                  Minecraft var15 = mc;
                  if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                     var15 = mc;
                     ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
                     if(this.shouldDrop(is, i)) {
                        this.drop(i);
                        timer.reset();
                        if(delay > 0L) {
                           break;
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public static boolean isBestArmor(ItemStack stack, int type) {
       float prot = getProtection(stack);
       String strType = "";
       if(type == 1) {
          strType = "helmet";
       } else if(type == 2) {
          strType = "chestplate";
       } else if(type == 3) {
          strType = "leggings";
       } else if(type == 4) {
          strType = "boots";
       }

       if(!stack.getUnlocalizedName().contains(strType)) {
          return false;
       } else {
          for(int i = 5; i < 45; ++i) {
             if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                   return false;
                }
             }
          }

          return true;
       }
    }
   
   public void shiftClick(int slot) {
      Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, 0, 1, Minecraft.getMinecraft().thePlayer);
   }

   public void swap(int slot1, int hotbarSlot) {
      Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, Minecraft.getMinecraft().thePlayer);
   }

   public void drop(int slot) {
      Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, 1, 4, Minecraft.getMinecraft().thePlayer);
   }

   public boolean isBestWeapon(ItemStack stack) {
      float damage = this.getDamage(stack);

      for(int i = 9; i < 45; ++i) {
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(this.getDamage(is) > damage && (is.getItem() instanceof ItemSword || !((Boolean)this.Sword.getValue()).booleanValue())) {
               return false;
            }
         }
      }

      if(!(stack.getItem() instanceof ItemSword) && ((Boolean)this.Sword.getValue()).booleanValue()) {
         return false;
      } else {
         return true;
      }
   }

   public void getBestWeapon(int slot) {
      for(int i = 9; i < 45; ++i) {
         Minecraft var10000 = mc;
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            var10000 = mc;
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(this.isBestWeapon(is) && this.getDamage(is) > 0.0F && (is.getItem() instanceof ItemSword || !((Boolean)this.Sword.getValue()).booleanValue())) {
               this.swap(i, slot - 36);
               timer.reset();
               break;
            }
         }
      }

   }

   private float getDamage(ItemStack stack) {
      float damage = 0.0F;
      Item item = stack.getItem();
      if(item instanceof ItemTool) {
         ItemTool tool = (ItemTool)item;
         damage += tool.getMaxDamage();
      }

      if(item instanceof ItemSword) {
         ItemSword sword = (ItemSword)item;
         damage += sword.getDamageVsEntity();
      }

      damage = damage + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01F;
      return damage;
   }

   public boolean shouldDrop(ItemStack stack, int slot) {
      if(stack.getDisplayName().contains("(点击右键)")) {
         return false;
      } else if(stack.getDisplayName().toLowerCase().contains("(right click)")) {
         return false;
      } else {
         if(((Boolean)this.UHC.getValue()).booleanValue()) {
            if(stack.getDisplayName().toLowerCase().contains("apple")) {
               return false;
            }

            if(stack.getDisplayName().toLowerCase().contains("head")) {
               return false;
            }

            if(stack.getDisplayName().toLowerCase().contains("gold")) {
               return false;
            }

            if(stack.getDisplayName().toLowerCase().contains("crafting table")) {
               return false;
            }

            if(stack.getDisplayName().toLowerCase().contains("stick")) {
               return false;
            }
         }

         if(slot == weaponSlot) {
            Minecraft var10001 = mc;
            if(this.isBestWeapon(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) {
               return false;
            }
         }

         if(slot == pickaxeSlot) {
            Minecraft var6 = mc;
            if(this.isBestPickaxe(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0) {
               return false;
            }
         }

         if(slot == axeSlot) {
            Minecraft var7 = mc;
            if(this.isBestAxe(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0) {
               return false;
            }
         }

         if(slot == shovelSlot) {
            Minecraft var8 = mc;
            if(this.isBestShovel(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()) && shovelSlot >= 0) {
               return false;
            }
         }

         if(stack.getItem() instanceof ItemArmor) {
            for(int type = 1; type < 5; ++type) {
               Minecraft var10000 = mc;
               if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                  var10000 = mc;
                  ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                  if(isBestArmor(is, type)) {
                     continue;
                  }
               }

               if(isBestArmor(stack, type)) {
                  return false;
               }
            }
         }

         if(!(stack.getItem() instanceof ItemBlock) || this.getBlockCount() <= ((Double)this.BlockCap.getValue()).intValue() && !Scaffold.blacklistedBlocks.contains(((ItemBlock)stack.getItem()).getBlock())) {
            if(stack.getItem() instanceof ItemPotion && this.isBadPotion(stack)) {
               return true;
            } else if(stack.getItem() instanceof ItemFood && ((Boolean)this.Food.getValue()).booleanValue() && !(stack.getItem() instanceof ItemAppleGold)) {
               return true;
            } else if(!(stack.getItem() instanceof ItemHoe) && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemArmor)) {
               if((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow")) && ((Boolean)this.Archery.getValue()).booleanValue()) {
                  return true;
               } else if(!stack.getItem().getUnlocalizedName().contains("tnt") && !stack.getItem().getUnlocalizedName().contains("stick") && !stack.getItem().getUnlocalizedName().contains("egg") && !stack.getItem().getUnlocalizedName().contains("string") && !stack.getItem().getUnlocalizedName().contains("cake") && !stack.getItem().getUnlocalizedName().contains("mushroom") && !stack.getItem().getUnlocalizedName().contains("flint") && !stack.getItem().getUnlocalizedName().contains("compass") && !stack.getItem().getUnlocalizedName().contains("dyePowder") && !stack.getItem().getUnlocalizedName().contains("feather") && !stack.getItem().getUnlocalizedName().contains("bucket") && (!stack.getItem().getUnlocalizedName().contains("chest") || stack.getDisplayName().toLowerCase().contains("collect")) && !stack.getItem().getUnlocalizedName().contains("snow") && !stack.getItem().getUnlocalizedName().contains("fish") && !stack.getItem().getUnlocalizedName().contains("enchant") && !stack.getItem().getUnlocalizedName().contains("exp") && !stack.getItem().getUnlocalizedName().contains("shears") && !stack.getItem().getUnlocalizedName().contains("anvil") && !stack.getItem().getUnlocalizedName().contains("torch") && !stack.getItem().getUnlocalizedName().contains("seeds") && !stack.getItem().getUnlocalizedName().contains("leather") && !stack.getItem().getUnlocalizedName().contains("reeds") && !stack.getItem().getUnlocalizedName().contains("skull") && !stack.getItem().getUnlocalizedName().contains("record") && !stack.getItem().getUnlocalizedName().contains("snowball") && !(stack.getItem() instanceof ItemGlassBottle) && !stack.getItem().getUnlocalizedName().contains("piston")) {
                  return false;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   }

   public ArrayList getWhitelistedItem() {
      return this.whitelistedItems;
   }

   private int getBlockCount() {
      int blockCount = 0;

      for(int i = 0; i < 45; ++i) {
         Minecraft var10000 = mc;
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            var10000 = mc;
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if(is.getItem() instanceof ItemBlock && !Scaffold.blacklistedBlocks.contains(((ItemBlock)item).getBlock())) {
               blockCount += is.stackSize;
            }
         }
      }

      return blockCount;
   }

   private void getBestPickaxe(int slot) {
      for(int i = 9; i < 45; ++i) {
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(this.isBestPickaxe(is) && pickaxeSlot != i && !this.isBestWeapon(is)) {
               if(!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                  this.swap(i, pickaxeSlot - 36);
                  timer.reset();
                  if(((Double)this.Delay.getValue()).longValue() > 0L) {
                     return;
                  }
               } else {
                  if(!this.isBestPickaxe(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                     this.swap(i, pickaxeSlot - 36);
                     timer.reset();
                     if(((Double)this.Delay.getValue()).longValue() > 0L) {
                        return;
                     }
                  }
               }
            }
         }
      }

   }

   private void getBestShovel(int slot) {
      for(int i = 9; i < 45; ++i) {
         Minecraft var10000 = mc;
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            var10000 = mc;
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(this.isBestShovel(is) && shovelSlot != i && !this.isBestWeapon(is)) {
               if(!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                  this.swap(i, shovelSlot - 36);
                  timer.reset();
                  if(((Double)this.Delay.getValue()).longValue() > 0L) {
                     return;
                  }
               } else {
                  if(!this.isBestShovel(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())) {
                     this.swap(i, shovelSlot - 36);
                     timer.reset();
                     if(((Double)this.Delay.getValue()).longValue() > 0L) {
                        return;
                     }
                  }
               }
            }
         }
      }

   }

   private void getBestAxe(int slot) {
      for(int i = 9; i < 45; ++i) {
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            if(this.isBestAxe(is) && axeSlot != i && !this.isBestWeapon(is)) {
               if(!Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                  this.swap(i, axeSlot - 36);
                  timer.reset();
                  if(((Double)this.Delay.getValue()).longValue() > 0L) {
                     return;
                  }
               } else {
                  Minecraft var10001 = mc;
                  if(!this.isBestAxe(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(axeSlot).getStack())) {
                     this.swap(i, axeSlot - 36);
                     timer.reset();
                     if(((Double)this.Delay.getValue()).longValue() > 0L) {
                        return;
                     }
                  }
               }
            }
         }
      }

   }

   private boolean isBestPickaxe(ItemStack stack) {
      Item item = stack.getItem();
      if(!(item instanceof ItemPickaxe)) {
         return false;
      } else {
         float value = this.getToolEffect(stack);

         for(int i = 9; i < 45; ++i) {
            Minecraft var10000 = mc;
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               var10000 = mc;
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
               if(this.getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean isBestShovel(ItemStack stack) {
      Item item = stack.getItem();
      if(!(item instanceof ItemSpade)) {
         return false;
      } else {
         float value = this.getToolEffect(stack);

         for(int i = 9; i < 45; ++i) {
            Minecraft var10000 = mc;
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               var10000 = mc;
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
               if(this.getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private boolean isBestAxe(ItemStack stack) {
      Item item = stack.getItem();
      if(!(item instanceof ItemAxe)) {
         return false;
      } else {
         float value = this.getToolEffect(stack);

         for(int i = 9; i < 45; ++i) {
            Minecraft var10000 = mc;
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               var10000 = mc;
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
               if(this.getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !this.isBestWeapon(stack)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private float getToolEffect(ItemStack stack) {
      Item item = stack.getItem();
      if(!(item instanceof ItemTool)) {
         return 0.0F;
      } else {
         String name = item.getUnlocalizedName();
         ItemTool tool = (ItemTool)item;
         float value = 1.0F;
         if(item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if(name.toLowerCase().contains("gold")) {
               value -= 5.0F;
            }
         } else if(item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if(name.toLowerCase().contains("gold")) {
               value -= 5.0F;
            }
         } else {
            if(!(item instanceof ItemAxe)) {
               return 1.0F;
            }

            value = tool.getStrVsBlock(stack, Blocks.log);
            if(name.toLowerCase().contains("gold")) {
               value -= 5.0F;
            }
         }

         value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D);
         value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0D);
         return value;
      }
   }

   private boolean isBadPotion(ItemStack stack) {
      if(stack != null && stack.getItem() instanceof ItemPotion) {
         ItemPotion potion = (ItemPotion)stack.getItem();
         if(potion.getEffects(stack) == null) {
            return true;
         }

         for(Object o : potion.getEffects(stack)) {
            PotionEffect effect = (PotionEffect)o;
            if(effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
               return true;
            }
         }
      }

      return false;
   }

   boolean invContainsType(int type) {
      for(int i = 9; i < 45; ++i) {
         Minecraft var10000 = mc;
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            var10000 = mc;
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if(item instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)item;
               if(type == armor.armorType) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void getBestArmor() {
      for(int type = 1; type < 5; ++type) {
         Minecraft var10000 = mc;
         if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
            var10000 = mc;
            ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(4 + type).getStack();
            if(isBestArmor(is, type)) {
               continue;
            }

            this.drop(4 + type);
         }

         for(int i = 9; i < 45; ++i) {
            var10000 = mc;
            if(Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               var10000 = mc;
               ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
               if(isBestArmor(is, type) && getProtection(is) > 0.0F) {
                  this.shiftClick(i);
                  timer.reset();
                  if(((Double)this.Delay.getValue()).longValue() > 0L) {
                     return;
                  }
               }
            }
         }
      }

   }

   public static float getProtection(ItemStack stack) {
       float prot = 0.0F;
       if(stack.getItem() instanceof ItemArmor) {
          ItemArmor armor = (ItemArmor)stack.getItem();
          prot = (float)((double)prot + (double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D);
          prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0D);
          prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0D);
          prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0D);
          prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0D);
          prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0D);
       }

       return prot;
    }
   
   static enum EMode {
      Basic,
      OpenInv;
   }
}
