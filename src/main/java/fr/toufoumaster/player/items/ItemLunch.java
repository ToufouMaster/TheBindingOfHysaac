package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemLunch extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        // One heart Container
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Lunch";
    }

    @Override
    public String getDescription() {
        return "HP UP";
    }
}
