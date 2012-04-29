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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.utils.MechanicsConfig;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Cycler {
    public static boolean cycle(Player p, Block b, MechanicsConfig c) {
        if (c.getPermissionConfig().checkZonesCreate(p, b)) {
            byte newByte = (byte) (b.getData() + 1);
            newByte = (newByte == 6) ? 2 : newByte;
            b.setData(newByte);
            return true;
        } else {
            return false;
        }
    }
}
