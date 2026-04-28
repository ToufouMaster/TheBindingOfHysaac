package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemExperimentalTreatment extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        // Random increase or decrease of stats
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Experimental Treatment";
    }

    @Override
    public String getDescription() {
        return "Randomly modifies stats.";
    }
}
