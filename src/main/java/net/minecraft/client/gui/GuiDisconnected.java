package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import me.skidsense.alt.GuiAltManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen {
    private String reason;
    private IChatComponent message;
    private List multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    private static final String __OBFID = "CL_00000693";
    private String ip = "";

    public static ServerData serverData;
    
    public GuiDisconnected(GuiScreen screen, String reason, IChatComponent component) {
        this.parentScreen = screen;
        this.reason = I18n.format(reason, new Object[0]);
        this.message = component;    
    }
    public void setIp(String ip){
    	this.ip = ip;
    }
    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttonList.clear();
    	this.buttonList.add(new GuiButton(997, 5, 8, 98, 20, "AltManager"));
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        //this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i/2 + 25 + this.fontRendererObj.FONT_HEIGHT, "ReConnect"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (button.id == 997)
        {
            this.mc.displayGuiScreen(new GuiAltManager());
        }
        if(button.id == 1){
        	connectToLastServer();
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    
    public static void connectToLastServer() {
        if(serverData == null)
            return;

        Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), Minecraft.getMinecraft(), serverData));
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int var4 = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null) {
            for (Iterator var5 = this.multilineMessage.iterator(); var5.hasNext(); var4 += this.fontRendererObj.FONT_HEIGHT) {
                String var6 = (String) var5.next();
                /*if(var6.equalsIgnoreCase("WATCHDOG CHEAT DETECTION")) {
                	var6 = "Cheating through the use of unfair game advantages.";
                }*/
                String kodyString = var6.replace("Cheating through the use of unfair game advantages.", "Blocklisted Modifications [PP]");
                this.drawCenteredString(this.fontRendererObj, kodyString, this.width / 2, var4, 16777215);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
