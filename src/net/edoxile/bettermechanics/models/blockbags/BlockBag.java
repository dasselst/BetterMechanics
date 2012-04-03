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

package net.edoxile.bettermechanics.models.blockbags;

import net.edoxile.bettermechanics.BetterMechanics;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class BlockBag {

    public enum Direction {
        SOURCE,
        SINK,
        ANY
    }

    public void removeItems(ItemStack itemStack) throws BlockBagException {
        removeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), itemStack.getAmount());
    }

    public void removeItems(ItemStack itemStack, int amount) throws BlockBagException {
        removeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), amount);
    }

    public void removeItems(int id, int amount) throws BlockBagException {
        removeItems(id, (byte) 0, amount);
    }

    public void removeItems(int id, byte data, int amount) throws BlockBagException {
        BetterMechanics.log("Method 'storeItems' was called but not overwritten", Level.WARNING);
        throw new BlockBagException(BlockBagException.Type.NOT_ENOUGH_MATERIALS);
    }

    public void storeItems(ItemStack itemStack) throws BlockBagException {
        storeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), itemStack.getAmount());
    }

    public void storeItems(ItemStack itemStack, int amount) throws BlockBagException {
        storeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), amount);
    }

    public void storeItems(int id, int amount) throws BlockBagException {
        storeItems(id, (byte) 0, amount);
    }

    public void storeItems(int id, byte data, int amount) throws BlockBagException {
        BetterMechanics.log("Method 'storeItems' was called but not overwritten", Level.WARNING);
        throw new BlockBagException(BlockBagException.Type.NO_SPACE_LEFT);
    }

    public abstract Direction getDirection();
}
