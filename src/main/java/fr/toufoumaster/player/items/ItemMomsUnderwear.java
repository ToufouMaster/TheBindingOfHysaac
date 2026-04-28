package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMomsUnderwear extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(2.5f);
        // spawns 3-6 blue flies on pickup
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Mom's Underwear";
    }

    @Override
    public String getDescription() {
        return "RANGE UP";
    }
}
