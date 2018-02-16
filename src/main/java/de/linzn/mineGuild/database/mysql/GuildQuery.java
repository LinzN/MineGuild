package de.linzn.mineGuild.database.mysql;

import de.linzn.mineGuild.objects.Guild;
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
                setGuildRangs(guild);
                setGuildPlayers(guild);
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private static void setGuildRangs(Guild guild) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement insert = null;
            for (GuildRang guildRang : guild.guildRangs) {
                insert = conn
                        .prepareStatement("INSERT INTO guild_rang (guild_uuid, rang_name) VALUES ('"
                                + guild.guildUUID.toString() + "', '" + guildRang.rangName + "');");
                insert.executeUpdate();

                insert = conn.prepareStatement("SELECT id FROM guild_rang WHERE guild_uuid = '" + guild.guildUUID.toString() + "' AND rang_name = '" + guildRang.rangName + "';");
                ResultSet result = insert.executeQuery();
                int rang_id = -1;
                if (result.next()) {
                    rang_id = result.getInt("id");
                }
                guildRang.setRangId(rang_id);

                setRangPermissions(guildRang);
            }
            if (insert != null) {
                insert.close();
            }
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setRangPermissions(GuildRang guildRang) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement insert = null;
            for (String permission : guildRang.permissions) {
                insert = conn
                        .prepareStatement("INSERT INTO guild_rang_permission (rang_id, permission) VALUES ('"
                                + guildRang.rangId + "', '" + permission + "');");
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

    private static void setGuildPlayers(Guild guild) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement insert = null;
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                insert = conn.prepareStatement("SELECT player_uuid FROM guild_entities WHERE player_uuid = '" + guildPlayer.getUUID() + "';");
                ResultSet result = insert.executeQuery();
                if (result.next()) {
                    insert = conn
                            .prepareStatement("UPDATE guild_entities SET guild_uuid = '" + guild.guildUUID.toString() + "', guild_rang = '" + guildPlayer.getGuildRang().rangName + "';");
                    insert.executeUpdate();
                } else {
                    insert = conn
                            .prepareStatement("INSERT INTO guild_entities (player_uuid, guild_uuid, guild_rang) VALUES ('"
                                    + guildPlayer.getUUID() + "', '" + guild.guildUUID + "', '" + guildPlayer.getGuildRang().rangName + "');");
                    insert.executeUpdate();
                }
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
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT GuildUUID FROM guild_object WHERE guild_uuid = '" + guildUUID.toString() + "';");
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                PreparedStatement update1 = conn.prepareStatement(
                        "DELETE FROM guild_object WHERE guild_uuid = '" + guildUUID.toString() + "';");
                update1.executeUpdate();
                update1.close();
                success = true;
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);

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

                ArrayList<GuildRang> guildRangs = getGuildRangs(guildUUID);
                /* add to list */
                for (GuildRang guildRang : guildRangs) {
                    guild.setGuildRang(guildRang);
                }

                ArrayList<GuildPlayer> guildPlayers = getGuildPlayers(guildUUID);
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

    private static ArrayList<GuildRang> getGuildRangs(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        ArrayList<GuildRang> rangList = new ArrayList<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT id, rang_name FROM guild_rang WHERE guild_uuid = '" + guildUUID.toString() + "';");
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                int rang_id = result.getInt("id");
                String rang_name = result.getString("rang_name");
                GuildRang guildRang = new GuildRang(rang_name);
                guildRang.setRangId(rang_id);
                ArrayList<String> permissions = getRangPermission(rang_id);
                for (String permission : permissions) {
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

    private static ArrayList<String> getRangPermission(int rang_id) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        ArrayList<String> permissions = new ArrayList<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT permission FROM guild_rang_permission WHERE rang_id = '" + rang_id + "';");
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                String permission = result.getString("permission");
                /* add to list */
                permissions.add(permission);
            }
            result.close();
            sql.close();
            manager.release("MineSuiteGuild", conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    private static ArrayList<GuildPlayer> getGuildPlayers(UUID guildUUID) {
        MySQLConnectionManager manager = MySQLConnectionManager.DEFAULT;
        ArrayList<GuildPlayer> guildPlayers = new ArrayList<>();
        try {
            Connection conn = manager.getConnection("MineSuiteGuild");
            PreparedStatement sql = conn.prepareStatement(
                    "SELECT player_uuid, guild_rang FROM guild_entities WHERE guild_uuid = '" + guildUUID.toString() + "';");
            ResultSet result = sql.executeQuery();
            if (result.next()) {
                UUID playerUUID = UUID.fromString(result.getString("player_uuid"));
                String guildRang = result.getString("guild_rang");
                GuildPlayer guildPlayer = new GuildPlayer(playerUUID);
                guildPlayer.setRangName(guildRang);
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
