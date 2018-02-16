package de.linzn.mineGuild.database;


import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class GuildDatabase {
    private static HashMap<String, GuildPlayer> guildPlayers = new HashMap<>();
    private static HashMap<UUID, Guild> guilds = new HashMap<>();

    public static GuildPlayer getGuildPlayer(String name) {
        return guildPlayers.getOrDefault(name.toLowerCase(), null);
    }

    public static GuildPlayer getGuildPlayer(UUID uuid) {
        for (GuildPlayer player : guildPlayers.values()) {
            if (player.getUUID().toString().equalsIgnoreCase(uuid.toString())) {
                return player;
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
        for (Guild g : guilds.values()) {
            if (g.guildName.equalsIgnoreCase(gName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerOnline(String pName) {
        return guildPlayers.containsKey(pName.toLowerCase());
    }

    public static boolean isPlayerOnline(UUID uuid) {
        for (GuildPlayer player : guildPlayers.values()) {
            if (player.getUUID().toString().equalsIgnoreCase(uuid.toString())) {
                return true;
            }
        }
        return false;

    }

    public static void addGuildPlayer(GuildPlayer gPlayer) {
        guildPlayers.put(gPlayer.getName().toLowerCase(), gPlayer);
    }

    public static void removeGuildPlayer(String gPlayername) {
        guildPlayers.remove(gPlayername.toLowerCase());
    }


    public static void removeGuildPlayersFromGuild(UUID uuid) {
        HashSet<GuildPlayer> updatePlayers = new HashSet<>();
        for (GuildPlayer guildPlayer : guildPlayers.values()) {
            if (guildPlayer.getGuild() != null && guildPlayer.getGuild().guildUUID == uuid) {
                updatePlayers.add(guildPlayer);
            }
        }
        for (GuildPlayer guildPlayer : updatePlayers) {
            removeGuildPlayer(guildPlayer.getName());
        }
    }

    public static void addGuild(Guild guild) {
        guilds.put(guild.guildUUID, guild);
    }

    public static void removeGuild(UUID uuid) {
        guilds.remove(uuid);
    }

    public static Collection<GuildPlayer> getOnlineGuildPlayers() {
        return guildPlayers.values();

    }

}