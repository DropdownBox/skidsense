package net.minecraft.client.gui;

import me.skidsense.alt.GuiAltManager;
import me.skidsense.color.ColorContainer;
import me.skidsense.color.ColorManager;
import me.skidsense.color.Colors;
import me.skidsense.util.MenuButton;
import me.skidsense.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMainMenu extends GuiScreen {
//private String memetext;
   
   
   public GuiMainMenu() {
	     // this.splashText = "KawaiivaticAntiLeak Protected";
	      //this.memetext = "KawaiivaticAntiLeak Protected";
   }

   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      String strSSP = I18n.format("Single");
      String strSMP = I18n.format("Multi");
      String strOptions = I18n.format("Options");
      String strQuit = I18n.format("Exit Game");
      String strLang = I18n.format("Language");
      String strAccounts = "Accounts";
      int initHeight = this.height / 4 + 48;
      int objHeight = 16;
      int objWidth = 130;
      int xMid = this.width / 2 - objWidth / 2;
      this.buttonList.add(new MenuButton(0, xMid, initHeight, objWidth, objHeight, strSSP));
      this.buttonList.add(new MenuButton(1, xMid, initHeight + 20, objWidth, objHeight, strSMP));
      this.buttonList.add(new MenuButton(2, xMid, initHeight + 40, objWidth, objHeight, strOptions));
      this.buttonList.add(new MenuButton(3, xMid, initHeight + 60, objWidth, objHeight, strLang));
      this.buttonList.add(new MenuButton(4, xMid, initHeight + 80, objWidth, objHeight, strAccounts));
      this.buttonList.add(new MenuButton(5, xMid, initHeight + 100, objWidth, objHeight, strQuit));
   }

	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		} else if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		} else if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		} else if (button.id == 3) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		} else if (button.id == 4) {
			this.mc.displayGuiScreen(new GuiAltManager());
      } else if (button.id == 5) {
         this.mc.shutdown();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
      this.mc.getTextureManager().bindTexture(new ResourceLocation("skidsense/mainmenu/mainmenu_back.png"));
      Gui.drawScaledCustomSizeModalRect(0.0, 0.0, 0.0f, 0.0f, scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), scaledRes.getScaledWidth(), scaledRes.getScaledHeight());
      String ClientName = "Exusiai";
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.width / 2 - this.mc.fontRendererObj.getStringWidth(ClientName) * 2), 43.0F, 0.0F);
      GlStateManager.scale(4.0F, 4.0F, 4.0F);
      this.mc.fontRendererObj.drawStringWithShadow(ClientName, 0.0F, 0.0F, ColorManager.hudColor.getColorInt());
      GlStateManager.popMatrix();
      /*GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
      GlStateManager.rotate(20.0F, 0.0F, 0.0F, 1.0F);
      float var9 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * 3.1415927F * 2.0F) * 0.1F);
      var9 = var9 * 110.0F / (float)(this.fontRendererObj.getStringWidth(this.memetext) + 32);
      GlStateManager.scale(var9+0.1F, var9+0.1F, var9+0.1F);
      this.drawCenteredString(this.fontRendererObj, this.memetext, 0, -15, Colors.getColor(255, 255, 255, 30));
      GlStateManager.popMatrix();*/
      drawSkeetBox();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      int width = 150;
      int hei = 26;
      boolean override = false;

      for(int i = 0; i < this.buttonList.size(); ++i) {
	      GuiButton g = this.buttonList.get(i);
         if (!override) {
            g.drawButton(this.mc, mouseX, mouseY);
         } else {
            int x = g.xPosition;
            int y = g.yPosition;
            boolean over = mouseX >= x && mouseY >= y && mouseX < x + g.getButtonWidth() && mouseY < y + hei;
            if (over) {
	            fillHorizontalGrad(x, y, width, hei, new ColorContainer(5, 40, 85, 255), new ColorContainer(0, 0, 0, 0));
            } else {
	            fillHorizontalGrad(x, y, width, hei, new ColorContainer(0, 0, 0, 255), new ColorContainer(0, 0, 0, 0));
            }

            this.fontRendererObj.drawCenteredString(g.displayString, (float)(g.xPosition + 10), (float)(g.yPosition + hei / 2 - 3), -1);
         }
      }
   }
   
   private void drawSkeetBox2() {
	   ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	   //外框
	   RenderUtil.rectangleBordered((double) (sr.getScaledWidth() / 2 - 52) - 0.5D, (double) (this.height / 4 + 20) - 0.3D, (double) (sr.getScaledWidth() / 2 + 52) + 0.5D, (double) (this.height / 4 + 165) + 0.3D, 0.5D, Colors.getColor(60), Colors.getColor(10));
	   RenderUtil.rectangleBordered((double) (sr.getScaledWidth() / 2 - 52) + 0.5D, (double) (this.height / 4 + 20) + 0.6D, (double) (sr.getScaledWidth() / 2 + 52) - 0.5D, (double) (this.height / 4 + 165) - 0.6D, 1.3D, Colors.getColor(60), Colors.getColor(40));
	   RenderUtil.rectangleBordered((double) (sr.getScaledWidth() / 2 - 52) + 2.5D, (double) (this.height / 4 + 20) + 2.5D, (double) (sr.getScaledWidth() / 2 + 52) - 2.5D, (double) (this.height / 4 + 165) - 2.5D, 0.5D, Colors.getColor(22), Colors.getColor(12));

	   //rainbow条
	   RenderUtil.drawGradientSideways(sr.getScaledWidth() / 2 - 52 + 3, this.height / 4 + 20 + 3, sr.getScaledWidth() / 2, this.height / 4 + 20 + 4, Colors.getColor(55, 177, 218), Colors.getColor(204, 77, 198));
	   RenderUtil.drawGradientSideways(sr.getScaledWidth() / 2, this.height / 4 + 20 + 3, sr.getScaledWidth() / 2 + 52 - 3, this.height / 4 + 20 + 4, Colors.getColor(204, 77, 198), Colors.getColor(204, 227, 53));

	   RenderUtil.rectangle(sr.getScaledWidth() / 2 - 52 + 3, (double) (this.height / 4 + 20) + 3.5D, sr.getScaledWidth() / 2 + 52 - 3, this.height / 4 + 20 + 4, Colors.getColor(0, 110));
	   RenderUtil.rectangleBordered(sr.getScaledWidth() / 2 - 52 + 6, this.height / 4 + 20 + 8, (double) (sr.getScaledWidth() / 2 + 52) - 6.5D, this.height / 4 + 159, 0.3D, Colors.getColor(48), Colors.getColor(10));
	   RenderUtil.rectangle(sr.getScaledWidth() / 2 - 52 + 6 + 1, this.height / 4 + 20 + 9, (double) (sr.getScaledWidth() / 2 + 52) - 7.5D, this.height / 4 + 159 - 1, Colors.getColor(17));
	   RenderUtil.rectangle((float) (sr.getScaledWidth() / 2 - 52 + 6) + 4.5F, this.height / 4 + 20 + 8, sr.getScaledWidth() / 2 - 52 + 35, this.height / 4 + 20 + 9, Colors.getColor(17));

	   GlStateManager.pushMatrix();
	   GlStateManager.translate((float) (sr.getScaledWidth() / 2 - 52 + 6 + 5), (float) (this.height / 4 + 20 + 8), 0.0F);
	   GlStateManager.scale(0.5D, 0.5D, 0.5D);
	   this.mc.fontRendererObj.drawStringWithShadow("Main Menu", 0.0F, 0.0F, -1);
	   GlStateManager.popMatrix();
   }
   
   private void drawSkeetBox() {
	   ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	   //外框
	   RenderUtil.rectangleBordered((double) (sr.getScaledWidth() / 2 - 62) - 0.5D, (double) (this.height / 4 + 30) - 0.3D, (double) (sr.getScaledWidth() / 2 + 62) + 0.5D, (double) (this.height / 4 + 175) + 0.3D, 0.5D, Colors.getColor(60), Colors.getColor(10));
	   RenderUtil.rectangleBordered((double) (sr.getScaledWidth() / 2 - 62) + 0.5D, (double) (this.height / 4 + 30) + 0.6D, (double) (sr.getScaledWidth() / 2 + 62) - 0.5D, (double) (this.height / 4 + 175) - 0.6D, 1.3D, Colors.getColor(60), Colors.getColor(40));
	   RenderUtil.rectangleBordered((double) (sr.getScaledWidth() / 2 - 62) + 2.5D, (double) (this.height / 4 + 30) + 2.5D, (double) (sr.getScaledWidth() / 2 + 62) - 2.5D, (double) (this.height / 4 + 175) - 2.5D, 0.5D, Colors.getColor(22), Colors.getColor(12));

	   //rainbow条
	   RenderUtil.drawGradientSideways(sr.getScaledWidth() / 2 - 62 + 3, this.height / 4 + 30 + 3, sr.getScaledWidth() / 2, this.height / 4 + 30 + 4, Colors.getColor(55, 177, 218), Colors.getColor(204, 77, 198));
	   RenderUtil.drawGradientSideways(sr.getScaledWidth() / 2, this.height / 4 + 30 + 3, sr.getScaledWidth() / 2 + 62 - 3, this.height / 4 + 30 + 4, Colors.getColor(204, 77, 198), Colors.getColor(204, 227, 53));

	   RenderUtil.rectangle(sr.getScaledWidth() / 2 - 62 + 3, (double) (this.height / 4 + 30) + 3.5D, sr.getScaledWidth() / 2 + 62 - 3, this.height / 4 + 30 + 4, Colors.getColor(0, 110));
	   RenderUtil.rectangleBordered(sr.getScaledWidth() / 2 - 62 + 6, this.height / 4 + 30 + 8, (double) (sr.getScaledWidth() / 2 + 62) - 6.5D, this.height / 4 + 169, 0.3D, Colors.getColor(48), Colors.getColor(10));
	   RenderUtil.rectangle(sr.getScaledWidth() / 2 - 62 + 6 + 1, this.height / 4 + 30 + 9, (double) (sr.getScaledWidth() / 2 + 62) - 7.5D, this.height / 4 + 169 - 1, Colors.getColor(17));
	   RenderUtil.rectangle((float) (sr.getScaledWidth() / 2 - 62 + 6) + 4.5F, this.height / 4 + 30 + 8, sr.getScaledWidth() / 2 - 62 + 35, this.height / 4 + 30 + 9, Colors.getColor(17));

	   GlStateManager.pushMatrix();
	   GlStateManager.translate((float) (sr.getScaledWidth() / 2 - 62 + 6 + 5), (float) (this.height / 4 + 30 + 8), 0.0F);
	   GlStateManager.scale(0.5D, 0.5D, 0.5D);
	   this.mc.fontRendererObj.drawStringWithShadow("Exusiai", 5.0F, -2.5F, -1);
	   GlStateManager.popMatrix();
   }
   
   public static void fillHorizontalGrad(double x, double y, double x2, double y2, ColorContainer ColorContainer, ColorContainer c2) {
	      float a1 = (float)c2.getAlpha() / 255.0F;
	      float r1 = (float)c2.getRed() / 255.0F;
	      float g1 = (float)c2.getGreen() / 255.0F;
	      float b1 = (float)c2.getBlue() / 255.0F;
	      float a2 = (float)ColorContainer.getAlpha() / 255.0F;
	      float r2 = (float)ColorContainer.getRed() / 255.0F;
	      float g2 = (float)ColorContainer.getGreen() / 255.0F;
	      float b2 = (float)ColorContainer.getBlue() / 255.0F;
	      Tessellator tess = Tessellator.getInstance();
	      WorldRenderer wr = tess.getWorldRenderer();
	      GlStateManager.disableTexture2D();
	      GlStateManager.enableBlend();
	      GlStateManager.disableAlpha();
	      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	      GlStateManager.shadeModel(7425);
	      tess.draw();
	      wr.color(r1, g1, b1, a1);
	      wr.pos(x + x2, y + y2, 0.0D);
	      wr.pos(x + x2, y, 0.0D);
	      wr.color(r2, g2, b2, a2);
	      wr.pos(x, y, 0.0D);
	      wr.pos(x, y + y2, 0.0D);
	      tess.draw();
	      GlStateManager.enableTexture2D();
	   }
}
