package net.edoxile.bettermechanics.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class PlayerEvent extends Event {
    public enum Action{
        RIGHT_CLICK,
        LEFT_CLICK,
        PLACE,
        BREAK
    }

    private final Player player;
    private final Action action;
    
    public PlayerEvent(Type t, Block b, Action a, Player p){
        super(t, b);
        player = p;
        action = a;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public Action getAction(){
        return action;
    }
}
