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

package de.linzn.mineGuild;


import codecrafter47.bungeetablistplus.api.bungee.BungeeTabListPlusAPI;
import de.linzn.jSocket.server.JServer;
import de.linzn.mineGuild.listener.ConnectionListener;
import de.linzn.mineGuild.manager.GuildManager;
import de.linzn.mineGuild.socket.commandStream.JServerGuildCommandListener;
import de.linzn.mineGuild.socket.commandStream.JServerGuildCommandOutput;
import de.linzn.mineGuild.socket.controlStream.JServerGuildControlListener;
import de.linzn.mineGuild.socket.controlStream.JServerGuildControlOutput;
import de.linzn.mineGuild.socket.editStream.JServerGuildEditListener;
import de.linzn.mineGuild.socket.editStream.JServerGuildEditOutput;
import de.linzn.mineGuild.socket.rangStream.JServerGuildRangListener;
import de.linzn.mineGuild.socket.rangStream.JServerGuildRangOutput;
import de.linzn.mineGuild.socket.updateStream.JServerGuildUpdateListener;
import de.linzn.mineGuild.socket.updateStream.JServerGuildUpdateOutput;
import de.linzn.mineGuild.utils.BungeeTabListPlusHook;
import de.linzn.mineGuild.utils.GuildChat;
import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;
import de.linzn.mineSuite.bungee.module.chat.ChatManager;
import net.md_5.bungee.api.plugin.Plugin;

public class MineGuildPlugin extends Plugin {

    private static MineGuildPlugin inst;

    public static MineGuildPlugin inst() {
        return inst;
    }

    public void onDisable() {
        this.getLogger().info("Disable MineGuild");
    }

    public void onEnable() {
        this.getLogger().info("Enable MineGuild");
        inst = this;
        GuildManager.loadData();
        JServer jServer = MineSuiteBungeePlugin.getInstance().getMineJSocketServer().jServer;
        jServer.registerIncomingDataListener(JServerGuildCommandOutput.headerChannel, new JServerGuildCommandListener());
        jServer.registerIncomingDataListener(JServerGuildEditOutput.headerChannel, new JServerGuildEditListener());
        jServer.registerIncomingDataListener(JServerGuildRangOutput.headerChannel, new JServerGuildRangListener());
        jServer.registerIncomingDataListener(JServerGuildUpdateOutput.headerChannel, new JServerGuildUpdateListener());
        jServer.registerIncomingDataListener(JServerGuildControlOutput.headerChannel, new JServerGuildControlListener());
        this.getProxy().getPluginManager().registerListener(this, new ConnectionListener());

        ChatManager.registerChat(new GuildChat());

        if (this.getProxy().getPluginManager().getPlugin("BungeeTabListPlus") != null) {
            BungeeTabListPlusAPI.registerVariable(this, new BungeeTabListPlusHook());
            this.getLogger().info("Hooked into bungeeTablistPlus!");
        }
    }
}
