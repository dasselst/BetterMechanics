package net.edoxile.bettermechanics.mechanics;

import net.edoxile.bettermechanics.event.PlayerEvent;
import net.edoxile.bettermechanics.event.RedstoneEvent;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.mechanics.interfaces.SignMechanicListener;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class Bridge extends SignMechanicListener {

    public Bridge() {
        ConfigHandler.BridgeConfig config = ConfigHandler.getInstance().getBridgeConfig();
    }

    @Override
    public void onSignPowerOn(RedstoneEvent event) {
        //Close gate
    }

    @Override
    public void onSignPowerOff(RedstoneEvent event) {
        //Open gate
    }

    @Override
    public void onPlayerRightClickSign(PlayerEvent event) {
        //Toggle gate
    }

    @Override
    public List<String> getIdentifier() {
        return Arrays.asList("[Bridge]", "[sBridge]");
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
    public List<Material> getMechanicActivator() {
        return null;
    }

    @Override
    public String getName() {
        return "Bridge";
    }
}
