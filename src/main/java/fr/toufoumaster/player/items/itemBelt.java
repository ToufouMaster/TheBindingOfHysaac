package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

// Change the name here too
public class itemBelt extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setSpeed(0.3f); // =
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Belt"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "Speed UP"; // Change the description here
    }
}