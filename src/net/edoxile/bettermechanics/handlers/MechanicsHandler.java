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

package net.edoxile.bettermechanics.handlers;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.Event;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanicListener;
import net.edoxile.bettermechanics.mechanics.interfaces.IMechanicCommandListener;
import net.edoxile.bettermechanics.mechanics.interfaces.IMechanicListener;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */

public class MechanicsHandler {

    private final HashMap<String, ArrayList<SignMechanicListener>> redstoneSignMechanicMap = new HashMap<String, ArrayList<SignMechanicListener>>();
    private final HashMap<Material, ArrayList<BlockMechanicListener>> redstoneBlockMechanicMap = new HashMap<Material, ArrayList<BlockMechanicListener>>();

    private final HashMap<String, ArrayList<SignMechanicListener>> signMechanicMap = new HashMap<String, ArrayList<SignMechanicListener>>();
    private final HashMap<Material, ArrayList<BlockMechanicListener>> blockMechanicMap = new HashMap<Material, ArrayList<BlockMechanicListener>>();

    private final HashMap<String, IMechanicCommandListener> commandableMechanicMap = new HashMap<String, IMechanicCommandListener>();

    public void addMechanic(IMechanicListener mechanicListener) {
        //TODO: check if this list works as it's supposed to (with passing a reference)
        if (mechanicListener instanceof SignMechanicListener) {
            SignMechanicListener signMechanic = (SignMechanicListener) mechanicListener;
            if (signMechanic.isTriggeredByRedstone()) {
                for (String identifier : signMechanic.getIdentifiers()) {
                    ArrayList<SignMechanicListener> list = redstoneSignMechanicMap.get(identifier);
                    if (list == null) {
                        list = new ArrayList<SignMechanicListener>();
                        list.add(signMechanic);
                        redstoneSignMechanicMap.put(identifier, list);
                    } else {
                        list.add(signMechanic);
                        //redstoneSignMechanicMap.put(identifier, list);
                    }
                }
            }
            if (signMechanic.isTriggeredByPlayer()) {
                for (String identifier : signMechanic.getIdentifiers()) {
                    ArrayList<SignMechanicListener> list = signMechanicMap.get(identifier);
                    if (list == null) {
                        list = new ArrayList<SignMechanicListener>();
                        list.add(signMechanic);
                        signMechanicMap.put(identifier, list);
                    } else {
                        list.add(signMechanic);
                        //signMechanicMap.put(identifier, list);
                    }
                }
            }
        } else if (mechanicListener instanceof BlockMechanicListener) {
            BlockMechanicListener blockMechanicListener = (BlockMechanicListener) mechanicListener;
            if (blockMechanicListener.isTriggeredByRedstone()) {
                for (Material target : blockMechanicListener.getMechanicTargets()) {
                    ArrayList<BlockMechanicListener> list = redstoneBlockMechanicMap.get(target);
                    if (list == null) {
                        list = new ArrayList<BlockMechanicListener>();
                        list.add(blockMechanicListener);
                        redstoneBlockMechanicMap.put(target, list);
                    } else {
                        list.add(blockMechanicListener);
                        //redstoneBlockMechanicMap.put(target, list);
                    }
                }
            }
            if (blockMechanicListener.isTriggeredByPlayer()) {
                for (Material target : blockMechanicListener.getMechanicTargets()) {
                    ArrayList<BlockMechanicListener> list = blockMechanicMap.get(target);
                    if (list == null) {
                        list = new ArrayList<BlockMechanicListener>();
                        list.add(blockMechanicListener);
                        blockMechanicMap.put(target, list);
                    } else {
                        list.add(blockMechanicListener);
                        //blockMechanicMap.put(target, list);
                    }
                }
            }
        }

        if (mechanicListener instanceof IMechanicCommandListener) {
            IMechanicCommandListener mechanicCommandListener = (IMechanicCommandListener) mechanicListener;
            if (commandableMechanicMap.containsKey(mechanicCommandListener.getName())) {
                BetterMechanics.log("Mechanic: " + mechanicCommandListener.getName() + " tried to register a command that has already been registered!", Level.SEVERE);
            } else {
                commandableMechanicMap.put(mechanicCommandListener.getName(), mechanicCommandListener);
            }
        }
    }

