/*
 * Copyright (c) 2012 sk89q, Edoxile
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

package net.edoxile.bettermechanics.models;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Edoxile
 */


import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.handlers.ConfigHandler;
import net.edoxile.bettermechanics.utils.datastorage.MaterialMap;
import net.edoxile.bettermechanics.utils.datastorage.MaterialMapIterator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Store of recipes.
 *
 * @author sk89q, Edoxile
 */
public class CauldronCookbook {
    private final Set<Recipe> recipes = new HashSet<Recipe>();

    private final ConfigHandler configHandler;

    private File cauldronConfigFile;
    private FileConfiguration cauldronConfig;

    @SuppressWarnings("unchecked")
    public CauldronCookbook(ConfigHandler c) {
        configHandler = c;

        cauldronConfigFile = configHandler.getCauldronConfigFile();
        cauldronConfig = configHandler.getCauldronConfiguration();

        Set<String> keys = cauldronConfig.getKeys(true);
        HashSet<String> names = new HashSet<String>();
        for (String key : keys) {
            String[] splitKeys = key.split("\\.");
            if (splitKeys.length == 2) {
                names.add(splitKeys[1]);
            }
        }
        if (names.isEmpty()) {
            BetterMechanics.log("Error loading cauldron recipes: no recipes found! (you probably messed up the yml format somewhere)");
            return;
        }
        for (String name : names) {
            MaterialMap ingredients = new MaterialMap();
            MaterialMap results = new MaterialMap();

            try {
                List<List<Integer>> list = (List<List<Integer>>) cauldronConfig.get("recipes." + name + ".ingredients");
                for (List<Integer> l : list) {
                    if (l.size() == 2) {
                        ingredients.put(l.get(0), (byte) 0, l.get(1));
                    } else if (l.size() == 3) {
                        ingredients.put(l.get(0), l.get(1).byteValue(), l.get(2));
                    } else {
                        BetterMechanics.log("Cauldron recipes contains a typo in the ingredients of " + name, Level.WARNING);
                    }
                }

                list = (List<List<Integer>>) cauldronConfig.get("recipes." + name + ".results");
                for (List<Integer> l : list) {
                    if (l.size() == 2) {
                        results.put(l.get(0), (byte) 0, l.get(1));
                    } else if (l.size() == 3) {
                        results.put(l.get(0), l.get(1).byteValue(), l.get(2));
                    } else {
                        BetterMechanics.log("Cauldron recipes contains a typo in the results of " + name, Level.WARNING);
                    }
                }
            } catch (Exception e) {
                recipes.clear();
                BetterMechanics.log("Error loading cauldron recipes: " + e.getMessage() + "(" + e.getClass().getName() + ") (you probably messed up the yaml format somewhere)");
                return;
            }

            add(new Recipe(name, ingredients, results, cauldronConfig.getBoolean("recipes." + name + ".hidden", false)));
        }
        BetterMechanics.log("Cauldron loaded " + size() + " recipes");
    }

    private void add(Recipe recipe) {
        recipes.add(recipe);
    }

    public Recipe find(MaterialMap ingredients) {
        for (Recipe recipe : recipes) {
            if (recipe.hasAllIngredients(ingredients)) {
                return recipe;
            }
        }
        return null;
    }

    public int size() {
        return recipes.size();
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public static final class Recipe {
        private final String name;
        private final MaterialMap ingredients;
        private final MaterialMap results;
        private final boolean hidden;

        public Recipe(String name, MaterialMap ingredients, MaterialMap results, boolean hidden) {
            this.name = name;
            this.ingredients = ingredients;
            this.results = results;
            this.hidden = hidden;
        }

        public String getName() {
            return name;
        }

        public boolean hasAllIngredients(MaterialMap check) {
            MaterialMapIterator iterator = ingredients.iterator();
            do {
                iterator.next();
                if (check.get(iterator.id(), iterator.data()) < iterator.value()) {
                    return false;
                }
            } while (iterator.hasNext());
            return true;
        }

        public MaterialMap getResults() {
            return results;
        }

        public MaterialMap getIngredients() {
            return ingredients;
        }

        public boolean isHidden() {
            return hidden;
        }
    }

    public void reloadConfigFile() {
        try {
            cauldronConfigFile = configHandler.getCauldronConfigFile();
            cauldronConfig = new YamlConfiguration();
            cauldronConfig.load(cauldronConfigFile);
        } catch (IOException e) {
            BetterMechanics.log("Error reading cauldron-recipes.yml");
        } catch (InvalidConfigurationException e) {
            BetterMechanics.log("Error loading cauldron-recipes.yml: " + e.getMessage());
        }
    }
}