package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemLatchKey extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setLuck(1);
        // one soul heart and spawns 2 keys
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Latch Key";
    }

    @Override
    public String getDescription() {
        return "Luck UP";
    }
}
