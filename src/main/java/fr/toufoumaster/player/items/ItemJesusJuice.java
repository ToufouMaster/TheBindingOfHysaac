package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemJesusJuice extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(0.5f);
        stats.setRange(1.5f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Jesus Juice";
    }

    @Override
    public String getDescription() {
        return "DMG UP, RANGE UP";
    }
}
