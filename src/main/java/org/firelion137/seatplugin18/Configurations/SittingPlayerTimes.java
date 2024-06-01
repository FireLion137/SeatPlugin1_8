package org.firelion137.seatplugin18.Configurations;

import org.bukkit.configuration.file.YamlConfiguration;
import org.firelion137.seatplugin18.SeatPlugin18;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SittingPlayerTimes {
    private static final SittingPlayerTimes instance= new SittingPlayerTimes();
    private final Map<UUID, Double> playerTimes= new HashMap<>();
    private File file;
    private YamlConfiguration config;

    private SittingPlayerTimes() {}

    public static SittingPlayerTimes getInstance() {
        return instance;
    }

    public void load() {
        file= new File(SeatPlugin18.getInstance().getDataFolder(), "data/sittingTimes.yml");
        config= new YamlConfiguration();

        if(!file.exists()) {
            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String key : config.getKeys(false)) {
            playerTimes.put(UUID.fromString(key), config.getDouble(key + ".sittingTime"));
        }
    }

    public void savePlayerTimes() {
        playerTimes.forEach((uuid, time) -> config.set(uuid.toString() + ".sittingTime", time));

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getTime(UUID uuid) {
        return playerTimes.getOrDefault(uuid, 0D);
    }
    public void setTimeIfAbsent(UUID uuid, double time) {
        playerTimes.putIfAbsent(uuid, time);
    }
    public void updateTime(UUID uuid, double time) {
        playerTimes.put(uuid, playerTimes.getOrDefault(uuid, 0D) + time);
    }

    public String getFormattedTime(UUID uuid, boolean _short) {
        int seconds = (int) getTime(uuid);

        int years = seconds / (365 * 24 * 60 * 60);
        seconds %= 365 * 24 * 60 * 60;
        int months = seconds / (30 * 24 * 60 * 60);
        seconds %= 30 * 24 * 60 * 60;
        int weeks = seconds / (7 * 24 * 60 * 60);
        seconds %= 7 * 24 * 60 * 60;
        int days = seconds / (24 * 60 * 60);
        seconds %= 24 * 60 * 60;
        int hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        int minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        int count = 0;

        if (years > 0) {
            sb.append(years).append(" ").append(_short ? "Y" : (days == 1 ? "anno" : "anni"));
            count++;
        }
        if (months > 0) {
            if (count > 0) sb.append(", ");
            sb.append(months).append(" ").append(_short ? "M" : (days == 1 ? "mese" : "mesi"));
            count++;
        }
        if (weeks > 0 && count < 2) {
            if (count > 0) sb.append(", ");
            sb.append(weeks).append(" ").append(_short ? "w" : (days == 1 ? "settimana" : "settimane"));
            count++;
        }
        if (days > 0 && count < 2) {
            if (count > 0) sb.append(", ");
            sb.append(days).append(" ").append(_short ? "d" : (days == 1 ? "giorno" : "giorni"));
            count++;
        }
        if (hours > 0 && count < 2) {
            if (count > 0) sb.append(", ");
            sb.append(hours).append(" ").append(_short ? "h" : (hours == 1 ? "ora" : "ore"));
            count++;
        }
        if (minutes > 0 && count < 2) {
            if (count > 0) sb.append(", ");
            sb.append(minutes).append(" ").append(_short ? "m" : (minutes == 1 ? "minuto" : "minuti"));
            count++;
        }
        if (seconds > 0 && count < 2) {
            if (count > 0) sb.append(", ");
            sb.append(seconds).append(" ").append(_short ? "s" : (seconds == 1 ? "secondo" : "secondi"));
        }

        return sb.toString();
    }

    public List<UUID> getTopPlayerTimes(int limit) {
        if(limit <= 0)
            return new ArrayList<>();

        return playerTimes.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
