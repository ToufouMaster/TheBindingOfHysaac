package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemBinky extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.75f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Binky";
    }

    @Override
    public String getDescription() {
        return "TEARS UP";
    }
}
