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

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.mechanics.interfaces.ISignMechanic;
import net.edoxile.bettermechanics.utils.ConfigHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Gate extends ISignMechanic {
    private boolean enabled = true;
    private int maxLength = 32;
    private int maxWidth = 3;
    private int maxHeight = 32;
    Set<Material> materialList = null;

    public Gate(BetterMechanics p) {
        ConfigHandler.GateConfig config = ConfigHandler.getInstance().getGateConfig();
        enabled = config.isEnabled();
        maxLength = config.getMaxLength();
        maxWidth = config.getMaxWidth();
        maxHeight = config.getMaxHeight();
        materialList = config.getAllowedMaterials();
    }

    public void onSignPowerOn(Block sign) {
        //Close gate
    }

    public void onSignPowerOff(Block sign) {
        //Open gate
    }

    public void onPlayerRightClickSign(Player player, Block sign) {
        //Toggle gate
    }

    public String[] getIdentifier() {
        return new String[]{"[Gate]","[dGate]"};
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return true;
    }

    @Override
    public boolean hasBlockMapper() {
        return true;
    }

    public Material[] getMechanicActivator() {
        return null;
    }

    public String getName() {
        return "Gate";
    }
    
    private boolean isAllowedMaterial(Material material){
        return materialList.contains(material);
    }

    public boolean mapBlocks(){
        return false;
    }
}
