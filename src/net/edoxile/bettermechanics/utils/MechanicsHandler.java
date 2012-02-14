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

package net.edoxile.bettermechanics.utils;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.mechanics.interfaces.IBlockMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.ICommandableMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.IMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.ISignMechanic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
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

    private HashMap<String, ArrayList<ISignMechanic>> redstoneSignMechanicMap = new HashMap<String, ArrayList<ISignMechanic>>();
    private HashMap<Material, ArrayList<IBlockMechanic>> redstoneBlockMechanicMap = new HashMap<Material, ArrayList<IBlockMechanic>>();

    private HashMap<String, ArrayList<ISignMechanic>> signMechanicMap = new HashMap<String, ArrayList<ISignMechanic>>();
    private HashMap<Material, ArrayList<IBlockMechanic>> blockMechanicMap = new HashMap<Material, ArrayList<IBlockMechanic>>();

    private HashMap<String, ICommandableMechanic> commandableMechanicMap = new HashMap<String, ICommandableMechanic>();

    public void addMechanic(IMechanic mechanic) {
        //TODO: check if this list works as it's supposed to (with passing a reference)
        if (mechanic instanceof ISignMechanic) {
            ISignMechanic signMechanic = (ISignMechanic) mechanic;
            if (signMechanic.isTriggeredByRedstone()) {
                for (String identifier : signMechanic.getIdentifier()) {
                    ArrayList<ISignMechanic> list = redstoneSignMechanicMap.get(identifier);
                    if (list == null) {
                        list = new ArrayList<ISignMechanic>();
                        list.add(signMechanic);
                        redstoneSignMechanicMap.put(identifier, list);
                    } else {
                        list.add(signMechanic);
                        redstoneSignMechanicMap.put(identifier, list);
                    }
                }
            } else {
                for (String identifier : signMechanic.getIdentifier()) {
                    ArrayList<ISignMechanic> list = signMechanicMap.get(identifier);
                    if (list == null) {
                        list = new ArrayList<ISignMechanic>();
                        list.add(signMechanic);
                        signMechanicMap.put(identifier, list);
                    } else {
                        list.add(signMechanic);
                    }
                }
            }
        } else if (mechanic instanceof IBlockMechanic) {
            IBlockMechanic blockMechanic = (IBlockMechanic) mechanic;
            if (blockMechanic.isTriggeredByRedstone()) {
                for (Material target : blockMechanic.getMechanicTarget()) {
                    ArrayList<IBlockMechanic> list = redstoneBlockMechanicMap.get(target);
                    if (list == null) {
                        list = new ArrayList<IBlockMechanic>();
                        list.add(blockMechanic);
                        redstoneBlockMechanicMap.put(target, list);
                    } else {
                        list.add(blockMechanic);
                        redstoneBlockMechanicMap.put(target, list);
                    }
                }
            } else {
                for (Material target : blockMechanic.getMechanicTarget()) {
                    ArrayList<IBlockMechanic> list = blockMechanicMap.get(target);
                    if (list == null) {
                        list = new ArrayList<IBlockMechanic>();
                        list.add(blockMechanic);
                        blockMechanicMap.put(target, list);
                    } else {
                        list.add(blockMechanic);
                    }
                }
            }
        }

        if (mechanic instanceof ICommandableMechanic) {
            ICommandableMechanic commandableMechanic = (ICommandableMechanic) mechanic;
            if (commandableMechanicMap.containsKey(commandableMechanic.getName())) {
                BetterMechanics.log("Mechanic: " + commandableMechanic.getName() + " tried to register a command that has already been registered!", Level.SEVERE);
            } else {
                commandableMechanicMap.put(commandableMechanic.getName(), commandableMechanic);
            }
        }
    }

    public void callPlayerInteractEvent(PlayerInteractEvent event) {
        if (SignUtil.isSign(event.getClickedBlock())) {
            //TODO: Call SignMechanicEvent
        } else {
            //TODO: Call BlockMechanicEvent
        }
    }

    public void callRedstoneEvent(BlockRedstoneEvent event) {
        for (BlockFace direction : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP)) {
            Block block = event.getBlock().getRelative(direction);
            if (block.getTypeId() == Material.WALL_SIGN.getId() || block.getTypeId() == Material.SIGN_POST.getId()) {
                //TODO: Call SignMechanicEvent
            } else {
                //TODO: Call BlockMechanicEvent
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
        return mechanic != null && mechanic.onCommand(commandSender, command, args);
    }
}
