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

package de.linzn.mineGuild.utils;

import codecrafter47.bungeetablistplus.api.bungee.Variable;
import de.linzn.mineGuild.database.GuildDatabase;
import de.linzn.mineGuild.objects.GuildPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeTabListPlusHook extends Variable {


    public BungeeTabListPlusHook() {
        super("mineGuild_guild");
    }

    @Override
    public String getReplacement(ProxiedPlayer proxiedPlayer) {
        String value = LanguageDB.bungeetablist_no_guild;
        GuildPlayer guildPlayer = GuildDatabase.getGuildPlayer(proxiedPlayer.getUniqueId());
        if (guildPlayer != null) {
            value = guildPlayer.getGuild().guildName;
            if (value.length() > 16) {
                value = value.substring(0, 13) + "...";
            }
        }
        return value;
    }
}
