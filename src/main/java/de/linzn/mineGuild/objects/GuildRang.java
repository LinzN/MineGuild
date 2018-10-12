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

import java.util.ArrayList;
import java.util.UUID;

public class GuildRang {
    public UUID rangUUID;
    public String rangName;
    public int priority;
    public ArrayList<GuildPermission> permissions;

    public GuildRang(String rangName, UUID rangUUID, int priority) {
        this.rangName = rangName.toUpperCase();
        this.rangUUID = rangUUID;
        this.priority = priority;
        this.permissions = new ArrayList<>();
    }

    public boolean hasPermission(GuildPermission permission) {
        if (this.permissions.contains(GuildPermission.MASTERKEY)) {
            return true;
        } else {
            return this.permissions.contains(permission);
        }
    }

    public void setPermission(GuildPermission permission) {
        permissions.add(permission);
    }

    public void unsetPermission(GuildPermission permission) {
        permissions.remove(permission);
    }
}
