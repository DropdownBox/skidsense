package me.skidsense.module.collection.move;

import me.skidsense.Client;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventMove;
import me.skidsense.hooks.events.EventPreUpdate;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Mode;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.hooks.value.Option;
import me.skidsense.management.friend.FriendManager;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.combat.AntiBot;
import me.skidsense.module.collection.combat.KillAura;
import me.skidsense.module.collection.player.AntiFall;
import me.skidsense.module.collection.player.Scaffold;
import me.skidsense.util.MoveUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.RotationUtil;
import me.skidsense.util.TeamUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class AutoStrafe extends Mod {
	public Option<Boolean> players = new Option<Boolean>("Players", "Players", true);
	public Option<Boolean> mobs = new Option<Boolean>("Mobs", "Mobs", false);
	public Option<Boolean> animals = new Option<Boolean>("Animals", "Animals", false);
	public Option<Boolean> invis = new Option<Boolean>("Invisible", "Invisible", false);
	public Option<Boolean> villager = new Option<Boolean>("Villager", "Villager", false);
	public Option<Boolean> Render = new Option<Boolean>("Render", "Render", true);
	public static Option<Boolean> OnSpace = new Option<Boolean>("OnSpace", "OnSpace", true);
	public Numbers<Double> Distance = new Numbers<Double>("Distance", "Distance", 1.6,0.1,3.0,0.1);
    public static EntityLivingBase target;
    private List<EntityLivingBase> targets = new ArrayList<>();
	public static Vec3 indexPos;
    public static int index, arraySize;
    private boolean set, changeDir;
	private int direction = -1;
//	private Setting range;
//	private Setting render;
//	private Setting renderheight;
//	private Setting space;

	public AutoStrafe() {
		super("Auto Strafe",new String[]{"TargetStrafe","AutoStrafe"}, ModuleType.Move);
//		Sight.instance.sm.rSetting(space = new Setting("OnSpace", this, true));
//		Sight.instance.sm.rSetting(render = new Setting("Render", this, true));
//		Sight.instance.sm.rSetting(renderheight = new Setting("RenderHeight", this, 0.05, 0.01, 1, false));
//		Sight.instance.sm.rSetting(range = new Setting("Range", this, 1.6, 0.1, 3, false));
	}

    @Override
    public void onEnable() {
        set = false;
    }

    @Override
    public void onDisable() {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().timer.timerSpeed = 1f;
        set = false;
    }
    
    @Sub
    public void onUpdate(EventPreUpdate event) {
        if (!Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() || (!Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && OnSpace.getValue())) {target = null; set = false;return;}
        target = getTarget();
        if (target != null) {
            final ArrayList<Vec3> posArrayList = new ArrayList<>();
            for (float rotation = 0; rotation < (3.141592f * 2.0); rotation += 3.141592f * 2.0f / 27f) {
                final Vec3 pos = new Vec3(Distance.getValue() * Math.cos(rotation) + target.posX, target.posY, Distance.getValue() * Math.sin(rotation) + target.posZ);
                posArrayList.add(pos);
            }
            arraySize = posArrayList.size();
            if (!set) {
                final ArrayList<Vec3> posBuffer = new ArrayList<>(posArrayList);
                posBuffer.sort(Comparator.comparingDouble(vec3 -> Minecraft.getMinecraft().thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord)));
                index = posArrayList.indexOf(posBuffer.get(0));
                set = true;
            } else {
                final BlockPos blockPos = new BlockPos(posArrayList.get(index).xCoord, posArrayList.get(index).yCoord, posArrayList.get(index).zCoord);
                indexPos = new Vec3(blockPos.getX() + 0.5f, posArrayList.get(index).yCoord, blockPos.getZ());
                if (!(!inVoid(indexPos) && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY, indexPos.zCoord)).getBlock().getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY, indexPos.zCoord), Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY, indexPos.zCoord))) == null && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY + 1, indexPos.zCoord)).getBlock().getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY + 1, indexPos.zCoord), Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY + 1, indexPos.zCoord))) == null && Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY + 2, indexPos.zCoord)).getBlock().getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY + 2, indexPos.zCoord), Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY + 2, indexPos.zCoord))) == null)) {
                    Speed.strafeDirection = !Speed.strafeDirection;
                    if (!Speed.strafeDirection) {
                        if (index + 1 > posArrayList.size() - 1) index = 0;
                        else index++;
                    } else {
                        if (index - 1 < 0) index = posArrayList.size() - 1;
                        else index--;
                    }
                } else {
                    if (Minecraft.getMinecraft().thePlayer.isCollidedHorizontally) {
                        if (!changeDir) {
                            Speed.strafeDirection = !Speed.strafeDirection;
                            changeIndex(posArrayList);
                            changeDir = true;
                        }
                    } else changeDir = false;
                    if (Minecraft.getMinecraft().gameSettings.keyBindRight.isPressed()) {
                        Speed.strafeDirection = true;
                    } else if (Minecraft.getMinecraft().gameSettings.keyBindLeft.isPressed()) {
                        Speed.strafeDirection = false;
                    }
                    if (Minecraft.getMinecraft().thePlayer.getDistance(indexPos.xCoord, Minecraft.getMinecraft().thePlayer.posY, indexPos.zCoord) <= Minecraft.getMinecraft().thePlayer.getDistance(Minecraft.getMinecraft().thePlayer.prevPosX, Minecraft.getMinecraft().thePlayer.prevPosY, Minecraft.getMinecraft().thePlayer.prevPosZ) * 2) {
                        changeIndex(posArrayList);
                    }
                }
            }
        } else {
            set = false;
            index = 0;
            indexPos = null;
        }
    }
    
	@Sub
	public void onRender(EventRender3D e){
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
        for (Entity entity : Minecraft.getMinecraft().theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase.isDead || entityLivingBase == Minecraft.getMinecraft().thePlayer || !isTargetable(entityLivingBase, Minecraft.getMinecraft().thePlayer))
                    continue;
                drawCirle(entity, target != null && entity == target ? new Color(255, 72, 67) : new Color(255, 255, 255), e.getPartialTicks());
            }
        }
	}
	
    private void changeIndex(final ArrayList<Vec3> posArrayList) {
        if (!Speed.strafeDirection) {
            if (index + 1 > posArrayList.size() - 1) index = 0;
            else index++;
        } else {
            if (index - 1 < 0) index = posArrayList.size() - 1;
            else index--;
        }
    }
    
	/*@Sub
	public void onPreUpdate(EventPreUpdate e){
		if (mc.thePlayer.isCollidedHorizontally) {
			if (this.direction == -1) {
				this.direction = 1;
			} else {
				this.direction = -1;
			}
		}
	}

	@Sub
	public void onMove(EventMove e){
		if (this.canStrafe()) {
			this.strafe(e, MoveUtil.getBaseMoveSpeed());
			mc.thePlayer.isAirBorne = true;
			mc.thePlayer.triggerAchievement(StatList.jumpStat);
		}
	}

	@Sub
	public void onRender(EventRender3D e){
		if (KillAura.target != null) {
			this.drawRadius(KillAura.target, ((EventRender3D) e).getPartialTicks(), Distance.getValue());
		}
	}*/

	public void strafe(EventMove e, double moveSpeed) {
		mc.thePlayer.onGround = false;
		float[] rots = RotationUtil.getRotations(KillAura.target);
		double dist = mc.thePlayer.getDistanceToEntity(KillAura.target);
		if (dist >= Distance.getValue()) {
			setSpeed(e, moveSpeed, rots[0], direction, 1);
		} else {
			setSpeed(e, moveSpeed, rots[0], direction, 0);
		}
	}

    private void drawCirle(Entity entity, Color color, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        final double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, partialTicks) - Minecraft.getMinecraft().getRenderManager().renderPosX;
        final double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, partialTicks) - Minecraft.getMinecraft().getRenderManager().renderPosY;
        final double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        GL11.glLineWidth(4.0f);
        final ArrayList<Vec3> posArrayList = new ArrayList<>();
        for (float rotation = 0; rotation < (3.141592f * 2.0); rotation += 3.141592f * 2.0f / 27f) {
            final Vec3 pos = new Vec3(Distance.getValue() * Math.cos(rotation) + x, y, Distance.getValue() * Math.sin(rotation) + z);
            posArrayList.add(pos);
        }
        GL11.glEnable(GL11.GL_LINE_STIPPLE);
        GL11.glLineStipple(4, (short) 0xAAAA);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        {
            final float r = ((float) 1 / 255) * color.getRed();
            final float g = ((float) 1 / 255) * color.getGreen();
            final float b = ((float) 1 / 255) * color.getBlue();
            for (Vec3 pos : posArrayList) {
                GL11.glColor3d(Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() && posArrayList.indexOf(pos) == index ? 0.15f : r, Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() && posArrayList.indexOf(pos) == index ? 0.15f : g, Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() && posArrayList.indexOf(pos) == index ? 1 : b);
                GL11.glVertex3d(pos.xCoord, pos.yCoord, pos.zCoord);
            }
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_STIPPLE);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glLineWidth(1);
        GL11.glPopMatrix();
    }

    private EntityLivingBase getTarget() {
        targets.clear();
        double Dist = Double.MAX_VALUE;
        if (Minecraft.getMinecraft().theWorld != null) {
            for (Object object : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                if ((object instanceof EntityLivingBase)) {
                    EntityLivingBase e = (EntityLivingBase) object;
                    if ((Minecraft.getMinecraft().thePlayer.getDistanceToEntity(e) < Dist)) {
                        if (isTargetable(e, Minecraft.getMinecraft().thePlayer)) {
                            targets.add(e);
                        }
                    }
                }
            }
        }
        if (targets.isEmpty()) return null;
        targets.sort(Comparator.comparingDouble(target -> Minecraft.getMinecraft().thePlayer.getDistanceToEntity(target)));

        return targets.get(0);
    }

    public boolean inVoid(Vec3 vec3) {
        for (int i = (int) Math.ceil(vec3.yCoord); i >= 0; i--) {
            if (Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(vec3.xCoord, i, vec3.zCoord)).getBlock() != Blocks.air) {
                return false;
            }
        }
        return true;
    }

    private boolean isTargetable(EntityLivingBase entity, EntityPlayerSP clientPlayer) {
        return entity.getUniqueID() != clientPlayer.getUniqueID() && entity.isEntityAlive() && !(entity instanceof EntityPlayer && TeamUtils.isTeam(mc.thePlayer , (EntityPlayer) entity)) && !AntiBot.whitepig.contains(entity) && !FriendManager.isFriend(entity.getName()) && !(entity.isInvisible() && !invis.getValue()) && clientPlayer.getDistanceToEntity(entity) <= Distance.getValue() && (entity instanceof EntityPlayer && players.getValue() || (entity instanceof EntityMob || entity instanceof EntityGolem) && mobs.getValue() || (entity instanceof EntityAnimal && animals.getValue()));
    }
    
	public static void setSpeed(final EventMove moveEvent, final double moveSpeed, final float pseudoYaw,
								final double pseudoStrafe, final double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;

		if (forward == 0.0 && strafe == 0.0) {
			moveEvent.setZ(0);
			moveEvent.setX(0);
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += ((forward > 0.0) ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += ((forward > 0.0) ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}
			final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
			final double sin = Math.sin(Math.toRadians(yaw + 90.0f));

			moveEvent.setX((forward * moveSpeed * cos + strafe * moveSpeed * sin));
			moveEvent.setZ((forward * moveSpeed * sin - strafe * moveSpeed * cos));
		}
	}

	private void drawRadius(final Entity entity, final float partialTicks, final double rad) {
		float points = 90F;
		GlStateManager.enableDepth();
		for (double il = 0; il < 4.9E-324; il += 4.9E-324) {
			GL11.glPushMatrix();
			GL11.glDisable(3553);
			GL11.glEnable(2848);
			GL11.glEnable(2881);
			GL11.glEnable(2832);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glHint(3154, 4354);
			GL11.glHint(3155, 4354);
			GL11.glHint(3153, 4354);
			GL11.glDisable(2929);
			GL11.glLineWidth(6.0f);
			GL11.glBegin(3);
			final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
			final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
			final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
			final double pix2 = 6.283185307179586;
			float speed = 5000f;
			float baseHue = System.currentTimeMillis() % (int)speed;
			while (baseHue > speed) {
				baseHue -= speed;
			}
			baseHue /= speed;
			for (int i = 0; i <= 90; ++i) {
				float max = ((float) i + (float)(il * 8)) / points;
				float hue = max + baseHue ;
				while (hue > 1) {
					hue -= 1;
				}
				final float r = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getRed();
				final float g = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getGreen();
				final float b = 0.003921569f * new Color(Color.HSBtoRGB(hue, 0.75F, 1F)).getBlue();
				GL11.glColor3f(r, g, b);
				GL11.glVertex3d(x + rad * Math.cos(i * pix2 / points), y + il, z + rad * Math.sin(i * pix2 / points));
			}
			GL11.glEnd();
			GL11.glDepthMask(true);
			GL11.glEnable(2929);
			GL11.glDisable(2848);
			GL11.glDisable(2881);
			GL11.glEnable(2832);
			GL11.glEnable(3553);
			GL11.glPopMatrix();
			GlStateManager.color(255, 255, 255);
		}
	}

	public static boolean canStrafe() {
		if (OnSpace.getValue() && !mc.gameSettings.keyBindJump.isKeyDown()) {
			return false;
		}
		return KillAura.target != null && Client.getModuleManager().getModuleByClass(KillAura.class).isEnabled() &&Client.getModuleManager().getModuleByClass(Speed.class).isEnabled() && !Client.getModuleManager().getModuleByClass(Scaffold.class).isEnabled();
	}
}
