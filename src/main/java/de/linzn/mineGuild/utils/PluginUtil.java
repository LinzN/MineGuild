package de.linzn.mineGuild.utils;

public class PluginUtil {
    public static double get_mcmmo_multiplikator(int guildLevel) {
        double value;
        int maxLevel = 30;
        int maxShare = 60;
        if (guildLevel < maxLevel) {
            value = ((50D / (double) maxLevel) * (double) guildLevel);
        } else {
            value = ((maxShare / (double) maxLevel) * (double) guildLevel);
        }
        value = (value / 100D);
        return round(value, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static boolean check_guild_name(String guildName){
        if (!Character.isLetter(guildName.charAt(0))){
            return false;
        }
        if (guildName.length() < 3 || guildName.length() >= 20){
            return false;
        }
        for (char c : guildName.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
