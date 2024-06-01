package org.firelion137.seatplugin18.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.firelion137.seatplugin18.Displays.DisplayManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomDisplayCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only Players Allowed!");
            return true;
        }

        Player p= (Player) commandSender;

        switch(strings.length) {
            case 0:
                commandSender.sendMessage("Devi specificare l'azione! (spawn | delete)");
                return true;
            case 1:
                if (strings[0].equalsIgnoreCase("spawn")) {
                    commandSender.sendMessage("Devi specificare il DISPLAY_TYPE!");
                    return true;
                }
                else if (strings[0].equalsIgnoreCase("delete")) {
                    commandSender.sendMessage("Devi specificare il nome del display da cancellare!");
                    return true;
                }
                else
                    return false;
            case 2:
                if (strings[0].equalsIgnoreCase("spawn")) {
                    try {
                        DisplayManager.DisplayType.valueOf(strings[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        commandSender.sendMessage("Invalid Display Type: " + strings[1]);
                        return true;
                    }

                    commandSender.sendMessage("Devi specificare il nome del display da creare!");
                    return true;
                }
                else if (strings[0].equalsIgnoreCase("delete")) {
                    if (!DisplayManager.getInstance().getDisplayNames().contains(strings[1])) {
                        commandSender.sendMessage("Invalid Display Name: " + strings[1]);
                        return true;
                    }

                    boolean removed= DisplayManager.getInstance().removeDisplay(strings[1], true);

                    if(removed)
                        commandSender.sendMessage("Display '" + strings[1] + "' rimosso correttamente!");
                    else
                        commandSender.sendMessage("Errore nel remove del display!");
                    return true;
                }
                else
                    return false;
            case 3:
                if(strings[0].equalsIgnoreCase("spawn")) {
                    try {
                        DisplayManager.DisplayType.valueOf(strings[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        commandSender.sendMessage("Invalid Display Type: " + strings[1]);
                        return true;
                    }

                    if(DisplayManager.getInstance().hasDisplay(strings[2])) {
                        commandSender.sendMessage(ChatColor.RED + "Display '" + strings[2] + "' già esistente!");
                        return true;
                    }

                    boolean spawned= DisplayManager.getInstance().addDisplay(
                            strings[2],
                            DisplayManager.DisplayType.valueOf(strings[1].toUpperCase()),
                            p.getLocation(),
                            true
                    );

                    if(spawned)
                        commandSender.sendMessage("Display '" + strings[2] + "' creato correttamente!");
                    else
                        commandSender.sendMessage("Errore nello spawn del display!");
                    return true;
                }
                else
                    return false;
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1)
            return Arrays.asList("spawn", "delete");

        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("spawn")) {
                return Arrays.stream(DisplayManager.DisplayType.values())
                        .map(DisplayManager.DisplayType::name)
                        .collect(Collectors.toList());
            }
            else if(strings[0].equalsIgnoreCase("delete")) {
                return DisplayManager.getInstance().getDisplayNames();
            }
        }

        return new ArrayList<>();
    }
}
