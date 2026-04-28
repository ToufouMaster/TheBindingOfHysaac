package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemTornPhoto extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.7f);
        stats.setShootSpeed(0.16f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Torn Photo";
    }

    @Override
    public String getDescription() {
        return "TEARS UP, SHOT SPEED UP";
    }
}
