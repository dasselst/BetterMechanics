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

package net.edoxile.bettermechanics.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class InventoryManager {
    public static ItemStack[] putContents(Inventory iTo, ItemStack... sFrom) {
        ItemStack[] sTo = iTo.getContents();
        fromLoop:
        for (int i = 0; i < sFrom.length; i++) {
            ItemStack fStack = sFrom[i];
            if (fStack == null) {
                continue;
            } else {
                toLoop:
                for (int j = 0; j < sTo.length; j++) {
                    ItemStack tStack = sTo[j];
                    if (tStack == null) {
                        sTo[j] = fStack;
                        sFrom[i] = null;
                        continue fromLoop;
                    } else if (fStack.getTypeId() == tStack.getTypeId() && fStack.getDurability() == tStack.getDurability() && tStack.getEnchantments().isEmpty()) {
                        int total = fStack.getAmount() + tStack.getAmount();
                        if (total > 64) {
                            tStack.setAmount(64);
                            fStack.setAmount(total - 64);
                        } else {
                            tStack.setAmount(total);
                            int remainder = total - 64;
                            if (remainder == 0) {
                                sFrom[i] = null;
                                sTo[j] = tStack;
                                continue fromLoop;
                            } else {
                                fStack.setAmount(remainder);
                            }
                        }
                    } else {
                        continue;
                    }
                    sTo[j] = tStack;
                }
            }
            sFrom[i] = fStack;
        }
        return sFrom;
    }
}
