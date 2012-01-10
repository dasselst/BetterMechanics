package net.edoxile.bettermechanics.utils.BlockBags;

import net.edoxile.bettermechanics.exceptions.OutOfMaterialException;
import net.edoxile.bettermechanics.exceptions.OutOfSpaceException;
import net.edoxile.bettermechanics.utils.BlockBag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Sign;

/**
 * @author GuntherDW
 */
public class BlackHoleSign extends BlockBag {

    private Sign sign = null;

    public BlackHoleSign(Block bag) {
        super(bag);
    }

    @Override
    public boolean safeAddItems(ItemStack itemStack) throws OutOfSpaceException {
        return true;
    }

    @Override
    public boolean isBlockHole() {
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
            && ((Sign)block.getState()).getLine(1).equals("[Black Hole]")) {
            sign = (Sign) block.getState();
            return true;
        }
        return false;
    }
}
