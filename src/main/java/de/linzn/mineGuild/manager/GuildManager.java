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
import de.linzn.mineGuild.api.events.GuildCreateEvent;
import de.linzn.mineGuild.api.events.GuildDisbandEvent;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.database.mysql.GuildQuery;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPermission;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.objects.GuildRang;
import de.linzn.mineGuild.socket.updateStream.JServerGuildUpdateOutput;
import de.linzn.mineGuild.utils.LanguageDB;
import de.linzn.mineGuild.utils.PluginUtil;
import de.linzn.mineSuite.bungee.database.mysql.BungeeQuery;
import de.linzn.mineSuite.bungee.module.chat.ChatManager;
import de.linzn.mineSuite.bungee.module.chat.IChatChannel;
import de.linzn.mineSuite.bungee.module.core.BungeeManager;
import de.linzn.mineSuite.bungee.module.teleport.TeleportManager;
import de.linzn.mineSuite.bungee.utils.Location;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GuildManager {

    public static void showRangInfo(UUID actor, String rangName) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();
        GuildRang guildRang = guild.getGuildRang(rangName);
        if (guildRang == null) {
            actorP.sendMessage(LanguageDB.not_a_guild_rang);
            return;
        }

        actorP.sendMessage(LanguageDB.interface_ranginfo_header);
        actorP.sendMessage(LanguageDB.interface_ranginfo_rangname.replace("{rangname}", guildRang.rangName));
        actorP.sendMessage(LanguageDB.interface_ranginfo_ranguuid.replace("{ranguuid}", guildRang.rangUUID.toString()));
        actorP.sendMessage(LanguageDB.interface_ranginfo_priotiry.replace("{priority}", "" + guildRang.priority));
        StringBuilder permissions = new StringBuilder();
        permissions.append("[");
        int i = 1;
        for (GuildPermission guildPermission : guildRang.permissions) {
            permissions.append(guildPermission.name());
            if (i < guildRang.permissions.size()) {
                permissions.append(", ");
            }
            i++;
        }
        permissions.append("]");
        actorP.sendMessage(LanguageDB.interface_ranginfo_permissions.replace("{permissions}", permissions.toString()));
    }

    public static void showPlayerRang(UUID actor, String playerName) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        UUID targetUUID = BungeeQuery.getUUID(playerName);

        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(targetUUID);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.player_not_in_guild);
            return;
        }
        String targetName = BungeeQuery.getPlayerName(targetUUID);

        Guild guild = guildPlayer.getGuild();
        GuildRang guildRang = guildPlayer.getGuildRang();

        actorP.sendMessage(LanguageDB.interface_playerinfo_header);
        actorP.sendMessage(LanguageDB.interface_playerinfo_player.replace("{player}", targetName));
        actorP.sendMessage(LanguageDB.interface_playerinfo_guild.replace("{guild}", guild.guildName));
        actorP.sendMessage(LanguageDB.interface_playerinfo_rangname.replace("{rangname}", guildRang.rangName));
    }

    public static void showGuildRangList(UUID actor, int page) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();

        actorP.sendMessage(LanguageDB.interface_ranglist_header);
        for (GuildRang guildRang : guild.guildRangs) {
            actorP.sendMessage(LanguageDB.interface_ranglist_listEntry.replace("{priority}", "" + guildRang.priority).replace("{rangname}", guildRang.rangName));
        }
    }

    public static void setGuildHome(UUID actor, Location location) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.SETHOME)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        guild.set_guild_home(location);
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> GuildQuery.set_guild_home(guild.guildUUID, location));
        guild.broadcastInGuild(LanguageDB.guild_new_Home.replace("{actor}", actorP.getName()));
    }

    public static void setGuildMaster(UUID actor, String playerName) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.SETGUILDMASTER)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        GuildPlayer newMaster = GuildDatabase.getGuildPlayer(playerName);
        if (newMaster == null) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        if (newMaster.getGuild() != guild) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }
        if (newMaster == guildPlayer) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        GuildRang masterRang = guild.getGuildRang("master");
        GuildRang memberRang = guild.getGuildRang("member");

        newMaster.setRangUUID(masterRang.rangUUID);
        guildPlayer.setRangUUID(memberRang.rangUUID);

        String masterName = BungeeQuery.getPlayerName(newMaster.getUUID());
        guild.broadcastInGuild(LanguageDB.guild_new_master.replace("{guildmaster}", masterName));

        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            GuildQuery.updateGuildPlayer(newMaster);
            GuildQuery.updateGuildPlayer(guildPlayer);
        });

    }

    public static void setPlayerRANG(UUID actor, String playerName, String rangName) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer actorGuildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (actorGuildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = actorGuildPlayer.getGuild();

        if (!guild.hasPermission(actorGuildPlayer, GuildPermission.SETPLAYERRANG)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        GuildPlayer targetGuildPlayer = GuildDatabase.getGuildPlayer(playerName);
        if (targetGuildPlayer == null) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        if (targetGuildPlayer.getGuild() != guild) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }
        if (targetGuildPlayer == actorGuildPlayer) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }
        GuildRang masterRang = guild.getGuildRang("master");

        if (targetGuildPlayer.getGuildRang() == masterRang) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        GuildRang newRang = guild.getGuildRang(rangName);
        if (newRang == null) {
            actorP.sendMessage(LanguageDB.not_a_guild_rang);
            return;
        }

        if (newRang.priority <= actorGuildPlayer.getGuildRang().priority) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        targetGuildPlayer.setRangUUID(newRang.rangUUID);

        String targetName = BungeeQuery.getPlayerName(targetGuildPlayer.getUUID());
        guild.broadcastInGuild(LanguageDB.guild_set_playerrang.replace("{actor}", actorP.getName()).replace("{target}", targetName).replace("{rang}", newRang.rangName));

        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> GuildQuery.updateGuildPlayer(targetGuildPlayer));
    }

    public static void setGuildName(UUID actor, String guildName) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.SETGUILDNAME)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        if (guild.guildName.equalsIgnoreCase(guildName)){
            actorP.sendMessage(LanguageDB.same_guild_name);
            return;
        }

        guild.set_guild_name(guildName);
        guild.broadcastInGuild(LanguageDB.guild_change_name.replace("{actor}", actorP.getName()).replace("{guild}", guild.guildName));

        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> GuildQuery.updateGuildRaw(guild));
    }


    public static void showGuildList(UUID actor, int page) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(actor);
        /* Now show information */
        Collection<Guild> fullGuilds = GuildDatabase.getGuilds();

        @SuppressWarnings("unchecked")
        List<Guild> pageGuilds = (List<Guild>) createPageEntries(new ArrayList<>(fullGuilds), 10, page);
        player.sendMessage(LanguageDB.interface_guildlist_header);
        if (pageGuilds.isEmpty()) {
            player.sendMessage(LanguageDB.no_entries_on_page);
        }
        for (Guild guild : pageGuilds) {
            String guildName = guild.guildName;
            int guildLevel = guild.guildLevel;
            int memberSize = guild.guildPlayers.size();
            player.sendMessage(LanguageDB.interface_guildlist_entry.replace("{guildName}", guildName).replace("{level}", "" + guildLevel).replace("{memberSize}", "" + memberSize));
        }
    }

    public static void playerGuildHome(UUID actor) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.HOME)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        Location guildHome = guild.guildHome;
        if (guildHome == null) {
            actorP.sendMessage(LanguageDB.guild_no_home);
            return;
        }
        TeleportManager.teleportToLocation(actor, guildHome);
    }

    /* Public player leave guild from ioStream */
    public static void playerLeave(UUID actor) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);

        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.LEAVE)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        if (guildPlayer.getGuildRang().rangName.equalsIgnoreCase("master")) {
            actorP.sendMessage(LanguageDB.you_can_not_leave_guild);
            return;
        }

        if (remove_player_from_guild(guild.guildUUID, guildPlayer.getUUID())) {
            guild.broadcastInGuild(LanguageDB.guild_player_leave.replace("{player}", actorP.getName()));
            actorP.sendMessage(LanguageDB.you_guild_leave);
        }

    }

    /* Public guild invitation from ioStream */
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
            actorP.sendMessage(LanguageDB.player_action_not_possible);
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

    /* Public guild kick from ioStream */
    public static void processKick(UUID actor, String kickedPlayer) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(actor);
        if (guildPlayer == null) {
            actorP.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }
        Guild guild = guildPlayer.getGuild();
        if (!guild.hasPermission(guildPlayer, GuildPermission.KICK)) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }
        GuildPlayer kickedGuildPlayer = GuildDatabase.getGuildPlayer(kickedPlayer);

        if (kickedGuildPlayer == null) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }
        if (kickedGuildPlayer.getGuild() != guild) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        if (kickedGuildPlayer == guildPlayer) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        if (kickedGuildPlayer.getGuildRang().rangName.equalsIgnoreCase("master")) {
            actorP.sendMessage(LanguageDB.player_action_not_possible);
            return;
        }

        if (kickedGuildPlayer.getGuildRang().priority <= guildPlayer.getGuildRang().priority) {
            actorP.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        if (remove_player_from_guild(guild.guildUUID, kickedGuildPlayer.getUUID())) {
            guild.broadcastInGuild(LanguageDB.guild_kicked_player.replace("{actor}", actorP.getName()).replace("{player}", BungeeQuery.getPlayerName(kickedGuildPlayer.getUUID())));
            if (ProxyServer.getInstance().getPlayer(kickedPlayer) != null) {
                ProxyServer.getInstance().getPlayer(kickedPlayer).sendMessage(LanguageDB.you_kicked_from_guild.replace("{actor}", actorP.getName()));
            }
        }

    }

    /* Public guild accept invitation from ioStream */
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
        if (add_player_to_guild(guild.guildUUID, actor)) {
            guild.broadcastInGuild(LanguageDB.guild_new_member.replace("{player}", player.getName()));
        }
    }

    /* Public guild deny invitation from ioStream */
    public static void denyInvitation(UUID actor) {
        ProxiedPlayer actorP = ProxyServer.getInstance().getPlayer(actor);
        if (actorP == null) {
            return;
        }
        if (!GuildDatabase.hasGuildInvitation(actor)) {
            actorP.sendMessage(LanguageDB.you_no_open_invitations);
            return;
        }
        GuildDatabase.removeGuildInvitation(actor);
        actorP.sendMessage(LanguageDB.you_deny_invitation);

    }

    /* Public new guild from ioStream */
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

    /* Public disband guild from ioStream */
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

        if (InternalGuildManager.hasPendingConfirm(guildPlayer.getUUID())) {
            player.sendMessage(LanguageDB.waiting_command);
            return;
        }

        if (!InternalGuildManager.waitForGuildConfirm(guildPlayer.getUUID(), guild.guildUUID)) {
            player.sendMessage(LanguageDB.guild_action_canceled);
            return;
        }

        if (remove_guild(guildPlayer.getGuild().guildUUID, actor)) {
            player.sendMessage(LanguageDB.you_remove_guild.replace("{guild}", guildName));
            guild.broadcastInGuild(LanguageDB.guild_remove_guild_members);
            broadcastGlobal(LanguageDB.guild_remove_guild.replace("{guild}", guildName).replace("{player}", player.getName()));
        }
    }

    /* Public show guild info from ioStream */
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
        String server = player.getServer().getInfo().getName();
        double guildBalance = BungeeManager.request_balance(server, "guild_" + guild.guildUUID.toString());
        double mcmmoShare = PluginUtil.get_mcmmo_multiplikator(guildLevel);
        player.sendMessage(LanguageDB.interface_guildinfo_header);
        player.sendMessage(LanguageDB.interface_guildinfo_name.replace("{guild}", guild.guildName));
        player.sendMessage(LanguageDB.interface_guildinfo_membersize.replace("{guild_size}", "" + size));
        player.sendMessage(LanguageDB.interface_guildinfo_guildlevel.replace("{guild_level}", "" + guildLevel));
        player.sendMessage(LanguageDB.interface_guildinfo_balance.replace("{balance}", "" + guildBalance));
        player.sendMessage(LanguageDB.interface_guildinfo_guildexperience.replace("{exp}", "" + guildExperience).replace("{totalExp}", "" + requiredGuildExperience));
        player.sendMessage(LanguageDB.interface_guildinfo_xpshare.replace("{share}", "" + mcmmoShare));
    }

    /* Public show members of guild from ioStream */
    public static void showGuildMembers(UUID actor, String guildArg, int page) {
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
        HashSet<GuildPlayer> fullMembers = guild.guildPlayers;
        @SuppressWarnings("unchecked")
        List<GuildPlayer> guildMembers = (List<GuildPlayer>) createPageEntries(new ArrayList<>(fullMembers), 10, page);
        player.sendMessage(LanguageDB.interface_guildmembers_header);
        if (guildMembers.isEmpty()) {
            player.sendMessage(LanguageDB.no_entries_on_page);
        }
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
        GuildRang masterRang = getDefaultRang("master");
        guild.set_guild_rang(masterRang);
        GuildRang assistantRang = getDefaultRang("assistant");
        guild.set_guild_rang(assistantRang);
        GuildRang memberRang = getDefaultRang("member");
        guild.set_guild_rang(memberRang);

        /* Set master */
        GuildPlayer owner = new GuildPlayer(creator.getUniqueId());
        owner.setGuild(guild);
        owner.setRangUUID(masterRang.rangUUID);
        guild.addGuildPlayer(owner);
        GuildDatabase.addGuild(guild);

        /* Add guild async to mysql */
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            if (!GuildQuery.setGuild(guild)) {
                creator.sendMessage(LanguageDB.database_error);
                MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
            }
        });

        GuildCreateEvent gEvent = new GuildCreateEvent(guild, owner.getUUID());
        MineGuildPlugin.inst().getProxy().getPluginManager().callEvent(gEvent);

        JServerGuildUpdateOutput.add_guild(guild, owner.getUUID(), creator.getServer().getInfo().getName());
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

        GuildDisbandEvent gEvent = new GuildDisbandEvent(guildUUID, actor);
        MineGuildPlugin.inst().getProxy().getPluginManager().callEvent(gEvent);

        JServerGuildUpdateOutput.remove_guild(guildUUID, player.getServer().getInfo().getName());
        return true;
    }

    private static boolean add_player_to_guild(UUID guildUUID, UUID invitedUUID) {
        ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(invitedUUID);
        if (invitedPlayer == null) {
            //not online
            return false;
        }
        Guild guild = GuildDatabase.getGuild(guildUUID);
        if (guild == null) {
            return false;
        }
        GuildPlayer guildPlayer = new GuildPlayer(invitedPlayer.getUniqueId());
        guildPlayer.setRangUUID(guild.getGuildRang("member").rangUUID);
        guildPlayer.setGuild(guild);
        guild.addGuildPlayer(guildPlayer);

        /* Guild async from mysql */
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            if (!GuildQuery.addGuildPlayer(guildPlayer)) {
                MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
                invitedPlayer.sendMessage(LanguageDB.database_error);
            }
        });
        JServerGuildUpdateOutput.add_guildplayer(guildPlayer);
        return true;
    }

    private static boolean remove_player_from_guild(UUID guildUUID, UUID removedUUID) {
        Guild guild = GuildDatabase.getGuild(guildUUID);
        if (guild == null) {
            return false;
        }
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(removedUUID);
        if (guildPlayer == null) {
            return false;
        }
        guildPlayer.setGuild(null);
        guild.removeGuildPlayer(guildPlayer);
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(removedUUID);
        /* Guild async from mysql */
        ProxyServer.getInstance().getScheduler().runAsync(MineGuildPlugin.inst(), () -> {
            if (!GuildQuery.removeGuildPlayer(removedUUID)) {
                MineGuildPlugin.inst().getLogger().severe("Error in Database save!");
                if (proxiedPlayer != null) {
                    proxiedPlayer.sendMessage(LanguageDB.database_error);
                }
            }
        });
        JServerGuildUpdateOutput.remove_guildplayer(guildUUID, removedUUID);
        return true;
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

    public static GuildRang getDefaultRang(String rangName) {
        GuildRang rang = null;
        if (rangName.equalsIgnoreCase("master")) {
            rang = new GuildRang("master", UUID.randomUUID(), -1, true);
            rang.setPermission(GuildPermission.MASTERKEY);
        } else if (rangName.equalsIgnoreCase("assistant")) {
            rang = new GuildRang("assistant", UUID.randomUUID(), 1, false);
            rang.setPermission(GuildPermission.INVITE);
            rang.setPermission(GuildPermission.SETPLAYERRANG);
            rang.setPermission(GuildPermission.WITHDRAW);
            rang.setPermission(GuildPermission.KICK);

            rang.setPermission(GuildPermission.HOME);
            rang.setPermission(GuildPermission.HELP);
            rang.setPermission(GuildPermission.INFO);
            rang.setPermission(GuildPermission.LEAVE);
            rang.setPermission(GuildPermission.CHAT);
            rang.setPermission(GuildPermission.DEPOSIT);
        } else if (rangName.equalsIgnoreCase("member")) {
            rang = new GuildRang("member", UUID.randomUUID(), 2, true);
            rang.setPermission(GuildPermission.HOME);
            rang.setPermission(GuildPermission.HELP);
            rang.setPermission(GuildPermission.INFO);
            rang.setPermission(GuildPermission.LEAVE);
            rang.setPermission(GuildPermission.CHAT);
            rang.setPermission(GuildPermission.DEPOSIT);
        }
        return rang;
    }

    private static List<?> createPageEntries(List<?> fullObjects, int showMax, int number) {
        int pageNumb;
        if (number < 1) {
            pageNumb = 0;
        } else {
            pageNumb = number - 1;
        }
        int count = fullObjects.size();
        if (pageNumb * showMax > count) {
            return new ArrayList<>();
        }
        return fullObjects.subList(pageNumb * showMax, pageNumb * showMax + showMax > count ? count : pageNumb * showMax + showMax);
    }

    public static void broadcastGlobal(String text) {
        IChatChannel broadcastChannel = ChatManager.getChat("BROADCAST");
        if (broadcastChannel != null) {
            broadcastChannel.sendChat(null, text, null, null);
        }
    }

    public static void player_withdraw_task(UUID actor, double amount, String sourceServer) {
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

        if (!guild.hasPermission(guildPlayer, GuildPermission.WITHDRAW)) {
            player.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }
        player.sendMessage(LanguageDB.guild_transaction);
        JServerGuildUpdateOutput.accept_withdraw(guild.guildUUID, actor, amount, sourceServer);
    }

    public static void player_deposit_task(UUID actor, double amount, String sourceServer) {
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

        if (!guild.hasPermission(guildPlayer, GuildPermission.DEPOSIT)) {
            player.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        player.sendMessage(LanguageDB.guild_transaction);
        JServerGuildUpdateOutput.accept_deposit(guild.guildUUID, actor, amount, sourceServer);
    }
}
