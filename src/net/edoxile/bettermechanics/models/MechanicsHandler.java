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

package net.edoxile.bettermechanics.models;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.mechanics.interfaces.IBlockMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.ICommandableMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.IMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.ISignMechanic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */

public class MechanicsHandler {
    private ArrayList<IMechanic> mechanicsList = new ArrayList<IMechanic>();

    private HashMap<Material, ArrayList<IBlockMechanic>> blockMechanicMap = new HashMap<Material, ArrayList<IBlockMechanic>>();
    private HashMap<String, ArrayList<ISignMechanic>> signMechanicMap = new HashMap<String, ArrayList<ISignMechanic>>();
    private HashMap<String, ICommandableMechanic> commandableMechanicMap = new HashMap<String, ICommandableMechanic>();

    public void addMechanic(IMechanic mechanic) {
        if (mechanic instanceof IBlockMechanic) {
            IBlockMechanic blockMechanic = (IBlockMechanic) mechanic;
            ArrayList<IBlockMechanic> blockMechanicList = blockMechanicMap.get(blockMechanic.getMechanicActivator());
            if (blockMechanicList == null) {
                blockMechanicList = new ArrayList<IBlockMechanic>();
            }
            blockMechanicList.add(blockMechanic);
            blockMechanicMap.put(blockMechanic.getMechanicTarget(), blockMechanicList);
        } else if (mechanic instanceof ISignMechanic) {
            ISignMechanic signMechanic = (ISignMechanic) mechanic;
            ArrayList<ISignMechanic> signMechanicList = signMechanicMap.get(signMechanic.getIdentifier());
            if (signMechanicList == null) {
                signMechanicList = new ArrayList<ISignMechanic>();

            }
            signMechanicList.add(signMechanic);
            signMechanicMap.put(signMechanic.getIdentifier(), signMechanicList);
        }

        if (mechanic instanceof ICommandableMechanic) {
            ICommandableMechanic commandableMechanic = (ICommandableMechanic) mechanic;
            if (commandableMechanicMap.containsKey(commandableMechanic.getIdentifier())) {
                BetterMechanics.log("Mechanic " + commandableMechanic.getName() + " is trying to overwrite command: '" + commandableMechanic.getIdentifier() + "'.", Level.SEVERE);
            } else {
                commandableMechanicMap.put(commandableMechanic.getIdentifier(), commandableMechanic);
            }
        }
    }

    public void callPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock().getTypeId() == Material.WALL_SIGN.getId() || event.getClickedBlock().getTypeId() == Material.SIGN_POST.getId()) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            List<ISignMechanic> mechanicList = signMechanicMap.get(sign.getLine(2));
            if (mechanicList == null) {
                mechanicList = signMechanicMap.get("");
                if (mechanicList == null)
                    return;
            }
            for (ISignMechanic mechanic : mechanicList) {
                if (mechanic != null && (mechanic.getMechanicActivator() == null || mechanic.getMechanicActivator() == event.getPlayer().getItemInHand().getType())) {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        mechanic.onPlayerRightClickSign(event.getPlayer(), event.getClickedBlock());
                    } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        mechanic.onPlayerLeftClickSign(event.getPlayer(), event.getClickedBlock());
                    }
                }
            }
        } else {
            List<IBlockMechanic> blockMechanicList = blockMechanicMap.get(event.getClickedBlock().getType());
            for (IBlockMechanic mechanic : blockMechanicList) {
                if (mechanic != null && (mechanic.getMechanicActivator() == null || mechanic.getMechanicActivator() == event.getPlayer().getItemInHand().getType())) {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        mechanic.onBlockRightClick(event.getPlayer(), event.getClickedBlock());
                    } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        mechanic.onBlockLeftClick(event.getPlayer(), event.getClickedBlock());
                    }
                }
            }
        }
    }

    public void callRedstoneEvent(BlockRedstoneEvent event) {
        for (BlockFace direction : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
            Block block = event.getBlock().getRelative(direction);
            if (block.getTypeId() == Material.WALL_SIGN.getId() || block.getTypeId() == Material.SIGN_POST.getId()) {
                Sign sign = (Sign) block.getState();
                List<ISignMechanic> mechanicList = signMechanicMap.get(sign.getLine(2));
                for (ISignMechanic mechanic : mechanicList) {
                    if (mechanic != null) {
                        if (event.getNewCurrent() > 0) {
                            mechanic.onSignPowerOn(block);
                        } else {
                            mechanic.onSignPowerOff(block);
                        }
                    }
                }
            } else {
                List<IBlockMechanic> blockMechanicList = blockMechanicMap.get(event.getBlock().getType());
                for (IBlockMechanic mechanic : blockMechanicList) {
                    if (mechanic != null) {
                        if (event.getNewCurrent() > 0) {
                            mechanic.onBlockPowerOn(block);
                        } else {
                            mechanic.onBlockPowerOff(block);
                        }
                    }
                }
            }
        }
    }

    public void callBlockEvent(BlockEvent event) {
        List<IBlockMechanic> blockMechanicList = blockMechanicMap.get(event.getBlock().getType());
        for (IBlockMechanic mechanic : blockMechanicList) {
            if (mechanic != null) {
                if (event instanceof BlockBreakEvent) {
                    mechanic.onBlockBreak(((BlockBreakEvent) event).getPlayer(), event.getBlock());
                } else if (event instanceof BlockPlaceEvent) {
                    mechanic.onBlockPlace(((BlockPlaceEvent) event).getPlayer(), event.getBlock());
                }
            }
        }
    }

    public boolean callCommandEvent(Command command, CommandSender commandSender, String[] args) {
        ICommandableMechanic mechanic = commandableMechanicMap.get(command.getName());
        if (mechanic != null) {
            return mechanic.onCommand(commandSender, command, args);
        }
        return false;
    }
}
