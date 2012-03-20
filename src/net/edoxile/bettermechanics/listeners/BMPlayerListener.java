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
import net.edoxile.bettermechanics.event.Event;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.handlers.MechanicsHandler;
import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */

public class BMPlayerListener implements Listener {
    private BetterMechanics plugin;
    private MechanicsHandler mechanicsHandler;

    public BMPlayerListener(BetterMechanics bm) {
        plugin = bm;
        mechanicsHandler = bm.getMechanicsHandler();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;

        if (event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            PlayerEvent playerEvent = new PlayerEvent(
                    SignUtil.isSign(event.getClickedBlock()) ? Event.Type.SIGN : Event.Type.BLOCK,
                    event.getClickedBlock(),
                    event.getAction() == Action.LEFT_CLICK_BLOCK ? PlayerEvent.Action.LEFT_CLICK : PlayerEvent.Action.RIGHT_CLICK,
                    event.getPlayer()
            );

            mechanicsHandler.callPlayerEvent(playerEvent);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        PlayerEvent playerEvent = new PlayerEvent(Event.Type.BLOCK, event.getBlock(), PlayerEvent.Action.BREAK, event.getPlayer());
        mechanicsHandler.callPlayerEvent(playerEvent);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event){
        if (event.isCancelled())
            return;

        PlayerEvent playerEvent = new PlayerEvent(Event.Type.BLOCK, event.getBlock(), PlayerEvent.Action.PLACE, event.getPlayer());
        mechanicsHandler.callPlayerEvent(playerEvent);
    }
}
