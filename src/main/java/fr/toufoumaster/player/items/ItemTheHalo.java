package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemTheHalo extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(0.3f);
        stats.setTearRate(0.2f);
        stats.setRange(0.25f);
        stats.setSpeed(0.3f);
        stats.setShootSpeed(0.5f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "The Halo";
    }

    @Override
    public String getDescription() {
        return "DMG UP, TEARS UP, RANGE UP, SPEED UP, SHOT SPEED UP";
    }
}
