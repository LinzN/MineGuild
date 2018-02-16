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

public class GuildRang {
    public int rangId;
    public String rangName;
    public ArrayList<String> permissions;

    public GuildRang(String rangName) {
        this.rangName = rangName.toUpperCase();
        this.permissions = new ArrayList<>();
    }

    public void setRangId(int rangId) {
        this.rangId = rangId;
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission.toUpperCase());
    }

    public void setPermission(String permission) {
        permissions.add(permission.toUpperCase());
    }

    public void unsetPermission(String permission) {
        permissions.remove(permission.toUpperCase());
    }
}
