package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMagicMushroom extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(0.3f);
        stats.setDamageMultiplier(1.5f);
        stats.setRange(5.25f);
        stats.setSpeed(0.3f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Magic Mushroom";
    }

    @Override
    public String getDescription() {
        return "DAMAGE UP, RANGE UP, SPEED UP";
    }
}
