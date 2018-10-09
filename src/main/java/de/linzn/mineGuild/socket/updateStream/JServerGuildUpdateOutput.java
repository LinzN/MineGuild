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

package de.linzn.mineGuild.socket.updateStream;

import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildUpdateOutput {

    public static String headerChannel = "mineGuild_update";

    public static void add_guildplayer(GuildPlayer guildPlayer) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("all");
            dataOutputStream.writeUTF("guild_add_guildplayer");
            dataOutputStream.writeUTF(guildPlayer.getGuild().guildUUID.toString());
            dataOutputStream.writeUTF(guildPlayer.getUUID().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }

    public static void remove_guildplayer(UUID guildUUID, UUID playerUUID) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("all");
            dataOutputStream.writeUTF("guild_remove_guildplayer");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(playerUUID.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }

    public static void add_guild(Guild guild, UUID owner) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("all");
            dataOutputStream.writeUTF("guild_add_guild");
            dataOutputStream.writeUTF(owner.toString());
            dataOutputStream.writeUTF(guild.guildUUID.toString());
            dataOutputStream.writeInt(guild.guildLevel);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());

    }

    public static void remove_guild(UUID guildUUID) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("all");
            dataOutputStream.writeUTF("guild_remove_guild");
            dataOutputStream.writeUTF(guildUUID.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());

    }

    public static void update_guild(Guild guild) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeUTF("all");
            dataOutputStream.writeUTF("guild_update_guild");
            dataOutputStream.writeUTF(guild.guildUUID.toString());
            dataOutputStream.writeInt(guild.guildLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }

    public static void accept_withdraw(UUID guildUUID, UUID actor, double amount, String sourceServer){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeUTF(sourceServer);
            dataOutputStream.writeUTF("guild_accept_withdraw");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(actor.toString());
            dataOutputStream.writeDouble(amount);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }

    public static void accept_deposit(UUID guildUUID, UUID actor, double amount, String sourceServer){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeUTF(sourceServer);
            dataOutputStream.writeUTF("guild_accept_deposit");
            dataOutputStream.writeUTF(guildUUID.toString());
            dataOutputStream.writeUTF(actor.toString());
            dataOutputStream.writeDouble(amount);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }


}
