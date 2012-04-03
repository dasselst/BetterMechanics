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

import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanicListener;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Cauldron extends BlockMechanicListener {

    @Override
    public void onBlockRightClick(PlayerEvent event) {
        Block block = event.getBlock();
        //
        if (isLava(block.getRelative(0, -1, 0))) {
            //
        } else if (isLava(block.getRelative(0, -2, 0))) {
            //
        } else if (isLava(block.getRelative(0, -3, 0))) {
            //
        } else if (isLava(block.getRelative(0, -4, 0))) {
            //
        }
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return false;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public List<Material> getMechanicActivators() {
        return null;
    }

    @Override
    public List<Material> getMechanicTargets() {
        return null;
    }

    @Override
    public String getName() {
        return "Cauldron";
    }

    private boolean isLava(Block block) {
        return block.getTypeId() == Material.STATIONARY_LAVA.getId() || block.getTypeId() == Material.LAVA.getId();
    }
}
