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

package net.edoxile.bettermechanics;

import net.edoxile.bettermechanics.handlers.MechanicsHandler;
import net.edoxile.bettermechanics.handlers.PermissionHandler;
import net.edoxile.bettermechanics.listeners.BMBlockListener;
import net.edoxile.bettermechanics.listeners.BMPlayerListener;
import net.edoxile.bettermechanics.mechanics.Bridge;
import net.edoxile.bettermechanics.mechanics.Pen;
import net.edoxile.bettermechanics.models.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class BetterMechanics extends JavaPlugin {
    private static BetterMechanics instance = null;
    private static PermissionHandler permissionHandler = null;
    private static Logger logger = Logger.getLogger("Minecraft");
    private static boolean debugMode;
    private MechanicsHandler mechanicsHandler = new MechanicsHandler();
    private BMPlayerListener playerListener = new BMPlayerListener(this);
    private BMBlockListener blockListener = new BMBlockListener(this);

    public void onEnable() {
        instance = this;
        permissionHandler = new PermissionHandler();
        //Register different Mechanics
        mechanicsHandler.addMechanic(new Pen());
        mechanicsHandler.addMechanic(new Bridge());

        //Register different events
        getServer().getPluginManager().registerEvents(blockListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        //TODO: fix this (deprecation etc.)
        loadConfig();

        debugMode = getPluginConfig().getBoolean("debug-mode", false);
        log("Enabled! Version: " + getDescription().getVersion() + ".");
    }

    public void onDisable() {
        instance = null;
        log("Disabled.");
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return mechanicsHandler.callCommandEvent(command, commandSender, args);
    }

    public MechanicsHandler getMechanicsHandler() {
        return mechanicsHandler;
    }

    public boolean hasPermission(Player player, Block block, PermissionType type) {
        return true;
    }

    public static void log(String msg) {
        log(msg, Level.INFO);
    }

    public static void log(String msg, Level level) {
        if (level == Level.FINEST && !debugMode) {
            return;
        } else if (debugMode && level == Level.FINEST) {
            level = Level.INFO;
        }
        logger.log(level, msg);

    }

    public FileConfiguration getPluginConfig() {
        return this.getConfig();
    }

    public File getJarFile() {
        return getFile();
    }

    public Logger getLogger() {
        return logger;
    }

    public static BetterMechanics getInstance() {
        return instance;
    }

    public static PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    public void loadConfig(){

    }
}