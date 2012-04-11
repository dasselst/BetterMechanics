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

import net.edoxile.bettermechanics.utils.BlockBags.BlackHoleSign;
import net.edoxile.bettermechanics.utils.BlockBags.BlockSourceSign;
import net.edoxile.bettermechanics.utils.BlockBags.ChestBag;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GuntherDW
 */
public class BlockBagManager {

    public Set<BlockBag> sources;
    public Set<BlockBag> holes;
    private MechanicsConfig.BlockBagConfig config;

    public BlockBagManager(MechanicsConfig c) {
        this.config = c.getBlockBagConfig();
        sources = new HashSet<BlockBag>();
        holes = new HashSet<BlockBag>();
        registerBlockBag(new ChestBag(null));
        registerBlockBag(new BlockSourceSign(null));
        registerBlockBag(new BlackHoleSign(null));
    }

    public void registerBlockBag(BlockBag blockBag) {
        if (blockBag.isBlockHole())
            holes.add(blockBag);

        if (blockBag.isBlockSource())
            sources.add(blockBag);
    }

    public BlockBag isSuitableBlockBag(Block block, boolean source, boolean hole) {

        if (source) {
            for (BlockBag bagInstance : sources) {
                if (bagInstance.isBlockBag(block))
                    return bagInstance;
            }
        }

        if (hole) {
            for (BlockBag bagInstance : holes) {
                if (bagInstance.isBlockBag(block))
                    return bagInstance;
            }
        }

        return null;
    }

    public BlockBag searchBlockBag(Block block, boolean source, boolean hole) {
        int sw = config.searchWidth;
        Block start = block;

        for (int dy = 0; dy <= sw; dy++) {
            for (int dx = 0; dx <= sw; dx++) {
                for (int dz = 0; dz <= sw; dz++) {
                    HashSet<Block> blockSet = new HashSet<Block>();
                    blockSet.add(start.getRelative(dx, dy, dz));
                    blockSet.add(start.getRelative(-dx, dy, dz));
                    blockSet.add(start.getRelative(dx, dy, -dz));
                    blockSet.add(start.getRelative(-dx, dy, -dz));
                    blockSet.add(start.getRelative(dx, -dy, dz));
                    blockSet.add(start.getRelative(-dx, -dy, dz));
                    blockSet.add(start.getRelative(dx, -dy, -dz));
                    blockSet.add(start.getRelative(-dx, -dy, -dz));
                    for (Block b : blockSet) {
                        BlockBag suitable = this.isSuitableBlockBag(b, source, hole);
                        if (suitable != null) {
                            return suitable;
                        }
                    }
                }
            }
        }

        return null;
    }

}
