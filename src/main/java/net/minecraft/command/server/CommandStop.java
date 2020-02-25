package net.minecraft.command.server;

import cn.margele.mlproject.impl.MinecraftServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandStop extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "stop";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.stop.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) {
        if (MinecraftServer.getServer().worldServers != null) {
            notifyOperators(sender, this, "commands.stop.start");
        }

        MinecraftServer.getServer().initiateShutdown();
    }
}
