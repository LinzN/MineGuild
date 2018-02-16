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

package de.linzn.mineGuild.socket;

import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;
import de.linzn.mineSuite.bungee.utils.Location;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildOutput {

    public static void deletePlayerFromGuild(String pname, String uuid)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("DeleteGuildFromPlayer");
            dataOutputStream.writeUTF(pname);
            dataOutputStream.writeUTF(uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void addPlayerToGuild(String pname, UUID guildUUID, String gRang)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("AddGuildToPlayer");
            dataOutputStream.writeUTF(pname);
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(gRang);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void updateGuildMaster(UUID guildUUID, String oldMaster, String newMaster)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("UpdateGuildMaster");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(oldMaster);
            dataOutputStream.writeUTF(newMaster);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void updateGuildPlayer(UUID guildUUID, String player, String rang)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("UpdateGuildPlayer");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(player);
            dataOutputStream.writeUTF(rang);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void deleteGuild(UUID guildUUID)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("deleteGuild");
            dataOutputStream.writeUTF(guildUUID.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void createGuild(UUID guildUUID, String guildName, String guildMaster)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("createGuild");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(guildName);
            dataOutputStream.writeUTF(guildMaster);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void finishInvite(ProxiedPlayer invitedPlayer, UUID guildUUID)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("FinishGuildInvite");
            dataOutputStream.writeUTF(invitedPlayer.getName());
            dataOutputStream.writeUTF(guildUUID.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void updateGuildSpawn(UUID guildUUID, String server, String world, double x, double y, double z,
                                        float yaw, float pitch)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("UpdateGuildSpawn");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(server);
            dataOutputStream.writeUTF(world);
            dataOutputStream.writeDouble(x);
            dataOutputStream.writeDouble(y);
            dataOutputStream.writeDouble(z);
            dataOutputStream.writeFloat(yaw);
            dataOutputStream.writeFloat(pitch);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void sendExpUpdate(String server, UUID guildUUID, long exp, long totalXP)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("SendExpUpdate");
            dataOutputStream.writeUTF(server);
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeLong(exp);
            dataOutputStream.writeLong(totalXP);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void updateGuildName(UUID guildUUID, String guildName)

    {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("UpdateGuildName");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(guildName);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }

    public static void teleportToGuildSpawn(ProxiedPlayer player, Location loc)

    {
        ServerInfo serverNew = ProxyServer.getInstance().getServerInfo(loc.getServer());
        if (serverNew == null) {
            MineSuiteBungeePlugin.getInstance().getLogger()
                    .severe("Location has no Server, this should never happen. Please check");
            new Exception("").printStackTrace();
            return;
        }

        if (player == null) {
            new Exception("").printStackTrace();
            return;
        }

        if (player.getServer() == null || !player.getServer().getInfo().toString().equals(serverNew.toString())) {
            player.connect(serverNew);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("TeleportToGuildSpawn");
            dataOutputStream.writeUTF(serverNew.getName());
            dataOutputStream.writeUTF(player.getName());
            dataOutputStream.writeUTF(loc.getWorld());
            dataOutputStream.writeDouble(loc.getX());
            dataOutputStream.writeDouble(loc.getY());
            dataOutputStream.writeDouble(loc.getZ());
            dataOutputStream.writeFloat(loc.getYaw());
            dataOutputStream.writeFloat(loc.getPitch());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineSuiteGuild", byteArrayOutputStream.toByteArray());
    }
}
