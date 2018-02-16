package de.linzn.mineGuild.objects;


import java.util.HashSet;
import java.util.UUID;

public class Guild {
    public String guildName;
    public UUID guildUUID;
    public long guildExperience;
    public int guildLevel;
    public HashSet<GuildRang> guildRangs;
    public HashSet<GuildPlayer> guildPlayers;


    public Guild(String guildName, UUID guildUUID) {
        this.guildName = guildName;
        this.guildUUID = guildUUID;
        this.guildExperience = 0L;
        this.guildLevel = 1;
        this.guildRangs = new HashSet<>();
        this.guildPlayers = new HashSet<>();
    }

    public void setGuildRang(GuildRang guildRang) {
        this.guildRangs.add(guildRang);
    }

    public void unsetGuildRang(GuildRang guildRang) {
        this.guildRangs.remove(guildRang);
    }

    public void setGuildPlayer(GuildPlayer guildPlayer) {
        this.guildPlayers.add(guildPlayer);
    }

    public void unsetGuildPlayer(GuildPlayer guildPlayer) {
        this.guildPlayers.remove(guildPlayer);
    }

    public GuildRang getGuildRang(String rangName) {
        for (GuildRang guildRang : this.guildRangs) {
            if (guildRang.rangName.equalsIgnoreCase(rangName)) {
                return guildRang;
            }
        }
        return null;
    }

    public boolean hasPermission(GuildPlayer guildPlayer, String permission) {
        return guildPlayer.getGuildRang().hasPermission(permission);
    }


    public long getGuildRequiredExperience() {
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
        return (base ^ this.guildLevel) * (long) (multi * this.guildLevel) * 100L;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public void setTotalExp(long exp) {
        this.guildExperience = exp;
    }

    public void setLevel(int level) {
        this.guildLevel = level;
    }


}