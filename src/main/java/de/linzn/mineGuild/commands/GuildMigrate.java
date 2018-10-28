package de.linzn.mineGuild.commands;

import de.linzn.mineGuild.MineGuildPlugin;
import de.linzn.mineGuild.manager.InternalGuildManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class GuildMigrate extends Command {
    public GuildMigrate(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            commandSender.sendMessage("No CONSOLE!");
            return;
        }
        commandSender.sendMessage("Start migrating task for old guild data");
        ProxyServer.getInstance().getScheduler().schedule(MineGuildPlugin.inst(), InternalGuildManager::migrate_guild_data, 10, TimeUnit.SECONDS);
    }
}
