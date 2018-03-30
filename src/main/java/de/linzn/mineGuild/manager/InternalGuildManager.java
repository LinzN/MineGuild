package de.linzn.mineGuild.manager;

import de.linzn.mineGuild.MineGuildPlugin;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.database.mysql.GuildQuery;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.socket.controlStream.JServerGuildControlOutput;
import net.md_5.bungee.api.ProxyServer;

import java.util.HashSet;
import java.util.UUID;

public class InternalGuildManager {
    public static void migrate_guild_data() {
        HashSet<Guild> old_guilds = GuildQuery.load_old_database_guilds();
        for (Guild guild : old_guilds) {
            GuildQuery.setGuild(guild);
            MineGuildPlugin.inst().getLogger().info("Migrate guild " + guild.guildUUID);
        }
        GuildManager.loadData();
        server_data_request("all");
    }


    public static void add_exp_to_guild(UUID guildUUID, double data) {
        Guild guild = GuildDatabase.getGuild(guildUUID);
        if (guild == null) {
            return;
        }
        guild.addExperience(data);
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            GuildQuery.updateGuildRaw(guild);
        });
    }

    public static void server_data_request(String serverName) {
        MineGuildPlugin.inst().getLogger().info("Request guild_data for server " + serverName);
        HashSet<Guild> guilds = new HashSet<>(GuildDatabase.getGuilds());
        for (Guild guild : guilds) {
            JServerGuildControlOutput.set_guild_data(serverName, guild);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        for (Guild guild : guilds) {
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                JServerGuildControlOutput.set_guildplayer_data(serverName, guildPlayer);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
