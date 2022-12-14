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

package de.linzn.mineGuild.socket.rangStream;


import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.mineGuild.manager.GuildManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildRangListener implements IncomingDataListener {


    @Override
    public void onEvent(String channel, UUID clientUUID, byte[] dataInBytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(dataInBytes));
        String subChannel;
        try {
            subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("guild_rang_setplayer")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String playerName = in.readUTF();
                String rangName = in.readUTF();

                GuildManager.setPlayerRANG(actorUUID, playerName, rangName);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_rang_show_rang_info")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String rangName = in.readUTF();
                GuildManager.showRangInfo(actorUUID, rangName);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_rang_show_player_rang")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String playerName = in.readUTF();
                GuildManager.showPlayerRang(actorUUID, playerName);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_rang_show_list")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                int page = in.readInt();
                GuildManager.showGuildRangList(actorUUID, page);
                return;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
