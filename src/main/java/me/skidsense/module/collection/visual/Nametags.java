package me.skidsense.module.collection.visual;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import me.skidsense.Client;
import me.skidsense.hooks.EventHandler;
import me.skidsense.hooks.events.EventRender3D;
import me.skidsense.hooks.value.Numbers;
import me.skidsense.management.FriendManager;
import me.skidsense.module.Module;
import me.skidsense.module.ModuleType;
import me.skidsense.module.collection.player.Teams;
import me.skidsense.util.MathUtil;
import me.skidsense.util.RenderUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import optifine.Config;
import org.lwjgl.opengl.GL11;

public class Nametags
extends Module {
	private boolean armor = true;
	public boolean formatting = true;
    public Nametags() {
        super("Name Tag", new String[]{"NameTag"}, ModuleType.Visual);
        this.setColor(new Color(29, 187, 102).getRGB());
    }

    @EventHandler
    private void onRender(EventRender3D render) {
    	 if (Minecraft.getMinecraft().theWorld != null) {
             boolean wasBobbing = Minecraft.getMinecraft().gameSettings.viewBobbing;
             Iterator var4 = Minecraft.getMinecraft().theWorld.loadedEntityList.iterator();

             while(var4.hasNext()) {
                Object o = var4.next();
                Entity ent = (Entity)o;
                if (ent != Minecraft.getMinecraft().thePlayer && !ent.isInvisible() && ent instanceof EntityPlayer) {
                   double posX = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double)this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
                   double posY = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double)this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
                   double posZ = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double)this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
   				renderNameTag((EntityPlayer) ent, String.valueOf(ent.getDisplayName()) , posX, posY, posZ);
                }
             }

             Minecraft.getMinecraft().gameSettings.viewBobbing = wasBobbing;
          }
    }
    public void renderNameTag(EntityPlayer entity, String tag, double pX, double pY, double pZ) {
		FontRenderer var12 = this.mc.fontRendererObj;
		pY += (entity.isSneaking() ? 0.5D : 0.7D);
		float var13 = this.mc.thePlayer.getDistanceToEntity(entity) / 6.0F;
		if (var13 < 1.2F) {
			var13 = 1.2F;
		}
		int colour = 16777215;
		if(entity.isInvisible()) {
			colour = 16756480;
		} else if(entity.isSneaking()) {
			colour = 11468800;
		}
		if (!this.formatting == true) {
			tag = ChatColor.stripColor(tag);
		}
			tag = entity.getDisplayName().getFormattedText();
		double health = Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
		ChatColor healthCol;
		if (health < 10D) {
			healthCol = ChatColor.DARK_RED;
		} else {
			if ((health > 10D) && (health < 13D)) {
				healthCol = ChatColor.GOLD;
			} else
				healthCol = ChatColor.DARK_GREEN;
		}
		String distance = null;
		if (mc.thePlayer.getDistanceToEntity(entity) > 50) {
			distance = " \247a" + (int)mc.thePlayer.getDistanceToEntity(entity) +"m \247r";
		} else if(mc.thePlayer.getDistanceToEntity(entity) < 50 && mc.thePlayer.getDistanceToEntity(entity) > 20) {
			distance = " \2476" + (int)mc.thePlayer.getDistanceToEntity(entity) + "m \247r";
		} else if(mc.thePlayer.getDistanceToEntity(entity) < 20) {
			distance = " \247c" + (int)mc.thePlayer.getDistanceToEntity(entity) + "m \247r";
		}
		String ping = null;
		if(this.getPing(entity) < 150) {
			ping = "\247a\247l" + (int)this.getPing(entity) + "ms\247r";
		} else if(this.getPing(entity) > 150 && this.getPing(entity) < 250) {
			ping = "\2476\247l" + (int)this.getPing(entity) + "ms\247r";
		} else if(this.getPing(entity) > 250) {
			ping = "\247c\247l" + (int)this.getPing(entity) + "ms\247r";
		}
		
		String linehealth = null;
		if(health >= 20) {
			linehealth = ChatColor.GREEN + " ||||||||||";
		} else if(health > 18) {
			linehealth = ChatColor.GREEN + " |||||||||" + ChatColor.RED + "|";
		} else if(health > 16) {
			linehealth = ChatColor.GREEN + " ||||||||" + ChatColor.RED + "||";
		} else if(health > 14) {
			linehealth = ChatColor.GREEN + " |||||||" + ChatColor.RED + "|||";
		} else if(health > 12) {
			linehealth = ChatColor.GREEN + " ||||||" + ChatColor.RED + "||||";
		} else if(health > 10) {
			linehealth = ChatColor.GREEN + " |||||" + ChatColor.RED + "|||||";
		} else if(health > 8) {
			linehealth = ChatColor.GREEN + " ||||" + ChatColor.RED + "||||||";
		} else if(health > 6) {
			linehealth = ChatColor.GREEN + " |||" + ChatColor.RED + "|||||||";
		} else if(health > 4) {
			linehealth = ChatColor.GREEN + " ||" + ChatColor.RED + "||||||||";
		} else if(health > 2) {
			linehealth = ChatColor.GREEN + " |" + ChatColor.RED + "|||||||||";
		} else if(health > 0) {
			linehealth =  ChatColor.RED + " ||||||||||";
		}
		
		if (Math.floor(health) == health) {
			StringBuilder allstring = new StringBuilder();
			allstring.append(ping);
			allstring.append(distance);
			allstring.append(tag);
			allstring.append(healthCol);
			allstring.append(linehealth);
			tag = allstring.toString();
		}
		
		RenderManager renderManager = this.mc.getRenderManager();
		int color = 16776960;
		float scale = var13 * 2.0F;
		scale /= 100.0F;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) pX, (float) pY + 1.4F, (float) pZ);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		this.setGLCap(2896, false);
		this.setGLCap(2929, false);
		Tessellator var14 = Tessellator.getInstance();
		net.minecraft.client.renderer.WorldRenderer var15 = var14.getWorldRenderer();
		int width = this.mc.fontRendererObj.getStringWidth(tag) / 2;
		this.setGLCap(3042, true);
		GL11.glBlendFunc(770, 771);

		drawBorderedRect(-width - 2, -(this.mc.fontRendererObj.FONT_HEIGHT + 1), width + 2, 2.0F, 1.0F,-16777216, Integer.MIN_VALUE);
		var12.drawString(tag, -width, -(this.mc.fontRendererObj.FONT_HEIGHT - 1), colour, false);
		GL11.glPushMatrix();
		if (this.armor == true) {
			int xOffset = 0;
			for (ItemStack armourStack : entity.inventory.armorInventory) {
				if (armourStack != null)
					xOffset -= 8;
			}
			Object renderStack;
			if (entity.getHeldItem() != null) {
				xOffset -= 8;
				renderStack = entity.getHeldItem().copy();
				if ((((ItemStack) renderStack).hasEffect())
						&& (((((ItemStack) renderStack).getItem() instanceof ItemTool))
								|| ((((ItemStack) renderStack).getItem() instanceof ItemArmor))))
					((ItemStack) renderStack).stackSize = 1;
				renderItemStack((ItemStack) renderStack, xOffset, -30);
				xOffset += 16;
			}
			for (ItemStack armourStack : entity.inventory.armorInventory)
				if (armourStack != null) {

					ItemStack renderStack1 = armourStack.copy();
					if ((renderStack1.hasEffect()) && (((renderStack1.getItem() instanceof ItemTool))
							|| ((renderStack1.getItem() instanceof ItemArmor))))
						renderStack1.stackSize = 1;
					renderItemStack(renderStack1, xOffset, -26);
					xOffset += 16;
				}
		}
		GL11.glPopMatrix();
		this.revertAllCaps();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
	
	public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
	      drawRect(x, y, x2, y2, col2);
	      float f = (float)(col1 >> 24 & 255) / 255.0F;
	      float f1 = (float)(col1 >> 16 & 255) / 255.0F;
	      float f2 = (float)(col1 >> 8 & 255) / 255.0F;
	      float f3 = (float)(col1 & 255) / 255.0F;
	      GL11.glEnable(3042);
	      GL11.glDisable(3553);
	      GL11.glBlendFunc(770, 771);
	      GL11.glEnable(2848);
	      GL11.glPushMatrix();
	      GL11.glColor4f(f1, f2, f3, f);
	      GL11.glLineWidth(l1);
	      GL11.glBegin(1);
	      GL11.glVertex2d((double)x, (double)y);
	      GL11.glVertex2d((double)x, (double)y2);
	      GL11.glVertex2d((double)x2, (double)y2);
	      GL11.glVertex2d((double)x2, (double)y);
	      GL11.glVertex2d((double)x, (double)y);
	      GL11.glVertex2d((double)x2, (double)y);
	      GL11.glVertex2d((double)x, (double)y2);
	      GL11.glVertex2d((double)x2, (double)y2);
	      GL11.glEnd();
	      GL11.glPopMatrix();
	      GL11.glEnable(3553);
	      GL11.glDisable(3042);
	      GL11.glDisable(2848);
	}
	
	public static void drawRect(float g, float h, float i, float j, int col1) {
	      float f = (float)(col1 >> 24 & 255) / 255.0F;
	      float f1 = (float)(col1 >> 16 & 255) / 255.0F;
	      float f2 = (float)(col1 >> 8 & 255) / 255.0F;
	      float f3 = (float)(col1 & 255) / 255.0F;
	      GL11.glEnable(3042);
	      GL11.glDisable(3553);
	      GL11.glBlendFunc(770, 771);
	      GL11.glEnable(2848);
	      GL11.glPushMatrix();
	      GL11.glColor4f(f1, f2, f3, f);
	      GL11.glBegin(7);
	      GL11.glVertex2d((double)i, (double)h);
	      GL11.glVertex2d((double)g, (double)h);
	      GL11.glVertex2d((double)g, (double)j);
	      GL11.glVertex2d((double)i, (double)j);
	      GL11.glEnd();
	      GL11.glPopMatrix();
	      GL11.glEnable(3553);
	      GL11.glDisable(3042);
	      GL11.glDisable(2848);
}
	
	public void renderItemStack(ItemStack stack, int x, int y) {
		GL11.glPushMatrix();
		GL11.glDepthMask(true);
		GlStateManager.clear(256);
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		this.mc.getRenderItem().zLevel = -150.0F;
		whatTheFuckOpenGLThisFixesItemGlint();
		this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack, x, y);
		this.mc.getRenderItem().zLevel = 0.0F;
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.disableDepth();
		renderEnchantText(stack, x, y);
		GlStateManager.enableDepth();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GL11.glPopMatrix();
	}
	
	public void renderEnchantText(ItemStack stack, int x, int y) {
		int encY = y - 24;
		if ((stack.getItem() instanceof ItemArmor)) {
			int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
			int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
			int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			if (pLevel > 0) {
				this.mc.fontRendererObj.drawString("p" + pLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (tLevel > 0) {
				this.mc.fontRendererObj.drawString("t" + tLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (uLevel > 0) {
				this.mc.fontRendererObj.drawString("u" + uLevel, x * 2, encY, 16777215);
				encY += 7;
			}
		}
		if ((stack.getItem() instanceof net.minecraft.item.ItemBow)) {
			int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
			int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
			int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
			int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			if (sLevel > 0) {
				this.mc.fontRendererObj.drawString("d" + sLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (kLevel > 0) {
				this.mc.fontRendererObj.drawString("k" + kLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (fLevel > 0) {
				this.mc.fontRendererObj.drawString("f" + fLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (uLevel > 0) {
				this.mc.fontRendererObj.drawString("u" + uLevel, x * 2, encY, 16777215);
				encY += 7;
			}
		}
		if ((stack.getItem() instanceof net.minecraft.item.ItemSword)) {
			int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
			int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
			int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
			int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
			if (sLevel > 0) {
				this.mc.fontRendererObj.drawString("s" + sLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (kLevel > 0) {
				this.mc.fontRendererObj.drawString("k" + kLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (fLevel > 0) {
				this.mc.fontRendererObj.drawString("f" + fLevel, x * 2, encY, 16777215);
				encY += 7;
			}
			if (uLevel > 0) {
				this.mc.fontRendererObj.drawString("u" + uLevel, x * 2, encY, 16777215);
			}
		}
	}
	
	public void whatTheFuckOpenGLThisFixesItemGlint() {
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
	}
	
	TimerUtil time = new TimerUtil();
	
	public static double getPing(EntityPlayer player) {
		NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfo(player.getName());
		int playerPing = playerInfo != null ? playerInfo.getResponseTime() : -1;
		return playerPing;
	}
	
	
	//GLUtils start
	public static void setGLCap(int cap, boolean flag)
	  {
	    glCapMap.put(Integer.valueOf(cap), Boolean.valueOf(GL11.glGetBoolean(cap)));
	    if (flag) {
	      GL11.glEnable(cap);
	    } else {
	      GL11.glDisable(cap);
	    }
	  }
	  
	  public static void revertGLCap(int cap)
	  {
	    Boolean origCap = (Boolean)glCapMap.get(Integer.valueOf(cap));
	    if (origCap != null) {
	      if (origCap.booleanValue()) {
	        GL11.glEnable(cap);
	      } else {
	        GL11.glDisable(cap);
	      }
	    }
	  }
	  
	  public static void glEnable(int cap)
	  {
	    setGLCap(cap, true);
	  }
	  
	  public static void glDisable(int cap)
	  {
	    setGLCap(cap, false);
	  }
	  
	  public static void revertAllCaps()
	  {
	    for (Iterator localIterator = glCapMap.keySet().iterator(); localIterator.hasNext();)
	    {
	      int cap = ((Integer)localIterator.next()).intValue();
	      revertGLCap(cap);
	    }
	  }
	  
	  private static Map<Integer, Boolean> glCapMap = new HashMap();
	  //GLUtils end
	  
	  
	  
	  
	  //ChatColor start
	  public enum ChatColor {
	      BLACK("BLACK", 0, '0', "black"), 
	      DARK_BLUE("DARK_BLUE", 1, '1', "dark_blue"), 
	      DARK_GREEN("DARK_GREEN", 2, '2', "dark_green"), 
	      DARK_AQUA("DARK_AQUA", 3, '3', "dark_aqua"), 
	      DARK_RED("DARK_RED", 4, '4', "dark_red"), 
	      DARK_PURPLE("DARK_PURPLE", 5, '5', "dark_purple"), 
	      GOLD("GOLD", 6, '6', "gold"), 
	      GRAY("GRAY", 7, '7', "gray"), 
	      DARK_GRAY("DARK_GRAY", 8, '8', "dark_gray"), 
	      BLUE("BLUE", 9, '9', "blue"), 
	      GREEN("GREEN", 10, 'a', "green"), 
	      AQUA("AQUA", 11, 'b', "aqua"), 
	      RED("RED", 12, 'c', "red"), 
	      LIGHT_PURPLE("LIGHT_PURPLE", 13, 'd', "light_purple"), 
	      YELLOW("YELLOW", 14, 'e', "yellow"), 
	      WHITE("WHITE", 15, 'f', "white"), 
	      MAGIC("MAGIC", 16, 'k', "obfuscated"), 
	      BOLD("BOLD", 17, 'l', "bold"), 
	      STRIKETHROUGH("STRIKETHROUGH", 18, 'm', "strikethrough"), 
	      UNDERLINE("UNDERLINE", 19, 'n', "underline"), 
	      ITALIC("ITALIC", 20, 'o', "italic"), 
	      RESET("RESET", 21, 'r', "reset");
	      
	      public static final char COLOR_CHAR = '\247';
	      public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	      public static final Pattern STRIP_COLOR_PATTERN;
	      private static final Map<Character, ChatColor> BY_CHAR;
	      private final char code;
	      private final String toString;
	      private final String name;
	      
	      static {
	          STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('\247') + "[0-9A-FK-OR]");
	          BY_CHAR = new HashMap<Character, ChatColor>();
	          ChatColor[] arrayOfChatColor;
	          for (int j = (arrayOfChatColor = values()).length, i = 0; i < j; ++i) {
	              final ChatColor colour = arrayOfChatColor[i];
	              ChatColor.BY_CHAR.put(colour.code, colour);
	          }
	      }
	      
	      private ChatColor(final String s, final int n, final char code, final String name) {
	          this.code = code;
	          this.name = name;
	          this.toString = new String(new char[] { '\247', code });
	      }
	      
	      @Override
	      public String toString() {
	          return this.toString;
	      }
	      
	      public static String stripColor(final String input) {
	          if (input == null) {
	              return null;
	          }
	          return ChatColor.STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	      }
	      
	      public static String translateAlternateColorCodes(final char altColorChar, final String textToTranslate) {
	          final char[] b = textToTranslate.toCharArray();
	          for (int i = 0; i < b.length - 1; ++i) {
	              if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
	                  b[i] = '\247';
	                  b[i + 1] = Character.toLowerCase(b[i + 1]);
	              }
	          }
	          return new String(b);
	      }
	      
	      public static ChatColor getByChar(final char code) {
	          return ChatColor.BY_CHAR.get(code);
	      }
	  }
	  //ChatColor end
}