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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.UUID;

public class GuildManager {

    public static void createGuild(String guildName, UUID creator) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(creator);
        if (player == null) {
            return;
        }

        if (GuildDatabase.isGuild(guildName)) {
            player.sendMessage("Diese Gilde gibt es bereits");
            return;
        }
        UUID guildUUID = UUID.randomUUID();
        Guild guild = new Guild(guildName, guildUUID);
        if (!GuildQuery.setGuild(guild)) {
            player.sendMessage("Fehler in der datenbank!");
            return;
        }
        GuildDatabase.addGuild(guild);
        // todo send socket msg
        // todo add guildmaster add player
    }

    public static void removeGuild(String guildName, UUID creator) {
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
        GuildDatabase.removeGuildPlayersFromGuild(guild.guildUUID);
        GuildDatabase.removeGuild(guild.guildUUID);
        // todo send socket msg
    }

    public static void addPlayerToGuild(UUID guildUUID, UUID actionUUID, UUID invitedUUID) {
        ProxiedPlayer actionPlayer = ProxyServer.getInstance().getPlayer(actionUUID);
        ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(invitedUUID);
        if (actionPlayer == null || invitedPlayer == null) {
            //not online
            return;
        }
        Guild guild = GuildDatabase.getGuild(guildUUID);
        if (guild == null) {
            actionPlayer.sendMessage("Diese Gilde gibt es nicht");
            return;
        }

        GuildPlayer guildPlayer = new GuildPlayer(invitedPlayer.getUniqueId());
        // todo other rang system

        GuildDatabase.addGuildPlayer(guildPlayer);
        // todo send socket msg
    }

    /* Load data to plugin */
    public static void loadData() {
        HashSet<Guild> guilds = GuildQuery.getAllGuilds();
        for (Guild guild : guilds) {
            GuildDatabase.addGuild(guild);
            MineGuildPlugin.inst().getLogger().info("Load guild " + guild.guildName);
        }
        MineGuildPlugin.inst().getLogger().info("Loaded " + GuildDatabase.getGuilds().size() + " Guilds!");
        // todo load online players if is a reload
    }
}
