package org.firelion137.seatplugin18.Listeners;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.material.Stairs;
import org.firelion137.seatplugin18.Scoreboards.SittingTimeBoard;
import org.firelion137.seatplugin18.SeatPlugin18;
import org.firelion137.seatplugin18.Tasks.SittingPlayerTask;
import org.spigotmc.event.entity.EntityDismountEvent;

public class SeatListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && clickedBlock != null
                && clickedBlock.getType().toString().endsWith("_STAIRS")
                && !((Stairs) clickedBlock.getState().getData()).isInverted()) {
            event.setCancelled(true);

            Location loc = clickedBlock.getLocation().add(0.5, -1.2, 0.5);
            BlockFace blockFace = ((Stairs) clickedBlock.getState().getData()).getFacing();
            switch (blockFace) {
                case NORTH:
                    loc.setYaw(180);
                    break;
                case EAST:
                    loc.setYaw(270);
                    break;
                case SOUTH:
                    loc.setYaw(0);
                    break;
                case WEST:
                    loc.setYaw(90);
                    break;
            }

            if (SittingPlayerTask.getInstance().hasSeatLoc(loc))
                return;

            if(SittingPlayerTask.getInstance().hasPlayer(player.getUniqueId())) {
                Entity currentSeat= player.getVehicle();
                if(currentSeat instanceof ArmorStand) {
                    EntityArmorStand currentSeatNMS= ((CraftArmorStand) currentSeat).getHandle();
                    SittingPlayerTask.getInstance().removeSeatLoc(currentSeat.getLocation());
                    SittingPlayerTask.getInstance().addSeatLoc(loc);
                    currentSeatNMS.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                }
                return;
            }

            WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
            EntityArmorStand armorStand= new EntityArmorStand(world);
            armorStand.setInvisible(true);
            armorStand.setGravity(true);

            ArmorStand seat = (ArmorStand) armorStand.getBukkitEntity();
            seat.setCustomName("seat");

            armorStand.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            world.addEntity(armorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

            seat.setPassenger(player);
            SittingPlayerTask.getInstance().addPlayer(player.getUniqueId());
            SittingPlayerTask.getInstance().addSeatLoc(loc);
            SittingTimeBoard.getInstance().createNewScoreboard(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Entity entity = event.getDismounted();
        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) entity;

            EntityArmorStand armorStandNMS= ((CraftArmorStand) entity).getHandle();
            Location loc = armorStand.getLocation();
            armorStandNMS.setPosition(loc.getX(), loc.getY()+0.5, loc.getZ());
            // Delay removal by 1 tick to ensure event processing is complete
            Bukkit.getScheduler().runTask(SeatPlugin18.getInstance(), () -> {
                if (armorStand.getPassenger() == null) {
                    armorStand.remove();
                    SittingPlayerTask.getInstance().removePlayer(player.getUniqueId());
                    SittingPlayerTask.getInstance().removeSeatLoc(loc);
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            });
        }
    }

    @EventHandler
    public void onPlayerConnection(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(SeatPlugin18.getInstance(), () -> {
            Entity currentSeat= player.getVehicle();
            if(currentSeat instanceof ArmorStand && currentSeat.getName().equals("seat")) {
                if (SittingPlayerTask.getInstance().hasSeatLoc(currentSeat.getLocation())) {
                    currentSeat.eject();
                    currentSeat.remove();
                    return;
                }

                SittingPlayerTask.getInstance().addPlayer(player.getUniqueId());
                SittingPlayerTask.getInstance().addSeatLoc(currentSeat.getLocation());
                SittingTimeBoard.getInstance().createNewScoreboard(player.getUniqueId());
            }
        }, 1);
    }
}
