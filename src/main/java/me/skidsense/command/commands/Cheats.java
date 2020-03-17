/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.command.commands;

import me.skidsense.Client;
import me.skidsense.command.Command;
import me.skidsense.management.ModManager;
import me.skidsense.module.Mod;
import net.minecraft.util.EnumChatFormatting;

public class Cheats
extends Command {
    public Cheats() {
        super("Cheats", new String[]{"mods"}, "", "sketit");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Client.instance.getModuleManager();
            StringBuilder list = new StringBuilder(String.valueOf(ModManager.getModules().size()) + " Cheats - ");
            Client.instance.getModuleManager();
            for (Mod cheat : ModManager.getModules()) {
                list.append((Object)(cheat.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED)).append(cheat.getName()).append(", ");
            }
            Client.sendMessage("> " + list.toString().substring(0, list.toString().length() - 2));
        } else {
        	Client.sendMessage("> Correct usage .cheats");
        }
        return null;
    }
}

