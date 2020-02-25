package me.skidsense.command.commands;

import me.skidsense.Client;
import me.skidsense.command.Command;
import me.skidsense.util.MathUtil;
import me.skidsense.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumChatFormatting;

public class VClip
extends Command {
    private TimerUtil timer = new TimerUtil();

    public VClip() {
        super("Vc", new String[]{"Vclip", "clip", "verticalclip", "clip"}, "", "Teleport down a specific ammount");
    }

    @Override
    public String execute(String[] args) {
            if (args.length > 0) {
                if (MathUtil.parsable(args[0], (byte)4)) {
                    float distance = Float.parseFloat(args[0]);
                    Client.mc.thePlayer.setPosition(Client.mc.thePlayer.posX, Client.mc.thePlayer.posY + (double)distance, Client.mc.thePlayer.posZ);
                    Client.sendMessage("> Vclipped " + distance + " blocks");
                } else {
                    this.syntaxError((Object)((Object)EnumChatFormatting.GRAY) + args[0] + " is not a valid number");
                }
            } else {
                this.syntaxError((Object)((Object)EnumChatFormatting.GRAY) + "Valid .vclip <number>");
            }
        return null;
	}
}

