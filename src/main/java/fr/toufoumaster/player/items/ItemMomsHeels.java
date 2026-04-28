package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMomsHeels extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(2.5f);
        // enemies that touch isaac take 12damage
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Mom's Heels";
    }

    @Override
    public String getDescription() {
        return "RANGE UP";
    }
}
