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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Gate extends SignMechanicListener {

    public Gate() {
        ConfigHandler.GateConfig config = ConfigHandler.getInstance().getGateConfig();
    }

    @Override
    public void onSignPowerOn(RedstoneEvent event) {
        //Close gate
    }

    @Override
    public void onSignPowerOff(RedstoneEvent event) {
        //Open gate
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        //Toggle gate
    }

    @Override
    public List<String> getIdentifier() {
        return Arrays.asList("[Gate]", "[dGate]", "[sGate]");
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return true;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public boolean hasBlockMapper() {
        return true;
    }

    @Override
    public boolean hasBlockBag() {
        return true;
    }

    @Override
    public List<Material> getMechanicActivator() {
        return null;
    }

    @Override
    public String getName() {
        return "Gate";
    }
}
