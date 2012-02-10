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
import net.edoxile.configparser.annotations.ConfigEntityNode;
import net.edoxile.configparser.annotations.NodeType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
@ConfigEntityNode("gate")
public class Gate extends ConfigEntity implements ISignMechanic {

    @NodeType(
            node = "enabled",
            clazz = Boolean.class
    )
    private boolean enabled = true;

    @NodeType(
            node = "max-length",
            clazz = Integer.class
    )
    private int maxLength = 32;

    @NodeType(
            node = "max-width",
            clazz = Integer.class
    )
    private int maxWidth = 3;

    @NodeType(
            node = "allowed-materials",
            clazz = Integer.class
    )
    ArrayList<Integer> materialList = new ArrayList<Integer>(Arrays.asList(Material.IRON_FENCE.getId(), Material.FENCE.getId()));

    private BetterMechanics plugin = null;

    public Gate(BetterMechanics p) {
        super(p);
        plugin = p;
        //loadConfig(p.getConfiguration());
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

    public void onPlayerLeftClickSign(Player player, Block sign) {
    }

    public String[] getIdentifier() {
        return "[Gate]";
    }

    public Material getMechanicActivator() {
        return null;
    }
}
