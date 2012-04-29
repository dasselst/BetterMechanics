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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.bettermechanics;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import net.edoxile.bettermechanics.exceptions.ConfigWriteException;
import net.edoxile.bettermechanics.listeners.MechanicsBlockListener;
import net.edoxile.bettermechanics.listeners.MechanicsPlayerListener;
import net.edoxile.bettermechanics.mechanics.Pen;
import net.edoxile.bettermechanics.utils.BlockBagManager;
import net.edoxile.bettermechanics.utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BetterMechanics extends JavaPlugin {
    public static final Logger log = Logger.getLogger("Minecraft");
    private MechanicsPlayerListener playerListener;
    private MechanicsBlockListener blockListener;
    private TweakcraftUtils tcutils = null;
    private MechanicsConfig configManager;
    private BlockBagManager blockBagManager;
    private File configFile;

    @Override
    public void onDisable() {
        log.info("[BetterMechanics] disabled.");
    }

    @Override
    public void onEnable() {
        try {
            configFile = this.getFile();
            configManager = new MechanicsConfig(this);
            blockBagManager = new BlockBagManager(configManager);
            blockListener = new MechanicsBlockListener(configManager, blockBagManager);
            playerListener = new MechanicsPlayerListener(configManager, blockBagManager);

            registerEvents();
            if (configManager.useTweakcraftUtils) {
                log.info("[BetterMechanics] Using TweakcraftUtils!");
                setupTweakcraftUtils();
            }
            log.info("[BetterMechanics] Loading completed.");
        } catch (ConfigWriteException ex) {
            log.severe("[BetterMechanics] Couldn't create config file.");
            this.setEnabled(false);
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public String getPluginName() {
        return getDescription().getName();
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        /* pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this); */
        pm.registerEvents(blockListener, this);
        pm.registerEvents(playerListener, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pen")) {
            if (configManager.getPenConfig().enabled) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.DARK_RED + "Incorrect usage. Usage: /pen [set|clear|setline|help]");
                    } else {
                        if (args[0].equalsIgnoreCase("set")) {
                            if (args.length < 2) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                Pen.setLines(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("clear")) {
                            Pen.clear(player);
                            player.sendMessage(ChatColor.GOLD + "Pen data cleared.");
                        } else if (args[0].equalsIgnoreCase("dump")) {
                            Pen.dump(player);
                        } else if (args[0].equalsIgnoreCase("setline")) {
                            if (args.length < 3) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                Pen.setLine(player, args);
                            }
                        } else if (args[0].equalsIgnoreCase("setline")) {
                            if (args.length < 2) {
                                player.sendMessage(ChatColor.DARK_RED + "Too few arguments.");
                            } else {
                                Pen.clearLine(player, args);
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
                } else {
                    sender.sendMessage(ChatColor.RED + "Consoles aren't allowed in this pen party club!");
                }
            }
            sender.sendMessage(ChatColor.DARK_RED + "The pen is not enabled.");
            return true;
        } else if (command.getName().equalsIgnoreCase("bettermechanics")) {
            if (sender instanceof Player && !configManager.permissionConfig.checkPermissions((Player) sender, "bettermechanics.reloadconfig"))
                return true;
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "I need to know what to do!");
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (args.length == 1) {
                    try {
                        configManager = new MechanicsConfig(this);
                        playerListener.setConfig(configManager);
                        blockListener.setConfig(configManager);
                        if (sender instanceof Player)
                            sender.sendMessage(ChatColor.GOLD + "Reloaded config.");
                        log.info("[BetterMechanics] Reloaded config.");
                    } catch (ConfigWriteException e) {
                        log.severe("[BetterMechanics] Couldn't create config file.");
                        this.setEnabled(false);
                    }
                } else if (args[1].equalsIgnoreCase("cauldron")) {
                    configManager.reloadCauldronConfig();
                    playerListener.setConfig(configManager);
                    blockListener.setConfig(configManager);
                    if (sender instanceof Player)
                        sender.sendMessage(ChatColor.GOLD + "Reloaded cauldron recipes.");
                    log.info("[BetterMechanics] Reloaded cauldron recipes.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong usage. Usage: /bm reload <cauldron>");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Wrong usage. Usage: /bm reload <cauldron>");
            }
            return true;
        } else {
            return false;
        }
    }

    public void setupTweakcraftUtils() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("TweakcraftUtils");
        if (tcutils == null)
            if (plugin != null)
                tcutils = (TweakcraftUtils) plugin;
    }

    public TweakcraftUtils getTweakcraftUtils() {
        return tcutils;
    }
}
