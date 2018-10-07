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

import java.util.HashMap;
import java.util.Map;

public enum GuildPermission {
    MASTERKEY("GUILD::MASTERKEY"),
    DELETE("GUILD::DELETE"),
    DEPOSIT("GUILD::DEPOSIT"),
    WITHDRAW("GUILD::WITHDRAW"),
    RANGS("GUILD::RANGS"),
    HOME("GUILD::HOME"),
    CHAT("GUILD::CHAT"),
    INVITE("GUILD::INVITE"),
    KICK("GUILD::KICK"),
    LEAVE("GUILD::LEAVE"),
    SETGUILDMASTER("GUILD::SETGUILDMASTER"),
    SETGUILDNAME("GUILD::SETGUILDNAME"),
    SETHOME("GUILD::SETHOME"),
    ACCEPT("GUILD::ACCEPT"),
    HELP("GUILD::HELP"),
    INFO("GUILD::INFO"),
    LIST("GUILD::LIST"),
    MEMBERS("GUILD::MEMBERS"),
    CREATE("GUILD::CREATE"),
    SETRANG("GUILD::SETRANG");

    private static final Map<String, GuildPermission> lookup = new HashMap<>();

    static {
        for (GuildPermission env : GuildPermission.values()) {
            lookup.put(env.getValue(), env);
        }
    }

    private String perm;

    GuildPermission(String perm) {
        this.perm = perm;
    }

    public static GuildPermission getPerm(String stringPermission) {
        return lookup.get(stringPermission);
    }

    public String getValue() {
        return perm;
    }

}
