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

package net.edoxile.bettermechanics;

import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.handlers.MechanicsHandler;
import net.edoxile.bettermechanics.listeners.BMListener;
import net.edoxile.bettermechanics.mechanics.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
    private static BetterMechanics instance;
    private static final Logger logger = Logger.getLogger("minecraft");
    public static boolean DEBUG = false;
    private final File dataFolder;
    private final File jarFile;

    private final MechanicsHandler mechanicsHandler = new MechanicsHandler();
    private final BMListener listener = new BMListener(this);
    private ConfigHandler configHandler;

    public BetterMechanics() {
        instance = this;

        dataFolder = new File("./plugins/BetterMechanics");
        jarFile = new File("./plugins/BetterMechanics.jar");

        if (!dataFolder.exists()) {
            if (dataFolder.mkdirs()) {
                log("Creating new config");
                ConfigHandler.createConfig(this);
            } else {
                log("The config was missing, but couldn't create new config files");
                setEnabled(false);
            }
        }
        configHandler = new ConfigHandler(this);
    }

    @Override
    public void onEnable() {
        //Register different Mechanics
        mechanicsHandler.addMechanic(new Pen());
        mechanicsHandler.addMechanic(new Gate());
        mechanicsHandler.addMechanic(new Bridge());
        mechanicsHandler.addMechanic(new Cauldron());
        mechanicsHandler.addMechanic(new Lift());
        mechanicsHandler.addMechanic(new Cycler());
        mechanicsHandler.addMechanic(new Cycler.SignCycler());
        mechanicsHandler.addMechanic(new PowerBlock());

        //Register different events
        getServer().getPluginManager().registerEvents(listener, this);

        //log("Enabled! Version: " + getDescription().getVersion() + ".");
    }

    @Override
    public void onDisable() {
        instance = null;
        //log("Disabled.");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return mechanicsHandler.callCommandEvent(command, commandSender, args);
    }

    public MechanicsHandler getMechanicsHandler() {
        return mechanicsHandler;
    }

    public static void log(String msg) {
        log(msg, Level.INFO);
    }

    public static void log(String msg, Level level) {
        if (level == Level.FINEST && !DEBUG) {
            return;
        } else if (DEBUG && level == Level.FINEST) {
            level = Level.INFO;
        }
        logger.log(level, "[BetterMechanics] " + msg);
    }


    @Deprecated
    public FileConfiguration getPluginConfig() {
        return getConfig();
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    public File getJarFile() {
        return jarFile;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static BetterMechanics getInstance() {
        return instance;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}