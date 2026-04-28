package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemWoodenSpoon extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setSpeed(0.3f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Wooden Spoon";
    }

    @Override
    public String getDescription() {
        return "SPEED UP";
    }
}
