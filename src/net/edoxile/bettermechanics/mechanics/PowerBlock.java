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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanicListener;
import org.bukkit.Material;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class PowerBlock extends BlockMechanicListener {

    private ConfigHandler.PowerBlockConfig config = BetterMechanics.getInstance().getConfigHandler().getPowerBlockConfig();

    @Override
    public boolean isTriggeredByRedstone() {
        return true;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return false;
    }

    @Override
    public Material[] getMechanicActivators() {
        return null;
    }

    @Override
    public Material[] getMechanicTargets() {
        return voidActor;
    }

    @Override
    public String getName() {
        return "PowerBlock";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }
}
