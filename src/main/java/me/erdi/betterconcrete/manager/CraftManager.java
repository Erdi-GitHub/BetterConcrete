package me.erdi.betterconcrete.manager;

import me.erdi.betterconcrete.api.type.ConcreteType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftManager implements Listener {
    private final JavaPlugin plugin;

    public CraftManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if(!(recipe instanceof ShapelessRecipe))
            return;

        if(!((ShapelessRecipe) recipe).getKey().getNamespace().equalsIgnoreCase(plugin.getName()))
            return;

        for(ItemStack stack : event.getInventory().getMatrix()) {
            if(stack == null)
                continue;

            ConcreteType type = ConcreteType.getByItem(stack);
            if(type != null) {
                event.getInventory().getResult().setAmount(stack.getAmount());
                break;
            }
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        if(!(recipe instanceof ShapelessRecipe))
            return;

        if(!((ShapelessRecipe) recipe).getKey().getNamespace().equalsIgnoreCase(plugin.getName()))
            return;

        ItemStack[] matrix = event.getInventory().getMatrix();
        for(int i = 0; i < matrix.length; i++) {
            ItemStack stack = matrix[i];
            if(stack == null)
                continue;

            ConcreteType type = ConcreteType.getByItem(stack);
            if(type != null) {
                stack.setAmount(0);
                continue;
            }

            if(stack.getType() == Material.POTION) {
                int finalI = i;

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ItemStack[] invMatrix = event.getInventory().getMatrix();
                    invMatrix[finalI] = new ItemStack(Material.GLASS_BOTTLE);

                    event.getInventory().setMatrix(invMatrix);
                }, 1);
            }

            if(stack.getType() == Material.WATER_BUCKET) {
                int finalI = i;

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ItemStack[] invMatrix = event.getInventory().getMatrix();
                    invMatrix[finalI] = new ItemStack(Material.BUCKET);

                    event.getInventory().setMatrix(invMatrix);
                }, 1);
            }
        }
    }
}
