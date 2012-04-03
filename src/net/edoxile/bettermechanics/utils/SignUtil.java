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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import static net.edoxile.bettermechanics.utils.StringUtil.stripBrackets;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class SignUtil {

    //TODO: lolwhut, this has to be simplified
    public static BlockFace getFacing(Sign sign) {
        BlockState state = sign.getBlock().getState();
        if (state instanceof org.bukkit.material.Sign) {
            org.bukkit.material.Sign s = (org.bukkit.material.Sign) state;
            return s.getFacing();
        } else {
            return BlockFace.SELF;
        }
    }

    public static BlockFace getAttachedFace(Sign sign) {
        BlockState state = sign.getBlock().getState();
        if (state instanceof org.bukkit.material.Sign) {
            org.bukkit.material.Sign s = (org.bukkit.material.Sign) state;
            return s.getAttachedFace();
        } else {
            return BlockFace.SELF;
        }
    }

    public static BlockFace getOrdinalFacing(Sign sign) {
        BlockFace blockFace = getFacing(sign);
        switch (blockFace) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                return blockFace;
            default:
                return null;
        }
    }

    public static BlockFace getOrdinalAttachedFace(Sign sign) {
        BlockFace blockFace = getAttachedFace(sign);
        switch (blockFace) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                return blockFace;
            default:
                return null;
        }
    }

    public static boolean isSign(Block block) {
        return block.getTypeId() == Material.WALL_SIGN.getId() || block.getTypeId() == Material.SIGN_POST.getId();
    }

    public static String getMechanicsIdentifier(Sign sign) {
        return stripBrackets(sign.getLine(1));
    }

    public static void setMechanicsIdentifier(Sign sign, String identifier) {
        sign.setLine(1, identifier);
    }

    public static Sign getSign(Block block) {
        if (block.getState() instanceof Sign) {
            return (Sign) block.getState();
        } else {
            return null;
        }
    }
}
