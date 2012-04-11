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

import net.edoxile.bettermechanics.utils.BlockMapper;
import net.edoxile.bettermechanics.utils.MechanicsConfig;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class HiddenSwitch {
    private Sign sign;
    private Player player;
    private MechanicsConfig.HiddenSwitchConfig config;
    private HashSet<Block> levers;

    public HiddenSwitch(MechanicsConfig c, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getHiddenSwitchConfig();
    }

    public boolean map() {
        if (!config.enabled)
            return false;
        // levers = BlockMapper.mapAllInCuboidRegion(sign.getBlock(), 1, Material.LEVER);
        levers = BlockMapper.mapHiddenSwitch(sign.getBlock());
        return (!levers.isEmpty());
    }

    public void toggleLevers() {
        for (Block b : levers) {
            b.setData((byte) (b.getData() ^ 0x8));
        }
    }
}
