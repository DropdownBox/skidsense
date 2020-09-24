package me.skidsense.module.collection.combat;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPostUpdate;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TimerUtil;

import java.awt.Color;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AutoPotion
extends Mod {
    private Numbers<Double> health = new Numbers<Double>("Health", "health", 3.0, 0.0, 10.0, 0.5);
	public Option<Boolean> regen = new Option<Boolean>("Regen", "regen", false);
	public Option<Boolean> speed = new Option<Boolean>("Speed", "speed", false);
	public Option<Boolean> PREDICT = new Option<Boolean>("Predict", "predict", false);
    public static boolean potting;
    public TimerUtil timer = new TimerUtil();

    public AutoPotion() {
        super("Auto Potion", new String[]{"autopotion","autopot", "autop", "autosoup"}, ModuleType.Fight);
        this.setColor(new Color(76, 249, 247).getRGB());
    }

    @Sub
    private void onUpdate(EventPreUpdate e) {
        if(timer.check(200)){
        	if(potting)
        		potting = false;
        }
        int spoofSlot = getBestSpoofSlot();
        int pots[] = {6,-1,-1};
        if(regen.getValue())
        	pots[1] = 10;
        if(speed.getValue())
        	pots[2] = 1;
        
        for(int i = 0; i < pots.length; i ++){
        	if(pots[i] == -1)
        		continue;
        	if(pots[i] == 6 || pots[i] == 10){
        		if(timer.check(500) && !mc.thePlayer.isPotionActive(pots[i])){
            		if(mc.thePlayer.getHealth() < health.getValue() * 2){
            			getBestPot(spoofSlot, pots[i]);
            		}
        		}
        	}else
        	if(timer.check(1000) && !mc.thePlayer.isPotionActive(pots[i])){
        		getBestPot(spoofSlot, pots[i]);               		
        	}
        }  
    }

    public void swap(int slot1, int hotbarSlot){
    	mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }
    float[] getRotations(){    	
        double movedPosX = mc.thePlayer.posX + mc.thePlayer.motionX * 26.0D;
        double movedPosY = mc.thePlayer.getEntityBoundingBox().minY - 3.6D;
        double movedPosZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * 26.0D;	
        if(PREDICT.getValue())
        	return RotationUtil.getRotationFromPosition(movedPosX, movedPosZ, movedPosY);
        else
        	return new float[]{mc.thePlayer.rotationYaw, 90};
    }
    int getBestSpoofSlot(){  	
    	int spoofSlot = 5;
    	for (int i = 36; i < 45; i++) {       		
    		if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
     			spoofSlot = i - 36;
     			break;
            }else if(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
            	spoofSlot = i - 36;
     			break;
            }
        }
    	return spoofSlot;
    }
    void getBestPot(int hotbarSlot, int potID){
    	for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() &&(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(is.getItem() instanceof ItemPotion){
              	  ItemPotion pot = (ItemPotion)is.getItem();
              	  if(pot.getEffects(is).isEmpty())
              		  return;
              	  PotionEffect effect = (PotionEffect) pot.getEffects(is).get(0);              	  
                  int potionID = effect.getPotionID();
                  if(potionID == potID)
              	  if(ItemPotion.isSplash(is.getItemDamage()) && isBestPot(pot, is)){
              		  if(36 + hotbarSlot != i)
              			  swap(i, hotbarSlot);
              		  timer.reset();
              		  boolean canpot = true;
              		  int oldSlot = mc.thePlayer.inventory.currentItem;
              		  mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(hotbarSlot));
          			  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(getRotations()[0], getRotations()[1], mc.thePlayer.onGround));
          			  mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
          			  mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
          			  potting = true;
          			  break;
              	  }               	  
                }              
            }
        }
    }
    
    boolean isBestPot(ItemPotion potion, ItemStack stack){
    	if(potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1)
    		return false;
        PotionEffect effect = (PotionEffect) potion.getEffects(stack).get(0);
        int potionID = effect.getPotionID();
        int amplifier = effect.getAmplifier(); 
        int duration = effect.getDuration();
    	for (int i = 9; i < 45; i++) {    		
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {           	
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(is.getItem() instanceof ItemPotion){
                	ItemPotion pot = (ItemPotion)is.getItem();
                	 if (pot.getEffects(is) != null) {
                         for (Object o : pot.getEffects(is)) {
                             PotionEffect effects = (PotionEffect) o;
                             int id = effects.getPotionID();
                             int ampl = effects.getAmplifier(); 
                             int dur = effects.getDuration();
                             if (id == potionID && ItemPotion.isSplash(is.getItemDamage())){
                            	 if(ampl > amplifier){
                            		 return false;
                            	 }else if (ampl == amplifier && dur > duration){
                            		 return false;
                            	 }
                             }                            
                         }
                     }
                }
            }
        }
    	return true;
    }
}

