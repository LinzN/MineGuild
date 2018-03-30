package de.linzn.mineGuild.api.events;

import de.linzn.mineGuild.objects.Guild;
import net.md_5.bungee.api.plugin.Event;

public class GuildLevelUpEvent extends Event {

    private final Guild guild;
    private final int newGuildLevel;

    public GuildLevelUpEvent(Guild guild, int newGuildLevel) {
        this.guild = guild;
        this.newGuildLevel = newGuildLevel;
    }

    public Guild getGuild() {
        return guild;
    }

    public int getNewGuildLevel() {
        return newGuildLevel;
    }
}
