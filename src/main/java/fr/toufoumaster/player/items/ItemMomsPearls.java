package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMomsPearls extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(1.25f);
        stats.setShootSpeed(0.5f);
        stats.setLuck(1);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Mom's Pearls";
    }

    @Override
    public String getDescription() {
        return "RANGE UP, SHOT SPEED UP, LUCK UP";
    }
}
