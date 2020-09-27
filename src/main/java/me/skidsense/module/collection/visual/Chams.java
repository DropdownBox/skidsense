package me.skidsense.module.collection.visual;

import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class Chams extends Mod {
	
	public Option<Boolean> players = new Option<Boolean>("Players", "Players", true);
	public Option<Boolean> mobs = new Option<Boolean>("Mobs", "Mobs", true);
	public Option<Boolean> animals = new Option<Boolean>("Animals", "Animals", false);
	public Option<Boolean> invisibles = new Option<Boolean>("Invisibles", "Invisibles", false);
	public Option<Boolean> passives = new Option<Boolean>("Passives", "Passives", false);
	public Option<Boolean> colored = new Option<Boolean>("Colored", "Colored", false);
	public Option<Boolean> hands = new Option<Boolean>("Hands", "Hands", false);
	public Option<Boolean> flat = new Option<Boolean>("Flat", "Flat", true);
	public Option<Boolean> rainbow = new Option<Boolean>("Rainbow", "Rainbow", false);
					
	public Numbers<Double> visiblered = new Numbers<Double>("VisibleRed", "VisibleRed", 1.0, 0.001, 1.0, 0.001);
	public Numbers<Double> visiblegreen = new Numbers<Double>("VisibleGreen", "VisibleGreen", 1.0, 0.001, 1.0, 0.001);
	public Numbers<Double> visibleblue = new Numbers<Double>("VisibleBlue", "VisibleBlue", 1.0, 0.001, 1.0, 0.001);
	
	public Numbers<Double> hiddenred = new Numbers<Double>("HiddenRed", "HiddenRed", 1.0, 0.001, 1.0, 0.001);
	public Numbers<Double> hiddengreen = new Numbers<Double>("HiddenGreen", "HiddenGreen", 1.0, 0.001, 1.0, 0.001);
	public Numbers<Double> hiddenblue = new Numbers<Double>("HiddenBlue", "HiddenBlue", 1.0, 0.001, 1.0, 0.001);
	
	public Numbers<Double> alpha = new Numbers<Double>("Alpha", "Alpha", 1.0, 0.001, 1.0, 0.001);
	
	public Chams() {
		super("Chams", new String[]{"Chams"}, ModuleType.Visual);
	}
	
    public boolean isValid(EntityLivingBase entity) {
        return isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisibles.getValue());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.getValue() && entity instanceof EntityPlayer) || (mobs.getValue() && (entity instanceof EntityMob || entity instanceof EntitySlime) || (passives.getValue() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.getValue() && entity instanceof EntityAnimal));
    }
}
