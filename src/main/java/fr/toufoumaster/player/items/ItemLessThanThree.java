package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemLessThanThree extends IsaacItem {

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
        return "<3";
    }

    @Override
    public String getDescription() {
            return "Not yet implemented";
    }
}
