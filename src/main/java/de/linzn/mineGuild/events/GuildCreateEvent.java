package de.linzn.mineGuild.events;

import de.linzn.mineGuild.objects.Guild;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class GuildCreateEvent extends Event {

    private final Guild guild;
    private final UUID ownerUUID;


    public GuildCreateEvent(Guild guild, UUID ownerUUID) {
        this.guild = guild;
        this.ownerUUID = ownerUUID;
    }

    public Guild getGuild() {
        return guild;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}
