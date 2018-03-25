package de.linzn.mineGuild.manager;

import de.linzn.mineGuild.MineGuildPlugin;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.socket.updateStream.JServerGuildUpdateOutput;

import java.util.HashSet;

public class InternalGuildManager {

    public static void server_data_request(String serverName) {
        MineGuildPlugin.inst().getLogger().info("Request guild_data for server " + serverName);
        HashSet<Guild> guilds = new HashSet<>(GuildDatabase.getGuilds());
        for (Guild guild : guilds) {
            JServerGuildUpdateOutput.set_guild_data(serverName, guild);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        for (Guild guild : guilds) {
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                JServerGuildUpdateOutput.set_guildplayer_data(serverName, guildPlayer);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
