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
import de.linzn.mineGuild.utils.LanguageDB;
import de.linzn.mineSuite.bungee.database.mysql.BungeeQuery;
import de.linzn.mineSuite.bungee.module.chat.ChatManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GuildManager {

    public static void processInvitation(UUID actor, String invitedPlayer) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);
        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();
        if (!guild.hasPermission(guildPlayer, GuildPermission.INVITE)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }
        ProxiedPlayer invitedP = ProxyServer.getInstance().getPlayer(invitedPlayer);
        if (invitedP == null) {
            actorP.sendMessage(LanguageDB.player_not_online);
            return;
        }
        if (invitedP == actorP) {
            actorP.sendMessage(LanguageDB.you_not_invite_self);
            return;
        }
        if (GuildDatabase.getGuildPlayer(invitedP.getUniqueId()) != null) {
            actorP.sendMessage(LanguageDB.player_already_in_guild);
            return;
        }
        if (GuildDatabase.hasGuildInvitation(invitedP.getUniqueId())) {
            actorP.sendMessage(LanguageDB.player_has_already_invitation);
            return;
        }

        GuildDatabase.addGuildInvitation(invitedP.getUniqueId(), guild);
        ProxyServer.getInstance().getScheduler().schedule(MineGuildPlugin.inst(), () -> {
            if (GuildDatabase.hasGuildInvitation(invitedP.getUniqueId())) {
                GuildDatabase.removeGuildInvitation(invitedP.getUniqueId());
                invitedP.sendMessage(LanguageDB.guild_invitation_expired);
                if (actorP != null) {
                    actorP.sendMessage(LanguageDB.your_guild_invitation_expired);
                }
            }
        }, 40, TimeUnit.SECONDS);

        guild.broadcastInGuild(LanguageDB.guild_get_guild_invitation.replace("{player}", invitedP.getName()));

        invitedP.sendMessage(LanguageDB.you_get_guild_invitation.replace("{actor}", actorP.getName()).replace("{guild}", guild.guildName));
    }

    public static void acceptInvitation(UUID actor) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        if (player == null) {
            return;
        }
        if (!GuildDatabase.hasGuildInvitation(actor)) {
            player.sendMessage(LanguageDB.you_no_open_invitations);
            return;
        }
        Guild guild = GuildDatabase.getGuild(GuildDatabase.getGuildInvitationGuildUUID(actor));
        if (guild == null) {
            player.sendMessage(LanguageDB.guild_not_exist);
            GuildDatabase.removeGuildInvitation(actor);
            return;
        }
        GuildDatabase.removeGuildInvitation(actor);
        player.sendMessage(LanguageDB.you_accept_invitation);
        add_player_to_guild(guild.guildUUID, actor);
        guild.broadcastInGuild(LanguageDB.guild_new_member.replace("{player}", player.getName()));
    }

    public static void denyInvitation(UUID actor) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        if (player == null) {
            return;
        }
        if (!GuildDatabase.hasGuildInvitation(actor)) {
            player.sendMessage(LanguageDB.you_no_open_invitations);
            return;
        }
        GuildDatabase.removeGuildInvitation(actor);
        player.sendMessage(LanguageDB.you_deny_invitation);

    }

    public static void establishGuild(String guildName, UUID creator) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(creator);
        if (player == null) {
            return;
        }
        if (GuildDatabase.getGuildPlayer(creator) != null) {
            player.sendMessage(LanguageDB.you_already_in_guild);
            return;
        }

        if (GuildDatabase.isGuild(guildName)) {
            player.sendMessage(LanguageDB.guild_already_exist);
            return;
        }

        if (create_guild(guildName, player)) {
            player.sendMessage(LanguageDB.you_create_guild.replace("{guild}", guildName));
            broadcastGlobal(LanguageDB.guild_create_guild.replace("{guild}", guildName).replace("{player}", player.getName()));
        }
    }

    public static void disbandGuild(UUID actor) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        if (player == null) {
            return;
        }
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);
        if (guildPlayer == null) {
            player.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }

        Guild guild = guildPlayer.getGuild();
        String guildName = guild.guildName;

        if (!guild.hasPermission(guildPlayer, GuildPermission.DELETE)) {
            player.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        if (remove_guild(guildPlayer.getGuild().guildUUID, actor)) {
            player.sendMessage(LanguageDB.you_remove_guild.replace("{guild}", guildName));
            guild.broadcastInGuild(LanguageDB.guild_remove_guild_members);
            broadcastGlobal(LanguageDB.guild_remove_guild.replace("{guild}", guildName).replace("{player}", player.getName()));
        }
    }

    public static void showGuildInformation(UUID actor, String guildArg) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        Guild guild;
        if (guildArg.equalsIgnoreCase("null")) {
            GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);
            if (guildPlayer == null) {
                player.sendMessage(LanguageDB.you_not_in_guild);
                return;
            }
            guild = guildPlayer.getGuild();
        } else {
            guild = GuildDatabase.getGuild(guildArg);
            if (guild == null) {
                player.sendMessage(LanguageDB.guild_not_exist);
                return;
            }
        }
        /* Now show information */
        int size = guild.guildPlayers.size();
        int guildLevel = guild.guildLevel;
        double guildExperience = guild.guildExperience;
        double requiredGuildExperience = guild.getGuildRequiredExperience();

        player.sendMessage(LanguageDB.interface_guildinfo_header);
        player.sendMessage(LanguageDB.interface_guildinfo_name.replace("{guild}", guild.guildName));
        player.sendMessage(LanguageDB.interface_guildinfo_membersize.replace("{guild_size}", "" + size));
        player.sendMessage(LanguageDB.interface_guildinfo_guildlevel.replace("{guild_level}", "" + guildLevel));
        player.sendMessage(LanguageDB.interface_guildinfo_guildexperience.replace("{exp}", "" + guildExperience).replace("{totalExp}", "" + requiredGuildExperience));
    }

    public static void showGuildMembers(UUID actor, String guildArg) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        Guild guild;
        if (guildArg.equalsIgnoreCase("null")) {
            GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);
            if (guildPlayer == null) {
                player.sendMessage(LanguageDB.you_not_in_guild);
                return;
            }
            guild = guildPlayer.getGuild();
        } else {
            guild = GuildDatabase.getGuild(guildArg);
            if (guild == null) {
                player.sendMessage(LanguageDB.guild_not_exist);
                return;
            }
        }
        /* Now show information */
        HashSet<GuildPlayer> guildMembers = guild.guildPlayers;

        player.sendMessage(LanguageDB.interface_guildmembers_header);
        for (GuildPlayer guildPlayer : guildMembers) {
            String name = BungeeQuery.getPlayerName(guildPlayer.getUUID());
            player.sendMessage(LanguageDB.interface_guildmembers_entry.replace("{player}", name).replace("{rang}", guildPlayer.getGuildRang().rangName));
        }
    }


    /* ##########################
     * Private guild functions
      ##########################*/

    private static boolean create_guild(String guildName, ProxiedPlayer creator) {
        UUID guildUUID = UUID.randomUUID();
        Guild guild = new Guild(guildName, guildUUID);

        /* Set default rang */
        GuildRang masterRang = getDefaultRang("static_master");
        guild.setGuildRang(masterRang);
        GuildRang assistantRang = getDefaultRang("assistant");
        guild.setGuildRang(assistantRang);
        GuildRang memberRang = getDefaultRang("static_member");
        guild.setGuildRang(memberRang);

        /* Set master */
        GuildPlayer owner = new GuildPlayer(creator.getUniqueId());
        owner.setGuild(guild);
        owner.setRangUUID(masterRang.rangUUID);
        guild.setGuildPlayer(owner);
        GuildDatabase.addGuild(guild);

        /* Add guild async to mysql */
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            if (!GuildQuery.setGuild(guild)) {
                creator.sendMessage(LanguageDB.database_error);
                MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
            }
        });

        // todo send socket msg
        return true;
    }

    private static boolean remove_guild(UUID guildUUID, UUID actor) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        if (player == null) {
            return false;
        }
        Guild guild = GuildDatabase.getGuild(guildUUID);

        /* remove loaded guild */
        GuildDatabase.removeGuild(guild.guildUUID);

        /* Remove guild async from mysql */
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            if (!GuildQuery.unsetGuild(guild.guildUUID)) {
                MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
                player.sendMessage(LanguageDB.database_error);
            }
        });
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
        guildPlayer.setGuild(guild);
        guild.setGuildPlayer(guildPlayer);

        GuildQuery.addGuildPlayer(guildPlayer);
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
        GuildQuery.removeGuildPlayer(removedUUID);
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

    public static void broadcastGlobal(String text) {
        ChatManager.broadcastChat(text);
    }
}
