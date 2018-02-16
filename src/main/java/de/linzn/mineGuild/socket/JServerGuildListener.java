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
import de.linzn.mineSuite.bungee.database.DataHashTable;
import de.linzn.mineSuite.bungee.module.core.BungeeManager;
import de.linzn.mineSuite.bungee.utils.Location;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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

            if (subChannel.equalsIgnoreCase("createGuild")) {
                UUID guildUUID = UUID.fromString(in.readUTF());
                String guildName = in.readUTF();
                String guildMaster = in.readUTF();
                JServerGuildOutput.createGuild(guildUUID, guildName, guildMaster);
                return;
            }

            if (subChannel.equalsIgnoreCase("deleteGuild")) {
                UUID guildUUID = UUID.fromString(in.readUTF());
                JServerGuildOutput.deleteGuild(guildUUID);
                return;
            }
            if (subChannel.equalsIgnoreCase("UpdateGuildMaster")) {
                UUID guildUUID = UUID.fromString(in.readUTF());
                String oldMaster = in.readUTF();
                String newMaster = in.readUTF();
                JServerGuildOutput.updateGuildMaster(guildUUID, oldMaster, newMaster);
                return;
            }
            if (subChannel.equalsIgnoreCase("AddGuildToPlayer")) {
                String pname = in.readUTF();
                UUID guildUUID = UUID.fromString(in.readUTF());
                String gRang = in.readUTF();
                JServerGuildOutput.addPlayerToGuild(pname, guildUUID, gRang);
                return;
            }
            if (subChannel.equalsIgnoreCase("DeleteGuildFromPlayer")) {
                String pname = in.readUTF();
                String uuid = in.readUTF();
                JServerGuildOutput.deletePlayerFromGuild(pname, uuid);
                return;
            }

            if (subChannel.equals("SendGuildInvite")) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());
                ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(in.readUTF());
                String guildName = in.readUTF();
                UUID guildUUID = UUID.fromString(in.readUTF());

                if (invitedPlayer == null) {
                    player.sendMessage(ChatColor.RED + "Dieser Spieler ist nicht online!");
                    return;
                }
                invitedPlayer.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN
                        + " hat dir eine Einladung in die Gilde " + ChatColor.YELLOW + guildName + ChatColor.GREEN
                        + " gesendet. " + ChatColor.YELLOW + "/guild accept " + ChatColor.GREEN + "um anzunehmen!");
                DataHashTable.guildInvites.put(invitedPlayer.getUniqueId(), guildUUID);
                return;
            }
            if (subChannel.equals("AcceptGuildInvite")) {
                ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(in.readUTF());
                if (DataHashTable.guildInvites.containsKey(invitedPlayer.getUniqueId())) {
                    UUID guildUUID = DataHashTable.guildInvites.get(invitedPlayer.getUniqueId());
                    JServerGuildOutput.finishInvite(invitedPlayer, guildUUID);
                } else {
                    invitedPlayer.sendMessage(ChatColor.RED + "Du hast keine offenen Einladungen!");
                }
                DataHashTable.guildInvites.remove(invitedPlayer.getUniqueId());

            }

            if (subChannel.equals("UpdateGuildSpawn")) {
                UUID guildUUID = UUID.fromString(in.readUTF());
                String server = in.readUTF();
                String world = in.readUTF();
                double x = in.readDouble();
                double y = in.readDouble();
                double z = in.readDouble();
                float yaw = in.readFloat();
                float pitch = in.readFloat();

                JServerGuildOutput.updateGuildSpawn(guildUUID, server, world, x, y, z, yaw, pitch);

            }

            if (subChannel.equals("TeleportToGuildSpawn")) {
                ProxiedPlayer player = BungeeManager.getPlayer(in.readUTF());
                Location location = new Location(in.readUTF(), in.readUTF(), in.readDouble(),
                        in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat());
                JServerGuildOutput.teleportToGuildSpawn(player, location);
                ProxyServer.getInstance().getLogger().info("[MineSuite]" + player.getName() + " has been teleported with guild system.");
                ProxyServer.getInstance().getLogger().info("[MineSuite] S: " + location.getServer() + " W:" + location.getWorld() + " X:" + location.getX() + " Y:" + location.getY() + " Z:" + location.getZ());
                return;
            }

            if (subChannel.equals("SendExpUpdate")) {
                String server = in.readUTF();
                UUID guildUUID = UUID.fromString(in.readUTF());
                long exp = in.readLong();
                long totalXp = in.readLong();
                JServerGuildOutput.sendExpUpdate(server, guildUUID, exp, totalXp);

            }

            if (subChannel.equals("UpdateGuildName")) {
                UUID guildUUID = UUID.fromString(in.readUTF());
                String guildName = in.readUTF();
                JServerGuildOutput.updateGuildName(guildUUID, guildName);

            }

            if (subChannel.equals("UpdateGuildPlayer")) {
                UUID guildUUID = UUID.fromString(in.readUTF());
                String player = in.readUTF();
                String rang = in.readUTF();
                JServerGuildOutput.updateGuildPlayer(guildUUID, player, rang);

            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
