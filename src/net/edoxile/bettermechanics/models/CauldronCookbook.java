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

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */


import net.edoxile.bettermechanics.BetterMechanics;
import net.edoxile.bettermechanics.utils.MaterialMap;
import net.edoxile.bettermechanics.utils.MaterialMapIterator;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Store of recipes.
 *
 * @author sk89q, Edoxile
 */
public class CauldronCookbook {
    private List<Recipe> recipes = new ArrayList<Recipe>();
    private static final Logger log = Logger.getLogger("Minecraft");
    private BetterMechanics instance;

    @SuppressWarnings("unchecked")
    public CauldronCookbook(BetterMechanics plugin) {
        try {
            File configFile = new File("plugins/BetterMechanics/cauldron-recipes.yml");
            BetterMechanics.log("Loading cauldron recipes from " + configFile.getAbsolutePath());
            Configuration config = new Configuration(configFile);
            config.load();
            List<String> recipeNames = config.getKeys("recipes");
            if (recipeNames == null) {
                BetterMechanics.log("Error loading cauldron recipes: no recipes found! (you probably messed up the yml format somewhere)");
                return;
            }
            for (String name : recipeNames) {
                MaterialMap ingredients = new MaterialMap();
                MaterialMap results = new MaterialMap();

                try {
                    List<List<Integer>> list = (List<List<Integer>>) config.getProperty("recipes." + name + ".ingredients");
                    for (List<Integer> l : list) {
                        ingredients.put(l.get(0), l.get(1).byteValue(), l.get(2));
                    }
                    list = (List<List<Integer>>) config.getProperty("recipes." + name + ".results");
                    for (List<Integer> l : list) {
                        results.put(l.get(0), l.get(1).byteValue(), l.get(2));
                    }
                } catch (Exception e) {
                    recipes.clear();
                    BetterMechanics.log("Error loading cauldron recipes: " + e.getMessage() + "(" + e.getClass().getName() + ") (you probably messed up the yaml format somewhere)");
                    return;
                }

                add(new Recipe(name, ingredients, results));
            }
            BetterMechanics.log("Cauldron loaded " + size() + " recipes.");
        } catch (Exception e) {
            BetterMechanics.log("Something went wrong loading the config file.");
        }
    }

    public void add(Recipe recipe) {
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

    public static final class Recipe {
        private final String name;
        private final MaterialMap ingredients;
        private final MaterialMap results;

        public Recipe(String name, MaterialMap ingredients, MaterialMap results) {
            this.name = name;
            this.ingredients = ingredients;
            this.results = results;
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
    }
}