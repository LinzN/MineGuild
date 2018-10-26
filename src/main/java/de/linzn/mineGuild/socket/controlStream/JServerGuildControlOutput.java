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

package de.linzn.mineGuild.socket.controlStream;

import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildControlOutput {

    public static String headerChannel = "mineGuild_control";

    public static void send_guild_packet(String server, JSONObject jsonObject)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(server);
            dataOutputStream.writeUTF("guild_set_guild_packet");
            dataOutputStream.writeUTF(jsonObject.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }

    public static void request_guild_action_confirm(String server, UUID playerUUID, UUID guildUUID)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(server);
            dataOutputStream.writeUTF("request_confirm_guild_action");
            dataOutputStream.writeUTF(playerUUID.toString());
            dataOutputStream.writeUTF(guildUUID.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients(headerChannel, byteArrayOutputStream.toByteArray());
    }

}
