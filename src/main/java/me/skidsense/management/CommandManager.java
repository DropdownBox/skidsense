package me.skidsense.management;

import java.util.Collection;
import java.util.HashMap;

import me.skidsense.management.command.Command;
import me.skidsense.management.command.impl.Bind;
import me.skidsense.management.command.impl.Clear;
import me.skidsense.management.command.impl.ClientName;
import me.skidsense.management.command.impl.Damage;
import me.skidsense.management.command.impl.Friend;
import me.skidsense.management.command.impl.Help;
import me.skidsense.management.command.impl.Say;
import me.skidsense.management.command.impl.Target;
import me.skidsense.management.command.impl.Toggle;
import me.skidsense.management.command.impl.Waypoint;

public class CommandManager {
   public static final HashMap<String, Command> commandMap = new HashMap<String, Command>();

   public void addCommand(String name, Command command) {
      commandMap.put(name, command);
   }

   public Collection<Command> getCommands() {
      return commandMap.values();
   }

   public Command getCommand(String name) {
      return (Command)commandMap.get(name.toLowerCase());
   }

   public void setup() {
	  (new Help(new String[]{"help", "halp", "h"}, "Help for commands.")).register(this);
      (new ClientName(new String[]{"clientname", "cn"}, "Change ClientName")).register(this);
      (new Damage(new String[]{"damage","d", "dmg", "kys", "suicide", "amandatodd"}, "Kill yourself u worthless minecraft bloachxD!")).register(this);
      (new Toggle(new String[]{"toggle", "t"}, "Toggles the module.")).register(this);
      (new Say(new String[]{"say", "talk", "chat"}, "Send a message with your chat prefix.")).register(this);
      (new Bind(new String[]{"bind", "key", "b"}, "Send a message with your chat prefix.")).register(this);
      (new Friend(new String[]{"friend", "fr", "f"}, "Add and remove friends.")).register(this);
      (new Clear(new String[]{"clear", "cl", "clr"}, "Clears chat for you.")).register(this);
	  (new Target(new String[]{"Target"}, "set vip target.")).register(this);
      (new Waypoint(new String[]{"waypoint", "wp", "marker"}, "Waypoint command.")).register(this);
   }
}
