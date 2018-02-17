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

package de.linzn.mineGuild.database.mysql;

import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPermission;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineGuild.objects.GuildRang;
import de.linzn.mineSuite.bungee.database.mysql.setup.MySQLConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class GuildQuery {

    public static boolean setGuild(Guild guild) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        boolean success = false;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn
                    .prepareStatement("SELECT guild_name FROM guild_object WHERE guild_name = '" + guild.guildName + "';");
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                PreparedStatement insert = conn
                        .prepareStatement("INSERT INTO guild_object (guild_uuid, guild_name, guild_level, guild_experience) VALUES ('"
                                + guild.guildUUID.toString() + "', '" + guild.guildName + "', '"
                                + guild.guildLevel + "', '" + guild.guildExperience + "');");
                insert.executeUpdate();
                insert.close();
                success = true;
                private_set_guild_rangs(guild);
                private_set_guild_players(guild);
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private static void private_set_guild_rangs(Guild guild) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement insert = null;
            for (GuildRang guildRang : guild.guildRangs) {
                insert = conn
                        .prepareStatement("INSERT INTO guild_rang (rang_uuid, guild_uuid, rang_name) VALUES ('" + guildRang.rangUUID.toString() + "', '"
                                + guild.guildUUID.toString() + "', '" + guildRang.rangName + "');");
                insert.executeUpdate();
                private_set_rang_permissions(guildRang);
            }
            if (insert != null) {
                insert.close();
            }
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void private_set_rang_permissions(GuildRang guildRang) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement insert = null;
            for (GuildPermission permission : guildRang.permissions) {
                insert = conn
                        .prepareStatement("INSERT INTO guild_rang_permission (rang_uuid, permission) VALUES ('"
                                + guildRang.rangUUID.toString() + "', '" + permission.getValue() + "');");
                insert.executeUpdate();
            }
            if (insert != null) {
                insert.close();
            }
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void private_set_guild_players(Guild guild) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement insert = null;
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                insert = conn.prepareStatement("SELECT player_uuid FROM guild_entities WHERE player_uuid = '" + guildPlayer.getUUID() + "';");
                ResultSet result = insert.executeQuery();
                if (result.next()) {
                    insert = conn
                            .prepareStatement("UPDATE guild_entities SET guild_uuid = '" + guild.guildUUID.toString() + "', guild_rang = '" + guildPlayer.getGuildRang().rangUUID.toString() + "' WHERE player_uuid = '" + guildPlayer.getUUID() + "';");
                } else {
                    insert = conn
                            .prepareStatement("INSERT INTO guild_entities (player_uuid, guild_uuid, guild_rang) VALUES ('"
                                    + guildPlayer.getUUID() + "', '" + guild.guildUUID + "', '" + guildPlayer.getGuildRang().rangUUID.toString() + "');");
                }
                insert.executeUpdate();
            }
            if (insert != null) {
                insert.close();
            }
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean unsetGuild(UUID guildUUID) {

        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        boolean success = false;
        private_unset_guild_rangs(guildUUID);
        private_unset_guild_players(guildUUID);
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement update1 = conn.prepareStatement(
                    "DELETE FROM guild_object WHERE guild_uuid = '" + guildUUID.toString() + "';");
            update1.executeUpdate();
            update1.close();
            manager.release("MineSuiteGuild", conn);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private static boolean private_unset_guild_rangs(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        boolean success = false;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement update1;
            update1 = conn.prepareStatement("SELECT rang_uuid FROM guild_rang WHERE guild_uuid = '" + guildUUID.toString() + "';");
            ResultSet result = update1.executeQuery();
            while (result.next()) {
                success = private_unset_guild_permissions(UUID.fromString(result.getString("rang_uuid")));
            }
            update1 = conn.prepareStatement(
                    "DELETE FROM guild_rang WHERE guild_uuid = '" + guildUUID.toString() + "';");
            update1.executeUpdate();
            update1.close();
            manager.release("MineSuiteGuild", conn);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private static boolean private_unset_guild_permissions(UUID rang_uuid) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        boolean success = false;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement update1 = conn.prepareStatement(
                    "DELETE FROM guild_rang_permission WHERE rang_uuid = '" + rang_uuid.toString() + "';");
            update1.executeUpdate();
            update1.close();
            manager.release("MineSuiteGuild", conn);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private static boolean private_unset_guild_players(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        boolean success = false;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement update1 = conn.prepareStatement(
                    "DELETE FROM guild_entities WHERE guild_uuid = '" + guildUUID + "';");
            update1.executeUpdate();
            update1.close();
            manager.release("MineSuiteGuild", conn);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }


    /* Load all guild */
    public static HashSet<Guild> getAllGuilds() {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        HashSet<Guild> guilds = new HashSet<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn
                    .prepareStatement("SELECT guild_uuid FROM guild_object;");
            final ResultSet result = sql.executeQuery();
            if (result != null) {
                while (result.next()) {
                    UUID guildUUID = UUID.fromString(result.getString("guild_uuid"));
                    Guild guild = getGuild(guildUUID);
                    /* add to list */
                    guilds.add(guild);
                }
                result.close();

            }
            sql.close();
            manager.release("MineSuiteGuild", conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guilds;
    }

    public static Guild getGuild(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        Guild guild = null;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn
                    .prepareStatement("SELECT guild_name, guild_level, guild_experience FROM guild_object WHERE guild_uuid = '" + guildUUID + "';");
            final ResultSet result = sql.executeQuery();
            if (result.next()) {
                String guildName = result.getString("guild_name");

                guild = new Guild(guildName, guildUUID);
                guild.setLevel(result.getInt("guild_level"));
                guild.setTotalExp(result.getLong("guild_experience"));

                ArrayList<GuildRang> guildRangs = private_get_guild_rangs(guildUUID);
                /* add to list */
                for (GuildRang guildRang : guildRangs) {
                    guild.setGuildRang(guildRang);
                }

                ArrayList<GuildPlayer> guildPlayers = private_get_guild_players(guildUUID);
                /* add to list */
                for (GuildPlayer guildPlayer : guildPlayers) {
                    guildPlayer.setGuild(guild);
                    guild.setGuildPlayer(guildPlayer);
                }
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guild;
    }

    private static ArrayList<GuildRang> private_get_guild_rangs(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        ArrayList<GuildRang> rangList = new ArrayList<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT rang_uuid, rang_name FROM guild_rang WHERE guild_uuid = '" + guildUUID.toString() + "';");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                UUID rang_uuid = UUID.fromString(result.getString("rang_uuid"));
                String rang_name = result.getString("rang_name");
                GuildRang guildRang = new GuildRang(rang_name, rang_uuid);
                ArrayList<GuildPermission> permissions = private_get_rang_permission(rang_uuid);
                for (GuildPermission permission : permissions) {
                    guildRang.setPermission(permission);
                }
                /* add to list */
                rangList.add(guildRang);
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rangList;
    }

    private static ArrayList<GuildPermission> private_get_rang_permission(UUID rang_uuid) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        ArrayList<GuildPermission> permissions = new ArrayList<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT permission FROM guild_rang_permission WHERE rang_uuid = '" + rang_uuid.toString() + "';");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                String permission = result.getString("permission");
                /* add to list */
                permissions.add(GuildPermission.getPerm(permission));
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    private static ArrayList<GuildPlayer> private_get_guild_players(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        ArrayList<GuildPlayer> guildPlayers = new ArrayList<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT player_uuid, guild_rang FROM guild_entities WHERE guild_uuid = '" + guildUUID.toString() + "';");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                UUID playerUUID = UUID.fromString(result.getString("player_uuid"));
                UUID guild_rang_uuid = UUID.fromString(result.getString("guild_rang"));
                GuildPlayer guildPlayer = new GuildPlayer(playerUUID);
                guildPlayer.setRangUUID(guild_rang_uuid);
                /* add to list */
                guildPlayers.add(guildPlayer);
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guildPlayers;
    }


}