    public boolean callCommandEvent(Command command, CommandSender commandSender, String[] args) {
        IMechanicCommandListener mechanic = commandableMechanicMap.get(command.getName());
        return mechanic != null && mechanic.onCommand(commandSender, command, args);
    }

    public void callPlayerEvent(PlayerEvent event) {
        if (SignUtil.isSign(event.getBlock())) {
            List<SignMechanicListener> listeners = getSignListeners(event);
            main:
            for (SignMechanicListener listener : listeners) {
                if (listener.isTriggeredByPlayer()) {
                    switch (event.getAction()) {
                        case RIGHT_CLICK:
                            listener.onPlayerRightClickSign(event);
                            break;
                        case LEFT_CLICK:
                            listener.onPlayerLeftClickSign(event);
                            break;
                        default:
                            break main;
                    }
                }
            }
        } else {
            List<BlockMechanicListener> listeners = getBlockListeners(event);
            main:
            for (BlockMechanicListener listener : listeners) {
                if (listener.isTriggeredByPlayer()) {
                    switch (event.getAction()) {
                        case RIGHT_CLICK:
                            listener.onBlockRightClick(event);
                            break;
                        case LEFT_CLICK:
                            listener.onBlockLeftClick(event);
                            break;
                        case BREAK:
                            listener.onBlockBreak(event);
                            break;
                        case PLACE:
                            listener.onBlockPlace(event);
                        default:
                            break main;
                    }
                }
            }
        }
    }

    public void callRedstoneEvent(RedstoneEvent event) {
        if (SignUtil.isSign(event.getBlock())) {
            List<SignMechanicListener> listeners = getSignListeners(event);
            main:
            for (SignMechanicListener listener : listeners) {
                if (listener.isTriggeredByRedstone()) {
                    switch (event.getState()) {
                        case ON:
                            listener.onSignPowerOn(event);
                            break;
                        case OFF:
                            listener.onSignPowerOff(event);
                            break;
                        default:
                            break main;
                    }
                }
            }
        } else {
            List<BlockMechanicListener> listeners = getBlockListeners(event);
            main:
            for (BlockMechanicListener listener : listeners) {
                if (listener.isTriggeredByRedstone()) {
                    switch (event.getState()) {
                        case ON:
                            listener.onBlockPowerOn(event);
                            break;
                        case OFF:
                            listener.onBlockPowerOff(event);
                            break;
                        default:
                            break main;
                    }
                }
            }
        }
    }

    private List<SignMechanicListener> getSignListeners(Event event) {
        if (!SignUtil.isSign(event.getBlock()))
            return null;

        List<SignMechanicListener> listeners = new ArrayList<SignMechanicListener>();
        Sign sign = (Sign) event.getBlock().getState();

        if (event instanceof RedstoneEvent) {
            listeners.addAll(redstoneSignMechanicMap.get(SignUtil.getMechanicsIdentifier(sign)));
            listeners.addAll(redstoneSignMechanicMap.get(""));
        } else if (event instanceof PlayerEvent) {
            listeners.addAll(signMechanicMap.get(SignUtil.getMechanicsIdentifier(sign)));
            listeners.addAll(signMechanicMap.get(""));
        } else {
            BetterMechanics.log("Something went wrong, unknown type passed to getSignListeners()");
        }

        return listeners;
    }

    private List<BlockMechanicListener> getBlockListeners(Event event) {
        if (SignUtil.isSign(event.getBlock()))
            return null;

        List<BlockMechanicListener> listeners = new ArrayList<BlockMechanicListener>();
        Block block = event.getBlock();

        if (event instanceof RedstoneEvent) {
            listeners.addAll(redstoneBlockMechanicMap.get(block.getType()));
            listeners.addAll(redstoneBlockMechanicMap.get(Material.AIR));
        } else if (event instanceof PlayerEvent) {
            listeners.addAll(blockMechanicMap.get(block.getType()));
            listeners.addAll(blockMechanicMap.get(Material.AIR));
        } else {
            BetterMechanics.log("Something went wrong, unknown type passed to getBlockListeners()");
        }

        return listeners;
    }
}
