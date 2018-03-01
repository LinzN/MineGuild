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

import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JServerGuildOutput {

    public static void sendGuildData(Guild guild)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("guild_update_data");
            dataOutputStream.writeUTF(guild.guildUUID.toString());
            dataOutputStream.writeUTF(guild.guildName);
            dataOutputStream.writeInt(guild.guildLevel);
            dataOutputStream.writeInt(guild.guildPlayers.size());
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                dataOutputStream.writeUTF(guildPlayer.getUUID().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineGuild", byteArrayOutputStream.toByteArray());
    }

}
