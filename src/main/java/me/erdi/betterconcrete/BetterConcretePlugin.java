package me.erdi.betterconcrete;

import me.erdi.betterconcrete.api.type.ConcreteType;
import me.erdi.betterconcrete.manager.CraftManager;
import me.erdi.betterconcrete.manager.SplashManager;
import me.erdi.betterconcrete.manager.WetManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "BetterConcrete", version = "1.0.0")
@Description("Makes concrete make sense")
@Author("Erdi__")
@ApiVersion(ApiVersion.Target.v1_13)
public class BetterConcretePlugin extends JavaPlugin implements Listener {
    private static boolean legacy = Material.getMaterial("WHITE_CONCRETE") == null;

    public static final Material LEGACY_CONCRETE = Material.getMaterial("CONCRETE");
    public static final Material LEGACY_CONCRETE_POWDER = Material.getMaterial("CONCRETE_POWDER");

    @Override
    public void onLoad() {
        if(legacy && Material.getMaterial("CONCRETE") == null) {
            getLogger().severe(getDescription().getName() + " does not support versions lower than 1.12. Disabling...");
            getPluginLoader().disablePlugin(this);
        }

        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CraftManager(this), this);
        getServer().getPluginManager().registerEvents(new SplashManager(), this);
        getServer().getPluginManager().registerEvents(
                new WetManager(this, Math.max(1, getConfig().getInt("water-check-interval"))),
                this
        );

        registerRecipes();
    }

    private boolean registerRecipe(Recipe recipe) {
        try {
            return Bukkit.addRecipe(recipe);
        } catch(IllegalStateException e) {
            return false;
        }
    }

    private void registerRecipes() {
        getLogger().info("Registering recipes...");
        int added = 0;

        if(isLegacy()) {
            for(ConcreteType type : ConcreteType.values()) {
                byte data = type.getLegacyId();
                boolean success = registerRecipe(
                        new ShapelessRecipe(
                                new NamespacedKey(this, type.name()), new ItemStack(LEGACY_CONCRETE, 1, (short)0, data)
                        ).addIngredient(new MaterialData(LEGACY_CONCRETE_POWDER, data)).addIngredient(Material.WATER_BUCKET)
                );

                if(success)
                    added++;

                success = registerRecipe(
                        new ShapelessRecipe(
                                new NamespacedKey(this, type.name() + "_POT"), new ItemStack(LEGACY_CONCRETE, 1, (short)0, data)
                        ).addIngredient(new MaterialData(LEGACY_CONCRETE_POWDER, data)).addIngredient(Material.POTION)
                );

                if(success)
                    added++;
            }
        } else {
            for(ConcreteType type : ConcreteType.values()) {
                ItemStack result = new ItemStack(Material.valueOf(type.getConcreteId()));
                Material powder = Material.valueOf(type.getPowderId());

                boolean success = registerRecipe(
                        new ShapelessRecipe(
                                new NamespacedKey(this, type.name()), result
                        ).addIngredient(powder).addIngredient(Material.WATER_BUCKET)
                );

                if(success)
                    added++;

                success = registerRecipe(
                        new ShapelessRecipe(
                                new NamespacedKey(this, type.name() + "_POT"), result
                        ).addIngredient(powder).addIngredient(Material.POTION)
                );

                if(success)
                    added++;
            }
        }

        getLogger().info("Registered " + added + " recipes.");
    }


    public static boolean isLegacy() {
        return legacy;
    }
}