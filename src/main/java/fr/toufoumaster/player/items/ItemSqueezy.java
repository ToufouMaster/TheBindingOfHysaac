package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemSqueezy extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.4f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Squeezy";
    }

    @Override
    public String getDescription() {
        return "TEARS UP";
    }
}
