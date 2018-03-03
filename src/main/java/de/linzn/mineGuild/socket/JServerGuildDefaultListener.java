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

public class JServerGuildDefaultListener implements IncomingDataListener {


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

            if (subChannel.equalsIgnoreCase("guild_info_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                String guildArg = in.readUTF();
                GuildManager.showGuildInformation(actor, guildArg);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_info_guild_members")) {
                UUID actor = UUID.fromString(in.readUTF());
                String guildArg = in.readUTF();
                GuildManager.showGuildMembers(actor, guildArg);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_invite_to_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                String invitedName = in.readUTF();
                GuildManager.processInvitation(actor, invitedName);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_accept_invite_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                GuildManager.acceptInvitation(actor);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_deny_invite_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                GuildManager.denyInvitation(actor);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_kick_from_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                String kickedName = in.readUTF();
                GuildManager.processKick(actor, kickedName);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_leave_from_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                GuildManager.playerLeave(actor);
                return;
            }
            if (subChannel.equalsIgnoreCase("guild_send_spawn_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                // GuildManager.playerLeave(actor);
                return;
            }

            if (subChannel.equalsIgnoreCase("guild_list_all_guild")) {
                UUID actor = UUID.fromString(in.readUTF());
                int page = in.readInt();
                // GuildManager.playerLeave(actor);
                return;
            }


        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
