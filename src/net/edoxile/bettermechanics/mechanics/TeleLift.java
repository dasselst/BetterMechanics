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

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class TeleLift extends Lift {

    private final String[] identifiers = new String[]{"[TeleLift]"};
    private ConfigHandler.TeleLiftConfig config = BetterMechanics.getInstance().getConfigHandler().getTeleLiftConfig();

    @Override
    public String[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String getName() {
        return "TeleLift";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        BlockFace backSide = SignUtil.getAttachedFace(event.getSign());
        Block block = event.getBlock().getRelative(backSide).getRelative(backSide);
        if (SignUtil.isWallSign(block)) {
            Sign sign = SignUtil.getSign(block);
            Block to = parseLocation(sign);
            if (to == null)
                to = parseLocation(event.getSign());
            if (to != null)
                movePlayer(to, event.getPlayer());
            else
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "Invalid syntax!");
        }
    }

    private Block parseLocation(Sign s) {
        String[] data = s.getLine(2).split(":");
        if (data.length == 3) {
            try {
                int x = Integer.parseInt(data[0]);
                int z = Integer.parseInt(data[1]);
                int y = Integer.parseInt(data[2]);
                if (y < 0)
                    return null;
                return s.getWorld().getBlockAt(x, y, z);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
