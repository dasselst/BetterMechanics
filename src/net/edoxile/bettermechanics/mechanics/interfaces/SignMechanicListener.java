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

package net.edoxile.bettermechanics.mechanics.interfaces;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.models.blockbags.BlockBag;
import net.edoxile.bettermechanics.utils.datastorage.BlockMap;
import net.edoxile.bettermechanics.utils.datastorage.BlockMapException;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class SignMechanicListener extends BlockMechanicListener {
    
    private BlockMap blockMap;
    private BlockBag blockBag;
    
    public BlockMap getBlockMap(){
        return blockMap;
    }
    
    public BlockBag getBlockBag(){
        return blockBag;
    }
    
    protected void setBlockMap(BlockMap map){
        blockMap = map;
    }
    
    protected void setBlockBag(BlockBag bag){
        blockBag = bag;
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
        BetterMechanics.log("BlockMapper called but not implemented in this mechanic.", Level.WARNING);
        throw new BlockMapException(BlockMapException.Type.NO_BLOCKMAP);
    }

    public abstract List<String> getIdentifier();

    public abstract List<Material> getMechanicActivator();

    public List<Material> getMechanicTarget() {
        return Arrays.asList(Material.WALL_SIGN, Material.SIGN_POST);
    }
}
