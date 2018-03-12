package de.linzn.mineGuild.utils;

import net.md_5.bungee.api.ChatColor;

public class LanguageDB {
    /* Error */
    public static String you_not_in_guild = ChatColor.DARK_RED + "Du bist in keiner Gilde!";
    public static String you_already_in_guild = ChatColor.DARK_RED + "Du bist bereits in einer Gilde!";
    public static String you_no_guild_perm = ChatColor.DARK_RED + "Du hast für diese Aktion keine Berechtigung!";
    public static String player_not_online = ChatColor.DARK_RED + "Dieser Spieler ist nicht online!";
    public static String player_action_not_possible = ChatColor.DARK_RED + "Dies ist mit diesem Spieler nicht möglich!";
    public static String you_no_open_invitations = ChatColor.DARK_RED + "Du hast keine offenen Einladungen!";
    public static String player_already_in_guild = ChatColor.DARK_RED + "Dieser Spieler ist bereits in einer Gilde!";
    public static String player_has_already_invitation = ChatColor.DARK_RED + "Dieser Spieler hat noch eine Einladung offen!";
    public static String guild_already_exist = ChatColor.DARK_RED + "Diese Gilde gibt es bereits!";
    public static String guild_not_exist = ChatColor.DARK_RED + "Diese Gilde gibt es nicht!";
    public static String guild_no_home = ChatColor.DARK_RED + "Die Gilde hat keinen Homepunkt gesetzt!";
    public static String you_can_not_leave_guild = ChatColor.DARK_RED + "Du kannst die Gilde so nicht verlassen. Bist du Gildenmeister?";
    public static String database_error = "" + ChatColor.DARK_RED + ChatColor.BOLD + "Es ist ein Datenbankfehler aufgetreten. Bitte an ein Teammitglied wenden!";

    /* Info */
    public static String guild_invitation_expired = ChatColor.YELLOW + "Die Einladung ist abgelaufen!";
    public static String your_guild_invitation_expired = ChatColor.YELLOW + "Deine Einladung ist abgelaufen ohne aktivität!";
    public static String you_get_guild_invitation = ChatColor.YELLOW + "{actor} " + ChatColor.GREEN + " möchte dich in die Gilde " + ChatColor.YELLOW + "{guild} " + ChatColor.GREEN + "aufnehmen.\n" + ChatColor.GREEN + "Gib " + ChatColor.YELLOW + "/guild accept " + ChatColor.GREEN + "um anzunehmen oder " + ChatColor.YELLOW + "/guild deny " + ChatColor.GREEN + "um abzulehnen ein.";
    public static String you_kicked_from_guild = ChatColor.YELLOW + "Du wurdest von {actor} aus der Gilde entfernt!";
    public static String no_entries_on_page = ChatColor.RED + "Es wurden hier keine Einträge gefunden!";

    /* Guild broadcast */
    public static String guild_get_guild_invitation = ChatColor.GREEN + "Es wurde " + ChatColor.YELLOW + "{player} " + ChatColor.GREEN + "in die Gilde eingeladen sich dieser anzuschliesen!";
    public static String guild_create_guild = ChatColor.GREEN + "Spieler " + ChatColor.YELLOW + "{player} " + ChatColor.GREEN + " hat die Gilde " + ChatColor.YELLOW + "{guild} " + ChatColor.GREEN + " erstellt!";
    public static String guild_remove_guild = ChatColor.GOLD + "Spieler " + ChatColor.YELLOW + "{player} " + ChatColor.GOLD + "hat die Gilde " + ChatColor.YELLOW + "{guild} " + ChatColor.GOLD + "aufgelöst!";
    public static String guild_remove_guild_members = ChatColor.RED + "Eure Gilde wurde aufgelöst!";
    public static String guild_new_member = ChatColor.GREEN + "Spieler " + ChatColor.YELLOW + "{player} " + ChatColor.GREEN + "hat sich der Gilde angeschlossen. Willkommen!";
    public static String guild_kicked_player = ChatColor.RED + "Spieler {player} wurde aus der Gilde von {actor} entfernt!";
    public static String guild_player_leave = ChatColor.YELLOW + "Spieler {player} hat die Gilde gerade verlassen!";

    /* Success */
    public static String you_create_guild = ChatColor.DARK_GREEN + "Glückwunsch. Du hast eine neue Gilde namens " + ChatColor.YELLOW + "{guild} " + ChatColor.DARK_GREEN + "erstellt!";
    public static String you_remove_guild = ChatColor.GOLD + "Du hast die Gilde " + ChatColor.YELLOW + "{guild} " + ChatColor.GOLD + "aufgelöst!";
    public static String you_accept_invitation = ChatColor.DARK_GREEN + "Die Einladung wurde angenommen!";
    public static String you_deny_invitation = ChatColor.GOLD + "Du hast die Einladungen abgelehnt!";
    public static String you_guild_leave = ChatColor.GOLD + "Du hast die Gilde verlassen!";

    /* Interface Info*/
    public static String interface_guildinfo_header = ChatColor.GOLD + "-===================[" + ChatColor.DARK_GREEN + "Gilden Info" + ChatColor.GOLD + "]===================-";
    public static String interface_guildinfo_name = ChatColor.GREEN + "Name: " + ChatColor.YELLOW + "{guild}";
    public static String interface_guildinfo_membersize = ChatColor.GREEN + "Mitgliederstärke: " + ChatColor.YELLOW + "{guild_size}";
    public static String interface_guildinfo_guildlevel = ChatColor.GREEN + "Gildenlevel: " + ChatColor.YELLOW + "{guild_level}";
    public static String interface_guildinfo_guildexperience = ChatColor.GREEN + "Erfahrung: " + ChatColor.YELLOW + "{exp}/{totalExp}";

    public static String interface_guildmembers_header = ChatColor.GOLD + "-===============[" + ChatColor.DARK_GREEN + "Gilden Mitglieder" + ChatColor.GOLD + "]===============-";
    public static String interface_guildmembers_entry = ChatColor.GREEN + "Spieler: " + ChatColor.YELLOW + "{player} " + ChatColor.GREEN + " Rang: " + ChatColor.YELLOW + "{rang}";

    public static String interface_guildlist_header = ChatColor.GOLD + "-====================[" + ChatColor.DARK_GREEN + "Gilden" + ChatColor.GOLD + "]====================-";
    public static String interface_guildlist_entry = ChatColor.GREEN + "Gilde: " + ChatColor.YELLOW + "{guildName} " + ChatColor.GREEN + " Mitglieder: " + ChatColor.YELLOW + "{memberSize} " + ChatColor.GREEN + " Level: " + ChatColor.YELLOW + "{level}";
}
