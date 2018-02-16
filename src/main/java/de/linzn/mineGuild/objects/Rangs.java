package de.linzn.mineGuild.objects;

import java.util.HashMap;
import java.util.HashSet;

public enum Rangs {
    MASTER, ASSISTANT, MEMBER, DEFAULT;

    private static HashMap<Rangs, HashSet<GuildAction>> permValues = new HashMap<Rangs, HashSet<GuildAction>>();

    public static boolean hasGuildPerm(GuildPlayer player, GuildAction aktion) {
        Rangs rang = player.getGuildRang();
        boolean hasPerm = false;
        HashSet<GuildAction> permList;
        switch (rang) {
            case MASTER:
                permList = permValues.get(MASTER);
                if (permList.contains(aktion)) {
                    hasPerm = true;
                }
                break;
            case ASSISTANT:
                permList = permValues.get(ASSISTANT);
                if (permList.contains(aktion)) {
                    hasPerm = true;
                }
                break;
            case MEMBER:
                permList = permValues.get(MEMBER);
                if (permList.contains(aktion)) {
                    hasPerm = true;
                }
                break;
            case DEFAULT:
                permList = permValues.get(DEFAULT);
                if (permList.contains(aktion)) {
                    hasPerm = true;
                }
                break;
            default:
                hasPerm = false;
                break;
        }
        return hasPerm;

    }

    public static void setupPerms() {
        permValues.put(DEFAULT, getPerms(DEFAULT));
        permValues.put(MEMBER, getPerms(MEMBER));
        permValues.put(ASSISTANT, getPerms(ASSISTANT));
        permValues.put(MASTER, getPerms(MASTER));
    }

    private static HashSet<GuildAction> getPerms(Rangs rang) {
        HashSet<GuildAction> perms;
        switch (rang) {
            case MASTER:
                // Master (All Permissions)
                perms = new HashSet<>();
                for (GuildAction gEnum : GuildAction.values()) {
                    perms.add(gEnum);
                }
                break;
            case ASSISTANT:
                // Assistant
                perms = new HashSet<GuildAction>();
                perms.add(GuildAction.KICK);
                perms.add(GuildAction.SETGUILDNAME);
                perms.add(GuildAction.SETHOME);
                perms.add(GuildAction.INVITE);
                perms.add(GuildAction.LEAVE);
                perms.add(GuildAction.HOME);
                perms.add(GuildAction.WITHDRAW);
                for (GuildAction ac : getPerms(MEMBER)) {
                    perms.add(ac);
                }
                break;
            case MEMBER:
                // Member
                perms = new HashSet<GuildAction>();
                perms.add(GuildAction.LEAVE);
                perms.add(GuildAction.HOME);
                perms.add(GuildAction.DEPOSIT);
                for (GuildAction ac : getPerms(DEFAULT)) {
                    perms.add(ac);
                }
                break;
            case DEFAULT:
                // Member
                perms = new HashSet<GuildAction>();
                perms.add(GuildAction.ACCEPT);
                perms.add(GuildAction.CREATE);
                perms.add(GuildAction.LIST);
                perms.add(GuildAction.INFO);
                perms.add(GuildAction.MEMBERS);
                perms.add(GuildAction.HELP);
                perms.add(GuildAction.RANGS);
                break;
            default:
                perms = new HashSet<GuildAction>();
                break;
        }
        return perms;
    }

    public static boolean isValidEnumValue(String value) {
        boolean isValid = false;
        for (Rangs rang : Rangs.values()) {
            if (rang.toString().equalsIgnoreCase(value)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

}
