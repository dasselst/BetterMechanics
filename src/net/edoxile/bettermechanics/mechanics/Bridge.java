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
import net.edoxile.bettermechanics.models.BlockMap;
import net.edoxile.bettermechanics.models.BlockMapException;
import net.edoxile.bettermechanics.models.SignMechanicEventData;
import net.edoxile.bettermechanics.utils.ConfigHandler;
import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Bridge extends ISignMechanic {
    private boolean enabled;
    private int maxLength;
    private Set<Material> allowedMaterials;

    public Bridge() {
        reloadConfig();
    }

    public void reloadConfig() {
        ConfigHandler.BridgeConfig config = ConfigHandler.getInstance().getBridgeConfig();
        enabled = config.isEnabled();
        maxLength = config.getMaxLength();
        allowedMaterials = config.getAllowedMaterials();
    }

    public void onSignPowerOn(SignMechanicEventData data) {
        if (enabled) {
            open(data);
        }
    }

    public void onSignPowerOff(SignMechanicEventData data) {
        if (enabled) {
            close(data);
        }
    }

    public void onPlayerRightClickSign(Player player, SignMechanicEventData data) {
        if (enabled) {
            if (isClosed(data)) {
                open(data);
            } else {
                close(data);
            }
        }
    }

    @Override
    public boolean hasBlockMapper() {
        return true;
    }

    @Override
    public boolean hasBlockBag() {
        return true;
    }

    public List<String> getIdentifier() {
        return Arrays.asList("[Bridge]", "[sBridge]", "[Bridge End]");
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return true;
    }

    public List<Material> getMechanicActivator() {
        return null;
    }

    public BlockMap mapBlocks(Sign sign) throws BlockMapException{
        BlockFace orientation = SignUtil.getOrdinalAttachedFace(sign);
        boolean foundOtherSide = false;
        Block otherSide = null;
        Material bridgeMaterial;
        List<Block> blockList = new ArrayList<Block>();
        
        if (orientation != null) {
            int travelDistance = maxLength;
            while (travelDistance > 0) {
                travelDistance--;
                otherSide = sign.getBlock().getRelative(orientation, maxLength - travelDistance);
                if (otherSide.getState() instanceof Sign && getIdentifier().contains(((Sign) otherSide.getState()).getLine(1))) {
                    foundOtherSide = true;
                    break;
                }
            }
            if (foundOtherSide) {
                boolean isNorthSouth = (orientation.equals(BlockFace.NORTH) || orientation.equals(BlockFace.SOUTH));
                travelDistance = maxLength - travelDistance - 1;
                if (allowedMaterials.contains(otherSide.getRelative(BlockFace.UP).getType())) {
                    otherSide = otherSide.getRelative(BlockFace.UP);
                    bridgeMaterial = otherSide.getType();
                } else if (allowedMaterials.contains(otherSide.getRelative(BlockFace.DOWN).getType())) {
                    otherSide = otherSide.getRelative(BlockFace.DOWN);
                    bridgeMaterial = otherSide.getType();
                } else {
                    throw new BlockMapException(BlockMapException.Type.NON_ALLOWED_MATERIAL);
                }
                for (; travelDistance > 0; travelDistance--) {
                    otherSide = sign.getBlock().getRelative(orientation, travelDistance);
                    blockList.add(otherSide);
                    if (isNorthSouth) {
                        blockList.add(otherSide.getRelative(BlockFace.NORTH));
                        blockList.add(otherSide.getRelative(BlockFace.SOUTH));
                    } else {
                        blockList.add(otherSide.getRelative(BlockFace.EAST));
                        blockList.add(otherSide.getRelative(BlockFace.WEST));
                    }
                }
                return new BlockMap(blockList,sign.getBlock(), otherSide, bridgeMaterial);
            } else {
                throw new BlockMapException(BlockMapException.Type.END_NOT_FOUND);
            }
        } else {
            throw new BlockMapException(BlockMapException.Type.NON_ORDINAL_SIGN);
        }
    }

    private boolean isClosed(SignMechanicEventData data) {
        Material material = data.getBlockMap().getMaterial();
        for (Block b : data.getBlockMap().getList()) {
            if (b.getType() == Material.AIR) {
                return false;
            } else if (b.getType() == material) {
                return true;
            }
        }
        return true;
    }

    private int close(SignMechanicEventData data) {
        int i = 0;
        Material material = data.getBlockMap().getMaterial();
        for (Block b : data.getBlockMap().getList()) {
            if (b.getType() == Material.AIR) {
                b.setType(material);
            }
        }
        BetterMechanics.log("Changed " + Integer.toString(i) + " blocks while closing gate.", Level.FINEST);
        return i;
    }

    private int open(SignMechanicEventData data) {
        int i = 0;
        Material material = data.getBlockMap().getMaterial();
        for (Block b : data.getBlockMap().getList()) {
            if (b.getType() == material) {
                b.setType(Material.AIR);
                i++;
            }
        }
        BetterMechanics.log("Changed " + Integer.toString(i) + " blocks while opening gate.", Level.FINEST);
        return i;
    }

    @Override
    public String getName() {
        return "Bridge";
    }
}
