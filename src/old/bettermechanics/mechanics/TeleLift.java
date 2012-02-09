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

package old.bettermechanics.mechanics;

import old.bettermechanics.utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class TeleLift {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Sign sign;
    private Player player;
    private MechanicsConfig config;
    private MechanicsConfig.TeleLiftConfig teleLiftConfig;
    private static List<Integer> nonSolidBlockList = Arrays.asList(0, 6, 8, 9, 10, 11, 27, 28, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 78, 83, 90, 93, 94);
    private Location destination;

    public TeleLift(MechanicsConfig c, Sign s, Player p) {
        sign = s;
        player = p;
        config = c;
        teleLiftConfig = config.getTeleLiftConfig();
    }

    public boolean map() throws NumberFormatException {
        if (!teleLiftConfig.enabled)
            return false;
        destination = parseDestination();
        if (destination.getY() <= 127) {
            Block dest1 = destination.getBlock();
            Block dest2 = dest1.getRelative(BlockFace.UP);
            Block dest3 = dest1.getRelative(BlockFace.DOWN);
            if (canPassThrough(dest1.getType()) && canPassThrough(dest2.getType())) {
                if (canPassThrough(dest3.getType()) && canPassThrough(dest3.getRelative(BlockFace.DOWN).getType())) {
                    player.sendMessage(ChatColor.RED + "You have no place to stand on!");
                    return false;
                }
                return true;
            } else if (canPassThrough(dest1.getType()) && canPassThrough(dest3.getType())) {
                if (canPassThrough(dest3.getRelative(BlockFace.DOWN).getType()) && canPassThrough(dest3.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType())) {
                    player.sendMessage(ChatColor.RED + "You have no place to stand on!");
                    return false;
                }
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "You would be obscured!");
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean movePlayer() {
        Location oldloc = player.getLocation();
        if (player.teleport(destination)) {
            player.sendMessage(ChatColor.GOLD + "You magically teleported to an other location!");
            config.addTeleportHistoryLine(player, oldloc);
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Something went wrong teleporting you!");
            log.warning("Couldn't teleport player '" + player.getName() + "' via lift.");
            return false;
        }
    }

    private boolean canPassThrough(Material m) {
        return nonSolidBlockList.contains(m.getId());
    }

    public Location parseDestination() throws NumberFormatException {
        Location newLocation = player.getLocation();
        String[] locations = sign.getLine(2).split(":");
        if (locations.length == 3) {
            newLocation.setX(Integer.parseInt(locations[0]) - 0.5);
            newLocation.setZ(Integer.parseInt(locations[1]) + 0.5);
            newLocation.setY(Integer.parseInt(locations[2]));
            if (newLocation.getY() < 2) {
                player.sendMessage(ChatColor.RED + "Invalid Y location. Teleporting to y = 130");
                newLocation.setY(130);
            }
            return newLocation;
        } else {
            return null;
        }
    }
}
