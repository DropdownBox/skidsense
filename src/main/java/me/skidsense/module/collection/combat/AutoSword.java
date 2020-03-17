package me.skidsense.module.collection.combat;


import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;

import java.awt.Color;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoSword
extends Mod {
    public TimerUtil timer = new TimerUtil();
	public static Numbers<Double> delay = new Numbers<Double>("Delay", "Delay", 100.0, 1.0, 2000.0, 1.0);
    public AutoSword() {
        super("Auto Sword", new String[]{"autosword"}, ModuleType.Fight);
        this.setColor(new Color(208, 30, 142).getRGB());
        this.addValues(delay);
    }

    @Sub
    private void onUpdate(EventPreUpdate event) {
        if (!timer.hasReached(delay.getValue()) || (mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory)))
            return;
        int best = -1;
        float swordDamage = 0;
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    float swordD = getItemDamage(is);
                    if (swordD > swordDamage) {
                        swordDamage = swordD;
                        best = i;
                    }
                }
            }
        }
        final ItemStack current = mc.thePlayer.inventoryContainer.getSlot(36).getStack();
        if (best != -1 && (current == null || !(current.getItem() instanceof ItemSword) || swordDamage > getItemDamage(current))) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, best, 0, 2, mc.thePlayer);
            timer.reset();
        }
    }

    private float getItemDamage(final ItemStack itemStack) {
        float damage = ((ItemSword) itemStack.getItem()).getDamageVsEntity();
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01f;
        return damage;
    }
}

