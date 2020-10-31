package me.skidsense.management.authentication;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.lwjgl.input.Keyboard;

import me.skidsense.Client;
import me.skidsense.management.authentication.impl.GuiTextField;
import me.skidsense.management.authentication.impl.PasswordField;
import me.skidsense.management.security.Encrypt;
import me.skidsense.management.security.HWID;
import me.skidsense.util.MenuButton;
import me.skidsense.util.Panorama;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiLoginMenu extends GuiScreen {
   private transient PasswordField password;
   private transient GuiTextField username;
   private transient Status status;
   private Encrypt myshitEncrypt = new Encrypt();
   private Panorama panorama;
   
   public GuiLoginMenu() {
      this.status = GuiLoginMenu.Status.Idle;
      panorama = new Panorama(this,
              new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_0.png"),
              new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_1.png"),
              new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_2.png"),
              new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_3.png"),
              new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_4.png"),
              new ResourceLocation("skidsense/mainmenu/main_menu/panorama/panorama_5.png"));
   }

   public void initGui() {
	  panorama.init();
      int var3 = this.height / 4 + 24;
      this.buttonList.add(new MenuButton(0, this.width / 2 - 100, var3 + 72 + 12,200,20, "Login"));
      this.username = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.password = new PasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 90, 200, 20);
      this.password.setMaxStringLength(256);
      this.username.setFocused(true);
      List<?> okHand = LoginUtil.getLoginInformation();
      try {
         if (!okHand.isEmpty() && okHand.size() > 1) {
             this.username.setText(this.getDecrypted((String)okHand.get(0)));
             this.password.setText(this.getDecrypted((String)okHand.get(1)));
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }
   
   protected void actionPerformed(GuiButton button) {
      if (button.id <= 0) {
          if (this.username.getText() != null && !Objects.equals(this.username.getText(), "") && this.password.getText() != null && !Objects.equals(this.password.getText(), "")) {
        	  this.status = GuiLoginMenu.Status.Authenticating;
              Object one = this.getCrypted(this.username.getText());
              Object two = this.getCrypted(this.password.getText());
        	  Client.instance.authuser = new AuthUser(username.getText(), myshitEncrypt.SHA256(this.password.getText()) ,HWID.getHWID());
        		LoginUtil.saveLogin((String)one, (String)two);
          	  	this.status = GuiLoginMenu.Status.Success;
        		Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
          } else {
       	   		this.status = GuiLoginMenu.Status.Failed;
          }     
      }
   }

   public void drawScreen(int x, int y, float z) {
	  panorama.draw(x, y, z);
      this.username.drawTextBox();
      this.password.drawTextBox();
      this.drawCenteredString(this.mc.fontRendererObj, this.status.name(), this.width / 2, 32, -1);
      GuiButton button = (GuiButton)this.buttonList.get(0);
      button.enabled = !this.status.equals(GuiLoginMenu.Status.Authenticating);
      boolean renderUser = this.username.getText().isEmpty() && !this.username.isFocused();
      if (renderUser) {
         this.drawString(this.mc.fontRendererObj, "§oUsername", this.width / 2 - 96, 66, -7829368);
      }

      boolean renderPass = this.password.getText().isEmpty() && !this.password.isFocused();
      if (renderPass) {
         this.drawString(this.mc.fontRendererObj, "§oPassword", this.width / 2 - 96, 96, -7829368);
      }
      super.drawScreen(x, y, z);
   }

   protected void keyTyped(char character, int key) {
      if (character == '\t') {
         if (!this.username.isFocused() && !this.password.isFocused()) {
            this.username.setFocused(true);
         } else {
            this.username.setFocused(this.password.isFocused());
            this.password.setFocused(!this.username.isFocused());
         }
      }

      if (character == '\r') {
         this.actionPerformed((GuiButton)this.buttonList.get(0));
      }

      this.username.textboxKeyTyped(character, key);
      this.password.textboxKeyTyped(character, key);
   }

   protected void mouseClicked(int x, int y, int button) {
      try {
         super.mouseClicked(x, y, button);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      this.username.mouseClicked(x, y, button);
      this.password.mouseClicked(x, y, button);
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void updateScreen() {
	  this.panorama.update();
      this.username.updateCursorCounter();
      this.password.updateCursorCounter();
   }

   private static SecretKeySpec getSecretNew() {
	   byte[] secret = Crypto.getUserKey(16);
	   return new SecretKeySpec(secret, 0, secret.length, "AES");
   }

   private String getCrypted(String str) {
	   try {
		return Crypto.encrypt(getSecretNew(), str);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return str;
   }

   private String getDecrypted(String str) {
	   try {
		return Crypto.decrypt(getSecretNew(), str);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return str;
   }
	   
   public static enum Status {
      Idle,
      Authenticating,
      Success,
      Failed,
      Error;
   }
}
