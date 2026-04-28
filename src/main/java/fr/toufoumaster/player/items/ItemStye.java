package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemStye extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamageMultiplier(1.28f);
        stats.setRange(6.5f);
        stats.setShootSpeed(-0.3f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Stye";
    }

    @Override
    public String getDescription() {
        return "DMG UP, RANGE UP, SHOT SPEED DOWN";
    }
}
