package net.edoxile.bettermechanics.event;

import org.bukkit.block.Block;

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

    public RedstoneEvent(State s, Type t, Block b) {
        super(t, b);
        state = s;
    }
    
    public State getState(){
        return state;
    }
}
