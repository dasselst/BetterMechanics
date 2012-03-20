package net.edoxile.bettermechanics.event;

import org.bukkit.block.Block;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public abstract class Event {
    public enum Type {
        SIGN,
        BLOCK
    }

    protected Type type;
    protected Block block;

    public Event(Type t, Block b) {
        type = t;
        block = b;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block b) {
        block = b;
    }

    public void setData(Type t, Block b) {
        type = t;
        block = b;
    }
}
