package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemRoidRage extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setSpeed(0.6f);
        stats.setRange(5.25f);
        stats.setShootSpeed(0.5f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Roid Rage";
    }

    @Override
    public String getDescription() {
        return "SPEED UP, RANGE UP, SHOT SPEED UP";
    }
}
