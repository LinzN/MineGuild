package de.linzn.mineGuild.objects;

import java.util.ArrayList;

public class GuildRang {
    public int rangId;
    public String rangName;
    public ArrayList<String> permissions;

    public GuildRang(String rangName) {
        this.rangName = rangName;
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
