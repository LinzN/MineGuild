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

package de.linzn.mineGuild.socket.editStream;


import de.linzn.jSocket.core.IncomingDataListener;
import de.linzn.mineGuild.manager.GuildManager;
import de.linzn.mineSuite.bungee.utils.Location;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class JServerGuildEditListener implements IncomingDataListener {


    @Override
    public void onEvent(String channel, UUID clientUUID, byte[] dataInBytes) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(dataInBytes));
        String subChannel;
        try {
            subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("guild_edit_guild_name")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String guildName = in.readUTF();
                GuildManager.setGuildName(actorUUID, guildName);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_edit_guild_master")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String playerName = in.readUTF();
                GuildManager.setGuildMaster(actorUUID, playerName);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_edit_guild_home")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String server = in.readUTF();
                String world = in.readUTF();
                double X = in.readDouble();
                double Y = in.readDouble();
                double Z = in.readDouble();
                float yaw = in.readFloat();
                float pitch = in.readFloat();
                Location location = new Location(server, world, X, Y, Z, yaw, pitch);
                GuildManager.setGuildHome(actorUUID, location);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_edit_setrang_player")) {
                UUID actorUUID = UUID.fromString(in.readUTF());
                String playerName = in.readUTF();
                String rangName = in.readUTF();

                GuildManager.setPlayerRANG(actorUUID, playerName, rangName);
                return;
            }


        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
