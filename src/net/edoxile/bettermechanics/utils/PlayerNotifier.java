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
