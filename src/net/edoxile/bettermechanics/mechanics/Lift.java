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
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import org.bukkit.Material;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Lift extends SignMechanicListener {
    private final String[] identifiers = new String[]{"[Lift Up]","[Lift Down]","[TeleLift]"};
    private final String[] passiveIdentifiers = new String[]{"[Lift]"};

    private ConfigHandler.LiftConfig config = BetterMechanics.getInstance().getConfigHandler().getLiftConfig();

    @Override
    public boolean hasBlockMapper() {
        return false;
    }

    @Override
    public boolean hasBlockBag() {
        return false;
    }

    @Override
    public String[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String[] getPassiveIdentifiers() {
        return passiveIdentifiers;
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return false;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public Material[] getMechanicActivators() {
        return voidActor;
    }

    @Override
    public String getName() {
        return "Lift";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }
}
