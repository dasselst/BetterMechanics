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

package net.edoxile.bettermechanics.listeners;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.utils.MechanicsHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class BMBlockListener extends BlockListener {
    private BetterMechanics plugin;
    private MechanicsHandler mechanicsHandler;

    public BMBlockListener(BetterMechanics bm) {
        plugin = bm;
        mechanicsHandler = bm.getMechanicsHandler();
    }

    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == event.getOldCurrent() || event.getNewCurrent() > 0 && event.getOldCurrent() > 0)
            return;
        mechanicsHandler.callRedstoneEvent(event);
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        mechanicsHandler.callBlockEvent(event);
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        mechanicsHandler.callBlockEvent(event);
    }
}
