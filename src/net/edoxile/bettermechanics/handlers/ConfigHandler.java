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

package net.edoxile.bettermechanics.handlers;

import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.models.CauldronCookbook;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */
public class ConfigHandler {
    private static File configFile;
    private static FileConfiguration configuration;
    private static File cauldronConfigFile;
    private static FileConfiguration cauldronConfiguration;

    public ConfigHandler(BetterMechanics plugin) {

        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "config.yml");
        if (cauldronConfigFile == null)
            cauldronConfigFile = new File(plugin.getDataFolder(), "cauldron-recipes.yml");
        try {
            configuration = new YamlConfiguration();
            configuration.load(configFile);

            cauldronConfiguration = new YamlConfiguration();
            cauldronConfiguration.load(cauldronConfigFile);
        } catch (FileNotFoundException e) {
            BetterMechanics.log("The config-file was not found: " + e.getMessage(), Level.WARNING);
        } catch (InvalidConfigurationException e) {
            BetterMechanics.log("The config-file has an invalid syntax: " + e.getMessage(), Level.WARNING);
        } catch (IOException e) {
            BetterMechanics.log("The config-file could not be read", Level.WARNING);
        }

        bridgeConfig = new BridgeConfig();
        gateConfig = new GateConfig();
        doorConfig = new DoorConfig();
        liftConfig = new LiftConfig();
        teleLiftConfig = new TeleLiftConfig();
        hiddenSwitchConfig = new HiddenSwitchConfig();
        ammeterConfig = new AmmeterConfig();
        cauldronConfig = new CauldronConfig(this);
        penConfig = new PenConfig();
        cyclerConfig = new CyclerConfig();
        powerBlockConfig = new PowerBlockConfig();
    }

    private final BridgeConfig bridgeConfig;
    private final GateConfig gateConfig;
    private final DoorConfig doorConfig;
    private final LiftConfig liftConfig;
    private final TeleLiftConfig teleLiftConfig;
    private final HiddenSwitchConfig hiddenSwitchConfig;
    private final AmmeterConfig ammeterConfig;
    private CauldronConfig cauldronConfig;
    private final CyclerConfig cyclerConfig;
    private PowerBlockConfig powerBlockConfig;
    private final PenConfig penConfig;
    private PermissionsConfig permisionsConfig;

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public static void createConfig(BetterMechanics plugin) {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        cauldronConfigFile = new File(plugin.getDataFolder(), "cauldron-recipes.yml");

        try {
            JarFile jar = new JarFile(plugin.getJarFile());
            JarEntry entry = jar.getJarEntry("config.yml");
            InputStream is = jar.getInputStream(entry);
            FileOutputStream os = new FileOutputStream(configFile);
            byte[] buf = new byte[(int) entry.getSize()];
            if (is.read(buf, 0, (int) entry.getSize()) == (int) entry.getSize()) {
                os.write(buf);
                os.close();
            } else {
                BetterMechanics.log("Error while creating new config: buffer overflow", Level.WARNING);
            }

            jar = new JarFile(plugin.getJarFile());
            entry = jar.getJarEntry("cauldron-recipes.yml");
            is = jar.getInputStream(entry);
            os = new FileOutputStream(cauldronConfigFile);
            buf = new byte[(int) entry.getSize()];
            if (is.read(buf, 0, (int) entry.getSize()) == (int) entry.getSize()) {
                os.write(buf);
                os.close();
            } else {
                BetterMechanics.log("Error while creating new cauldron-config: buffer overflow", Level.WARNING);
            }
        } catch (IOException e) {
            BetterMechanics.log("Couldn't write to config file");
        }
    }

    public void reloadCauldronConfig() {
        cauldronConfig.getCookBook().reloadConfigFile();
    }

    public class BridgeConfig {
        private final List<Material> defaultMaterials = Arrays.asList(
                Material.WOOD, Material.SPONGE, Material.LAPIS_BLOCK, Material.WOOL, Material.GOLD_BLOCK, Material.IRON_BLOCK,
                Material.STEP, Material.BRICK, Material.BOOKSHELF, Material.DIAMOND_BLOCK, Material.SNOW_BLOCK
        );
        private final boolean enabled;
        private final Set<Material> materials;
        private final int maxLength;

        public BridgeConfig() {
            enabled = configuration.getBoolean("bridge.enabled", true);
            maxLength = configuration.getInt("bridge.max-length", 32);
            List<Integer> list = configuration.getIntegerList("bridge.allowed-materials");
            Set<Material> hashSet = new HashSet<Material>();
            if (list.isEmpty()) {
                hashSet.addAll(defaultMaterials);
            } else {
                for (int m : list) {
                    hashSet.add(Material.getMaterial(m));
                }
            }
            materials = Collections.unmodifiableSet(hashSet);
        }

        public boolean canUseMaterial(Material m) {
            return materials.contains(m);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public Set<Material> getAllowedMaterials() {
            return materials;
        }
    }

    public class GateConfig {
        private final List<Material> defaultMaterials = Arrays.asList(
                Material.FENCE, Material.IRON_FENCE
        );
        private final boolean enabled;
        private final int maxLength;
        private final int maxWidth;
        private final int maxHeight;
        private final Set<Material> materials;

        public GateConfig() {
            enabled = configuration.getBoolean("gate.enabled", true);
            maxHeight = configuration.getInt("gate.max-height", 32);
            maxLength = configuration.getInt("gate.max-length", 32);
            maxWidth = configuration.getInt("gate.max-width", 3);
            List<Integer> list = configuration.getIntegerList("gate.allowed-materials");
            Set<Material> set = new HashSet<Material>();
            if (list.isEmpty()) {
                set.addAll(defaultMaterials);
            } else {
                for (int m : list) {
                    set.add(Material.getMaterial(m));
                }
            }
            materials = Collections.unmodifiableSet(set);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public int getMaxWidth() {
            return maxWidth;
        }

        public int getMaxHeight() {
            return maxHeight;
        }

        public boolean canUseMaterial(Material m) {
            return materials.contains(m);
        }

        public Set<Material> getAllowedMaterials() {
            return materials;
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

    public class CyclerConfig {
        private final boolean enabled;
        private final Material cyclerTool;

        public CyclerConfig() {
            enabled = configuration.getBoolean("cycler.enabled", true);
            cyclerTool = Material.getMaterial(configuration.getInt("cycler.material", 280));
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Material getCyclerTool() {
            return cyclerTool;
        }
    }

    public class DoorConfig {
        private final List<Material> defaultMaterials = Arrays.asList(
                Material.WOOD, Material.SPONGE, Material.LAPIS_BLOCK, Material.WOOL, Material.GOLD_BLOCK, Material.IRON_BLOCK,
                Material.STEP, Material.BRICK, Material.BOOKSHELF, Material.DIAMOND_BLOCK, Material.SNOW_BLOCK
        );
        private final boolean enabled;
        private final int maxHeight;
        private final Set<Material> materials;

        public DoorConfig() {
            enabled = configuration.getBoolean("door.enabled", true);
            maxHeight = configuration.getInt("door.max-height", 32);
            List<Integer> list = configuration.getIntegerList("door.allowed-materials");
            Set<Material> set = new HashSet<Material>();
            if (list.isEmpty()) {
                set.addAll(defaultMaterials);
            } else {
                for (int m : list) {
                    set.add(Material.getMaterial(m));
                }
            }
            materials = Collections.unmodifiableSet(set);
        }

        public boolean canUseMaterial(Material m) {
            return materials.contains(m);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getMaxHeight() {
            return maxHeight;
        }
    }

    public class LiftConfig {
        private final boolean enabled;
        private final int maxSearchHeight;

        public LiftConfig() {
            enabled = configuration.getBoolean("lift.enabled", true);
            maxSearchHeight = configuration.getInt("lift.max-search-height", 32);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getMaxSearchHeight() {
            return maxSearchHeight;
        }
    }

    public class TeleLiftConfig {
        private final boolean enabled;

        public TeleLiftConfig() {
            enabled = configuration.getBoolean("telelift.enabled", true);
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public class HiddenSwitchConfig {
        private final boolean enabled;

        public HiddenSwitchConfig() {
            enabled = configuration.getBoolean("hidden-switch.enabled", true);
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public class PowerBlockConfig {
        private final boolean enabled;
        private HashMap<Material, Material> powerMap = new HashMap<Material, Material>();
        private HashMap<Material, Material> unpowerMap = new HashMap<Material, Material>();

        public PowerBlockConfig() {
            enabled = configuration.getBoolean("powered-block.enabled", false);
            if (enabled) {
                Set<String> keys = configuration.getKeys(true);

                HashSet<String> names = new HashSet<String>();
                for (String key : keys) {
                    String[] splitKeys = key.split("\\.");
                    if (splitKeys.length == 2) {
                        names.add(splitKeys[1]);
                    }
                }
                if (names.isEmpty()) {
                    BetterMechanics.log("Error loading powered blocks: no blockchanges found! (you probably messed up the yml format somewhere)");
                    return;
                }
                for (String name : names) {
                    Material powered = Material.getMaterial(configuration.getInt("powered-block.blocks." + name + ".powered"));
                    Material unpowered = Material.getMaterial(configuration.getInt("powered-block.blocks." + name + ".unpowered"));

                    if (powered != null && unpowered != null) {
                        powerMap.put(unpowered, powered);
                        unpowerMap.put(powered, unpowered);
                    } else {
                        if (powered == null) {
                            BetterMechanics.log("The 'powered' blockid of " + name + " is an invalid Material.", Level.WARNING);
                        } else {
                            BetterMechanics.log("The 'unpowered' blockid of " + name + " is an invalid Material.", Level.WARNING);
                        }
                    }
                }
            }
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Material getPoweredBlock(Material unpowered) {
            return powerMap.get(unpowered);
        }

        public Material getUnpoweredBlock(Material powered) {
            return unpowerMap.get(powered);
        }
    }

    public class CauldronConfig {
        private final boolean enabled;
        private final CauldronCookbook cauldronCookbook;

        public CauldronConfig(ConfigHandler ch) {
            if (configuration.getBoolean("cauldron.enabled", true)) {
                cauldronCookbook = new CauldronCookbook(ch);
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

        public boolean isEnabled() {
            return enabled;
        }

        public CauldronCookbook getCookBook() {
            return cauldronCookbook;
        }
    }

    public class AmmeterConfig {
        private final boolean enabled;
        private final Material tool;

        public AmmeterConfig() {
            enabled = configuration.getBoolean("ammeter.enabled", true);
            //TODO: check if tool id is correct (coal)
            tool = Material.getMaterial(configuration.getInt("ammeter.tool", 283));
        }

        public boolean isEnabled() {
            return enabled;
        }

        public Material getTool(){
            return tool;
        }
    }

    public class PermissionsConfig {
        private final boolean useZones;
        private final boolean useWorldGuard;
        private final boolean enabled;

        public PermissionsConfig() {
            useZones = configuration.getBoolean("permissions.use-zones", false);
            useWorldGuard = configuration.getBoolean("permissions.use-worldguard", false);
            enabled = configuration.getBoolean("permissions.use-permissions", false);
        }

        public boolean canUseZones() {
            return useZones;
        }

        public boolean canUseWorldGuard() {
            return useWorldGuard;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public BridgeConfig getBridgeConfig() {
        return bridgeConfig;
    }

    public GateConfig getGateConfig() {
        return gateConfig;
    }

    public DoorConfig getDoorConfig() {
        return doorConfig;
    }

    public HiddenSwitchConfig getHiddenSwitchConfig() {
        return hiddenSwitchConfig;
    }

    public LiftConfig getLiftConfig() {
        return liftConfig;
    }

    public TeleLiftConfig getTeleLiftConfig() {
        return teleLiftConfig;
    }

    public AmmeterConfig getAmmeterConfig() {
        return ammeterConfig;
    }

    public CauldronConfig getCauldronConfig() {
        return cauldronConfig;
    }

    public PenConfig getPenConfig() {
        return penConfig;
    }

    public PermissionsConfig getPermissionsConfig() {
        return permisionsConfig;
    }

    public CyclerConfig getCyclerConfig() {
        return cyclerConfig;
    }

    public PowerBlockConfig getPowerBlockConfig() {
        return powerBlockConfig;
    }

    public File getCauldronConfigFile() {
        return cauldronConfigFile;
    }

    public FileConfiguration getCauldronConfiguration() {
        return cauldronConfiguration;
    }
}