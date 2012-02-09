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

import net.edoxile.bettermechanics.listeners.BMBlockListener;
import net.edoxile.bettermechanics.listeners.BMPlayerListener;
import net.edoxile.bettermechanics.mechanics.Bridge;
import net.edoxile.bettermechanics.models.MechanicsHandler;
import net.edoxile.bettermechanics.models.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

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
    private static Logger logger = Logger.getLogger("Minecraft");
    private static boolean debugMode;
    private MechanicsHandler mechanicsHandler = new MechanicsHandler();
    private BMPlayerListener playerListener = new BMPlayerListener(this);
    private BMBlockListener blockListener = new BMBlockListener(this);

    public void onEnable() {
        instance = this;
        //Register different Mechanics
        /*mechanicsHandler.addMechanic(new Gate(this));
        mechanicsHandler.addMechanic(new Pen(this));*/
        mechanicsHandler.addMechanic(new Bridge(this));

        //TODO: Register different events
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);

        debugMode = getConfiguration().getBoolean("debug-mode", false);
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

    //TODO: implement
    public Configuration getConfig() {
        return this.getConfiguration();
    }
    
    public File getJarFile(){
        return getFile();
    }

    public Logger getLogger() {
        return logger;
    }

    public static BetterMechanics getInstance(){
        return instance;
    }
}