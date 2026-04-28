package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemDadsLostCoin extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(1.5f);
        stats.setShootSpeed(1.0f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Dad's Lost Coin";
    }

    @Override
    public String getDescription() {
        return "RANGE UP, SHOT SPEED UP";
    }
}
