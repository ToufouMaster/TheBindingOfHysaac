package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMagicScab extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setLuck(1);
        // one heart container
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Magic Scab";
    }

    @Override
    public String getDescription() {
        return "LUCK UP, HP UP";
    }
}
