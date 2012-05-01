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
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.handlers.PermissionHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import net.edoxile.bettermechanics.utils.PlayerNotifier;
import net.edoxile.bettermechanics.utils.SignUtil;
import net.edoxile.bettermechanics.utils.datastorage.BlockMap;
import net.edoxile.bettermechanics.utils.datastorage.BlockMapException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Bridge extends SignMechanicListener {

    private final ConfigHandler.BridgeConfig config = BetterMechanics.getInstance().getConfigHandler().getBridgeConfig();
    private final String[] identifiers = new String[]{"Bridge", "sBridge"};
    private final String[] passiveIdentifiers = new String[]{"Bridge", "sBridge", "Bridge End", "sBridge End"};

    @Override
    public void onSignPowerOn(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            close();
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onSignPowerOff(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            open();
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        try {
            String node = SignUtil.getMechanicsIdentifier(SignUtil.getSign(event.getBlock()));
            if (!PermissionHandler.getInstance().hasPermission(event.getPlayer(), event.getBlock(), node, PermissionHandler.Checks.HIT))
                throw new PlayerNotifier("Seems like you don't have permission to do this!", PlayerNotifier.Level.INFO, event.getBlock().getLocation());
            loadData(SignUtil.getSign(event.getBlock()));
            if (isOpen()) {
                close();
                event.getPlayer().sendMessage(ChatColor.GOLD + "Bridge closed!");
            } else {
                open();
                event.getPlayer().sendMessage(ChatColor.GOLD + "Bridge opened!");
            }
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.notify(event.getPlayer());
            playerNotifier.log();
        }
    }

    @Override
    public String[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String[] getPassiveIdentifiers() {
        return passiveIdentifiers;
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return true;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public boolean hasBlockMapper() {
        return true;
    }

    @Override
    public boolean hasBlockBag() {
        return true;
    }

    @Override
    public Material[] getMechanicActivators() {
        return new Material[]{Material.AIR};
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String getName() {
        return "Bridge";
    }

    @Override
    public void mapBlocks(Sign sign) throws BlockMapException {
        final int maxDistance = config.getMaxLength();
        String node = SignUtil.getMechanicsIdentifier(sign);
        final boolean isSmall = node.equals("sBridge");
        BlockFace direction = SignUtil.getFacing(sign).getOppositeFace();
        if (SignUtil.isOrdinal(direction)) {
            Block startBlock = sign.getBlock();
            Block endBlock = sign.getBlock();
            for (int check = 0; check < maxDistance; check++) {
                endBlock = endBlock.getRelative(direction);
                if (SignUtil.isSign(endBlock) && isThisMechanic(SignUtil.getSign(endBlock))) {
                    if (SignUtil.getFacing(SignUtil.getSign(endBlock)).equals(direction)) {
                        BlockFace bridgeLocation = (
                                config.canUseMaterial(startBlock.getRelative(BlockFace.UP).getType())
                                        ? BlockFace.UP : ((config.canUseMaterial(startBlock.getRelative(BlockFace.DOWN).getType()))
                                        ? BlockFace.DOWN : BlockFace.SELF)
                        );
                        if (bridgeLocation.equals(BlockFace.SELF)) {
                            throw new BlockMapException(BlockMapException.Type.NON_ALLOWED_MATERIAL);
                        }
                        startBlock = startBlock.getRelative(bridgeLocation);
                        endBlock = endBlock.getRelative(bridgeLocation);
                        if (endBlock.getTypeId() == startBlock.getTypeId()) {
                            Material type = startBlock.getType();
                            byte data = startBlock.getData();
                            switch (direction) {
                                case NORTH:
                                case SOUTH:
                                    if (blocksEqual(type, data,
                                            startBlock.getRelative(BlockFace.EAST), startBlock.getRelative(BlockFace.WEST),
                                            endBlock.getRelative(BlockFace.EAST), endBlock.getRelative(BlockFace.WEST))) {
                                        Set<Block> blockList = new HashSet<Block>();
                                        Block currentBlock = startBlock.getRelative(direction);
                                        while (!currentBlock.equals(endBlock)) {
                                            blockList.add(currentBlock);
                                            if (isSmall) {
                                                blockList.add(currentBlock.getRelative(BlockFace.EAST));
                                                blockList.add(currentBlock.getRelative(BlockFace.WEST));
                                            }
                                            currentBlock = currentBlock.getRelative(direction);
                                        }
                                        blockMap = new BlockMap(blockList, startBlock, endBlock, type, data);
                                        return;
                                    } else {
                                        throw new BlockMapException(BlockMapException.Type.SIDES_DO_NOT_MATCH);
                                    }
                                case EAST:
                                case WEST:
                                    if (blocksEqual(type, data,
                                            startBlock.getRelative(BlockFace.NORTH), startBlock.getRelative(BlockFace.SOUTH),
                                            endBlock.getRelative(BlockFace.NORTH), endBlock.getRelative(BlockFace.SOUTH))) {
                                        Set<Block> blockList = new HashSet<Block>();
                                        Block currentBlock = startBlock.getRelative(direction);
                                        while (!currentBlock.equals(endBlock)) {
                                            blockList.add(currentBlock);
                                            if (isSmall) {
                                                blockList.add(currentBlock.getRelative(BlockFace.NORTH));
                                                blockList.add(currentBlock.getRelative(BlockFace.SOUTH));
                                            }
                                            currentBlock = currentBlock.getRelative(direction);
                                        }
                                        blockMap = new BlockMap(blockList, startBlock, endBlock, type, data);
                                        return;
                                    } else {
                                        throw new BlockMapException(BlockMapException.Type.SIDES_DO_NOT_MATCH);
                                    }
                                default:
                                    throw new BlockMapException(BlockMapException.Type.NON_ORDINAL_SIGN);
                            }
                        } else {
                            throw new BlockMapException(BlockMapException.Type.SIDES_DO_NOT_MATCH);
                        }
                    } else {
                        throw new BlockMapException(BlockMapException.Type.NON_ORDINAL_SIGN);
                    }
                }
            }
            throw new BlockMapException(BlockMapException.Type.END_NOT_FOUND);
        } else {
            throw new BlockMapException(BlockMapException.Type.NON_ORDINAL_SIGN);
        }
    }

    private boolean blocksEqual(Material type, byte data, Block... blocks) {
        for (Block block : blocks) {
            if (block.getType() != type || block.getData() != data) {
                return false;
            }
        }
        return true;
    }

    private boolean isOpen() {
        if (hasBlockMapper()) {
            for (Block b : blockMap.getSet()) {
                if (b.getTypeId() == blockMap.getMaterial().getId() && b.getData() == blockMap.getMaterialData())
                    return false;
                else if (b.getTypeId() == Material.AIR.getId())
                    return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
