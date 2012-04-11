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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Ammeter {
    private Block wire;
    private Player player;
    private MechanicsConfig.AmmeterConfig config;

    public Ammeter(MechanicsConfig c, Block w, Player p) {
        wire = w;
        player = p;
        config = c.getAmmeterConfig();
    }

    public void measure() {
        if (!config.enabled)
            return;
        if (wire.getType() == Material.REDSTONE_WIRE) {
            String msg = "Current is: [" + ChatColor.GREEN;
            for (byte i = 0; i < wire.getData(); i++) {
                msg += "|";
            }
            msg += ChatColor.DARK_RED;
            for (byte i = wire.getData(); i < 15; i++) {
                msg += "|";
            }
            msg += ChatColor.WHITE + "] " + ChatColor.RED + Byte.toString(wire.getData());
            player.sendMessage(msg);
        }
    }
}
