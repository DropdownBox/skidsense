package net.minecraft.command.server;

import net.minecraft.MinecraftServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListPlayers extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "list";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.players.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) {
        int i = MinecraftServer.getServer().getCurrentPlayerCount();
        sender.addChatMessage(new ChatComponentTranslation("commands.players.list", Integer.valueOf(i), Integer.valueOf(MinecraftServer.getServer().getMaxPlayers())));
        sender.addChatMessage(new ChatComponentText(MinecraftServer.getServer().getConfigurationManager().func_181058_b(args.length > 0 && "uuids".equalsIgnoreCase(args[0]))));
        sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, i);
    }
}
