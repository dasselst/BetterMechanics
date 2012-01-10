package net.edoxile.bettermechanics.utils;

import net.edoxile.bettermechanics.exceptions.OutOfMaterialException;
import net.edoxile.bettermechanics.exceptions.OutOfSpaceException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

/**
 * @author GuntherDW
 */
public abstract class BlockBag {

    protected Block bagBlock = null;
    protected final Logger log = Logger.getLogger("Minecraft");

    public BlockBag(Block bag) {
        this.bagBlock = bag;
    }

    public boolean safeRemoveItems(ItemStack itemStack) throws OutOfMaterialException {
        return false;
    }

    public boolean safeAddItems(ItemStack itemStack) throws OutOfSpaceException {
        return false;
    }

    /**
     * Is this an admin only blockbag?
     *
     * @return true if it is a restricted blockbag
     */
    public boolean isRestricted() {
        return false;
    }

    /**
     * Is this BlockBag a Block Source?
     *
     * @return true if it is a blocksource
     */
    public boolean isBlockSource() {
        return false;
    }

    /**
     * Is this BlockBag a Block Hole?
     *
     * @return true if it is a blockhole
     */
    public boolean isBlockHole() {
        return false;
    }

    /**
     * What kind of material is this BlockBag made of?
     *
     * @return Material the source material of this BlockBag
     */
    public Material getSourceMaterial() {
        return null;
    }

    /**
     * Checks if the mentioned block is an instance of this BlockBag
     *
     * @param block Block to check
     * @return true if it is a suitable instance
     */
    public boolean isBlockBag(Block block) {
        return false;
    }

}
