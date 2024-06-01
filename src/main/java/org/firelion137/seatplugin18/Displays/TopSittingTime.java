package org.firelion137.seatplugin18.Displays;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.firelion137.seatplugin18.Configurations.SittingPlayerTimes;

import java.util.ArrayList;
import java.util.UUID;

public class TopSittingTime extends CustomDisplayAbstract {
    private static final TopSittingTime instance = new TopSittingTime();
    private final SittingPlayerTimes sittingPlayerTimes= SittingPlayerTimes.getInstance();

    int topLimit= 5;

    private TopSittingTime() {
    }

    public static TopSittingTime getInstance() {
        return instance;
    }

    @Override
    public void spawnDisplay(String name, Location displayLocation) {
        if(getDisplayRows(name) != null && !getDisplayRows(name).isEmpty())
            deleleDisplay(name);

        getDisplayRowsMap().put(name, new ArrayList<>());

        for (int i = 0; i < topLimit + 1; i++) { // One for the title and the rest for the top players
            WorldServer world = ((CraftWorld) displayLocation.getWorld()).getHandle();
            EntityArmorStand armorStandNMS= new EntityArmorStand(world);
            armorStandNMS.setInvisible(true);
            armorStandNMS.setGravity(true);

            ArmorStand armorStand = (ArmorStand) armorStandNMS.getBukkitEntity();
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(" ");

            armorStandNMS.setPositionRotation(displayLocation.getX(), displayLocation.getY() + (-0.3 * i), displayLocation.getZ(), displayLocation.getYaw(), displayLocation.getPitch());
            world.addEntity(armorStandNMS, CreatureSpawnEvent.SpawnReason.CUSTOM);
            getDisplayRows(name).add(armorStand);
        }
        getDisplayRows(name).get(0).setCustomName(ChatColor.GOLD + "" + ChatColor.BOLD + "Tempo sulle sedie");
    }

    public void updateDisplay(String name) {
        if(getDisplayRows(name).isEmpty())
            return;

        int index= 1;
        for (UUID uuid : sittingPlayerTimes.getTopPlayerTimes(topLimit)) {
            getDisplayRows(name).get(index).setCustomName(
                    ChatColor.YELLOW + "" + index + "° " + Bukkit.getOfflinePlayer(uuid).getName() +
                            ChatColor.DARK_GRAY + " ⇨ " + ChatColor.RED + sittingPlayerTimes.getFormattedTime(uuid, false)
            );
            index++;
        }
    }
    public void updateAllDisplays() {
        for (String name : getDisplayRowsMap().keySet()) {
            updateDisplay(name);
        }
    }
}
