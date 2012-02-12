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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class IBlockMechanic implements IMechanic {

    public void onBlockRightClick(Player player, Block block) {
    }

    public void onBlockLeftClick(Player player, Block block) {
    }

    public void onBlockPlace(Player player, Block block) {
    }

    public void onBlockBreak(Player player, Block block) {
    }

    public void onBlockPowerOn(Block block) {
    }

    public void onBlockPowerOff(Block block) {
    }
    
    public boolean mapBlocks(Sign s){
        BetterMechanics.log("BlockMapper called but not implemented in this mechanic.", Level.WARNING);
        return false;
    }

    public abstract boolean isTriggeredByRedstone();
    
    public abstract boolean hasBlockMapper();

    public abstract Material[] getMechanicActivator();

    public abstract Material[] getMechanicTarget();
}
