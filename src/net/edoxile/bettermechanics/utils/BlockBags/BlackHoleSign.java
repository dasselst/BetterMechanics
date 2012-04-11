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

package net.edoxile.bettermechanics.utils.BlockBags;

import net.edoxile.bettermechanics.exceptions.OutOfMaterialException;
import net.edoxile.bettermechanics.exceptions.OutOfSpaceException;
import net.edoxile.bettermechanics.utils.BlockBag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Sign;

/**
 * @author GuntherDW
 */
public class BlackHoleSign extends BlockBag {

    private Sign sign = null;

    public BlackHoleSign(Block bag) {
        super(bag);
    }

    @Override
    public boolean safeAddItems(ItemStack itemStack) throws OutOfSpaceException {
        return true;
    }

    @Override
    public boolean isBlockHole() {
        return true;
    }

    @Override
    public boolean isRestricted() {
        return true;
    }

    public Material getSourceMaterial() {
        return Material.SIGN;
    }

    public boolean isBlockBag(Block block) {
        if ((block.getType().equals(Material.SIGN_POST)||block.getType().equals(Material.WALL_SIGN))
            && ((Sign)block.getState()).getLine(1).equals("[Black Hole]")) {
            sign = (Sign) block.getState();
            return true;
        }
        return false;
    }
}
