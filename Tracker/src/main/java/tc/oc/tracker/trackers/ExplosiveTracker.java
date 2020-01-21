package tc.oc.tracker.trackers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import tc.oc.tracker.Tracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ExplosiveTracker extends Tracker {

    boolean hasOwner(@Nonnull TNTPrimed entity);

    @Nullable
    Player getOwner(@Nonnull TNTPrimed entity);

    @Nullable
    Player setOwner(@Nonnull TNTPrimed entity, @Nullable Player player);

    boolean hasPlacer(@Nonnull Block block);

    @Nullable
    Player getPlacer(@Nonnull Block block);

    @Nullable
    Player setPlacer(@Nonnull Block block, @Nullable Player player);
}
