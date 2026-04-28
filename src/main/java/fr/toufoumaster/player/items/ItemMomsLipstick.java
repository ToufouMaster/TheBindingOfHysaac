package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMomsLipstick extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(2.5f);
        // spanws a heart on pickup
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Mom's Lipstick";
    }

    @Override
    public String getDescription() {
        return "RANGE UP";
    }
}
