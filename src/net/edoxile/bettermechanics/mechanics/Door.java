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
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Door {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Sign sign;
    private Sign endSign;
    private Player player;
    private MechanicsConfig.DoorConfig config;
    private Set<Block> blockSet;
    private MaterialData doorMaterial;
    private BlockBagManager blockBagManager;

    public Door(MechanicsConfig c, BlockBagManager bbm, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getDoorConfig();
        blockBagManager = bbm;
    }

    public boolean map() throws InvalidMaterialException, BlockNotFoundException, NonCardinalDirectionException {
        if (!config.enabled)
            return false;
        BlockFace direction;
        BlockFace orientation = SignUtil.getBlockFace(sign);

        MechanicsType doorType = SignUtil.getMechanicsType(sign);

        if (sign.getLine(1).equalsIgnoreCase("[Door Down]") || sign.getLine(1).equalsIgnoreCase("[sDoor Down]")) {
            direction = BlockFace.DOWN;
        } else {
            direction = BlockFace.UP;
        }

        if (config.canUseBlock(sign.getBlock().getRelative(direction).getType())) {
            doorMaterial = new MaterialData(sign.getBlock().getRelative(direction).getType(), sign.getBlock().getRelative(direction).getData());
        } else {
            throw new InvalidMaterialException();
        }

        endSign = BlockMapper.findMechanicsSign(sign.getBlock(), direction, doorType, config.maxHeight);
        Block startBlock = sign.getBlock().getRelative(direction).getRelative(direction);
        Block endBlock = null;
        switch (direction) {
            case UP:
                endBlock = endSign.getBlock().getRelative(BlockFace.DOWN);
                break;
            case DOWN:
                endBlock = endSign.getBlock().getRelative(BlockFace.UP);
                break;
        }
        try {
            blockSet = BlockMapper.mapVertical(direction, orientation, startBlock, endBlock, (doorType == MechanicsType.SMALL_DOOR));
            if (!blockSet.isEmpty()) {
                /* Block chestBlock = BlockMapper.mapCuboidRegion(sign.getBlock(), 3, Material.CHEST);
                if (chestBlock == null) {
                    //Check other sign

                    chestBlock = BlockMapper.mapCuboidRegion(endSign.getBlock(), 3, Material.CHEST);
                    if (chestBlock == null) {
                        throw new ChestNotFoundException();
                    }
                }
                chest = BlockbagUtil.getChest(chestBlock);
                if (chest == null) {
                    throw new ChestNotFoundException();
                } */
                return true;
            } else {
                log.info("[BetterMechanics] Empty blockSet?");
                return false;
            }
        } catch (InvalidDirectionException e) {
            log.info("[BetterMechanics] Our mapper is acting weird!");
            return false;
        }
    }

    public void toggleOpen() throws ChestNotFoundException {
        int amount = 0;
        try {

            BlockBag tmpbag = blockBagManager.searchBlockBag(sign.getBlock(), true, false);

            if (tmpbag == null)
                tmpbag = blockBagManager.searchBlockBag(endSign.getBlock(), true, false);

            if (tmpbag == null)
                throw new ChestNotFoundException();

            for (Block b : blockSet) {
                if (b.getType() == doorMaterial.getItemType() && b.getData() == doorMaterial.getData()) {
                    b.setType(Material.AIR);
                    amount++;
                }
            }
            // BlockbagUtil.safeAddItems(chest, doorMaterial.toItemStack(amount));


            tmpbag.safeAddItems(doorMaterial.toItemStack(amount));

            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Door opened!");
            }
        } catch (OutOfSpaceException ex) {
            for (Block b : blockSet) {
                if (b.getType() == Material.AIR) {
                    b.setType(doorMaterial.getItemType());
                    b.setData(doorMaterial.getData());
                    amount--;
                    if (amount == 0) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Not enough space in chest!");
                        }
                        return;
                    }
                }
            }
        }
    }

    public void toggleClosed() throws ChestNotFoundException {
        int amount = 0;
        try {

            BlockBag tmpbag = blockBagManager.searchBlockBag(sign.getBlock(), false, true);

            if (tmpbag == null)
                tmpbag = blockBagManager.searchBlockBag(endSign.getBlock(), false, true);

            if (tmpbag == null)
                throw new ChestNotFoundException();

            for (Block b : blockSet) {
                if (canPassThrough(b.getType())) {
                    b.setType(doorMaterial.getItemType());
                    b.setData(doorMaterial.getData());
                    amount++;
                }
            }
            // BlockbagUtil.safeRemoveItems(chest, doorMaterial.toItemStack(amount));

            tmpbag.safeRemoveItems(doorMaterial.toItemStack(amount));
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Door closed!");
            }
        } catch (OutOfMaterialException ex) {
            for (Block b : blockSet) {
                if (b.getType() == doorMaterial.getItemType()) {
                    b.setType(Material.AIR);
                    amount--;
                    if (amount == 0) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Not enough items in chest! Still need: " + Integer.toString(ex.getAmount()) + " of type: " + doorMaterial.getItemType().name());
                        }
                        return;
                    }
                }
            }
        }
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

    public boolean isClosed() {
        for (Block b : blockSet) {
            if (b.getType() == doorMaterial.getItemType() || canPassThrough(b.getType())) {
                return (!canPassThrough(b.getType()));
            }
        }
        return false;
    }
}