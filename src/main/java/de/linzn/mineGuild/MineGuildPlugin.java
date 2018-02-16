package de.linzn.mineGuild;


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
        MineSuiteBungeePlugin.getInstance().getMineJSocketServer().jServer.registerIncomingDataListener("mineSuiteGuild", new JServerGuildListener());
    }
}
