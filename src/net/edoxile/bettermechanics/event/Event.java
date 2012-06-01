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

import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class Event {
    public enum Type {
        SIGN,
        BLOCK
    }

    private Type type;
    private Block block = null;
    private Sign sign = null;
    private String mechanicIdentifier = "Block";
    private org.bukkit.event.Event bukkitEvent;

    public Event(Block b, org.bukkit.event.Event bEvent) {
        type = Type.BLOCK;
        block = b;
        bukkitEvent = bEvent;
    }

    public Event(Sign s, org.bukkit.event.Event bEvent) {
        type = Type.SIGN;
        sign = s;
        block = sign.getBlock();
        mechanicIdentifier = SignUtil.getMechanicsIdentifier(sign);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }

    public Block getBlock() {
        return block;
    }

    public Sign getSign(){
        return sign;
    }

    public String getMechanicIdentifier(){
        return mechanicIdentifier;
    }

    public void setBlock(Block b) {
        block = b;
    }

    public void setData(Type t, Block b) {
        type = t;
        block = b;
    }

    public org.bukkit.event.Event getBukkitEvent() {
        return bukkitEvent;
    }
}
