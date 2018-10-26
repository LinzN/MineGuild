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


import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.manager.InternalGuildManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildControlListener implements IncomingDataListener {


    @Override
    public void onEvent(String channel, UUID clientUUID, byte[] dataInBytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(dataInBytes));
        String subChannel;
        try {
            subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("request_all_guild_data")) {
                String serverName = in.readUTF();
                InternalGuildManager.server_data_request(serverName);
                return;
            }

            if (subChannel.equalsIgnoreCase("confirm_guild_action")) {
                UUID playerUUID = UUID.fromString(in.readUTF());
                UUID guildUUID = UUID.fromString(in.readUTF());
                if (GuildDatabase.waitingGuildConfirms.containsKey(playerUUID)){
                    if (GuildDatabase.waitingGuildConfirms.get(playerUUID).getKey().equals(guildUUID)) {
                        GuildDatabase.waitingGuildConfirms.get(playerUUID).getValue().set(true);
                    }
                }
                return;
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
