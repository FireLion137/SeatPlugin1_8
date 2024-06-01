package org.firelion137.seatplugin18.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.firelion137.seatplugin18.Configurations.SittingPlayerTimes;

import java.util.UUID;

public class SittingTimeBoard {
    private static final SittingTimeBoard instance= new SittingTimeBoard();

    private final SittingPlayerTimes sittingPlayerTimes= SittingPlayerTimes.getInstance();

    private SittingTimeBoard() {
    }

    public static SittingTimeBoard getInstance() {
        return instance;
    }

    public void createNewScoreboard(UUID uuid) {
        Player player= Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        Scoreboard scoreboard= Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective= scoreboard.registerNewObjective("SitPlTask", Criterias.HEALTH);
        objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "   Tempo sulle sedie   ");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore(ChatColor.DARK_RED.toString()).setScore(7);
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Giocatore").setScore(6);
        objective.getScore(ChatColor.DARK_GRAY + "⟩ " + ChatColor.DARK_PURPLE + player.getName()).setScore(5);
        objective.getScore(ChatColor.RED.toString()).setScore(4);
        objective.getScore(ChatColor.WHITE + "" + ChatColor.BOLD + "Tempo").setScore(3);
        objective.getScore(ChatColor.DARK_BLUE.toString()).setScore(1);
        objective.getScore(ChatColor.DARK_GRAY + "Zio Pera").setScore(0);

        Team timeTeam= scoreboard.registerNewTeam("SitPlTime");
        String timeTeamKey= ChatColor.BLUE.toString();

        timeTeam.addEntry(timeTeamKey);
        timeTeam.setPrefix(ChatColor.DARK_GRAY + "⟩ ");
        timeTeam.setSuffix(ChatColor.GOLD + sittingPlayerTimes.getFormattedTime(uuid, true));

        objective.getScore(timeTeamKey).setScore(2);
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(UUID uuid) {
        Player player= Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        Scoreboard scoreboard= player.getScoreboard();
        Team timeTeam= scoreboard.getTeam("SitPlTime");
        if(timeTeam == null)
            return;

        timeTeam.setSuffix(ChatColor.GOLD + sittingPlayerTimes.getFormattedTime(uuid, true));
    }

    public boolean checkScoreboardExists(UUID uuid) {
        Player player= Bukkit.getPlayer(uuid);
        if(player == null)
            return false;

        return player.getScoreboard().getObjective("SitPlTask") != null;
    }
}
