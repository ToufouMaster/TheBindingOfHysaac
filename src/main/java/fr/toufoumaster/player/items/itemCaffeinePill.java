package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

// Change the name here too
public class itemCaffeinePill extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setSpeed(0.3f);
        //réduit la taille d'isaac et donne une pill random
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Caffeine Pill"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "Speed UP, Size DOWN"; // Change the description here
    }
}