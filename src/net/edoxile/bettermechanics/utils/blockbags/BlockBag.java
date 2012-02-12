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

package net.edoxile.bettermechanics.utils.blockbags;

import net.edoxile.bettermechanics.BetterMechanics;
import org.bukkit.block.Sign;
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

    public boolean removeItems(ItemStack itemStack) {
        return removeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), itemStack.getAmount());
    }

    public boolean removeItems(ItemStack itemStack, int amount) {
        return removeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), amount);
    }

    public boolean removeItems(int id, int amount) {
        return removeItems(id, (byte) 0, amount);
    }

    public boolean removeItems(int id, byte data, int amount) {
        BetterMechanics.log("Method 'storeItems' was called but not overwritten", Level.WARNING);
        return false;
    }

    public boolean storeItems(ItemStack itemStack) {
        return storeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), itemStack.getAmount());
    }

    public boolean storeItems(ItemStack itemStack, int amount) {
        return storeItems(itemStack.getTypeId(), (byte) itemStack.getDurability(), amount);
    }

    public boolean storeItems(int id, int amount) {
        return storeItems(id, (byte) 0, amount);
    }

    public boolean storeItems(int id, byte data, int amount) {
        BetterMechanics.log("Method 'storeItems' was called but not overwritten", Level.WARNING);
        return false;
    }

    public abstract boolean searchStorage(Sign sign);

    public abstract Direction getDirection();
}
