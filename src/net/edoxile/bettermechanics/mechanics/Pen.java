/*
 * Copyright (c) 2012 Edoxile
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.handlers.PermissionHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.IMechanicCommandListener;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static net.edoxile.bettermechanics.utils.StringUtil.merge;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Pen extends SignMechanicListener implements IMechanicCommandListener {

    ConfigHandler.PenConfig config = BetterMechanics.getInstance().getConfigHandler().getPenConfig();

    public void onPlayerRightClickSign(Player player, Sign sign) {
        if (PermissionHandler.getInstance().playerHasNode(player, "pen")) {
            setLines(player, sign);
        } else {
            player.sendMessage(ChatColor.DARK_RED + "You aren't allowed to use /pen!");
        }
    }

    public void onPlayerLeftClickSign(Player player, Sign sign) {
        if (PermissionHandler.getInstance().hasPermission(player, sign.getBlock(), "pen", PermissionHandler.Checks.ALL)) {
            String[] lines = getLines(player);
            if (lines != null) {
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, lines[i]);
                }
                //Update sign so it's visible for everybody
                sign.update();
            } else {
                player.sendMessage(ChatColor.DARK_RED + "You haven't set the text to put on this sign yet!");
            }
        } else {
            player.sendMessage(ChatColor.DARK_RED + "You aren't allowed to use /pen!");
        }
    }

    @Override
    public String getCommandName() {
        return "pen";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String[] args) {
        if (command.getName().equalsIgnoreCase("pen")) {
            if (isEnabled() && commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (PermissionHandler.getInstance().playerHasNode(player, "pen")) {
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.DARK_RED + "Incorrect usage. Usage: /pen [set|clear|setline|help]");
                    } else {
                        if (args[0].equalsIgnoreCase("set")) {
                            if (args.length < 2) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                setLines(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("clear")) {
                            clear(player);
                            player.sendMessage(ChatColor.GOLD + "Pen data cleared.");
                        } else if (args[0].equalsIgnoreCase("dump")) {
                            dump(player);
                        } else if (args[0].equalsIgnoreCase("setline")) {
                            if (args.length < 3) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                setLine(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("setline")) {
                            if (args.length < 2) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                clearLine(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("help")) {
                            player.sendMessage("Pen help. The char '^' is a linebreak. Commands:");
                            player.sendMessage("/pen set [text] | set the sign text");
                            player.sendMessage("/pen setline [line] [text] | set one line of the text");
                            player.sendMessage("/pen clearline [line] | clears the specified line");
                            player.sendMessage("/pen clear | clears the current text");
                            player.sendMessage("/pen dump | dumps the current text");
                        } else {
                            player.sendMessage(ChatColor.DARK_RED + "Incorrect usage. Usage: /pen <set|clear>|setline|help>");
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "You aren't allowed to use /pen!");
                }
            } else {
                commandSender.sendMessage("Pen disabled or using from commandline!");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getIdentifiers() {
        return voidTarget;
    }

    @Override
    public String[] getPassiveIdentifiers() {
        return null;
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return false;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public boolean hasBlockMapper() {
        return false;
    }

    @Override
    public boolean hasBlockBag() {
        return false;
    }

    @Override
    public Material[] getMechanicActivators() {
        return new Material[]{config.getPenTool()};
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String getName() {
        return "Pen";
    }

    private final HashMap<Player, String[]> dataMap = new HashMap<Player, String[]>();

    private String[] getPlayerData(Player player) {
        String[] data = dataMap.get(player);
        if (data == null) {
            player.sendMessage(ChatColor.YELLOW + "You didn't have a message set. Using an empty sign.");
            return new String[]{"", "", "", ""};
        } else {
            return data;
        }
    }

    private int getLineIndex(String arg) {
        try {
            int number = Integer.parseInt(arg);
            return ((number < 0 || number > 3) ? -1 : number - 1);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void setLine(Player player, String[] args) {
        String[] data = getPlayerData(player);
        int line = getLineIndex(args[1]);
        if (line > -1) {
            String text = merge(args, 2);
            data[line] = text;
            dataMap.put(player, data);
            dump(player);
        } else {
            player.sendMessage(ChatColor.RED + "Invalid line number. Line numbers start at 1 and end at 4.");
        }
    }

    private void clear(Player player) {
        dataMap.put(player, null);
    }

    private void clearLine(Player player, String[] args) {
        String[] data = getPlayerData(player);
        int line = getLineIndex(args[1]);
        if (line > -1) {
            data[line] = "";
            dataMap.put(player, data);
            dump(player);
        } else {
            player.sendMessage(ChatColor.RED + "Invalid line number. Line numbers start at 1 and end at 4.");
        }
    }

    private void setLines(Player player, String[] args) {
        String[] data = parseLines(args);
        if (data != null) {
            dataMap.put(player, data);
            dump(player);
        } else {
            player.sendMessage(ChatColor.RED + "To many lines or lines to long ( < 15 characters ).");
        }
    }

    private void setLines(Player player, Sign otherSign) {
        dataMap.put(player, otherSign.getLines());
        dump(player);
    }

    private String[] getLines(Player player) {
        return dataMap.get(player);
    }

    private String[] parseLines(String[] data) {

        data = merge(data, 1).split("\\^");
        if (data.length > 4) {
            return null;
        }

        String[] lines = new String[]{"", "", "", ""};

        for (int i = 0; i < data.length; i++) {
            if (data[i].length() > 15)
                return null;
            lines[i] = data[i];
        }

        return lines;
    }

    private void dump(Player player) {
        if (dataMap.get(player) == null) {
            player.sendMessage(ChatColor.GOLD + "Your pen is empty.");
        } else {
            String[] lines = dataMap.get(player);
            player.sendMessage(ChatColor.GOLD + "Your pen contains:");
            for (String s : lines) {
                player.sendMessage(ChatColor.GOLD + "[" + s + "]");
            }
        }
    }
}