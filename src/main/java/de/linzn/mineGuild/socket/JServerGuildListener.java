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

import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.mineGuild.manager.GuildManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildListener implements IncomingDataListener {


    @Override
    public void onEvent(String channel, UUID clientUUID, byte[] dataInBytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(dataInBytes));
        String subChannel;
        try {
            subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("guild_create_guild")) {
                String guildName = in.readUTF();
                UUID creator = UUID.fromString(in.readUTF());
                GuildManager.establishGuild(guildName, creator);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_remove_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                GuildManager.disbandGuild(actor);
                return;
            }


        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
