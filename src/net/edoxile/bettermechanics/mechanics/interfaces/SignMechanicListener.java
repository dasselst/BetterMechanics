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

package net.edoxile.bettermechanics.mechanics.interfaces;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.BlockBagHandler;
import net.edoxile.bettermechanics.models.blockbags.BlockBagException;
import net.edoxile.bettermechanics.utils.PlayerNotifier;
import net.edoxile.bettermechanics.utils.SignUtil;
import net.edoxile.bettermechanics.utils.datastorage.BlockMap;
import net.edoxile.bettermechanics.utils.datastorage.BlockMapException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class SignMechanicListener extends BlockMechanicListener {

    protected String[] voidTarget = new String[]{""};

    protected BlockMap blockMap;
    protected BlockBagHandler blockBag;

    @Deprecated
    public BlockMap getBlockMap() {
        return blockMap;
    }

    @Deprecated
    public BlockBagHandler getBlockBag() {
        return blockBag;
    }

    public void onSignPowerOn(RedstoneEvent event) {
    }

    public void onSignPowerOff(RedstoneEvent event) {
    }

    public void onPlayerRightClickSign(PlayerEvent event) {
    }

    public void onPlayerLeftClickSign(PlayerEvent event) {
    }

    public abstract boolean hasBlockMapper();

    public abstract boolean hasBlockBag();

    public void mapBlocks(Sign s) throws BlockMapException {
        BetterMechanics.log("BlockMapper called but not implemented in " + getName() + ".", Level.WARNING);
        throw new BlockMapException(BlockMapException.Type.NO_BLOCKMAP);
    }

    public abstract String[] getIdentifiers();

    public abstract String[] getPassiveIdentifiers();

    @Override
    public abstract Material[] getMechanicActivators();

    public boolean isThisMechanic(Sign sign) {
        return isThisMechanic(sign, false);
    }

    private boolean isThisMechanic(Sign sign, boolean passive) {
        String id = SignUtil.getMechanicsIdentifier(sign);
        if (getIdentifiers() == null)
            return true;

        for (String s : getIdentifiers()) {
            if (s.equals(id))
                return true;
        }
        if (passive) {
            for (String s : getPassiveIdentifiers()) {
                if (s.equals(id))
                    return true;
            }
        }
        return false;
    }

    @Override
    public Material[] getMechanicTargets() {
        return new Material[]{Material.WALL_SIGN, Material.SIGN_POST};
    }

    protected void loadData(Sign sign) throws PlayerNotifier {
        if (sign == null) {
            BetterMechanics.log("A RedstoneEvent was thrown to a SignMechanic, but no Sign was passed.", Level.WARNING);
            return;
        }

        if (hasBlockMapper()) {
            try {
                mapBlocks(sign);
            } catch (BlockMapException e) {
                throw new PlayerNotifier(e.getMessage(), PlayerNotifier.Level.SEVERE, sign.getLocation());
            }
        }

        if (hasBlockBag()) {
            try {
                blockBag = BlockBagHandler.locate(sign);
            } catch (BlockBagException e) {
                throw new PlayerNotifier(e.getMessage(), PlayerNotifier.Level.SEVERE, sign.getLocation());
            }
        }
    }

    protected void open() throws PlayerNotifier {
        if (hasBlockMapper()) {
            int changed = 0;
            for (Block b : blockMap.getSet()) {
                if (b.getTypeId() == blockMap.getMaterial().getId() && b.getData() == blockMap.getMaterialData()) {
                    b.setType(Material.AIR);
                    changed++;
                }
            }
            if (hasBlockBag() && changed > 0) {
                if (!blockBag.storeItems(blockMap.getMaterial().getId(), blockMap.getMaterialData(), changed)) {
                    PlayerNotifier playerNotifier = new PlayerNotifier(
                            "There's not enough space left in the chest. " + changed + " empty spots needed.",
                            PlayerNotifier.Level.WARNING,
                            blockBag.getLocation(true)
                    );
                    for (Block b : blockMap.getSet()) {
                        if (b.getTypeId() == Material.AIR.getId()) {
                            b.setTypeIdAndData(blockMap.getMaterial().getId(), blockMap.getMaterialData(), false);
                            changed--;
                            if (changed == 0) {
                                break;
                            }
                        }
                    }
                    throw playerNotifier;
                }
            }
        }
    }

    protected void close() throws PlayerNotifier {
        if (hasBlockMapper()) {
            int changed = 0;
            for (Block b : blockMap.getSet()) {
                if (b.getTypeId() == Material.AIR.getId()) {
                    b.setTypeIdAndData(blockMap.getMaterial().getId(), blockMap.getMaterialData(), false);
                    changed++;
                }
            }
            if (hasBlockBag() && changed > 0) {
                if (!blockBag.storeItems(blockMap.getMaterial().getId(), blockMap.getMaterialData(), changed)) {
                    PlayerNotifier playerNotifier = new PlayerNotifier(
                            "There are not enough items in the chest. Still need " + changed + " of type " + blockMap.getMaterial().name() + ".",
                            PlayerNotifier.Level.WARNING,
                            blockBag.getLocation(true)
                    );
                    for (Block b : blockMap.getSet()) {
                        if (b.getTypeId() == blockMap.getMaterial().getId() && b.getData() == blockMap.getMaterialData()) {
                            b.setType(Material.AIR);
                            changed--;
                            if (changed == 0) {
                                break;
                            }
                        }
                    }
                    throw playerNotifier;
                }
            }
        }
    }

    protected boolean isOpen() throws PlayerNotifier{
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
