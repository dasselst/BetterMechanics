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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Bridge extends SignMechanicListener {
    ConfigHandler.BridgeConfig config = ConfigHandler.getInstance().getBridgeConfig();

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
        return Arrays.asList("Bridge", "sBridge", "Bridge End");
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
                                    List<Block> blockList = new ArrayList<Block>();
                                    Block currentBlock = startBlock.getRelative(direction);
                                    while (!currentBlock.equals(endBlock)) {
                                        blockList.add(currentBlock);
                                        blockList.add(currentBlock.getRelative(BlockFace.EAST));
                                        blockList.add(currentBlock.getRelative(BlockFace.WEST));
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
                                    List<Block> blockList = new ArrayList<Block>();
                                    Block currentBlock = startBlock.getRelative(direction);
                                    while (!currentBlock.equals(endBlock)) {
                                        blockList.add(currentBlock);
                                        blockList.add(currentBlock.getRelative(BlockFace.NORTH));
                                        blockList.add(currentBlock.getRelative(BlockFace.SOUTH));
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
