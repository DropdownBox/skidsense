package me.skidsense.command.commands;

import me.skidsense.Client;
import me.skidsense.command.Command;
import me.skidsense.module.collection.visual.HUD;

public class ClientName
extends Command {
    public ClientName() {
        super("ClientName","clientname");
    }

    @Override
    public String execute(String alias,String[] args) {
        if (args.length != 0) {
            Client.clientName = args[0];
            //HUD.aaa11 = args[0];
        }
        return null;
    }
}

