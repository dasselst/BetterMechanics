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

package net.edoxile.bettermechanics.mechanics.interfaces;

import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import org.bukkit.Material;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class BlockMechanicListener implements IMechanicListener {

    public void onBlockRightClick(PlayerEvent event) {
    }

    public void onBlockLeftClick(PlayerEvent event) {
    }

    public void onBlockPlace(PlayerEvent event) {
    }

    public void onBlockBreak(PlayerEvent event) {
    }

    public void onBlockPowerOn(RedstoneEvent event) {
    }

    public void onBlockPowerOff(RedstoneEvent event) {
    }

    public abstract boolean isTriggeredByRedstone();

    public abstract boolean isTriggeredByPlayer();

    public abstract List<Material> getMechanicActivators();

    public abstract List<Material> getMechanicTargets();
}
