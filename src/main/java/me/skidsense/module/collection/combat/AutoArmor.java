package me.skidsense.module.collection.combat;

import java.awt.Color;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class AutoArmor
extends Module {
    private Numbers<Double> delay = new Numbers<Double>("Delay", "delay", 50.0, 0.0, 1000.0, 10.0);
    private Option<Boolean> openinv = new Option<Boolean>("OpenInv", "OpenInv", true);
    private TimerUtil timer = new TimerUtil();
    private int slot = 5;
    private double enchantmentValue = -1.0;
    private double protectionValue;
    private int item = -1;

    public AutoArmor() {
        super("Auto Armor", new String[]{"AutoArmor","armorswap", "autoarmour"}, ModuleType.Fight);
        this.addValues(this.delay,this.openinv);
        this.setColor(new Color(27, 104, 204).getRGB());
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        if (this.timer.hasReached(this.delay.getValue()) && !Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && (this.mc.currentScreen != null || !this.openinv.getValue()) && !(this.mc.currentScreen instanceof GuiChat)) {
            for(int b = 5; b <= 8; ++b) {
               if (this.equipArmor(b)) {
                  this.timer.reset();
                  break;
               }
            }
        }
    }
    
    private boolean equipArmor(int b) {
        int currentProtection = -1;
        byte slot = -1;
        ItemArmor current = null;
        if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(b).getStack() != null && Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(b).getStack().getItem() instanceof ItemArmor) {
           current = (ItemArmor)Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(b).getStack().getItem();
           currentProtection = current.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(b).getStack());
        }

        for(byte i = 9; i <= 44; ++i) {
           ItemStack stack = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
           if (stack != null && stack.getItem() instanceof ItemArmor) {
              ItemArmor armor = (ItemArmor)stack.getItem();
              int armorProtection = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
              if (this.checkArmor(armor, b) && (current == null || currentProtection < armorProtection)) {
                 currentProtection = armorProtection;
                 current = armor;
                 slot = i;
              }
           }
        }

        if (slot != -1) {
           boolean isNull = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(b).getStack() == null;
           if (!isNull) {
              this.dropSlot(b);
              return true;
           } else {
              this.clickSlot(slot, 0, true);
              return true;
           }
        } else {
           return false;
        }
     }

    
     private boolean checkArmor(ItemArmor item, int b) {
        return b == 5 && item.getUnlocalizedName().startsWith("item.helmet") || b == 6 && item.getUnlocalizedName().startsWith("item.chestplate") || b == 7 && item.getUnlocalizedName().startsWith("item.leggings") || b == 8 && item.getUnlocalizedName().startsWith("item.boots");
     }

     private void clickSlot(int slot, int mouseButton, boolean shiftClick) {
        this.mc.playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, Minecraft.getMinecraft().thePlayer);
     }

     private void dropSlot(int slot) {
        this.mc.playerController.windowClick(0, slot, 1, 4, Minecraft.getMinecraft().thePlayer);
     }
}

