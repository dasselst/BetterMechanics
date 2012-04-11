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

package net.edoxile.bettermechanics.utils;

import net.edoxile.bettermechanics.BetterMechanics;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class PlayerNotifier extends Throwable {
    public enum Level {
        NOTICE(ChatColor.AQUA),
        INFO(ChatColor.GREEN),
        WARNING(ChatColor.RED),
        SEVERE(ChatColor.DARK_RED);

        private final ChatColor color;

        private Level(ChatColor c) {
            color = c;
        }

        public ChatColor getColor() {
            return color;
        }
    }

    private final String message;
    private final Level level;
    private final Location location;

    public PlayerNotifier(String msg, Level lvl, Location loc) {
        level = lvl;
        message = msg;
        location = loc;
    }

    public void notify(Player player) {
        player.sendMessage(level.getColor() + message);
    }

    public void log() {
        String logMessage = "Player got warning: '" + message + "' @{world:" + location.getWorld().getName() + ", x=" + location.getBlockX() + ", z=" + location.getBlockZ() + ", y=" + location.getBlockY() + "}";
        if (level == Level.WARNING) {
            BetterMechanics.log(logMessage, java.util.logging.Level.WARNING);
        } else if (level == Level.SEVERE) {
            BetterMechanics.log(logMessage, java.util.logging.Level.SEVERE);
        }
    }

    public void broadcast(Player player) {
        notify(player);
        log();
    }
}
