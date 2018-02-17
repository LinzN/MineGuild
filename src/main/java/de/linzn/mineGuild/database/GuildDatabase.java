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

package de.linzn.mineGuild.database;


import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineSuite.bungee.database.mysql.BungeeQuery;
import net.md_5.bungee.api.ProxyServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class GuildDatabase {
    private static HashMap<UUID, Guild> guilds = new HashMap<>();

    public static GuildPlayer getGuildPlayer(String playerName) {
        UUID playerUUID = null;
        if (ProxyServer.getInstance().getPlayer(playerName) != null) {
            playerUUID = ProxyServer.getInstance().getPlayer(playerName).getUniqueId();
        }
        if (playerUUID == null) {
            playerUUID = BungeeQuery.getUUID(playerName);
        }

        return getGuildPlayer(playerUUID);

    }

    public static GuildPlayer getGuildPlayer(UUID playerUUID) {
        for (Guild guild : guilds.values()) {
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                if (guildPlayer.getUUID().equals(playerUUID)) {
                    return guildPlayer;
                }
            }
        }
        return null;
    }

    public static Guild getGuild(UUID uuid) {
        return guilds.getOrDefault(uuid, null);
    }

    public static Guild getGuild(String gName) {
        for (Guild guild : guilds.values()) {
            if (guild.guildName.equalsIgnoreCase(gName)) {
                return guild;
            }
        }
        return null;

    }

    public static Collection<Guild> getGuilds() {
        return guilds.values();
    }

    public static boolean isGuild(String gName) {
        for (Guild guild : guilds.values()) {
            if (guild.guildName.equalsIgnoreCase(gName)) {
                return true;
            }
        }
        return false;
    }


    public static void addGuild(Guild guild) {
        guilds.put(guild.guildUUID, guild);
    }

    public static void removeGuild(UUID uuid) {
        guilds.remove(uuid);
    }


}