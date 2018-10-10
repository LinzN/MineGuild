package de.linzn.mineGuild.listener;

import de.linzn.mineGuild.api.events.GuildLevelUpEvent;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.utils.LanguageDB;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerDisconnectEvent event) {
        GuildDatabase.removeGuildInvitation(event.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onGuildLevelUp(GuildLevelUpEvent event) {
        Guild guild = event.getGuild();
        guild.broadcastInGuild(LanguageDB.guild_level_up.replace("{level}", "" + event.getNewGuildLevel()));
    }

}
