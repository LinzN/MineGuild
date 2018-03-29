package de.linzn.mineGuild.events;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class GuildDisbandEvent extends Event {

    private final UUID guildUUID;
    private final UUID actorUUID;


    public GuildDisbandEvent(UUID guildUUID, UUID actorUUID) {
        this.guildUUID = guildUUID;
        this.actorUUID = actorUUID;
    }

    public UUID getGuildUUID() {
        return guildUUID;
    }

    public UUID getActorUUID() {
        return actorUUID;
    }
}
