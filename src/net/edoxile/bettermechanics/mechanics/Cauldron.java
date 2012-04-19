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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanicListener;
import net.edoxile.bettermechanics.models.CauldronCookbook;
import net.edoxile.bettermechanics.utils.datastorage.MaterialMap;
import net.edoxile.bettermechanics.utils.datastorage.MaterialMapIterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Cauldron extends BlockMechanicListener {

    @Override
    public void onBlockRightClick(PlayerEvent event) {
        Block block = event.getBlock();
        int dx, dy = -1, dz;
        if (isLava(block.getRelative(0, -1, 0))) {
            dy = 0;
        } else if (isLava(block.getRelative(0, -2, 0))) {
            dy = -1;
        } else if (isLava(block.getRelative(0, -3, 0))) {
            dy = -2;
        } else if (isLava(block.getRelative(0, -4, 0))) {
            dy = -3;
        }
        if (dy > -1) {
            if (block.getRelative(BlockFace.WEST).getType() == Material.STONE && block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getType() == Material.STONE) {
                dz = -1;
            } else if (block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getType() == Material.STONE && block.getRelative(BlockFace.EAST).getType() == Material.STONE) {
                dz = 0;
            } else {
                return;
            }
            if (block.getRelative(BlockFace.NORTH).getType() == Material.STONE && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getType() == Material.STONE) {
                dx = 0;
            } else if (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getType() == Material.STONE && block.getRelative(BlockFace.SOUTH).getType() == Material.STONE) {
                dx = -1;
            } else {
                return;
            }

            MaterialMap map = new MaterialMap();
            Block referencePoint = block.getRelative(dx, dy, dz);
            for (int ndy = 0; ndy <= 3; ndy++) {
                for (int ndx = 0; ndx <= 1; ndx++) {
                    for (int ndz = 0; ndz <= 1; ndz++) {
                        if (referencePoint.getRelative(ndx, ndy, ndz).getType() != Material.AIR) {
                            Block temp = referencePoint.getRelative(ndx, ndy, ndz);
                            map.add(temp.getTypeId(), temp.getData(), 1);
                        }
                    }
                }
            }
            CauldronCookbook cookbook = new CauldronCookbook();
            CauldronCookbook.Recipe recipe = cookbook.find(map);
            if(recipe!=null) {
                MaterialMapIterator iterator = recipe.getResults().iterator();
                do {
                    iterator.next();
                    HashMap<Integer, ItemStack> returnData = event.getPlayer().getInventory().addItem(new ItemStack(iterator.id(), iterator.value(), iterator.data()));
                    for(ItemStack stack : returnData.values()){
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), stack);
                    }
                    //TODO: check spelling
                    event.getPlayer().sendMessage(ChatColor.GOLD + "In a proof of smoke you've made " +  recipe.getName());
                } while(iterator.hasNext());
            } else {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "The cauldron doesn't contain any known recipes.");
            }
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
    public Material[] getMechanicActivators() {
        return null;
    }

    @Override
    public Material[] getMechanicTargets() {
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
