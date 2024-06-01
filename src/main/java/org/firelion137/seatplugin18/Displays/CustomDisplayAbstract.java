package org.firelion137.seatplugin18.Displays;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomDisplayAbstract {
    private final Map<String, List<ArmorStand>> displayRowsMap = new HashMap<>();

    public abstract void spawnDisplay(String name, Location displayLocation);

    public void deleleDisplay(String name) {
        for (ArmorStand armorStand : displayRowsMap.get(name))
            armorStand.remove();

        this.displayRowsMap.remove(name);
    }

    public Map<String, List<ArmorStand>> getDisplayRowsMap() {
        return displayRowsMap;
    }
    public List<ArmorStand> getDisplayRows(String name) {
        return displayRowsMap.get(name);
    }
}
