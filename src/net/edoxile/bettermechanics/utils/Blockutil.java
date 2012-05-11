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

package net.edoxile.bettermechanics.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class BlockUtil {
    public static Block getUpperBlock(Block block) {
        while (block.getRelative(BlockFace.UP) != null
                && block.getRelative(BlockFace.UP).getTypeId() == block.getTypeId()
                && block.getRelative(BlockFace.UP).getData() == block.getData()) {
            block = block.getRelative(BlockFace.UP);
        }
        return block;
    }

    public static Block getTypeInColumn(Block start, Set<Material> materials, boolean highest) {
        for (int dy = -1; dy < 2; dy++) {
            if (materials.contains(start.getRelative(0, dy, 0).getType())) {
                if (highest)
                    return getUpperBlock(start.getRelative(0, dy, 0));
                else
                    return start.getRelative(0, dy, 0);
            }
        }
        return null;
    }

    public static Block getTypeInColumn(Block start, Material material, boolean highest) {
        for (int dy = -1; dy < 2; dy++) {
            if (material == start.getRelative(0, dy, 0).getType()) {
                if (highest)
                    return getUpperBlock(start.getRelative(0, dy, 0));
                else
                    return start.getRelative(0, dy, 0);
            }
        }
        return null;
    }

    public static Block locateNearestBlock(Block start, Material type, int distance) {
        for (int dy = 0; dy <= distance; dy++) {
            for (int dx = 0; dx <= distance; dx++) {
                for (int dz = 0; dz <= distance; dz++) {
                    if (dy == 0 && dx == 0 && dz == 0) {
                        continue;
                    }
                    if (dy == 0) {
                        if (start.getRelative(dx, dy, dz).getType() == type) {
                            return start.getRelative(dx, dy, dz);
                        }
                        if (dx != 0 && start.getRelative(dx, dy, dz).getType() == type) {
                            return start.getRelative(dx, dy, dz);
                        }
                        if (dz != 0 && start.getRelative(dx, dy, -dz).getType() == type) {
                            return start.getRelative(dx, dy, dz);
                        }
                        if (dx != 0 && dz != 0 && start.getRelative(-dx, dy, -dz).getType() == type) {
                            return start.getRelative(dx, dy, dz);
                        }
                    } else {
                        if (start.getRelative(dx, dy, dz).getType() == type) {
                            return start.getRelative(dx, dy, dz);
                        }
                        if (start.getRelative(dx, -dy, dz).getType() == type) {
                            return start.getRelative(dx, dy, dz);
                        }
                        if (dx != 0) {
                            if (start.getRelative(-dx, dy, dz).getType() == type) {
                                return start.getRelative(dx, dy, dz);
                            }
                            if (start.getRelative(-dx, -dy, dz).getType() == type) {
                                return start.getRelative(dx, dy, dz);
                            }
                        }
                        if (dz != 0) {
                            if (start.getRelative(dx, dy, -dz).getType() == type) {
                                return start.getRelative(dx, dy, dz);
                            }
                            if (start.getRelative(dx, -dy, -dz).getType() == type) {
                                return start.getRelative(dx, dy, dz);
                            }
                        }
                        if (dx != 0 && dz != 0) {
                            if (start.getRelative(-dx, dy, -dz).getType() == type) {
                                return start.getRelative(dx, dy, dz);
                            }
                            if (start.getRelative(-dx, -dy, -dz).getType() == type) {
                                return start.getRelative(dx, dy, dz);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean blocksEqual(Material type, byte data, Block... blocks) {
        for (Block block : blocks) {
            if (block.getType() != type || block.getData() != data) {
                return false;
            }
        }
        return true;
    }
}
