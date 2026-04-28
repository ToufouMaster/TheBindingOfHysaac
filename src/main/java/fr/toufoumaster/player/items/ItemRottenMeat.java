package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemRottenMeat extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        // No stat modifiers implemented for this item.
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Rotten Meat";
    }

    @Override
    public String getDescription() {
        return "Not yet implemented";
    }
}
