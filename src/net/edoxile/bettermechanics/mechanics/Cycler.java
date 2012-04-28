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
            /*Material.SIGN_POST,
            Material.WALL_SIGN,*/
            Material.LEVER,
            Material.STONE_BUTTON,
            Material.RAILS,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL
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
            byte maxData = getMaxData(event.getBlock().getType());
            if (maxData != 0) {
                byte data = event.getBlock().getData();
                data++;

                if (data >= maxData)
                    data = 0;

                event.getBlock().setData(data, false);
            }
        } else {
            event.getPlayer().sendMessage(ChatColor.DARK_RED + "Seems like you don't have permissions to use the cycler...");
        }
    }

    public byte getMaxData(Material m) {
        switch (m) {
            case CHEST:
                return 4;
            /*case SIGN_POST:
                return 4;
            case WALL_SIGN:
                return 4;*/
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
            default:
                return 0;
        }
    }

    //TODO: Mooie manier hier voor verzinnen? Misschien een SignAndBlockMech, maar dat is weer gelijk zo lomp...
    public static class SignCycler extends SignMechanicListener {

        @Override
        public void onPlayerRightClickSign(PlayerEvent event) {
            byte data = event.getBlock().getData();
            data++;
            if (event.getBlock().getType() == Material.WALL_SIGN && data > 3)
                data = 0;
            else if (event.getBlock().getType() == Material.SIGN_POST && data > 12)
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
