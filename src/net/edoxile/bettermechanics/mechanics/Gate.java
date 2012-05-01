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
import net.edoxile.bettermechanics.utils.datastorage.BlockMap;
import net.edoxile.bettermechanics.utils.datastorage.BlockMapException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.HashSet;

import static net.edoxile.bettermechanics.utils.BlockUtil.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Gate extends SignMechanicListener {

    private final ConfigHandler.GateConfig config = BetterMechanics.getInstance().getConfigHandler().getGateConfig();
    private final String[] identifiers = new String[]{"[Gate]", "[dGate]", "[sGate]"};

    @Override
    public void onSignPowerOn(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            if (isOpen())
                close();
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onSignPowerOff(RedstoneEvent event) {
        try {
            loadData(SignUtil.getSign(event.getBlock()));
            if (!isOpen())
                open();
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.log();
        }
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        try {
            String node = SignUtil.getMechanicsIdentifier(SignUtil.getSign(event.getBlock()));
            if (!PermissionHandler.getInstance().hasPermission(event.getPlayer(), event.getBlock(), node, PermissionHandler.Checks.HIT))
                throw new PlayerNotifier("Seems like you don't have permission to do this!", PlayerNotifier.Level.INFO, event.getBlock().getLocation());
            loadData(SignUtil.getSign(event.getBlock()));
            if (isOpen()) {
                close();
                event.getPlayer().sendMessage(ChatColor.GOLD + "Bridge closed!");
            } else {
                open();
                event.getPlayer().sendMessage(ChatColor.GOLD + "Bridge opened!");
            }
        } catch (PlayerNotifier playerNotifier) {
            playerNotifier.notify(event.getPlayer());
        }

    }

    @Override
    public String[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String[] getPassiveIdentifiers() {
        return null;
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
    public boolean hasBlockMapper() {
        return true;
    }

    @Override
    public boolean hasBlockBag() {
        return true;
    }

    @Override
    public Material[] getMechanicActivators() {
        return voidActor;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public String getName() {
        return "Gate";
    }

    @Override
    public void mapBlocks(Sign sign) throws BlockMapException {
        //TODO: fix material shit
        int maxWidth = config.getMaxWidth();
        int maxLength = config.getMaxLength();
        Material gateMaterial = null;
        boolean dGate = false;

        String id = SignUtil.getMechanicsIdentifier(sign);
        boolean smallGate = false;
        if (id.equals("[dGate]")) {
            dGate = true;
        } else if (id.equals("[sGate]")) {
            smallGate = true;
        } else {
            throw new BlockMapException(BlockMapException.Type.INVALID_KEY_ON_SIGN);
        }

        Block start = sign.getBlock();
        if (SignUtil.isWallSign(sign)) {
            start = start.getRelative(SignUtil.getAttachedFace(sign));
        }
        Block tempBlock;

        for (Material m : config.getAllowedMaterials()) {
            tempBlock = locateNearestBlock(start, m, dGate ? 6 : 4);
            if (tempBlock != null) {
                start = tempBlock;
                gateMaterial = m;
                break;
            }
        }
        if (gateMaterial == null) {
            throw new BlockMapException(BlockMapException.Type.MECHANIC_NOT_FOUND);
        }
        tempBlock = start;

        int west = 0, east = 0, south = 0, north = 0, width, length;
        while (getTypeInColumn(tempBlock.getRelative(BlockFace.WEST), config.getAllowedMaterials(), true) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.WEST);
            west++;
            if (smallGate)
                break;
        }
        tempBlock = start;
        while (getTypeInColumn(tempBlock.getRelative(BlockFace.EAST), config.getAllowedMaterials(), true) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.EAST);
            east++;
            if (smallGate)
                break;
        }
        tempBlock = start;
        while (getTypeInColumn(tempBlock.getRelative(BlockFace.NORTH), config.getAllowedMaterials(), true) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.NORTH);
            north++;
            if (smallGate)
                break;
        }
        tempBlock = start;
        while (getTypeInColumn(tempBlock.getRelative(BlockFace.SOUTH), config.getAllowedMaterials(), true) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.SOUTH);
            south++;
            if (smallGate)
                break;
        }
        if ((north + south) > (east + west)) {
            width = (east + west);
            length = (north + south);
        } else {
            length = (east + west);
            width = (north + south);
        }
        if (width > maxWidth || length > maxLength) {
            throw new BlockMapException(BlockMapException.Type.SIZE_LIMIT_EXCEEDED);
        }
        start = start.getRelative(-north, 0, -east);
        HashSet<Block> blockSet = new HashSet<Block>();
        for (int dx = 0; dx <= (north + south); dx++) {
            for (int dz = 0; dz <= (east + west); dz++) {
                tempBlock = getTypeInColumn(start.getRelative(dx, 0, dz), gateMaterial, false);
                if (tempBlock != null) {
                    blockSet.add(getUpperBlock(tempBlock));
                }
            }
        }
        if (!blockSet.isEmpty()) {
            tempBlock = blockSet.iterator().next();
            blockMap = new BlockMap(blockSet, null, null, tempBlock.getType(), tempBlock.getData());
        } else {
            throw new BlockMapException(BlockMapException.Type.MECHANIC_NOT_FOUND);
        }

    }

    private boolean isOpen() throws PlayerNotifier {
        try {
            return blockMap.getSet().iterator().next().getRelative(BlockFace.DOWN).getType() == Material.AIR;
        } catch (NullPointerException e) {
            throw new PlayerNotifier("Couldn't find any gates, blocklist is empty!", PlayerNotifier.Level.WARNING, null);
        }
    }

    @Override
    protected void close() throws PlayerNotifier {
        int n = 0;
        for (Block b : blockMap.getSet()) {
            int dy = 1;
            Block temp = b.getRelative(0, dy, 0);
            while (temp.getType() == Material.AIR && dy < config.getMaxHeight()) {
                b.setTypeIdAndData(blockMap.getMaterial().getId(), blockMap.getMaterialData(), false);
                n++;
            }
        }
        if (!blockBag.removeItems(blockMap.getMaterial().getId(), blockMap.getMaterialData(), n)) {
            PlayerNotifier playerNotifier = new PlayerNotifier("There are not enough items in the chest to open the gate. Still need " + n + " items.", PlayerNotifier.Level.WARNING, blockBag.getLocation(true));
            for (Block b : blockMap.getSet()) {
                int dy = 1;
                Block temp = b.getRelative(0, dy, 0);
                while (temp.getType() == blockMap.getMaterial() && temp.getData() == blockMap.getMaterialData() && dy < config.getMaxHeight()) {
                    b.setTypeIdAndData(blockMap.getMaterial().getId(), blockMap.getMaterialData(), false);
                    n--;
                    if (n == 0)
                        break;
                }
                if (n == 0)
                    break;
            }
            throw playerNotifier;
        }
    }

    @Override
    protected void open() throws PlayerNotifier {
        int n = 0;
        for (Block b : blockMap.getSet()) {
            int dy = 1;
            Block temp = b.getRelative(0, dy, 0);
            while (temp.getType() == Material.AIR && dy < config.getMaxHeight()) {
                b.setTypeIdAndData(blockMap.getMaterial().getId(), blockMap.getMaterialData(), false);
                n++;
            }
        }
        if (!blockBag.removeItems(blockMap.getMaterial().getId(), blockMap.getMaterialData(), n)) {
            PlayerNotifier playerNotifier = new PlayerNotifier("There's not enough space left in the chest to close the gate. Still need " + n + " empty spaces.", PlayerNotifier.Level.WARNING, blockBag.getLocation(true));
            for (Block b : blockMap.getSet()) {
                int dy = 1;
                Block temp = b.getRelative(0, dy, 0);
                while (temp.getType() == blockMap.getMaterial() && temp.getData() == blockMap.getMaterialData() && dy < config.getMaxHeight()) {
                    b.setTypeIdAndData(blockMap.getMaterial().getId(), blockMap.getMaterialData(), false);
                    n--;
                    if (n == 0)
                        break;
                }
                if (n == 0)
                    break;
            }
            throw playerNotifier;
        }
    }
}
