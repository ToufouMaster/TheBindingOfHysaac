package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

// Change the name here too
public class ItemBlueCap extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.7f);
        stats.setShootSpeed(-0.16f);
        // coeur en plus
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Blue Cap"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "Tears UP, Shot Speed DOWN, HP UP"; // Change the description here
    }
}
