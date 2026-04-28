package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMrDolly extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.7f);
        stats.setRange(5.25f);
        stats.setShootSpeed(0.5f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Mr. Dolly";
    }

    @Override
    public String getDescription() {
        return "TEARS UP, RANGE UP, SHOT SPEED UP";
    }
}
