package me.skidsense.command.commands;

import me.skidsense.command.Command;
import me.skidsense.module.collection.visual.HUD;

public class ClientName
extends Command {
    public ClientName() {
        super("ClientName", new String[]{"ClientName","clientname"}, "", "AutoLTest");
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 0) {
            //HUD.aaa11 = args[0];
    }
        return null;
    }
}

