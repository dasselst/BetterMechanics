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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.mechanics.interfaces.ISignMechanic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Bridge implements ISignMechanic {
    private static boolean enabled = true;
    private static int maxLength = 128;
    private static List<Integer> materialList = Arrays.asList(Material.WOOD.getId());

    static {

    }

    private ArrayList<Block> blockList = new ArrayList<Block>();
    private List<BlockFace> allowedOrientations = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private Sign mechanicSign = null;
    private Block otherSide = null;
    private Material bridgeMaterial = null;
    private BetterMechanics plugin = null;

    public Bridge(BetterMechanics p) {
        plugin = p;
        //loadConfig(plugin.getConfiguration());
    }

    public void onSignPowerOn(Block sign) {
        mechanicSign = (Sign) sign.getState();
        map(null);
        open();
        blockList.clear();
    }

    public void onSignPowerOff(Block sign) {
        mechanicSign = (Sign) sign.getState();
        map(null);
        close();
        blockList.clear();
    }

    public void onPlayerRightClickSign(Player player, Block sign) {
        mechanicSign = (Sign) sign.getState();
        map(player);
        if (isClosed()) {
            open();
        } else {
            close();
        }
        blockList.clear();
    }

    public void onPlayerLeftClickSign(Player player, Block sign) {
    }

    public String[] getIdentifier() {
        return "[Bridge]";
    }

    public Material getMechanicActivator() {
        return null;
    }

    private void map(Player player) {
        BlockFace orientation = ((org.bukkit.material.Sign) (mechanicSign.getData())).getFacing().getOppositeFace();
        boolean foundOtherSide = false;
        if (allowedOrientations.contains(orientation)) {
            int travelDistance = maxLength;
            while (travelDistance > 0) {
                travelDistance--;
                otherSide = mechanicSign.getBlock().getRelative(orientation, maxLength - travelDistance);
                if (otherSide.getState() instanceof Sign && ((Sign) otherSide.getState()).getLine(1).startsWith("[Bridge")) {
                    foundOtherSide = true;
                    break;
                }
            }
            if (foundOtherSide) {
                boolean isNorthSouth = (orientation.equals(BlockFace.NORTH) || orientation.equals(BlockFace.SOUTH));
                travelDistance = maxLength - travelDistance - 1;
                if (materialList.contains(otherSide.getRelative(BlockFace.UP).getTypeId())) {
                    otherSide = otherSide.getRelative(BlockFace.UP);
                    bridgeMaterial = otherSide.getType();
                } else if (materialList.contains(otherSide.getRelative(BlockFace.DOWN).getTypeId())) {
                    otherSide = otherSide.getRelative(BlockFace.DOWN);
                    bridgeMaterial = otherSide.getType();
                } else {
                    if (player != null) {
                        player.sendMessage(ChatColor.DARK_RED + "Hmm, seems like this gate is not made of a valid material...");
                    }
                    return;
                }
                for (; travelDistance > 0; travelDistance--) {
                    otherSide = mechanicSign.getBlock().getRelative(orientation, travelDistance);
                    blockList.add(otherSide);
                    if (isNorthSouth) {
                        blockList.add(otherSide.getRelative(BlockFace.NORTH));
                        blockList.add(otherSide.getRelative(BlockFace.SOUTH));
                    } else {
                        blockList.add(otherSide.getRelative(BlockFace.EAST));
                        blockList.add(otherSide.getRelative(BlockFace.WEST));
                    }
                }
            } else {
                if (player != null) {
                    player.sendMessage(ChatColor.DARK_RED + "Hmm, seems like there is no sign within reach...");
                }
            }
        } else {
            if (player != null)
                player.sendMessage(ChatColor.DARK_RED + "Hmm, seems like this sing is not placed correctly...");
        }
    }

    private boolean isClosed() {
        for (Block b : blockList) {
            if (b.getType() == Material.AIR) {
                return false;
            }
            if (b.getType() == bridgeMaterial) {
                return true;
            }
        }
        //TODO: You've built a bad bridge. Go kill yourself.
        return true;
    }

    private int close() {
        int i = 0;
        for (Block b : blockList) {
            if (b.getType() == Material.AIR) {
                b.setType(bridgeMaterial);
            }
        }
        BetterMechanics.log("Changed " + Integer.toString(i) + " blocks while closing gate.", Level.FINEST);
        return i;
    }

    private int open() {
        int i = 0;
        for (Block b : blockList) {
            if (b.getType() == bridgeMaterial) {
                b.setType(Material.AIR);
                i++;
            }
        }
        BetterMechanics.log("Changed " + Integer.toString(i) + " blocks while opening gate.", Level.FINEST);
        return i;
    }
}
