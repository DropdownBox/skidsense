package me.skidsense.command.commands;

import me.skidsense.Client;
import me.skidsense.command.Command;
import me.skidsense.management.notifications.Notifications;
import me.skidsense.module.collection.world.AutoL;

public class AutoLTest
extends Command {
    public AutoLTest() {
        super("AutoLTest", new String[]{"AutoLTest"}, "", "AutoLTest");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
        	Notifications.getManager().post(AutoL.AbuseText.get(AutoL.random.nextInt(AutoL.AbuseText.size())));
            //Client.sendMessageWithoutPrefix(AutoL.AbuseText.get(AutoL.random.nextInt(AutoL.AbuseText.size())));
    }
        return null;
    }
}

