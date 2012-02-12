/*
 * Copyright (c) 2012.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.edoxile.bettermechanics.models;

import net.edoxile.bettermechanics.BetterMechanics;
import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class MechanicsConfigHandler {
    private static MechanicsConfigHandler instance = new MechanicsConfigHandler();
    private static BetterMechanics plugin = BetterMechanics.getInstance();

    private MechanicsConfigHandler() {
        configuration.load();
        if (configuration == null) {
            createConfig();
            configuration.load();
            BetterMechanics.log("A new config file was created. It is recommended that you reload Bukkit.");
        }
        bridgeConfig = new BridgeConfig();
        gateConfig = new GateConfig();
        doorConfig = new DoorConfig();
        liftConfig = new LiftConfig();
        teleLiftConfig = new TeleLiftConfig();
        hiddenSwitchConfig = new HiddenSwitchConfig();
        ammeterConfig = new AmmeterConfig();
        cauldronConfig = new CauldronConfig();
        penConfig = new PenConfig();
    }

    public static MechanicsConfigHandler getInstance() {
        return instance;
    }

    private Configuration configuration = BetterMechanics.getInstance().getConfig();
    private BridgeConfig bridgeConfig;
    private GateConfig gateConfig;
    private DoorConfig doorConfig;
    private LiftConfig liftConfig;
    private TeleLiftConfig teleLiftConfig;
    private HiddenSwitchConfig hiddenSwitchConfig;
    private AmmeterConfig ammeterConfig;
    private CauldronConfig cauldronConfig;
    private PenConfig penConfig;

    public Configuration getConfiguration() {
        return configuration;
    }

    private void createConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.canRead()) {
            try {
                if (configFile.getParentFile().mkdirs()) {
                    JarFile jar = new JarFile(plugin.getJarFile());
                    JarEntry entry = jar.getJarEntry("config.yml");
                    InputStream is = jar.getInputStream(entry);
                    FileOutputStream os = new FileOutputStream(configFile);
                    byte[] buf = new byte[(int) entry.getSize()];
                    if (is.read(buf, 0, (int) entry.getSize()) == (int) entry.getSize()) {
                        os.write(buf);
                        os.close();
                        plugin.getConfiguration().load();
                    } else {
                        BetterMechanics.log("Error while creating new config: buffer overflow.", Level.WARNING);
                    }
                } else {
                    BetterMechanics.log("Error while creating directories: no permission.", Level.WARNING);
                }
            } catch (Exception e) {
                BetterMechanics.log("Error while creating new config: " + e.getMessage());
            }
        }
    }

    public void reloadCauldronConfig() {
        cauldronConfig = new CauldronConfig();
    }

    public class BridgeConfig {
        public final boolean enabled;
        public final Set<Material> materials;
        public final int maxLength;

        public BridgeConfig() {
            enabled = configuration.getBoolean("bridge.enabled", true);
            maxLength = configuration.getInt("bridge.max-length", 32);
            List<Integer> list = configuration.getIntList("bridge.allowed-materials", Arrays.asList(3, 4, 5, 22, 35, 41, 42, 45, 47, 57, 87, 88, 89, 91));
            Set<Material> hashSet = new HashSet<Material>();
            for (int m : list)
                hashSet.add(Material.getMaterial(m));
            materials = Collections.unmodifiableSet(hashSet);
        }

        public boolean canUseBlock(Material b) {
            return materials.contains(b);
        }
    }

    public class GateConfig {
        public final boolean enabled;
        public final int maxLength;
        public final int maxWidth;
        public final int maxHeight;
        public final Set<Material> materials;

        public GateConfig() {
            enabled = configuration.getBoolean("gate.enabled", true);
            maxHeight = configuration.getInt("gate.max-height", 32);
            maxLength = configuration.getInt("gate.max-length", 32);
            maxWidth = configuration.getInt("gate.max-width", 3);
            List<Integer> list = configuration.getIntList("gate.allowed-materials", Arrays.asList(1));
            Set<Material> set = new HashSet<Material>();
            for (int m : list) {
                set.add(Material.getMaterial(m));
            }
            materials = Collections.unmodifiableSet(set);
        }
    }

    public class PenConfig {
        private final boolean enabled;
        private final Material penTool;

        public PenConfig() {
            enabled = configuration.getBoolean("pen.enabled", true);
            penTool = Material.getMaterial(configuration.getInt("pen.material", 280));
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Material getPenTool() {
            return penTool;
        }
    }

    public class DoorConfig {
        public final boolean enabled;
        public final int maxHeight;
        public final Set<Material> materials;

        public DoorConfig() {
            enabled = configuration.getBoolean("door.enabled", true);
            maxHeight = configuration.getInt("door.max-height", 32);
            List<Integer> list = configuration.getIntList("door.allowed-materials", Arrays.asList(3, 4, 5, 22, 35, 41, 42, 45, 47, 57, 87, 88, 89, 91));
            Set<Material> set = new HashSet<Material>();
            for (int m : list) {
                set.add(Material.getMaterial(m));
            }
            materials = Collections.unmodifiableSet(set);
        }

        public boolean canUseBlock(Material b) {
            return materials.contains(b);
        }
    }

    public class LiftConfig {
        public final boolean enabled;
        public final int maxSearchHeight;

        public LiftConfig() {
            enabled = configuration.getBoolean("lift.enabled", true);
            maxSearchHeight = configuration.getInt("lift.max-search-height", 32);
        }
    }

    public class TeleLiftConfig {
        public final boolean enabled;

        public TeleLiftConfig() {
            enabled = configuration.getBoolean("telelift.enabled", true);
        }
    }

    public class HiddenSwitchConfig {
        public final boolean enabled;

        public HiddenSwitchConfig() {
            enabled = configuration.getBoolean("hidden-switch.enabled", true);
        }
    }

    public class CauldronConfig {
        public final boolean enabled;
        public final CauldronCookbook cauldronCookbook;

        public CauldronConfig() {
            if (configuration.getBoolean("cauldron.enabled", true)) {
                cauldronCookbook = new CauldronCookbook(plugin);
                if (cauldronCookbook.size() > 0) {
                    enabled = true;
                } else {
                    BetterMechanics.log("Disabled cauldron because there were no recipes found in the config.", Level.WARNING);
                    enabled = false;
                }
            } else {
                cauldronCookbook = null;
                enabled = false;
            }
        }
    }

    public class AmmeterConfig {
        public final boolean enabled;

        public AmmeterConfig() {
            enabled = configuration.getBoolean("ammeter.enabled", true);
        }
    }

    public BridgeConfig getBridgeConfig() {
        return this.bridgeConfig;
    }

    public GateConfig getGateConfig() {
        return this.gateConfig;
    }

    public DoorConfig getDoorConfig() {
        return this.doorConfig;
    }

    public HiddenSwitchConfig getHiddenSwitchConfig() {
        return this.hiddenSwitchConfig;
    }

    public LiftConfig getLiftConfig() {
        return this.liftConfig;
    }

    public TeleLiftConfig getTeleLiftConfig() {
        return this.teleLiftConfig;
    }

    public AmmeterConfig getAmmeterConfig() {
        return this.ammeterConfig;
    }

    public CauldronConfig getCauldronConfig() {
        return this.cauldronConfig;
    }

    public PenConfig getPenConfig() {
        return this.penConfig;
    }
}
