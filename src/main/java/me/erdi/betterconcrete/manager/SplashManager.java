package me.erdi.betterconcrete.manager;

import me.erdi.betterconcrete.api.event.block.BlockPowderToConcreteEvent;
import me.erdi.betterconcrete.api.event.item.EntityPowderToConcreteEvent;
import me.erdi.betterconcrete.api.type.ConcreteType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

public class SplashManager implements Listener {
    private static final int X_RAD = 2;
    private static final int Y_RAD = 1;
    private static final int Z_RAD = 2;

    @EventHandler
    public void onPotionSplash(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if(projectile.getType() != EntityType.SPLASH_POTION)
            return;

        ThrownPotion potion = (ThrownPotion) projectile;
        if(!potion.getEffects().isEmpty())
            return;

        List<Entity> entities = potion.getNearbyEntities(X_RAD, Y_RAD, Z_RAD);
        for(Entity entity : entities) {
            if(!(entity instanceof Item))
                continue;

            Item item = (Item) entity;
            if(ConcreteType.getByItem(item.getItemStack()) == null ||
                    !item.getItemStack().getType().name().contains("POWDER"))
                continue;

            EntityPowderToConcreteEvent convertEvent = new EntityPowderToConcreteEvent(item);
            Bukkit.getPluginManager().callEvent(convertEvent);

            if(!convertEvent.isCancelled())
                ConcreteType.turnPowderToConcrete(item);
        }

        Block middle = potion.getLocation().getBlock();
        for(int x = -X_RAD; x <= X_RAD; x++) {
            for(int y = -Y_RAD; y <= Y_RAD; y++) {
                for(int z = -Z_RAD; z <= Z_RAD; z++) {
                    Block powder = middle.getRelative(x, y, z);
                    if(ConcreteType.getByBlock(powder) == null ||
                            !powder.getType().name().contains("POWDER"))
                        continue;

                    BlockPowderToConcreteEvent convertEvent = new BlockPowderToConcreteEvent(powder);
                    Bukkit.getPluginManager().callEvent(convertEvent);

                    if(!convertEvent.isCancelled())
                        ConcreteType.turnPowderToConcrete(powder);
                }
            }
        }
    }
}
