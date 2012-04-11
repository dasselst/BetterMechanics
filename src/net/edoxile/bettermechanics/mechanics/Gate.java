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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.MechanicsType;
import net.edoxile.bettermechanics.exceptions.*;
import net.edoxile.bettermechanics.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Gate {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Sign sign;
    private Player player;
    private MechanicsConfig.GateConfig config;
    private boolean smallGate;
    private Set<Block> blockSet;
    private Material gateMaterial;
    private BlockBagManager blockBagManager;

    public Gate(MechanicsConfig c, BlockBagManager bbm, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getGateConfig();
        blockBagManager = bbm;
    }

    public boolean map() throws NonCardinalDirectionException, ChestNotFoundException, OutOfBoundsException, BlockNotFoundException {
        if (!config.enabled)
            return false;
        /* Block chestBlock = BlockMapper.mapCuboidRegion(sign.getBlock(), 3, Material.CHEST);
        if (chestBlock == null) {
            throw new ChestNotFoundException();
        } else {
             chest = BlockbagUtil.getChest(chestBlock);
            if (chest == null) {
                throw new ChestNotFoundException();
            }
        } */
        smallGate = (SignUtil.getMechanicsType(sign) == MechanicsType.SMALL_GATE);
        int sw = (smallGate ? 1 : 4);
        Block startBlock = sign.getBlock().getRelative(SignUtil.getBackBlockFace(sign));
        Block tempBlock = null;
        tempBlock = BlockMapper.mapColumn(startBlock, sw, sw, Material.FENCE);
        if (tempBlock == null) {
            tempBlock = BlockMapper.mapColumn(startBlock, sw, sw, Material.IRON_FENCE);
            if (tempBlock == null) {
                throw new BlockNotFoundException();
            } else {
                gateMaterial = Material.IRON_FENCE;
            }
        } else {
            gateMaterial = Material.FENCE;
        }
        blockSet = BlockMapper.mapFlatRegion(tempBlock, gateMaterial, config.maxWidth, config.maxLength);
        if (blockSet.isEmpty()) {
            log.info("BlockSet is empty. No blocks were found.");
            return false;
        } else {
            return true;
        }
    }

    public void toggleOpen() throws ChestNotFoundException {
        int amount = 0;
        Block tempBlock;
        try {

            BlockBag tmpbag = blockBagManager.searchBlockBag(sign.getBlock(), true, false);
            if(tmpbag == null)
                throw new ChestNotFoundException();

            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (tempBlock.getType() == gateMaterial) {
                    tempBlock.setType(Material.AIR);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount++;
                }
            }
            // BlockbagUtil.safeAddItems(chest, new ItemStack(gateMaterial, amount));

            tmpbag.safeAddItems(new ItemStack(gateMaterial, amount));


            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Gate opened!");
            }
        } catch (OutOfSpaceException ex) {
            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (tempBlock.getType() == Material.AIR && amount > 0) {
                    tempBlock.setType(gateMaterial);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount--;
                }
                if (amount == 0)
                    break;
            }
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough space in chest!");
            }
        }
    }

    public void toggleClosed() throws ChestNotFoundException {
        int amount = 0;
        Block tempBlock;
        try {

            BlockBag tmpbag = blockBagManager.searchBlockBag(sign.getBlock(), false, true);
            if(tmpbag == null)
                throw new ChestNotFoundException();

            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (canPassThrough(tempBlock.getType())) {
                    tempBlock.setType(gateMaterial);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount++;
                }
            }
            // BlockbagUtil.safeRemoveItems(chest, new ItemStack(gateMaterial, amount));

            tmpbag.safeRemoveItems(new ItemStack(gateMaterial, amount));

            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Gate closed!");
            }
        } catch (OutOfMaterialException ex) {
            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (tempBlock.getType() == gateMaterial && amount > 0) {
                    tempBlock.setType(Material.AIR);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount--;
                }
                if (amount == 0)
                    break;
            }
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough items in chest! Still need: " + Integer.toString(ex.getAmount()) + " of type: fence");
            }
        }
    }

    public boolean isClosed() {
        for (Block b : blockSet) {
            return b.getRelative(BlockFace.DOWN).getType() == gateMaterial;
        }
        return false;
    }

    private boolean canPassThrough(Material m) {
        switch (m) {
            case AIR:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case SNOW:
                return true;
            default:
                return false;
        }
    }
}
