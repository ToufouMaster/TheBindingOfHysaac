package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

// Change the name here too
public class ItemBreakfast extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        //  Coeur en plus
         // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Breakfast"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "HP UP"; // Change the description here
    }
}