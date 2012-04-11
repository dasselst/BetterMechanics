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

package net.edoxile.bettermechanics.handlers;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class InventoryHandler {

    public static boolean addContents(Inventory iTo, ItemStack stack, int amount) {
        if (stack == null || iTo == null) {
            return false;
        }
        ItemStack[] t = iTo.getContents();
        int leftover;
        for (int i = 0; i < t.length; i++) {
            if (amount > 64) {
                stack.setAmount(64);
                amount -= 64;
            } else if (amount > 0) {
                stack.setAmount(amount);
                amount = 0;
            } else {
                break;
            }
            ItemStack toStack = t[i];
            if (toStack == null) {
                t[i] = stack;
                leftover = 0;
            } else if (toStack.getTypeId() == stack.getTypeId() && toStack.getDurability() == stack.getDurability() && (toStack.getEnchantments().isEmpty() && stack.getEnchantments().isEmpty())) {
                if ((toStack.getAmount() + stack.getAmount()) > 64) {
                    leftover = (toStack.getAmount() + stack.getAmount()) - 64;
                    t[i].setAmount(64);
                } else {
                    leftover = 0;
                    t[i].setAmount(64);
                }
            } else {
                continue;
            }
            amount += leftover;
        }
        if (amount == 0) {
            iTo.setContents(t);
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeContents(Inventory iFrom, ItemStack stack, int amount) {
        return removeContents(iFrom, stack.getTypeId(), (byte) stack.getDurability(), amount);
    }

    public static boolean addContents(Inventory iTo, int id, byte data, int amount) {
        ItemStack stack = new ItemStack(id, 1, data);
        return addContents(iTo, stack, amount);
    }

    public static boolean removeContents(Inventory iFrom, int id, byte data, int amount) {
        if (Material.getMaterial(id) == null || iFrom == null) {
            return false;
        }
        ItemStack[] f = iFrom.getContents();
        for (int i = 0; i < f.length; i++) {
            if (amount == 0) {
                break;
            }
            ItemStack fromStack = f[i];
            if (fromStack != null && fromStack.getTypeId() == id && fromStack.getDurability() == data && fromStack.getEnchantments().isEmpty()) {
                if (fromStack.getAmount() >= amount) {
                    f[i].setAmount(fromStack.getAmount() - amount);
                    amount = 0;
                } else {
                    amount -= fromStack.getAmount();
                    f[i] = null;
                }
            }
        }
        if (amount == 0) {
            iFrom.setContents(f);
            return true;
        } else {
            return false;
        }
    }
}
