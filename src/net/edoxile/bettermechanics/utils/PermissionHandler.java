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

package net.edoxile.bettermechanics.utils;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.zones.Zones;
import net.edoxile.bettermechanics.BetterMechanics;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class PermissionHandler {

    public boolean enabled;
    public boolean zonesEnabled;
    public boolean worldGuardEnabled;

    private Zones zones = null;
    private WorldGuardPlugin worldGuard = null;

    public enum Checks {
        NODE,
        BUILD,
        HIT,
        ALL
    }

    public boolean hasPermission(Player player, Block block, String node, Checks checks) {
        switch (checks) {
            case NODE:
                return playerHasNode(player, node);
            case BUILD:
                return playerHasNode(player, node) && playerCanBuild(player, block);
            case HIT:
                return playerHasNode(player, node) && playerCanHit(player, block);
            case ALL:
                return playerHasNode(player, node) && playerCanBuild(player, block) && playerCanHit(player, block);
            default:
                BetterMechanics.log("Something called .hasPermission but didn't hand the 'checks' argument.", Level.WARNING);
                return false;
        }
    }

    public PermissionHandler() {
        ConfigHandler.PermissionsConfig config = ConfigHandler.getInstance().getPermissionsConfig();
        enabled = config.isEnabled();
        if (config.canUseZones()) {
            Plugin p = BetterMechanics.getInstance().getServer().getPluginManager().getPlugin("Zones");
            if (p != null && p instanceof Zones) {
                zones = (Zones) p;
                zonesEnabled = true;
            } else {
                zonesEnabled = false;
            }
        } else {
            zonesEnabled = false;
        }

        if (config.canUseWorldGuard()) {
            Plugin p = BetterMechanics.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
            if (p != null && p instanceof WorldGuardPlugin) {
                worldGuard = (WorldGuardPlugin) p;
                worldGuardEnabled = true;
            } else {
                worldGuardEnabled = false;
            }
        } else {
            worldGuardEnabled = false;
        }
    }

    public boolean playerHasNode(Player player, String node) {
        return !enabled || player.isOp() || player.hasPermission("BetterMechanics." + node);
    }

    public boolean playerCanBuild(Player player, Block block) {
        if (enabled) {
            boolean returnValue = false;
            if (zonesEnabled) {
                returnValue = zones.getWorldManager(player).getActiveZone(block).getAccess(player).canBuild();
            }
            if (worldGuardEnabled) {
                Vector vector = toVector(block);
                RegionManager regionManager = worldGuard.getGlobalRegionManager().get(block.getWorld());
                ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(vector);
                LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

                returnValue = returnValue || applicableRegions.canBuild(localPlayer);
            }
            return returnValue;
        } else {
            return false;
        }
    }

    public boolean playerCanHit(Player player, Block block) {
        if (enabled) {
            boolean returnValue = false;
            if (zonesEnabled) {
                returnValue = zones.getWorldManager(player).getActiveZone(block).getAccess(player).canHit();
            }
            if (worldGuardEnabled) {
                Vector vector = toVector(block);
                RegionManager regionManager = worldGuard.getGlobalRegionManager().get(block.getWorld());
                ApplicableRegionSet applicableRegions = regionManager.getApplicableRegions(vector);

                returnValue = returnValue || worldGuard.getGlobalRegionManager().hasBypass(player, block.getWorld())
                        && applicableRegions.allows(DefaultFlag.USE);
            }
            return returnValue;
        } else {
            return false;
        }
    }
}
