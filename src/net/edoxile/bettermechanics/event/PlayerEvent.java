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

package net.edoxile.bettermechanics.event;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class PlayerEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public enum Action {
        RIGHT_CLICK,
        LEFT_CLICK,
        PLACE,
        BREAK
    }

    private final Player player;
    private final Action action;

    public PlayerEvent(Block b, Action a, Player p, org.bukkit.event.Event e) {
        super(b, e);
        player = p;
        action = a;
    }

    public PlayerEvent(Sign s, Action a, Player p, org.bukkit.event.Event e){
        super(s, e);
        player = p;
        action = a;
    }

    public Player getPlayer() {
        return player;
    }

    public Action getAction() {
        return action;
    }
}
