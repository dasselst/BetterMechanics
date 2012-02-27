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
import net.edoxile.bettermechanics.mechanics.interfaces.*;
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanic;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanic;
import net.edoxile.bettermechanics.models.BlockMap;
import net.edoxile.bettermechanics.models.BlockMapException;
import net.edoxile.bettermechanics.models.SignMechanicEventData;
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

    private HashMap<String, ArrayList<SignMechanic>> redstoneSignMechanicMap = new HashMap<String, ArrayList<SignMechanic>>();
    private HashMap<Material, ArrayList<BlockMechanic>> redstoneBlockMechanicMap = new HashMap<Material, ArrayList<BlockMechanic>>();

    private HashMap<String, ArrayList<SignMechanic>> signMechanicMap = new HashMap<String, ArrayList<SignMechanic>>();
    private HashMap<Material, ArrayList<BlockMechanic>> blockMechanicMap = new HashMap<Material, ArrayList<BlockMechanic>>();

    private HashMap<String, ICommandableMechanic> commandableMechanicMap = new HashMap<String, ICommandableMechanic>();

    public void addMechanic(IMechanic mechanic) {
        //TODO: check if this list works as it's supposed to (with passing a reference)
        if (mechanic instanceof SignMechanic) {
            SignMechanic signMechanic = (SignMechanic) mechanic;
            if (signMechanic.isTriggeredByRedstone()) {
                for (String identifier : signMechanic.getIdentifier()) {
                    ArrayList<SignMechanic> list = redstoneSignMechanicMap.get(identifier);
                    if (list == null) {
                        list = new ArrayList<SignMechanic>();
                        list.add(signMechanic);
                        redstoneSignMechanicMap.put(identifier, list);
                    } else {
                        list.add(signMechanic);
                        redstoneSignMechanicMap.put(identifier, list);
                    }
                }
            } else {
                for (String identifier : signMechanic.getIdentifier()) {
                    ArrayList<SignMechanic> list = signMechanicMap.get(identifier);
                    if (list == null) {
                        list = new ArrayList<SignMechanic>();
                        list.add(signMechanic);
                        signMechanicMap.put(identifier, list);
                    } else {
                        list.add(signMechanic);
                    }
                }
            }
        } else if (mechanic instanceof BlockMechanic) {
            BlockMechanic blockMechanic = (BlockMechanic) mechanic;
            if (blockMechanic.isTriggeredByRedstone()) {
                for (Material target : blockMechanic.getMechanicTarget()) {
                    ArrayList<BlockMechanic> list = redstoneBlockMechanicMap.get(target);
                    if (list == null) {
                        list = new ArrayList<BlockMechanic>();
                        list.add(blockMechanic);
                        redstoneBlockMechanicMap.put(target, list);
                    } else {
                        list.add(blockMechanic);
                        redstoneBlockMechanicMap.put(target, list);
                    }
                }
            } else {
                for (Material target : blockMechanic.getMechanicTarget()) {
                    ArrayList<BlockMechanic> list = blockMechanicMap.get(target);
                    if (list == null) {
                        list = new ArrayList<BlockMechanic>();
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
            List<SignMechanic> mechanicsList = signMechanicMap.get(id);
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<SignMechanic>();
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
            for (SignMechanic mechanic : mechanicsList) {
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
            List<BlockMechanic> mechanicsList = blockMechanicMap.get(event.getClickedBlock().getType());
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<BlockMechanic>();
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
            for (BlockMechanic mechanic : mechanicsList) {
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
            List<BlockMechanic> mechanicsList = blockMechanicMap.get(blockBreakEvent.getBlock().getType());
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<BlockMechanic>();
            }
            if (blockMechanicMap.containsKey(null)) {
                mechanicsList.addAll(blockMechanicMap.get(null));
            }
            if (redstoneBlockMechanicMap.containsKey(blockBreakEvent.getBlock().getType())) {
                mechanicsList.addAll(redstoneBlockMechanicMap.get(blockBreakEvent.getBlock().getType()));
            }
            for (BlockMechanic mechanic : mechanicsList) {
                if (mechanic.getMechanicActivator() == null || mechanic.getMechanicActivator().contains(blockBreakEvent.getPlayer().getItemInHand().getType())) {
                    mechanic.onBlockBreak(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock());
                }
            }
        } else if (event instanceof BlockPlaceEvent) {
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) event;
            List<BlockMechanic> mechanicsList = blockMechanicMap.get(blockPlaceEvent.getBlock().getType());
            if (mechanicsList == null) {
                mechanicsList = new ArrayList<BlockMechanic>();
            }
            if (blockMechanicMap.containsKey(null)) {
                mechanicsList.addAll(blockMechanicMap.get(null));
            }
            if (redstoneBlockMechanicMap.containsKey(blockPlaceEvent.getBlock().getType())) {
                mechanicsList.addAll(redstoneBlockMechanicMap.get(blockPlaceEvent.getBlock().getType()));
            }
            for (BlockMechanic mechanic : mechanicsList) {
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
                List<SignMechanic> list = redstoneSignMechanicMap.get(SignUtil.getMechanicsIdentifier(sign));
                if (list == null) {
                    list = new ArrayList<SignMechanic>();
                }
                if (redstoneSignMechanicMap.containsKey(null)) {
                    list.addAll(redstoneSignMechanicMap.get(null));
                }
                for (SignMechanic mechanic : list) {
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
                List<BlockMechanic> list = redstoneBlockMechanicMap.get(block.getType());
                if (list == null) {
                    list = new ArrayList<BlockMechanic>();
                }
                if (blockMechanicMap.containsKey(null)) {
                    list.addAll(blockMechanicMap.get(null));
                }
                for (BlockMechanic mechanic : list) {
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
