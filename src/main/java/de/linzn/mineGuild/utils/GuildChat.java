package de.linzn.mineGuild.utils;

import de.linzn.mineSuite.bungee.module.chat.IChatChannel;

public class GuildChat implements IChatChannel {
    @Override
    public void sendChat(String sender, String text, String prefix, String suffix) {

    }

    @Override
    public String getChannelName() {
        return "GUILD";
    }
}
