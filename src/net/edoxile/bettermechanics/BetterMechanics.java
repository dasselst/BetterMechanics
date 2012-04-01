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
import net.edoxile.bettermechanics.listeners.BMListener;
import net.edoxile.bettermechanics.mechanics.Bridge;
import net.edoxile.bettermechanics.mechanics.Pen;
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
    private static PermissionHandler permissionHandler = new PermissionHandler();
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static boolean debugMode;
    private final MechanicsHandler mechanicsHandler = new MechanicsHandler();
    private final BMListener listener = new BMListener(this);

    @Override
    public void onEnable() {
        instance = this;
        permissionHandler = new PermissionHandler();
        //Register different Mechanics
        mechanicsHandler.addMechanic(new Pen());
        mechanicsHandler.addMechanic(new Bridge());

        //Register different events
        getServer().getPluginManager().registerEvents(listener, this);

        //TODO: fix this (deprecation etc.)
        loadConfig();

        debugMode = getPluginConfig().getBoolean("debug-mode", false);
        log("Enabled! Version: " + getDescription().getVersion() + ".");
    }

    @Override
    public void onDisable() {
        instance = null;
        log("Disabled.");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return mechanicsHandler.callCommandEvent(command, commandSender, args);
    }

    public MechanicsHandler getMechanicsHandler() {
        return mechanicsHandler;
    }

    public boolean hasPermission(Player player, Block block, String node, PermissionHandler.Checks checks) {
        return permissionHandler.hasPermission(player, block, node, checks);
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

    @Override
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