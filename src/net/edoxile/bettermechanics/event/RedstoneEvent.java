package net.edoxile.bettermechanics.event;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class RedstoneEvent extends Event {
    public enum State {
        ON,
        OFF
    }

    private final State state;
    private BlockFace origin;

    public RedstoneEvent(State s) {
        super(null, null);
        state = s;
    }

    public void setData(Type t, Block b, BlockFace o) {
        super.setData(t, b);
        origin = o;
    }

    public State getState() {
        return state;
    }

    public BlockFace getOrigin() {
        return origin;
    }
}
