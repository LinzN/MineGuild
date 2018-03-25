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

public class JServerGuildUpdateOutput {


    public static void set_guild_data(String server, Guild guild)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(server);
            dataOutputStream.writeUTF("guild_set_guild_data");
            dataOutputStream.writeUTF(guild.guildUUID.toString());
            dataOutputStream.writeUTF(guild.guildName);
            dataOutputStream.writeInt(guild.guildLevel);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineGuild_update", byteArrayOutputStream.toByteArray());
    }

    public static void set_guildplayer_data(String server, GuildPlayer guildPlayer)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(server);
            dataOutputStream.writeUTF("guild_set_guildplayer_data");
            dataOutputStream.writeUTF(guildPlayer.getGuild().guildUUID.toString());
            dataOutputStream.writeUTF(guildPlayer.getUUID().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineGuild_update", byteArrayOutputStream.toByteArray());
    }


}
