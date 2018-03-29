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

package de.linzn.mineGuild.objects;


import de.linzn.mineGuild.MineGuildPlugin;
import de.linzn.mineGuild.events.GuildLevelUpEvent;
import de.linzn.mineSuite.bungee.utils.Location;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.HashSet;
import java.util.UUID;

public class Guild {
    public String guildName;
    public UUID guildUUID;
    public double guildExperience;
    public int guildLevel;
    public HashSet<GuildRang> guildRangs;
    public HashSet<GuildPlayer> guildPlayers;
    public Location guildHome;


    public Guild(String guildName, UUID guildUUID) {
        this.guildName = guildName;
        this.guildUUID = guildUUID;
        this.guildExperience = 0L;
        this.guildLevel = 1;
        this.guildRangs = new HashSet<>();
        this.guildPlayers = new HashSet<>();
    }

    /* Open initial value */
    public void set_guild_name(String guildName) {
        this.guildName = guildName;
    }

    public void set_total_exp(long exp) {
        this.guildExperience = exp;
    }

    public void set_level(int level) {
        this.guildLevel = level;
    }

    public void set_guild_home(Location location) {
        this.guildHome = location;
    }

    public void set_guild_rang(GuildRang guildRang) {
        this.guildRangs.add(guildRang);
    }

    public void unset_guild_rang(GuildRang guildRang) {
        this.guildRangs.remove(guildRang);
    }

    /* Close initial value */

    public void addGuildPlayer(GuildPlayer guildPlayer) {
        this.guildPlayers.add(guildPlayer);
    }

    public void removeGuildPlayer(GuildPlayer guildPlayer) {
        this.guildPlayers.remove(guildPlayer);
    }

    public void addExperience(double data) {
        int maxLevel = 30;
        if (this.guildExperience + data >= getGuildRequiredExperience()) {
            double nextExp = this.guildExperience + data - getGuildRequiredExperience();
            this.guildExperience = 0L;
            this.guildLevel = this.guildLevel + 1;
            /* Call levelup event */
            GuildLevelUpEvent gEvent = new GuildLevelUpEvent(this, this.guildLevel);
            MineGuildPlugin.inst().getProxy().getPluginManager().callEvent((Event) gEvent);

            MineGuildPlugin.inst().getLogger().info("New Level UP Guild " + this.guildName);
            if (this.guildLevel == maxLevel) {
                this.guildExperience = 0L;
                return;
            }
            addExperience(nextExp);
        } else {
            this.guildExperience = this.guildExperience + data;
        }
    }

    public GuildRang getGuildRang(String rangName) {
        for (GuildRang guildRang : this.guildRangs) {
            if (guildRang.rangName.equalsIgnoreCase(rangName)) {
                return guildRang;
            }
        }
        return null;
    }

    public GuildRang getGuildRang(UUID rangUUID) {
        for (GuildRang guildRang : this.guildRangs) {
            if (guildRang.rangUUID.equals(rangUUID)) {
                return guildRang;
            }
        }
        return null;
    }

    public boolean hasPermission(GuildPlayer guildPlayer, GuildPermission permission) {
        return guildPlayer.getGuildRang().hasPermission(permission);
    }


    public double getGuildRequiredExperience() {
        long base = 1100;
        double multi;
        if (this.guildLevel <= 10) {
            multi = 1.24;
        } else if (this.guildLevel <= 20) {
            multi = 1.44;
        } else if (this.guildLevel <= 29) {
            multi = 1.64;
        } else {
            multi = 1.94;
        }
        return (base ^ this.guildLevel) * multi * this.guildLevel;
    }

    public void broadcastInGuild(String text) {
        for (GuildPlayer guildPlayer : this.guildPlayers) {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(guildPlayer.getUUID());
            if (player != null) {
                player.sendMessage(text);
            }
        }
    }

}