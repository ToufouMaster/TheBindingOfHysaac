package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemDessert extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        // Coeur en plus
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Dessert";
    }

    @Override
    public String getDescription() {
        return "HP UP";
    }
}
