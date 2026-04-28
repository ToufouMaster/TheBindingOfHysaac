package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMeat extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(0.3f);
        // One heart container
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "MEAT!";
    }

    @Override
    public String getDescription() {
        return "DMG UP, HP UP";
    }
}
