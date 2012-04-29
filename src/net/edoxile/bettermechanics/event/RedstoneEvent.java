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
import org.bukkit.block.BlockFace;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class RedstoneEvent extends Event {
    public enum State {
        ON,
        OFF
    }

    private final State state;
    private BlockFace origin;

    public RedstoneEvent(State s, org.bukkit.event.Event e) {
        super(null, null, e);
        state = s;
    }

    public void setData(Type t, Block b, BlockFace o) {
        super.setData(t, b);
        origin = o;
    }

    public State getState() {
        return state;
    }

    public BlockFace getOrigin() {
        return origin;
    }
}
