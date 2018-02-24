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


import de.linzn.mineGuild.listener.ConnectionListener;
import de.linzn.mineGuild.manager.GuildManager;
import de.linzn.mineGuild.socket.JServerGuildListener;
import de.linzn.mineSuite.bungee.MineSuiteBungeePlugin;
import net.md_5.bungee.api.plugin.Plugin;

public class MineGuildPlugin extends Plugin {

    private static MineGuildPlugin inst;

    public static MineGuildPlugin inst() {
        return inst;
    }

    public void onDisable() {
    }

    public void onEnable() {
        inst = this;
        GuildManager.loadData();
        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().jServer.registerIncomingDataListener("mineGuild", new JServerGuildListener());
        this.getProxy().getPluginManager().registerListener(this, new ConnectionListener());
    }
}
