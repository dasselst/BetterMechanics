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

package net.edoxile.bettermechanics.mechanics.interfaces;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class ISignMechanic extends IBlockMechanic {

    public void onSignPowerOn(Sign sign){}

    public void onSignPowerOff(Sign sign){}

    public void onPlayerRightClickSign(Player player, Sign sign){}

    public void onPlayerLeftClickSign(Player player, Sign sign){}

    public abstract String[] getIdentifier();

    public abstract Material[] getMechanicActivator();
    
    public Material[] getMechanicTarget(){
        return new Material[]{Material.WALL_SIGN, Material.SIGN_POST};
    }
}
