/*
 * Copyright (C) 2018. MineGaming - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 *  You should have received a copy of the LGPLv3 license with
 *  this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.mineGuild.manager;

import de.linzn.mineGuild.MineGuildPlugin;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.database.mysql.GuildQuery;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.objects.GuildRang;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.UUID;

public class GuildManager {


    /* ##########################
     * Private guild functions
      ##########################*/

    private static void create_guild(String guildName, UUID creator) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(creator);
        if (player == null) {
            return;
        }

        // todo check if is already in guild
        if (GuildDatabase.isGuild(guildName)) {
            player.sendMessage("Diese Gilde gibt es bereits");
            return;
        }
        UUID guildUUID = UUID.randomUUID();
        Guild tempGuild = new Guild(guildName, guildUUID);

        /* Set default rang */
        GuildRang masterRang = getDefaultRang("static_master");
        tempGuild.setGuildRang(masterRang);
        GuildRang assistantRang = getDefaultRang("assistant");
        tempGuild.setGuildRang(assistantRang);
        GuildRang memberRang = getDefaultRang("static_member");
        tempGuild.setGuildRang(memberRang);

        /* Set master */
        GuildPlayer owner = new GuildPlayer(creator);
        owner.setGuild(tempGuild);
        owner.setRangName(masterRang.rangName);
        tempGuild.setGuildPlayer(owner);

        /* Add guild to mysql */
        if (!GuildQuery.setGuild(tempGuild)) {
            player.sendMessage("Fehler in der datenbank!");
            return;
        }

        /* Load real guild from database */
        Guild guild = GuildQuery.getGuild(guildUUID);
        GuildDatabase.addGuild(guild);
        // todo send socket msg
    }

    private static void remove_guild(String guildName, UUID creator) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(creator);
        if (player == null) {
            return;
        }

        // todo check permissions of rang

        Guild guild = GuildDatabase.getGuild(guildName);

        if (guild == null) {
            player.sendMessage("Diese Gilde gibt es nicht");
            return;
        }

        /* remove guild from database */
        if (!GuildQuery.unsetGuild(guild.guildUUID)) {
            player.sendMessage("Fehler in der datenbank!");
            return;
        }
        /* remove loaded guild */
        GuildDatabase.removeGuild(guild.guildUUID);
        // todo send socket msg
    }

    private static void add_player_to_guild(UUID guildUUID, UUID invitedUUID) {
        ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(invitedUUID);
        if (invitedPlayer == null) {
            //not online
            return;
        }
        Guild guild = GuildDatabase.getGuild(guildUUID);
        if (guild == null) {
            return;
        }
        GuildPlayer guildPlayer = new GuildPlayer(invitedPlayer.getUniqueId());
        guildPlayer.setRangName("member");
        // todo add to database mysql
        guildPlayer.setGuild(guild);
        guild.setGuildPlayer(guildPlayer);
        // todo send socket msg
    }

    private static void remove_player_from_guild(UUID guildUUID, UUID removedUUID) {
        Guild guild = GuildDatabase.getGuild(guildUUID);
        if (guild == null) {
            return;
        }
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(removedUUID);
        if (guildPlayer == null) {
            return;
        }
        guildPlayer.setGuild(null);
        guild.unsetGuildPlayer(guildPlayer);
        // remove database player
        // todo send socket msg
    }

    /* Load data to plugin */
    public static void loadData() {
        HashSet<Guild> guilds = GuildQuery.getAllGuilds();
        for (Guild guild : guilds) {
            GuildDatabase.addGuild(guild);
            MineGuildPlugin.inst().getLogger().info("Loading -> Guild: " + guild.guildName + " Rangs: " + guild.guildRangs.size() + " Members: " + guild.guildPlayers.size());
        }
        MineGuildPlugin.inst().getLogger().info("Loaded " + GuildDatabase.getGuilds().size() + " Guilds!");
    }

    private static GuildRang getDefaultRang(String rangName) {
        GuildRang rang = null;
        if (rangName.equalsIgnoreCase("static_master")) {
            rang = new GuildRang("static_master");
            rang.setPermission("test1");
        } else if (rangName.equalsIgnoreCase("assistant")) {
            rang = new GuildRang("assistant");
            rang.setPermission("test2");
        } else if (rangName.equalsIgnoreCase("static_member")) {
            rang = new GuildRang("static_member");
            rang.setPermission("test3");
        }
        return rang;
    }
}
