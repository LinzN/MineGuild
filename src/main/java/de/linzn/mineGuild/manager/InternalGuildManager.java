package de.linzn.mineGuild.manager;

import com.google.gson.JsonObject;
import de.linzn.mineGuild.MineGuildPlugin;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.database.mysql.GuildQuery;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.socket.controlStream.JServerGuildControlOutput;
import de.linzn.mineGuild.socket.updateStream.JServerGuildUpdateOutput;
import net.md_5.bungee.api.ProxyServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.UUID;

public class InternalGuildManager {
    public static void migrate_guild_data() {
        HashSet<Guild> old_guilds = GuildQuery.load_old_database_guilds();
        for (Guild guild : old_guilds) {
            GuildQuery.setGuild(guild);
            JServerGuildUpdateOutput.send_plugin_migrate(guild.guildUUID, guild.guildName);
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
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> GuildQuery.updateGuildRaw(guild));
    }


    public static void server_data_request(String serverName){
        MineGuildPlugin.inst().getLogger().info("Request guild_data for server " + serverName);
        HashSet<Guild> guilds = new HashSet<>(GuildDatabase.getGuilds());

        for (Guild guild : guilds) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("guildUUID", guild.guildUUID.toString());
            jsonObject.put("guildName", guild.guildName);
            jsonObject.put("guildLevel", guild.guildLevel);

            JSONArray jsonArray = new JSONArray();
            for (GuildPlayer guildPlayer : guild.guildPlayers){
                jsonArray.put(guildPlayer.getUUID().toString());
            }
            jsonObject.put("guildMembers", jsonArray);
            MineGuildPlugin.inst().getLogger().info("Build JSONObject for " + guild.guildUUID.toString());
            JServerGuildControlOutput.send_guild_packet(serverName, jsonObject);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
