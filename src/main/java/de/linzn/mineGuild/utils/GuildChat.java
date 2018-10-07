package de.linzn.mineGuild.utils;

import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.objects.Guild;
import de.linzn.mineGuild.objects.GuildPermission;
import de.linzn.mineGuild.objects.GuildPlayer;
import de.linzn.mineSuite.bungee.module.chat.IChatChannel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GuildChat implements IChatChannel {

    @Override
    public void sendChat(String sender, String text, String prefix, String suffix) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(sender);
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(proxiedPlayer.getName());

        if (guildPlayer == null) {
            proxiedPlayer.sendMessage(LanguageDB.you_not_in_guild);
            return;
        }

        Guild guild = guildPlayer.getGuild();

        if (!guild.hasPermission(guildPlayer, GuildPermission.CHAT)) {
            proxiedPlayer.sendMessage(LanguageDB.you_no_guild_perm);
            return;
        }

        String chatFormat = "§6[§aGC§6] {player}: §a{text}";
        guild.broadcastInGuild(chatFormat.replace("{player}", proxiedPlayer.getName()).replace("{text}", text));

    }

    @Override
    public String getChannelName() {
        return "GUILD";
    }
}
