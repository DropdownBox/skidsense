package me.skidsense.command.commands;

import me.skidsense.Client;
import me.skidsense.command.Command;
import me.skidsense.management.ModuleManager;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.Module;
import org.lwjgl.input.Keyboard;

public class Bind
extends Command {
    public Bind() {
        super("Bind", new String[]{"b"}, "", "sketit");
    }

    @Override
    public String execute(String[] args) {
        if (args.length >= 2) {
            Module m = Client.instance.getModuleManager().getAlias(args[0]);
            if (m != null) {
                int k = Keyboard.getKeyIndex((String)args[1].toUpperCase());
                m.setKey(k);
                Object[] arrobject = new Object[2];
                arrobject[0] = m.getName();
                arrobject[1] = k == 0 ? "none" : args[1].toUpperCase();
                Notifications.getManager().post(String.format("> Bound %s to %s", arrobject));
                //Client.sendMessage(String.format("> Bound %s to %s", arrobject));
            } else {
                Notifications.getManager().post("> Invalid module name, double check spelling.");
            	//Client.sendMessage("> Invalid module name, double check spelling.");
            }
        } else {
            Notifications.getManager().post("\u00a7bCorrect usage:\u00a77 .bind <module> <key>");
        	//Client.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .bind <module> <key>");
        }
        return null;
    }
}

