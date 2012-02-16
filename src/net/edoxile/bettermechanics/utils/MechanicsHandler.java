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
import net.edoxile.bettermechanics.models.BlockMap;
import net.edoxile.bettermechanics.models.BlockMapException;
import net.edoxile.bettermechanics.models.SignMechanicEventData;
import net.edoxile.bettermechanics.models.blockbags.BlockBag;
import net.edoxile.bettermechanics.models.blockbags.BlockBagException;
import org.bukkit.ChatColor;
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
            Sign sign = SignUtil.getSign(event.getClickedBlock());
            String id = SignUtil.getMechanicsIdentifier(sign);
            List<ISignMechanic> mechanicsList = signMechanicMap.get(id);
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<ISignMechanic>();
            }
            if (signMechanicMap.containsKey(null)) {
                mechanicsList.addAll(signMechanicMap.get(null));
            }
            if (redstoneSignMechanicMap.containsKey(id)) {
                mechanicsList.addAll(redstoneSignMechanicMap.get(id));
            }
            if (redstoneSignMechanicMap.containsKey(null)) {
                mechanicsList.addAll(redstoneSignMechanicMap.get(null));
            }
            for (ISignMechanic mechanic : mechanicsList) {
                try {
                    BlockBagHandler bag = null;
                    BlockMap map = null;
                    if (mechanic.hasBlockMapper()) {
                        map = mechanic.mapBlocks(sign);
                    }
                    if (mechanic.hasBlockBag()) {
                        bag = BlockBagHandler.locate(sign);
                    }
                    SignMechanicEventData data = new SignMechanicEventData(map, bag);
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        mechanic.onPlayerLeftClickSign(event.getPlayer(), data);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        mechanic.onPlayerRightClickSign(event.getPlayer(), data);
                    }
                } catch (BlockMapException e) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + e.getMessage());
                    BetterMechanics.log(e.getMessage());
                } catch (BlockBagException e) {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + e.getMessage());
                    BetterMechanics.log(e.getMessage());
                }
            }
        } else {
            List<IBlockMechanic> mechanicsList = blockMechanicMap.get(event.getClickedBlock().getType());
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<IBlockMechanic>();
            }
            if (blockMechanicMap.containsKey(null)) {
                mechanicsList.addAll(blockMechanicMap.get(null));
            }
            if (redstoneBlockMechanicMap.containsKey(event.getClickedBlock().getType())) {
                mechanicsList.addAll(redstoneBlockMechanicMap.get(event.getClickedBlock().getType()));
            }
            if (redstoneBlockMechanicMap.containsKey(null)) {
                mechanicsList.addAll(redstoneBlockMechanicMap.get(null));
            }
            for (IBlockMechanic mechanic : mechanicsList) {
                if (mechanic.getMechanicActivator() == null || mechanic.getMechanicActivator().contains(event.getPlayer().getItemInHand().getType())) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        mechanic.onBlockLeftClick(event.getPlayer(), event.getClickedBlock());
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        mechanic.onBlockRightClick(event.getPlayer(), event.getClickedBlock());
                    }
                }
            }
        }
    }

    public void callBlockEvent(BlockEvent event) {
        if (event instanceof BlockBreakEvent) {
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            List<IBlockMechanic> mechanicsList = blockMechanicMap.get(blockBreakEvent.getBlock().getType());
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<IBlockMechanic>();
            }
            if (blockMechanicMap.containsKey(null)) {
                mechanicsList.addAll(blockMechanicMap.get(null));
            }
            if (redstoneBlockMechanicMap.containsKey(blockBreakEvent.getBlock().getType())) {
                mechanicsList.addAll(redstoneBlockMechanicMap.get(blockBreakEvent.getBlock().getType()));
            }
            for (IBlockMechanic mechanic : mechanicsList) {
                if (mechanic.getMechanicActivator() == null || mechanic.getMechanicActivator().contains(blockBreakEvent.getPlayer().getItemInHand().getType())) {
                    mechanic.onBlockBreak(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock());
                }
            }
        } else if (event instanceof BlockPlaceEvent) {
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) event;
            List<IBlockMechanic> mechanicsList = blockMechanicMap.get(blockPlaceEvent.getBlock().getType());
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<IBlockMechanic>();
            }
            if (blockMechanicMap.containsKey(null)) {
                mechanicsList.addAll(blockMechanicMap.get(null));
            }
            if (redstoneBlockMechanicMap.containsKey(blockPlaceEvent.getBlock().getType())) {
                mechanicsList.addAll(redstoneBlockMechanicMap.get(blockPlaceEvent.getBlock().getType()));
            }
            for (IBlockMechanic mechanic : mechanicsList) {
                if (mechanic.getMechanicActivator() == null || mechanic.getMechanicActivator().contains(blockPlaceEvent.getPlayer().getItemInHand().getType())) {
                    mechanic.onBlockBreak(blockPlaceEvent.getPlayer(), blockPlaceEvent.getBlock());
                }
            }
        }
    }

    public void callRedstoneEvent(BlockRedstoneEvent event) {
        boolean on = event.getNewCurrent() > event.getOldCurrent();

        for (BlockFace direction : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP)) {
            Block block = event.getBlock().getRelative(direction);
            if (SignUtil.isSign(block)) {
                Sign sign = SignUtil.getSign(block);
                List<ISignMechanic> list = redstoneSignMechanicMap.get(SignUtil.getMechanicsIdentifier(sign));
                if (list == null) {
                    list = new ArrayList<ISignMechanic>();
                }
                if (redstoneSignMechanicMap.containsKey(null)) {
                    list.addAll(redstoneSignMechanicMap.get(null));
                }
                for (ISignMechanic mechanic : list) {
                    try {
                        BlockBagHandler bag = null;
                        BlockMap map = null;
                        if (mechanic.hasBlockMapper()) {
                            map = mechanic.mapBlocks(sign);
                        }
                        if (mechanic.hasBlockBag()) {
                            bag = BlockBagHandler.locate(sign);
                        }
                        SignMechanicEventData data = new SignMechanicEventData(map, bag);
                        if (on) {
                            mechanic.onSignPowerOn(data);
                        } else {
                            mechanic.onSignPowerOff(data);
                        }
                    } catch (BlockMapException e) {
                        BetterMechanics.log(e.getMessage());
                    } catch (BlockBagException e) {
                        BetterMechanics.log(e.getMessage());
                    }
                }
            } else {
                List<IBlockMechanic> list = redstoneBlockMechanicMap.get(block.getType());
                if (list == null) {
                    list = new ArrayList<IBlockMechanic>();
                }
                if (blockMechanicMap.containsKey(null)) {
                    list.addAll(blockMechanicMap.get(null));
                }
                for (IBlockMechanic mechanic : list) {
                    if(on){
                        mechanic.onBlockPowerOn(event.getBlock());
                    } else {
                        mechanic.onBlockPowerOff(event.getBlock());
                    }
                }
            }
        }
    }

    public boolean callCommandEvent(Command command, CommandSender commandSender, String[] args) {
        ICommandableMechanic mechanic = commandableMechanicMap.get(command.getName());
        return mechanic != null && mechanic.onCommand(commandSender, command, args);
    }
}
