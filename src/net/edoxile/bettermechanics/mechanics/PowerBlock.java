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
import net.edoxile.bettermechanics.mechanics.interfaces.BlockMechanicListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class PowerBlock extends BlockMechanicListener {

    private ConfigHandler.PowerBlockConfig config = BetterMechanics.getInstance().getConfigHandler().getPowerBlockConfig();

    @Override
    public void onBlockPowerOn(RedstoneEvent event){
        if(!event.getBlock().getMetadata("bettermechanics.powerblock.original").get(0).asBoolean()) {

        }
    }

    @Override
    public void onBlockPowerOff(RedstoneEvent event){

    }

    @Override
    public void onBlockBreak(PlayerEvent e){
        if(e.getBukkitEvent() instanceof BlockBreakEvent){
            BlockBreakEvent event = (BlockBreakEvent) e.getBukkitEvent();
            if(!e.getBlock().getMetadata("bettermechanics.powerblock.original").get(0).asBoolean() && e.getBlock().isBlockPowered()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("Unpower this block first before destroying it.");
            }
        } else {
            BetterMechanics.log("onBlockBreak called but no BlockBreakEvent was passed.", Level.SEVERE);
        }
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
        return null;
    }

    @Override
    public Material[] getMechanicTargets() {
        return voidActor;
    }

    @Override
    public String getName() {
        return "PowerBlock";
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    private boolean checkMeta(Block b){
        List<MetadataValue> dataList = b.getMetadata("bettermechanics.powerblock.isOriginal");
        return dataList != null && dataList.size() == 1 && dataList.get(0).asBoolean();
    }

    private void putMeta(Block b, boolean bool){
        List<MetadataValue> dataList = b.getMetadata("bettermechanics.powerblock.isOriginal");
        LazyMetadataValue value = new LazyMetadataValue(BetterMechanics.getInstance(), bool);
        if(dataList != null){
            dataList.clear();
        }
        //b.setMetadata();
    }
}
