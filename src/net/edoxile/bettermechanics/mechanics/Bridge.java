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

import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import net.edoxile.bettermechanics.utils.PlayerNotifier;
import net.edoxile.bettermechanics.utils.SignUtil;
import net.edoxile.bettermechanics.utils.datastorage.BlockMap;
import net.edoxile.bettermechanics.utils.datastorage.BlockMapException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Bridge extends SignMechanicListener {
    private final ConfigHandler.BridgeConfig config = ConfigHandler.getInstance().getBridgeConfig();

    private enum Type {
        NORMAL("Bridge", "Bridge End"),
        SMALL("sBridge", "sBridge End");

        private static HashMap<String, Type> types = new HashMap<String, Type>();
        private String[] identifiers;

        private Type(String... ids) {
            identifiers = ids;
        }

        static{
            for(Type t : values()){
                for(String id : t.getIdentifiers()){
                    types.put(id,t);
                }
            }
        }

        public String[] getIdentifiers(){
            return identifiers;
        }

        public static Type getType(String id){
            return types.get(id);
        }
    }

    public Bridge() {
    }

    @Override
    public void onSignPowerOn(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            //Close gate
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onSignPowerOff(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            //Open gate
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            //Toggle gate
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public List<String> getIdentifiers() {
        return Arrays.asList("Bridge", "sBridge");
    }

    @Override
    public List<String> getPassiveIdentifiers() {
        return Arrays.asList("Bridge", "sBridge", "Bridge End", "sBridge End");
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
    public List<Material> getMechanicActivators() {
        return null;
    }

    @Override
    public String getName() {
        return "Bridge";
    }

    @Override
    public void mapBlocks(Sign sign) throws BlockMapException {
        final int maxDistance = config.getMaxLength();
        final boolean isSmall = SignUtil.getMechanicsIdentifier(sign).equals("sBridge");
        BlockFace direction = SignUtil.getFacing(sign).getOppositeFace();
        //TODO: Check if facing is correct (ordinal direction);
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
    }

    private boolean blocksEqual(Material type, byte data, Block... blocks) {
        for (Block block : blocks) {
            if (block.getType() != type || block.getData() != data) {
                return false;
            }
        }
        return true;
    }
}
