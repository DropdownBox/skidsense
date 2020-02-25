package me.skidsense.module.collection.world;

import java.awt.Color;

import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventTick;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.hooks.value.Value;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ChestStealer
extends Module {
    public static Numbers<Double> delay = new Numbers<Double>("Delay", "Delay", 50.0, 0.0, 1000.0, 10.0);
	private Option<Boolean> tarshskip = new Option<Boolean>("Close", "Close", true);
    private TimerUtil timer = new TimerUtil();
    private boolean isStealing;
    
    public ChestStealer() {
        super("Chest Steal", new String[]{"cheststealS","cheststeal", "chests", "stealer"}, ModuleType.World);
        this.addValues(this.delay,tarshskip);
        this.setColor(new Color(218, 97, 127).getRGB());
    }

    @EventHandler
    private void onUpdate(EventPreUpdate event) {
    	if (this.mc.currentScreen instanceof GuiChest) {
            String[] list;
            GuiChest guiChest = (GuiChest)this.mc.currentScreen;
            String name = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
            for (String str : list = new String[]{"menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock"}) {
                if (!name.contains(str)) continue;
                return;
            }
            this.isStealing = true;
            boolean full = true;
            for (ItemStack item : Minecraft.getMinecraft().thePlayer.inventory.mainInventory) {
                if (item != null) continue;
                full = false;
                break;
            }
            boolean containsItems = false;
            if (!full) {
                ItemStack stack;
                int index;
                for (index = 0; index < guiChest.lowerChestInventory.getSizeInventory(); ++index) {
                    stack = guiChest.lowerChestInventory.getStackInSlot(index);
                    if (stack == null || this.isBad(stack)) continue;
                    containsItems = true;
                    break;
                }
                if (containsItems) {
                    for (index = 0; index < guiChest.lowerChestInventory.getSizeInventory(); ++index) {
                        stack = guiChest.lowerChestInventory.getStackInSlot(index);
                        if (stack == null || !this.timer.delay(delay.getValue()) || this.isBad(stack)) continue;
                        Minecraft.getMinecraft().playerController.windowClick(guiChest.inventorySlots.windowId, index, 0, 1, Minecraft.getMinecraft().thePlayer);
                            Minecraft.getMinecraft().playerController.windowClick(guiChest.inventorySlots.windowId, index, 1, 1, Minecraft.getMinecraft().thePlayer);
                        this.timer.reset();
                    }
                } else if (this.tarshskip.getValue()) {
                    Minecraft.getMinecraft().thePlayer.closeScreen();
                    this.isStealing = false;
                }
            } else if (this.tarshskip.getValue()) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
                this.isStealing = false;
            }
        } else {
            this.isStealing = false;
        }
    }

    private boolean isEmpty() {
        if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (this.isBad(itemStack) || itemStack != null && itemStack.getItem() != null) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
    
    private boolean isBad(ItemStack item) {
        if (!tarshskip.getValue()) {
            return false;
        }
        return item != null && (item.getItem().getUnlocalizedName().contains("tnt") || item.getItem().getUnlocalizedName().contains("stick") || item.getItem().getUnlocalizedName().contains("egg") && !item.getItem().getUnlocalizedName().contains("leg") || item.getItem().getUnlocalizedName().contains("string") || item.getItem().getUnlocalizedName().contains("flint") || item.getItem().getUnlocalizedName().contains("compass") || item.getItem().getUnlocalizedName().contains("feather") || item.getItem().getUnlocalizedName().contains("bucket") || item.getItem().getUnlocalizedName().contains("snow") || item.getItem().getUnlocalizedName().contains("fish") || item.getItem().getUnlocalizedName().contains("enchant") || item.getItem().getUnlocalizedName().contains("exp") || item.getItem().getUnlocalizedName().contains("shears") || item.getItem().getUnlocalizedName().contains("anvil") || item.getItem().getUnlocalizedName().contains("torch") || item.getItem().getUnlocalizedName().contains("seeds") || item.getItem().getUnlocalizedName().contains("leather") || item.getItem() instanceof ItemPickaxe || item.getItem() instanceof ItemGlassBottle || item.getItem() instanceof ItemTool || item.getItem().getUnlocalizedName().contains("piston") || item.getItem().getUnlocalizedName().contains("potion") && this.isBadPotion(item));
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion)stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect o : potion.getEffects(stack)) {
                    PotionEffect effect = o;
                    if (effect.getPotionID() != Potion.poison.getId() && effect.getPotionID() != Potion.harm.getId() && effect.getPotionID() != Potion.moveSlowdown.getId() && effect.getPotionID() != Potion.weakness.getId()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private float getDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword)) {
            return 0.0f;
        }
        return (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + ((ItemSword)stack.getItem()).getDamageVsEntity();
    }
}

