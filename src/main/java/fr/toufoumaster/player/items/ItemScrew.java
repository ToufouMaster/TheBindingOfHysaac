package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemScrew extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.5f);
        stats.setShootSpeed(0.2f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Screw";
    }

    @Override
    public String getDescription() {
        return "TEARS UP, SHOT SPEED UP";
    }
}
