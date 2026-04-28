package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

// Change the name here too
public class itemCatoninetails extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(1.0f);
        stats.setShootSpeed(0.23f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Cat-o-nine-tails"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "DMG UP, RANGE UP"; // Change the description here
    }
}