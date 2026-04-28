package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

// Change the name here too
public class ItemBloodClot extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(0.5f);
        stats.setRange(1.375f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Blood Clot"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "DMG UP, RANGE UP"; // Change the description here
    }
}