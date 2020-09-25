package me.skidsense.module.collection.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.friend.FriendManager;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.KillAura.AuraPriority;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TeamUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;

public class BowAimBot extends Mod {
	
	public Mode<AimPriority> priority = new Mode<AimPriority>("Priority", "Priority", AimPriority.values(), AimPriority.Crosshair);
	public Option<Boolean> players = new Option<Boolean>("Players", "Players", true);
	public Option<Boolean> mobs = new Option<Boolean>("Mobs", "Mobs", false);
	public Option<Boolean> animals = new Option<Boolean>("Animals", "Animals", false);
	public Option<Boolean> invis = new Option<Boolean>("Invisible", "Invisible", false);
	public Option<Boolean> villager = new Option<Boolean>("Villager", "Villager", false);
	public Option<Boolean> silent = new Option<Boolean>("Silent", "Silent", true);
	public Numbers<Double> angle = new Numbers<Double>("FOV", "FOV", 360.0, 1.0, 360.0, 1.0);
	
	private EntityLivingBase target;
	private float velocity;
	   
	public BowAimBot() {
		super("Bow Aim Bot", new String[] {"BowAimBot"}, ModuleType.Visual);	
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	@Sub
	public void onPreUpdate(EventPreUpdate eventPreUpdate) {
		if (Minecraft.getMinecraft().thePlayer.rotationPitch <= -80.0F || Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() == null || !(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)) {
            this.target = null;
            return;
         }
         int bowCurrentCharge = Minecraft.getMinecraft().thePlayer.getItemInUseDuration();
         this.velocity = (float)bowCurrentCharge / 20.0F;
         this.velocity = (this.velocity * this.velocity + this.velocity * 2.0F) / 3.0F;
         if ((double)this.velocity < 0.1D) {
            return;
         }

         if (this.velocity > 1.0F) {
            this.velocity = 1.0F;
         }
         target = getTargets().get(0);
         double distanceToEnt = (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(this.target);
         double predictX = this.target.posX + (this.target.posX - this.target.lastTickPosX) * (distanceToEnt / (double)this.getVelocity() + 0.0D);
         double predictZ = this.target.posZ + (this.target.posZ - this.target.lastTickPosZ) * (distanceToEnt / (double)this.getVelocity() + 0.0D);
         double x = predictX - Minecraft.getMinecraft().thePlayer.posX;
         double z = predictZ - Minecraft.getMinecraft().thePlayer.posZ;
         double h = this.target.posY + (double)this.target.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.posY + 0.9D + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
         double h2 = Math.sqrt(x * x + z * z);
         Math.sqrt(h2 * h2 + h * h);
         float yaw = (float)(Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F;
         float pitch = -RotationUtil.getTrajAngleSolutionLow((float)h2, (float)h, this.velocity);
         if(silent.getValue()) {
             eventPreUpdate.setYaw(yaw);
             eventPreUpdate.setPitch(pitch); 
         }else {
             Minecraft.getMinecraft().thePlayer.rotationYaw = yaw;
             Minecraft.getMinecraft().thePlayer.rotationPitch = pitch;	
		}
	}
	
	private List<EntityLivingBase> getTargets() {
		ArrayList<EntityLivingBase> list = new ArrayList<EntityLivingBase>();
		Iterator<Entity> loadedentity = mc.theWorld.getLoadedEntityList().iterator();
		while(loadedentity.hasNext()) {
			Object o = loadedentity.next();
			if (o instanceof EntityLivingBase) {
				EntityLivingBase entityLivingBase = (EntityLivingBase)o;
				if (validEntity(entityLivingBase))
					list.add(entityLivingBase);
			}
		}
		return sortList(list);
	}
	
	private List<EntityLivingBase> sortList(List<EntityLivingBase> list) {
		if (this.priority.getValue() == AimPriority.Range) {
			list.sort((o1, o2) -> {
                double range1 = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(o1);
                double range2 = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(o2);
                return (range1 < range2) ? -1 : (range1 == range2) ? 0 : 1;
            });
		}
		if (this.priority.getValue() == AimPriority.Crosshair) {
			list.sort((o1, o2) -> {
                double rot1 = RotationUtil.getRotationToEntity(o1)[0];
                double rot2 = RotationUtil.getRotationToEntity(o2)[0];
                double h1 = (Minecraft.getMinecraft().thePlayer.rotationYaw - rot1) ;
                double h2 = (Minecraft.getMinecraft().thePlayer.rotationYaw - rot2) ;
                return (h1 < h2) ? -1 : (h1 == h2) ? 0 : 1;
            });
		}
		if (this.priority.getValue() == AimPriority.Health) {
            list.sort((o1, o2) -> {
                double h1 = ((EntityLivingBase) o1).getHealth();
                double h2 = ((EntityLivingBase) o2).getHealth();
                return (h1 < h2) ? -1 : (h1 == h2) ? 0 : 1;
            });
		}
		return list;
	}
	
	private boolean validEntity(EntityLivingBase entity) {
		   	AntiBot ab = (AntiBot) Client.getModuleManager().getModuleByClass(AntiBot.class);
		      boolean players = this.players.getValue();
		      boolean animals = this.animals.getValue();
		      boolean mobs = this.mobs.getValue();
		      boolean villager = this.villager.getValue();
		      boolean invis = this.invis.getValue();
		      boolean teams = Client.getModuleManager().getModuleByClass(Teams.class).isEnabled();
		      if (mc.thePlayer.getHealth() > 0.0F && entity.getHealth() > 0.0F && !entity.isDead) {
		         if (mc.thePlayer.canEntityBeSeen(entity)) {
		            if (entity instanceof EntityPlayer && players) {
		               if (!ab.isServerBot(entity) && !entity.isPlayerSleeping()) {
		                  EntityPlayer ent = (EntityPlayer)entity;
		                  return (!TeamUtils.isTeam(mc.thePlayer, ent) || !teams) && (!ent.isInvisible() || invis) && !FriendManager.isFriend(ent.getName());
		               }
		               return false;
		            }
		            return (entity instanceof EntityMob || entity instanceof EntitySlime) && mobs || entity instanceof EntityAnimal && animals || entity instanceof EntityVillager && villager;
		         }
		      }
		      return false;
	   }
	   
	private float getVelocity() {
		float vel = this.velocity;
		return vel * 2.0F;
	}
	
	public enum AimPriority {
		Crosshair, Range , Health;
	}
}
