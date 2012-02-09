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
import net.edoxile.bettermechanics.mechanics.interfaces.ICommandableMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.ISignMechanic;
import net.edoxile.configparser.annotations.ConfigEntityNode;
import net.edoxile.configparser.annotations.NodeType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
@ConfigEntityNode("pen")
public class Pen extends ConfigEntity implements ISignMechanic, ICommandableMechanic {

    @NodeType(
            node = "enabled",
            clazz = Boolean.class
    )
    private boolean enabled;

    @NodeType(
            node = "tool",
            clazz = Integer.class
    )
    private int tool;

    private BetterMechanics plugin = null;

    public Pen(BetterMechanics p) {
        super(p);
        plugin = p;
        //loadConfig(p.getConfiguration());
    }

    public void onSignPowerOn(Block sign) {
    }

    public void onSignPowerOff(Block sign) {
    }

    public void onPlayerRightClickSign(Player player, Block sign) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onPlayerLeftClickSign(Player player, Block sign) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean onCommand(CommandSender commandSender, Command command, String[] args) {
        return false;
    }

    public String getIdentifier() {
        return "";
    }

    public Material getMechanicActivator() {
        return Material.getMaterial(tool);
    }

    public void onCommand(Player player, Command command, String[] args) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
