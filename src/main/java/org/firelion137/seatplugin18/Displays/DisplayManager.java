package org.firelion137.seatplugin18.Displays;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.firelion137.seatplugin18.SeatPlugin18;

import java.io.File;
import java.util.*;

public class DisplayManager {
    private static final DisplayManager instance = new DisplayManager();

    public enum DisplayType {
        TOP_SITTING
    }

    public static class DisplayInfo {
        DisplayType type;
        Location location;

        public DisplayInfo(DisplayType displayType, Location location) {
            this.type = displayType;
            this.location = location;
        }
    }

    Map<String, DisplayInfo> displays = new HashMap<>();

    private File file;
    private YamlConfiguration config;

    private DisplayManager() {
        this.loadFile();
    }

    public static DisplayManager getInstance() {
        return instance;
    }

    public boolean addDisplay(String name, DisplayType displayType, Location location, boolean saveToFile) {
        if (saveToFile && displays.containsKey(name))
            return false;

        switch (displayType) {
            case TOP_SITTING:
                TopSittingTime.getInstance().spawnDisplay(name, location);
                if(saveToFile) {
                    displays.put(name, new DisplayInfo(displayType, location));
                    this.saveDisplaysToFile();
                }
                return true;
        }

        return false;
    }
    public void spawnAllDisplays() {
        displays.forEach((name, displayInfo) -> addDisplay(name, displayInfo.type, displayInfo.location, false));
    }

    public List<String> getDisplayNames() {
        return new ArrayList<>(displays.keySet());
    }
    public DisplayInfo getDisplay(String name) {
        return displays.get(name);
    }

    public boolean removeDisplay(String name, boolean deleteFromFile) {
        if (deleteFromFile && !displays.containsKey(name))
            return false;

        DisplayInfo displayInfo = getDisplay(name);
        switch (displayInfo.type) {
            case TOP_SITTING:
                TopSittingTime.getInstance().deleleDisplay(name);
                if(deleteFromFile) {
                    displays.remove(name);
                    this.deleteDisplayFromFile(name);
                }
                return true;
        }

        return false;
    }
    public void removeAllDisplays(boolean deleteFromFile) {
        displays.keySet().forEach(name -> removeDisplay(name, deleteFromFile));
    }

    public void updateDisplayByType(DisplayType displayType) {
        switch (displayType) {
            case TOP_SITTING:
                TopSittingTime.getInstance().updateAllDisplays();
        }
    }

    public boolean hasDisplay(String name) {
        return displays.containsKey(name);
    }

    public void loadFile() {
        file= new File(SeatPlugin18.getInstance().getDataFolder(), "data/customDisplays.yml");
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

        for (String name : config.getKeys(false)) {
            DisplayType displayType = DisplayType.valueOf(config.getString(name + ".displayType"));
            Map<String, Object> locationMap = Objects.requireNonNull(config.getConfigurationSection(name + ".location")).getValues(false);
            Location location = Location.deserialize(locationMap);

            displays.put(name, new DisplayInfo(displayType, location));
        }

        this.spawnAllDisplays();
    }

    public void saveDisplaysToFile() {
        displays.forEach((name, displayInfo) -> {
            config.set(name + ".displayType", displayInfo.type.toString());
            config.set(name + ".location", displayInfo.location.serialize());
        });

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDisplayFromFile(String name) {
        config.set(name, null);

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
