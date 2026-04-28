package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemSafetyPin extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(5.25f);
        stats.setShootSpeed(0.16f);
        stats.setShootSpeedMultiplier(0.5f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Safety Pin";
    }

    @Override
    public String getDescription() {
        return "RANGE UP, SHOT SPEED UP, SHOT SPEED MULTIPLIER DOWN";
    }
}
