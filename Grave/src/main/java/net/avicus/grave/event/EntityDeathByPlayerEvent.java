package net.avicus.grave.event;

import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.joda.time.Instant;
import tc.oc.tracker.Lifetime;

import java.util.List;

@ToString(callSuper = true)
public class EntityDeathByPlayerEvent extends EntityDeathByEntityEvent<Player> {

    public EntityDeathByPlayerEvent(Entity entity, Location location, Lifetime lifetime, Instant time,
                                    List<ItemStack> drops, int droppedExp, Player cause) {
        super(entity, location, lifetime, time, drops, droppedExp, cause);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
