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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.handlers.PermissionHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanicListener;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Cycler extends BlockMechanicListener {

    private static ConfigHandler.CyclerConfig config = BetterMechanics.getInstance().getConfigHandler().getCyclerConfig();
    private static Material[] activator = new Material[]{config.getCyclerTool()};

    private final Material[] targets = new Material[]{
            Material.CHEST,

            Material.LEVER,

            Material.STONE_BUTTON,

            Material.RAILS,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,

            Material.NETHER_BRICK_STAIRS,
            Material.SMOOTH_STAIRS,
            Material.BRICK_STAIRS,
            Material.WOOD_STAIRS,
            Material.COBBLESTONE_STAIRS,

            Material.IRON_DOOR,
            Material.WOOD_DOOR
    };

    @Override
    public boolean isTriggeredByRedstone() {
        return false;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public Material[] getMechanicActivators() {
        return activator;
    }

    @Override
    public Material[] getMechanicTargets() {
        return targets;
    }

    @Override
    public String getName() {
        return "Cycler";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void onBlockRightClick(PlayerEvent event) {
        if (PermissionHandler.getInstance().playerHasNode(event.getPlayer(), "cycler")) {
            byte data = event.getBlock().getData();
            switch (event.getBlock().getType()) {
                case CHEST:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case LEVER:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case STONE_BUTTON:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case RAILS:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case POWERED_RAIL:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case DETECTOR_RAIL:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case WOOD_STAIRS:
                case BRICK_STAIRS:
                case COBBLESTONE_STAIRS:
                case SMOOTH_STAIRS:
                case NETHER_BRICK_STAIRS:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                case WOODEN_DOOR:
                case IRON_DOOR:
                    data++;
                    if (data > 3)
                        data = 0;
                    break;
                default:
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "This blocktype can't be cycled...");
                    return;
            }
            event.getBlock().setData(data, false);
        } else {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "Seems like you don't have permissions to use the cycler...");
        }
    }

    public byte getMaxData(Material m) {
        switch (m) {
            case CHEST:
                return 4;
            case LEVER:
                return 4;
            case STONE_BUTTON:
                return 4;
            case RAILS:
                return 4;
            case POWERED_RAIL:
                return 8;
            case DETECTOR_RAIL:
                return 3;
            case WOOD_STAIRS:
            case BRICK_STAIRS:
            case NETHER_BRICK_STAIRS:
                return 8;
            default:
                return 0;
        }
    }

    public static class SignCycler extends SignMechanicListener {

        @Override
        public void onPlayerRightClickSign(PlayerEvent event) {
            byte data = event.getBlock().getData();
            data++;
            if (event.getBlock().getType() == Material.WALL_SIGN && data > 3)
                data = 0;
            else if (event.getBlock().getType() == Material.SIGN_POST && data > 11)
                data = 0;

            event.getBlock().setData(data, false);
        }

        @Override
        public boolean hasBlockMapper() {
            return false;
        }

        @Override
        public boolean hasBlockBag() {
            return false;
        }

        @Override
        public String[] getIdentifiers() {
            return voidTarget;
        }

        @Override
        public String[] getPassiveIdentifiers() {
            return null;
        }

        @Override
        public boolean isTriggeredByRedstone() {
            return false;
        }

        @Override
        public boolean isTriggeredByPlayer() {
            return true;
        }

        @Override
        public Material[] getMechanicActivators() {
            return activator;
        }

        @Override
        public String getName() {
            return "Cycler";
        }

        @Override
        public boolean isEnabled() {
            return config.isEnabled();
        }
    }
}
