package net.edoxile.bettermechanics.utils.BlockBags;

import net.edoxile.bettermechanics.exceptions.OutOfMaterialException;
import net.edoxile.bettermechanics.exceptions.OutOfSpaceException;
import net.edoxile.bettermechanics.utils.BlockBag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Sign;

/**
 * @author GuntherDW
 */
public class BlockSourceSign extends BlockBag {

    private Sign sign = null;

    public BlockSourceSign(Block bag) {
        super(bag);
    }

    @Override
    public boolean safeRemoveItems(ItemStack itemStack) throws OutOfMaterialException {
        return true;
    }

    @Override
    public boolean isBlockSource() {
        return true;
    }

    @Override
    public boolean isRestricted() {
        return true;
    }

    public Material getSourceMaterial() {
        return Material.SIGN;
    }

    public boolean isBlockBag(Block block) {
        if ((block.getType().equals(Material.SIGN_POST)||block.getType().equals(Material.WALL_SIGN))
            && ((Sign)block.getState()).getLine(1).equals("[Block Source]")) {
            sign = (Sign) block.getState();
            return true;
        }

        return false;
    }

}
