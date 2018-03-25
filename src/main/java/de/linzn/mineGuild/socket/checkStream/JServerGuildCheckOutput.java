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

package de.linzn.mineGuild.socket.checkStream;

import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JServerGuildCheckOutput {

    public static void checkDeposit(Guild guild)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("guild_check_deposit");
            dataOutputStream.writeUTF(guild.guildUUID.toString());
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                dataOutputStream.writeUTF(guildPlayer.getUUID().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineGuild", byteArrayOutputStream.toByteArray());
    }

    public static void checkWithdraw(Guild guild)

    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("guild_check_withdraw");
            dataOutputStream.writeUTF(guild.guildUUID.toString());
            for (GuildPlayer guildPlayer : guild.guildPlayers) {
                dataOutputStream.writeUTF(guildPlayer.getUUID().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().broadcastClients("mineGuild", byteArrayOutputStream.toByteArray());
    }


}
