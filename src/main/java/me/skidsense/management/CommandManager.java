/*
 * Decompiled with CFR 0_132.
 */
package me.skidsense.management;

import me.skidsense.Client;
import me.skidsense.SplashProgress;
import me.skidsense.command.Command;
import me.skidsense.command.commands.*;
import me.skidsense.hooks.EventManager;
import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventChatRecieve;
import me.skidsense.hooks.events.EventChatSend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandManager
implements Manager {
    private List<Command> commands;
    public String PREFIX = ".";
    @Override
    public void init() {
        SplashProgress.setProgress(2, "CommandManager Init");
        this.commands = new ArrayList<>();

        this.commands.add(new Help());
        this.commands.add(new AutoLTest());
        this.commands.add(new Toggle());
        this.commands.add(new Bind());
        this.commands.add(new VClip());
        this.commands.add(new Cheats());
        this.commands.add(new Enchant());
        this.commands.add(new ClientName());
        EventManager.getOtherEventManager().register(this);
        //EventBus.getInstance().register(this);
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public Optional<Command> getCommandByName(String name) {
        return this.commands.stream().filter(c2 -> {
            boolean isAlias = false;
            String[] arrstring = c2.getAlias();
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String str = arrstring[n2];
                if (str.equalsIgnoreCase(name)) {
                    isAlias = true;
                    break;
                }
                ++n2;
            }
            return c2.getName().equalsIgnoreCase(name) || isAlias;
        }).findFirst();
    }

    public void add(Command command) {
        this.commands.add(command);
    }

    @Sub
    private void onChat(EventChatSend e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith(PREFIX)) {
            e.setCancelled(true);
            String[] args = e.getMessage().trim().substring(PREFIX.length()).split(" ");
            Optional<Command> possibleCmd = this.getCommandByName(args[0]);
            if (possibleCmd.isPresent()) {
                String result = possibleCmd.get().execute(args[0],Arrays.copyOfRange(args, 1, args.length));
                if (result != null && !result.isEmpty()) {
                    Client.sendMessage(result);
                }
            } else {
            	Client.sendMessage(String.format("Command not found Try '%shelp'", PREFIX));
            }
        }
    }

}

