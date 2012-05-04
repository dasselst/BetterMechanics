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

package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.handlers.PermissionHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import net.edoxile.bettermechanics.utils.PlayerNotifier;
import net.edoxile.bettermechanics.utils.SignUtil;
import net.edoxile.bettermechanics.utils.datastorage.BlockMapException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Door extends SignMechanicListener {

    private final String[] identifiers = new String[]{"[Door Up]", "[Door Down]", "[sDoor Up]", "[sDoor Down]"};
    private final String[] passiveIdentifiers = new String[]{"[Door]", "sDoor"};
    private ConfigHandler.DoorConfig config = BetterMechanics.getInstance().getConfigHandler().getDoorConfig();

    @Override
    public boolean hasBlockMapper() {
        return true;
    }

    @Override
    public boolean hasBlockBag() {
        return true;
    }

    @Override
    public String[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String[] getPassiveIdentifiers() {
        return passiveIdentifiers;
    }

    @Override
    public boolean isTriggeredByRedstone() {
        return true;
    }

    @Override
    public boolean isTriggeredByPlayer() {
        return true;
    }

    @Override
    public Material[] getMechanicActivators() {
        return voidActor;
    }

    @Override
    public String getName() {
        return "Door";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        try {
            String node = SignUtil.getMechanicsIdentifier(SignUtil.getSign(event.getBlock()));
            if (!PermissionHandler.getInstance().hasPermission(event.getPlayer(), event.getBlock(), node, PermissionHandler.Checks.HIT))
                throw new PlayerNotifier("Seems like you don't have permission to do this!", PlayerNotifier.Level.INFO, event.getBlock().getLocation());
            if (isOpen()) {
                close();
            } else {
                open();
            }
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.notify(event.getPlayer());
        }
    }

    @Override
    public void onSignPowerOn(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            close();
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onSignPowerOff(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            open();
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void mapBlocks(Sign sign) throws BlockMapException {
        final int maxDistance = config.getMaxHeight();
        String node = SignUtil.getMechanicsIdentifier(sign);
        final boolean isSmall;
        final BlockFace direction;
        if (node.charAt(0) == 's') {
            isSmall = true;
            direction = node.equals("sDoor Up") ? BlockFace.UP : BlockFace.DOWN;
        } else {
            isSmall = false;
            direction = node.equals("Door Up") ? BlockFace.UP : BlockFace.DOWN;
        }
        Block startBlock = sign.getBlock();
        Block endBlock;
        boolean endFound = false;
        for (int dy = 1; dy < maxDistance; dy++) {
            endBlock = startBlock.getRelative(0, (direction == BlockFace.UP) ? dy : -dy, 0);
            if (SignUtil.isSign(endBlock)) {
                String endNode = SignUtil.getMechanicsIdentifier(SignUtil.getSign(endBlock));
                if ((isSmall && direction == BlockFace.UP && endNode.equals("sDoor Down"))
                        || (isSmall && direction == BlockFace.DOWN && endNode.equals("sDoor Up"))
                        || (!isSmall && direction == BlockFace.UP && endNode.equals("sDoor Down"))
                        || (!isSmall && direction == BlockFace.DOWN && endNode.equals("sDoor Up"))) {
                    endFound = true;
                    break;
                }
            }
        }
        if(endFound){
            //TODO: map blocks
        } else {
            throw new BlockMapException(BlockMapException.Type.END_NOT_FOUND);
        }
    }
}
