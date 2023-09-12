package me.erdi.betterconcrete.manager;

import me.erdi.betterconcrete.BetterConcretePlugin;
import me.erdi.betterconcrete.api.event.item.EntityPowderToConcreteEvent;
import me.erdi.betterconcrete.api.type.ConcreteType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WetManager implements Listener {
    private List<WeakReference<Item>> powders = new ArrayList<>();

    private boolean timerStarted = false;

    private final BetterConcretePlugin plugin;
    private final int interval;

    private static final Material WATER_MATERIAL = BetterConcretePlugin.isLegacy() ? Material.getMaterial("STATIONARY_WATER") : Material.WATER;

    public WetManager(BetterConcretePlugin plugin, int interval) {
        this.plugin = plugin;
        this.interval = interval;
    }

    private void setupTimer(BetterConcretePlugin plugin, int interval) {
        timerStarted = true;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Iterator<WeakReference<Item>> iterator = powders.iterator();
            while(iterator.hasNext()) {
                Item item = iterator.next().get();
                if(item == null || !item.isValid()) {
                    iterator.remove();
                    continue;
                }

                Block block = item.getLocation().getBlock();
                if(!checkCauldron(block) && !checkWater(block))
                    continue;

                EntityPowderToConcreteEvent convertEvent = new EntityPowderToConcreteEvent(item);
                Bukkit.getPluginManager().callEvent(convertEvent);

                if(convertEvent.isCancelled())
                    continue;

                ConcreteType.turnPowderToConcrete(item);
                iterator.remove();
            }
        }, interval, interval);
    }

    private boolean checkCauldron(Block block) {
        return (block.getType() == Material.CAULDRON || block.getType().name().equals("WATER_CAULDRON")) &&
                block.getData() > 0;
    }

    private boolean checkWater(Block block) {
        if(!BetterConcretePlugin.isLegacy() &&
                block.getBlockData() instanceof Waterlogged &&
                ((Waterlogged) block.getBlockData()).isWaterlogged())
            return true;

        return block.getType() == WATER_MATERIAL;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if(!timerStarted)
            setupTimer(plugin, interval);

        if(event.getEntity().getItemStack().getType().name().contains("POWDER"))
            powders.add(new WeakReference<>(event.getEntity()));
    }
}
