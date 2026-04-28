package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemSynthoil extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(1.0f);
        stats.setRange(5.25f);
        stats.setShootSpeed(0.5f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Synthoil";
    }

    @Override
    public String getDescription() {
        return "DMG UP, RANGE UP, SHOT SPEED UP";
    }
}
