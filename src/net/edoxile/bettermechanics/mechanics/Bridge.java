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
import org.bukkit.material.MaterialData;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Bridge {
    private Logger log = Logger.getLogger("Minecraft");
    private Sign sign;
    private Sign endSign;
    private Player player;
    private MechanicsConfig.BridgeConfig config;
    private Set<Block> blockSet;
    private MaterialData bridgeMaterial;
    private BlockBagManager blockBagManager;
    private BlockBag bag;

    public Bridge(MechanicsConfig c, BlockBagManager bbm, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getBridgeConfig();
        blockBagManager = bbm;
    }

    public boolean map() throws NonCardinalDirectionException, BlockNotFoundException, InvalidMaterialException {
        if (!config.enabled)
            return false;
        BlockFace bf;
        if (config.canUseBlock(sign.getBlock().getRelative(BlockFace.UP).getType())) {
            bf = BlockFace.UP;
            bridgeMaterial = new MaterialData(sign.getBlock().getRelative(BlockFace.UP).getType(), sign.getBlock().getRelative(BlockFace.UP).getData());
            // System.out.println("Using UP");
        } else if (config.canUseBlock(sign.getBlock().getRelative(BlockFace.DOWN).getType())) {
            bf = BlockFace.DOWN;
            bridgeMaterial = new MaterialData(sign.getBlock().getRelative(BlockFace.DOWN).getType(), sign.getBlock().getRelative(BlockFace.DOWN).getData());
            // System.out.println("Using DOWN");
        } else {
            throw new InvalidMaterialException();
        }

        MechanicsType bridgeType = SignUtil.getMechanicsType(sign);

        endSign = BlockMapper.findMechanicsSign(sign.getBlock(), SignUtil.getBackFacingDirection(sign), bridgeType, config.maxLength);
        Block startBlock = sign.getBlock().getRelative(SignUtil.getBackFacingDirection(sign)).getRelative(bf);
        Block endBlock = endSign.getBlock().getRelative(bf);
        try {
            blockSet = BlockMapper.mapHorizontal(SignUtil.getBackFacingDirection(sign), startBlock, endBlock, bridgeType == MechanicsType.SMALL_BRIDGE);
            if (!blockSet.isEmpty()) {
                return true;
            } else {
                log.info("[BetterMechanics] Empty blockSet?");
                return false;
            }
        } catch (InvalidDirectionException ex) {
            log.info("[BetterMechanics] Our mapper is acting weird!");
            return false;
        }
    }

    public void toggleOpen() throws ChestNotFoundException {
        int amount = 0;
        try {

            BlockBag tmpbag = blockBagManager.searchBlockBag(sign.getBlock(), false, true);

            if(tmpbag == null)
                tmpbag = blockBagManager.searchBlockBag(endSign.getBlock(), false, true);

            if(tmpbag == null)
                throw new ChestNotFoundException();

            for (Block b : blockSet) {
                if (b.getType() == bridgeMaterial.getItemType()) {
                    b.setType(Material.AIR);
                    amount++;
                }
            }
            tmpbag.safeAddItems(bridgeMaterial.toItemStack(amount));

            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Bridge opened!");
            }
        } catch (OutOfSpaceException ex) {
            for (Block b : blockSet) {
                if (b.getType() == Material.AIR) {
                    b.setType(bridgeMaterial.getItemType());
                    b.setData(bridgeMaterial.getData());
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
            BlockBag tmpbag = blockBagManager.searchBlockBag(sign.getBlock(), true, false);

            if(tmpbag == null)
                tmpbag = blockBagManager.searchBlockBag(endSign.getBlock(), true, false);

            if(tmpbag == null)
                throw new ChestNotFoundException();

            for (Block b : blockSet) {
                if (canPassThrough(b.getType())) {
                    b.setType(bridgeMaterial.getItemType());
                    b.setData(bridgeMaterial.getData());
                    amount++;
                }
            }

            tmpbag.safeRemoveItems(bridgeMaterial.toItemStack(amount));

            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Bridge closed!");
            }
        } catch (OutOfMaterialException ex) {
            for (Block b : blockSet) {
                if (b.getType() == bridgeMaterial.getItemType()) {
                    b.setType(Material.AIR);
                    amount--;
                    if (amount == 0) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Not enough items in chest! Still need: " + Integer.toString(ex.getAmount()) + " of type: " + bridgeMaterial.getItemType().name());
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
            if (b.getType() == bridgeMaterial.getItemType() || canPassThrough(b.getType())) {
                return (!canPassThrough(b.getType()));
            }
        }
        return false;
    }
}
