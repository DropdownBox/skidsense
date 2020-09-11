
package me.skidsense.module.collection.world;

import java.util.Arrays;

import me.skidsense.hooks.Sub;
import me.skidsense.hooks.events.EventChatSend;
import me.skidsense.management.CommandManager;
import me.skidsense.management.command.Command;
import me.skidsense.module.Mod;
import me.skidsense.module.ModuleType;

public class ChatCommands extends Mod {

	public String prefix = ".";
	public ChatCommands() {
		super("Chat Commands", new String[]{"ChatCommands"}, ModuleType.Player);
		this.setRemoved(true);
	}

	@Sub
	public void onLChat(EventChatSend e) {
		if(e.getMessage().startsWith(prefix)) {
			e.setCancelled(true);
	         String[] commandBits = e.getMessage().substring(prefix.length()).split(" ");
	         String commandName = commandBits[0];
	         Command command = (Command)CommandManager.commandMap.get(commandName);
	         if (command != null) {
	            if (commandBits.length > 1) {
	               String[] commandArguments = (String[])Arrays.copyOfRange(commandBits, 1, commandBits.length);
	               command.fire(commandArguments);
	            } else {
	               command.fire((String[])null);
	            }

	         }
		}
	}
}

