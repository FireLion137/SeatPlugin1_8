package org.firelion137.seatplugin18.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.firelion137.seatplugin18.Configurations.SittingPlayerTimes;
import org.firelion137.seatplugin18.Displays.DisplayManager;
import org.firelion137.seatplugin18.Scoreboards.SittingTimeBoard;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SittingPlayerTask implements Runnable {
    private static final SittingPlayerTask instance= new SittingPlayerTask();
    private final Set<UUID> sittingPlayers= new HashSet<>();
    private final Set<Location> seatLocations = new HashSet<>();
    private double taskSeconds;

    private final SittingPlayerTimes sittingPlayerTimes= SittingPlayerTimes.getInstance();
    private final SittingTimeBoard sittingTimeBoard= SittingTimeBoard.getInstance();

    private SittingPlayerTask() {
        sittingPlayerTimes.load();
    }

    @Override
    public void run() {
        sittingPlayers.forEach(this::countTime);
        if(!sittingPlayers.isEmpty())
            sittingPlayerTimes.savePlayerTimes();

        sittingPlayers.forEach(uuid -> {
            if (sittingTimeBoard.checkScoreboardExists(uuid))
                sittingTimeBoard.updateScoreboard(uuid);
            else
                sittingTimeBoard.createNewScoreboard(uuid);
        });

        DisplayManager.getInstance().updateDisplayByType(DisplayManager.DisplayType.TOP_SITTING);
    }

    private void countTime(UUID uuid) {
        Player player= Bukkit.getPlayer(uuid);
        if(player == null)
            return;

        sittingPlayerTimes.updateTime(uuid, taskSeconds);
    }

    public static SittingPlayerTask getInstance() {
        return instance;
    }
    public void setSecondsPeriod(int sittingTaskPeriod) {
        this.taskSeconds= (double) sittingTaskPeriod /20;
    }

    public boolean hasPlayer(UUID uuid) {
        return sittingPlayers.contains(uuid);
    }
    public void addPlayer(UUID uuid) {
        sittingPlayers.add(uuid);
        sittingPlayerTimes.setTimeIfAbsent(uuid, 0);
    }
    public void removePlayer(UUID uuid) {
        sittingPlayers.remove(uuid);
    }

    public boolean hasSeatLoc(Location location) {
        return seatLocations.contains(location);
    }
    public void addSeatLoc(Location location) {
        seatLocations.add(location);
    }
    public void removeSeatLoc(Location location) {
        seatLocations.remove(location);
    }
}
