package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemGrowthHormones extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(1.0f);
        stats.setSpeed(0.2f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Growth Hormones";
    }

    @Override
    public String getDescription() {
        return "DMG UP, SPEED UP";
    }
}
