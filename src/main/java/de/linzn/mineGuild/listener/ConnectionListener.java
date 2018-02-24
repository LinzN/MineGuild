package de.linzn.mineGuild.listener;

import de.linzn.mineGuild.database.GuildDatabase;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerDisconnectEvent event) {
        GuildDatabase.removeGuildInvitation(event.getPlayer().getUniqueId());
    }
}
