package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemTropicamide extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setRange(2.5f);
        stats.setTearSize(0.25f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Tropicamide";
    }

    @Override
    public String getDescription() {
        return "RANGE UP, INCREASED TEAR SIZE";
    }
}
