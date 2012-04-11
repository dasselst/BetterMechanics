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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.bettermechanics.listeners;

import net.edoxile.bettermechanics.mechanics.Bridge;
import net.edoxile.bettermechanics.mechanics.Door;
import net.edoxile.bettermechanics.mechanics.Gate;
import net.edoxile.bettermechanics.utils.BlockBagManager;
import net.edoxile.bettermechanics.utils.MechanicsConfig;
import net.edoxile.bettermechanics.utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

public class MechanicsBlockListener implements Listener {
    private MechanicsConfig config;
    private MechanicsConfig.PermissionConfig permissions;
    private BlockBagManager bagmanager;

    public MechanicsBlockListener(MechanicsConfig c, BlockBagManager manager) {
        config = c;
        bagmanager = manager;
        permissions = c.getPermissionConfig();
    }

    public void setConfig(MechanicsConfig c) {
        config = c;
    }

    public void onSignChange(SignChangeEvent event) {
        String str = event.getLine(1);
        if (SignUtil.getMechanicsType(str) == null) {
            // return;
        } else {
            if (!permissions.check(event.getPlayer(), SignUtil.getMechanicsType(str).name().toLowerCase() + ".create", event.getBlock(), false)) {
                event.setCancelled(true);
                return;
            } else {
            }
        }

        if (str.equalsIgnoreCase("[lift up]")) {
            event.setLine(1, "[Lift Up]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a lift!");
        } else if (str.equalsIgnoreCase("[Lift Down]")) {
            event.setLine(1, "[Lift Down]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a lift!");
        } else if (str.equalsIgnoreCase("[Lift]")) {
            event.setLine(1, "[Lift]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a lift!");
        } else if (str.equalsIgnoreCase("[TeleLift]")) {
            event.setLine(1, "[TeleLift]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a tele-lift!");
        } else if (str.equalsIgnoreCase("[gate]")) {
            event.setLine(1, "[Gate]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a gate!");
        } else if (str.equalsIgnoreCase("[dgate]")) {
            event.setLine(1, "[Dgate]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small gate!");
        } else if (str.equalsIgnoreCase("[bridge]")) {
            event.setLine(1, "[Bridge]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[bridge end]")) {
            event.setLine(1, "[Bridge End]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[sbridge]")) {
            event.setLine(1, "[sBridge]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[sbridge end]")) {
            event.setLine(1, "[sBridge End]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[door up]")) {
            event.setLine(1, "[Door Up]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a door!");
        } else if (str.equalsIgnoreCase("[door down]")) {
            event.setLine(1, "[Door Down]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a door!");
        } else if (str.equalsIgnoreCase("[door]")) {
            event.setLine(1, "[Door]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a door!");
        } else if (str.equalsIgnoreCase("[sdoor]")) {
            event.setLine(1, "[sDoor]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small door!");
        } else if (str.equalsIgnoreCase("[sdoor up]")) {
            event.setLine(1, "[sDoor Up]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small door!");
        } else if (str.equalsIgnoreCase("[sdoor down]")) {
            event.setLine(1, "[sDoor Down]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small door!");
        } else if (str.equalsIgnoreCase("[x]")) {
            event.setLine(1, "[X]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a hidden switch!");
        } else if (str.equalsIgnoreCase("[black hole]")) {
            if(!permissions.checkPermissions(event.getPlayer(), "blackhole.create")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED+"You don't have permission to create a black hole!");
                if(event.getBlock().getType().equals(Material.WALL_SIGN) || event.getBlock().getType().equals(Material.SIGN_POST)) {
                    event.getBlock().setTypeId(0);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
                }
            } else {
                event.setLine(1, "[Black Hole]");
                event.getPlayer().sendMessage(ChatColor.AQUA + "You created a black hole!");
            }
        } else if (str.equalsIgnoreCase("[block source]")) {
            if(!permissions.checkPermissions(event.getPlayer(), "blocksource.create")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED+"You don't have permission to create a block source!");
                if(event.getBlock().getType().equals(Material.WALL_SIGN) || event.getBlock().getType().equals(Material.SIGN_POST)) {
                    event.getBlock().setTypeId(0);
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
                }
            } else {
                event.setLine(1, "[Block Source]");
                event.getPlayer().sendMessage(ChatColor.AQUA + "You created a block source!");
            }
        } else {
            return;
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockAgainst();
        if (SignUtil.isSign(block)) {
            Sign sign = SignUtil.getSign(block);
            if (SignUtil.getMechanicsType(sign) != null) {
                event.setCancelled(true);
            }
        }
    }

    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if ((event.getNewCurrent() == event.getOldCurrent()) || (event.getNewCurrent() > 0 && event.getOldCurrent() > 0))
            return;
        Block block = event.getBlock();
        Sign sign = null;
        for (int dx = -1; dx <= 1; dx++) {
            if (SignUtil.isSign(block.getRelative(dx, 0, 0))) {
                sign = SignUtil.getSign(block.getRelative(dx, 0, 0));
                if (sign != null) {
                    if (SignUtil.getActiveMechanicsType(sign) != null) {
                        break;
                    } else {
                        sign = null;
                    }
                }
            }
        }
        if (sign == null) {
            for (int dy = -1; dy <= 1; dy++) {
                if (SignUtil.isSign(block.getRelative(0, dy, 0))) {
                    sign = SignUtil.getSign(block.getRelative(0, dy, 0));
                    if (sign != null) {
                        if (SignUtil.getActiveMechanicsType(sign) != null) {
                            break;
                        } else {
                            sign = null;
                        }
                    }
                }
            }
            if (sign == null) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (SignUtil.isSign(block.getRelative(0, 0, dz))) {
                        sign = SignUtil.getSign(block.getRelative(0, 0, dz));
                        if (sign != null) {
                            if (SignUtil.getActiveMechanicsType(sign) != null) {
                                break;
                            } else {
                                sign = null;
                            }
                        }
                    }
                }
                if (sign == null)
                    return;
            }
        }
        switch (SignUtil.getActiveMechanicsType(sign)) {
            case SMALL_BRIDGE:
            case BRIDGE: {
                Bridge bridge = new Bridge(config, bagmanager, sign, null);
                try {
                    if (!bridge.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        bridge.toggleClosed();
                    } else {
                        bridge.toggleOpen();
                    }
                } catch (Exception e) {
                }
            }
            break;
            case SMALL_GATE:
            case GATE: {
                Gate gate = new Gate(config, bagmanager, sign, null);
                try {
                    if (!gate.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        gate.toggleClosed();
                    } else {
                        gate.toggleOpen();
                    }
                } catch (Exception e) {
                }
            }
            break;
            case SMALL_DOOR:
            case DOOR: {
                Door door = new Door(config, bagmanager, sign, null);
                try {
                    if (!door.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        door.toggleClosed();
                    } else {
                        door.toggleOpen();
                    }
                } catch (Exception e) {
                }
            }
            break;
            default:
                return;
        }
    }
}