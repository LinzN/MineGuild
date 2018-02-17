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
import de.linzn.mineGuild.objects.GuildPermission;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.objects.GuildRang;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.UUID;

public class GuildManager {

    public static void establishGuild(String guildName, UUID creator) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(creator);
        if (player == null) {
            return;
        }
        if (GuildDatabase.getGuildPlayer(creator) != null) {
            player.sendMessage("Du bist bereits in einer Gilde!");
            return;
        }

        if (GuildDatabase.isGuild(guildName)) {
            player.sendMessage("Diese Gilde gibt es bereits");
            return;
        }

        if (create_guild(guildName, creator)) {
            player.sendMessage("Gilde wurde erstellt!");
        }
    }

    public static void disbandGuild(UUID actor) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        if (player == null) {
            return;
        }
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);
        if (guildPlayer == null) {
            player.sendMessage("Du bist in keiner Gilde!");
            return;
        }

        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.DELETE)) {
            player.sendMessage("Du hast dazu keine Berechtigung!");
            return;
        }

        if (remove_guild(guildPlayer.getGuild().guildUUID, actor)) {
            player.sendMessage("Gilde wurde aufgelöst!");
        }
    }


    /* ##########################
     * Private guild functions
      ##########################*/

    private static boolean create_guild(String guildName, UUID creator) {
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
        owner.setRangUUID(masterRang.rangUUID);
        tempGuild.setGuildPlayer(owner);

        /* Add guild to mysql */
        if (!GuildQuery.setGuild(tempGuild)) {
            MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
            return false;
        }

        /* Load real guild from database */
        Guild guild = GuildQuery.getGuild(guildUUID);
        GuildDatabase.addGuild(guild);
        // todo send socket msg
        return true;
    }

    private static boolean remove_guild(UUID guildUUID, UUID creator) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(creator);
        if (player == null) {
            return false;
        }
        Guild guild = GuildDatabase.getGuild(guildUUID);

        /* remove guild from database */
        if (!GuildQuery.unsetGuild(guild.guildUUID)) {
            MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
            return false;
        }
        /* remove loaded guild */
        GuildDatabase.removeGuild(guild.guildUUID);
        // todo send socket msg
        return true;
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
        guildPlayer.setRangUUID(guild.getGuildRang("static_member").rangUUID);
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
            rang = new GuildRang("static_master", UUID.randomUUID());
            for (GuildPermission guildPermission : GuildPermission.values()) {
                rang.setPermission(guildPermission);
            }
        } else if (rangName.equalsIgnoreCase("assistant")) {
            rang = new GuildRang("assistant", UUID.randomUUID());
            rang.setPermission(GuildPermission.INVITE);
            rang.setPermission(GuildPermission.DEPOSIT);
            rang.setPermission(GuildPermission.SETRANG);
        } else if (rangName.equalsIgnoreCase("static_member")) {
            rang = new GuildRang("static_member", UUID.randomUUID());
            rang.setPermission(GuildPermission.HOME);
            rang.setPermission(GuildPermission.HELP);
            rang.setPermission(GuildPermission.INFO);
        }
        return rang;
    }
}
