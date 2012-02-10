/*
 * Copyright (c) 2012.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.mechanics.interfaces.ICommandableMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.ISignMechanic;
import net.edoxile.bettermechanics.models.MechanicsConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Pen extends ISignMechanic implements ICommandableMechanic {

    private boolean enabled;

    private Material tool;

    private MechanicsConfigHandler.PenConfig config;

    public Pen() {
        reloadConfig();
    }

    public void reloadConfig() {
        config = MechanicsConfigHandler.getInstance().getPenConfig();
        enabled = config.isEnabled();
        tool = config.getPenTool();
    }

    public void onPlayerRightClickSign(Player player, Sign sign) {
        //TODO: check permissions
        setText(player, sign.getLines());
    }

    public void onPlayerLeftClickSign(Player player, Sign sign) {
        //TODO: check permissions
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
    }

    public boolean onCommand(CommandSender commandSender, Command command, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            //TODO: check if player has permission to use pen.
            if (enabled) {
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
                return true;
            }
        } else {
            commandSender.sendMessage("Consoles aren't allowed to use /pen!");
        }
        return true;
    }

    public String[] getIdentifier() {
        return null;
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return false;
    }

    public Material[] getMechanicActivator() {
        return new Material[]{tool};
    }

    public String getName() {
        return "Pen";
    }

    private HashMap<Player, String[]> dataMap = new HashMap<Player, String[]>();

    private void setLine(Player player, String[] args) {
        try {
            String[] data = dataMap.get(player);
            if (data == null) {
                player.sendMessage(ChatColor.YELLOW + "You didn't have a message set. Using an empty sign.");
                data = new String[4];
                data[0] = "";
                data[1] = "";
                data[2] = "";
                data[3] = "";
            }
            int line = Integer.parseInt(args[1]);
            String text = mergeString(args, " ", 2);
            data[line] = text;
            dataMap.put(player, data);
            player.sendMessage(ChatColor.GOLD + "New pen text:");
            for (String s : data) {
                player.sendMessage(ChatColor.GOLD + "[" + s + "]");
            }
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Invalid number format for line number.");
        } catch (IndexOutOfBoundsException ex) {
            player.sendMessage(ChatColor.RED + "Invalid line number. Line numbers start at 0 and end at 3.");
        }
    }

    private void clear(Player player) {
        dataMap.put(player, null);
    }

    private void clearLine(Player player, String[] args) {
        try {
            int line = Integer.parseInt(args[1]);
            String[] data = dataMap.get(player);
            data[line] = "";
            dataMap.put(player, data);
            player.sendMessage(ChatColor.GOLD + "New pen text:");
            for (String s : data) {
                player.sendMessage(ChatColor.GOLD + "[" + s + "]");
            }
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Invalid number format for line number.");
        } catch (IndexOutOfBoundsException ex) {
            player.sendMessage(ChatColor.RED + "Invalid line number. Line numbers start at 0 and end at 3.");
        }
    }

    private void setLines(Player player, String[] args) {
        try {
            String[] data = parseText(args);
            dataMap.put(player, data);
            player.sendMessage(ChatColor.GOLD + "New pen text:");
            for (String s : data) {
                player.sendMessage(ChatColor.GOLD + "[" + s + "]");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            player.sendMessage(ChatColor.DARK_RED + "Your text contains more than 4 lines.");
        } catch (StringIndexOutOfBoundsException e) {
            player.sendMessage(ChatColor.DARK_RED + "At least one of your lines has more than 15 chars.");
        }
    }

    private void setText(Player player, String[] args) {
        dataMap.put(player, args);
        player.sendMessage(ChatColor.GOLD + "New pen text:");
        for (String s : args) {
            player.sendMessage(ChatColor.GOLD + "[" + s + "]");
        }
    }

    public String[] getLines(Player player) {
        return dataMap.get(player);
    }

    private String[] parseText(String[] data) throws ArrayIndexOutOfBoundsException, StringIndexOutOfBoundsException {
        data = mergeString(data, " ", 1).split("\\^");
        if (data.length > 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        String[] lines = {"", "", "", ""};
        for (int i = 0; i < data.length; i++) {
            if (data[i].length() > 15)
                throw new StringIndexOutOfBoundsException();
            lines[i] = data[i];
        }
        return lines;
    }

    private String mergeString(String[] data, String glue, int offset) {
        String str = "";
        for (int i = offset; i < data.length; i++) {
            str += data[i];
            if ((i + 1) != data.length)
                str += glue;
        }
        return str;
    }

    private void dump(Player player) {
        if (dataMap.get(player) == null) {
            player.sendMessage(ChatColor.GOLD + "Your pen is empty.");
        } else {
            String[] lines = dataMap.get(player);
            player.sendMessage(ChatColor.GOLD + "Pen dump:");
            for (String s : lines) {
                player.sendMessage(ChatColor.GOLD + "[" + s + "]");
            }
        }
    }
}
