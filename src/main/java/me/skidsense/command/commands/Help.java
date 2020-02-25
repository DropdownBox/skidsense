package me.skidsense.command.commands;

import me.skidsense.Client;
import me.skidsense.command.Command;

public class Help
extends Command {
    public Help() {
        super("Help", new String[]{"list"}, "", "sketit");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Client.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
            Client.sendMessageWithoutPrefix("                    \u00a7b\u00a7lskidsense Client");
            Client.sendMessageWithoutPrefix("\u00a7b.help >\u00a77 list commands");
            Client.sendMessageWithoutPrefix("\u00a7b.bind >\u00a77 bind a module to a key");
            Client.sendMessageWithoutPrefix("\u00a7b.t >\u00a77 toggle a module on/off");
            Client.sendMessageWithoutPrefix("\u00a7b.friend >\u00a77 friend a player");
            Client.sendMessageWithoutPrefix("\u00a7b.cheats >\u00a77 list all modules");
            Client.sendMessageWithoutPrefix("\u00a7b.config >\u00a77 load a premade config");
            Client.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
        } else {
        	Client.sendMessage("invalid syntax Valid .help");
        }
        return null;
    }
}

