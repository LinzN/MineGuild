package de.linzn.mineGuild.objects;


import java.util.UUID;

public class GuildPlayer {
    private Guild guild;
    private UUID playerUUID;
    private String rangName;

    public GuildPlayer(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }


    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public void setRangName(String rangName) {
        this.rangName = rangName;
    }

    public GuildRang getGuildRang() {
        return this.guild.getGuildRang(rangName);
    }


    public UUID getUUID() {
        return this.playerUUID;
    }


}