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

package net.edoxile.bettermechanics.listeners;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.Event;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.MechanicsHandler;
import net.edoxile.bettermechanics.handlers.PermissionHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import net.edoxile.bettermechanics.models.Pair;
import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class BMListener implements Listener {
    private final MechanicsHandler mechanicsHandler;
    private final BlockFace[] blockFaces = new BlockFace[]{
            BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH
    };
    private HashMap<String,Pair<String>> idMap = new HashMap<String, Pair<String>>();

    public BMListener(BetterMechanics plugin) {
        mechanicsHandler = plugin.getMechanicsHandler();

        for (SignMechanicListener listener : mechanicsHandler.getSignMechanics()) {
            for (String identifier : listener.getIdentifiers()) {
                idMap.put(identifier.toLowerCase(), new Pair<String>(identifier, listener.getName()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == event.getOldCurrent() || event.getNewCurrent() > 0 && event.getOldCurrent() > 0)
            return;
        RedstoneEvent redstoneEvent = new RedstoneEvent(
                (event.getNewCurrent() > 0) ? RedstoneEvent.State.ON : RedstoneEvent.State.OFF, event
        );
        for (BlockFace blockFace : blockFaces) {
            Block block = event.getBlock().getRelative(blockFace);
            redstoneEvent.setData(
                    SignUtil.isSign(event.getBlock().getRelative(blockFace)) ? Event.Type.SIGN : Event.Type.BLOCK,
                    event.getBlock(),
                    blockFace.getOppositeFace()
            );
            mechanicsHandler.callRedstoneEvent(redstoneEvent);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;

        if (event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            PlayerEvent playerEvent;
            if (SignUtil.isSign(event.getClickedBlock())) {
                playerEvent = new PlayerEvent(
                        SignUtil.getSign(event.getClickedBlock()),
                        event.getAction() == Action.LEFT_CLICK_BLOCK ? PlayerEvent.Action.LEFT_CLICK : PlayerEvent.Action.RIGHT_CLICK,
                        event.getPlayer(),
                        event
                );
            } else {
                playerEvent = new PlayerEvent(
                        event.getClickedBlock(),
                        event.getAction() == Action.LEFT_CLICK_BLOCK ? PlayerEvent.Action.LEFT_CLICK : PlayerEvent.Action.RIGHT_CLICK,
                        event.getPlayer(),
                        event
                );
            }
            mechanicsHandler.callPlayerEvent(playerEvent);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        PlayerEvent playerEvent = new PlayerEvent(event.getBlock(), PlayerEvent.Action.BREAK, event.getPlayer(), event);
        mechanicsHandler.callPlayerEvent(playerEvent);
        event.setCancelled(playerEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        PlayerEvent playerEvent = new PlayerEvent(event.getBlock(), PlayerEvent.Action.PLACE, event.getPlayer(), event);
        mechanicsHandler.callPlayerEvent(playerEvent);
        event.setCancelled(playerEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        if (event.isCancelled())
            return;

        String line = event.getLine(1);
        Pair<String> data = idMap.get(line);
        if (data != null) {
            if (PermissionHandler.getInstance().playerHasNode(event.getPlayer(), data.getSecond()+".build")) {
                event.setLine(1, data.getFirst());
            } else {
                event.setCancelled(true);
            }
        }
    }
}
