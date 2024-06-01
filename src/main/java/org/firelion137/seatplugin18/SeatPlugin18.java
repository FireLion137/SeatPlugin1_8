package org.firelion137.seatplugin18;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.firelion137.seatplugin18.Commands.CustomDisplayCommand;
import org.firelion137.seatplugin18.Displays.DisplayManager;
import org.firelion137.seatplugin18.Listeners.SeatListener;
import org.firelion137.seatplugin18.Tasks.SittingPlayerTask;

public final class SeatPlugin18 extends JavaPlugin {
    private BukkitTask sittingTask;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SeatListener(), this);
        getCommand("customDisplay").setExecutor(new CustomDisplayCommand());

        int sittingTaskPeriod= 20; //seconds in tick for the sittingTask
        sittingTask= getServer().getScheduler().runTaskTimer(this, SittingPlayerTask.getInstance(), 0, sittingTaskPeriod);
        SittingPlayerTask.getInstance().setSecondsPeriod(sittingTaskPeriod);
    }

    @Override
    public void onDisable() {
        if (sittingTask != null && Bukkit.getScheduler().isCurrentlyRunning(sittingTask.getTaskId()))
            sittingTask.cancel();

        DisplayManager.getInstance().removeAllDisplays(false);
    }

    public static SeatPlugin18 getInstance() {
        return getPlugin(SeatPlugin18.class);
    }
}
