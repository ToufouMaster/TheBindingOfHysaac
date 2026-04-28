package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMagic8Ball extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setShootSpeed(0.16f);
        // spawns a random tarot card and increase the chances of getting a planetarium.
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Magic 8 Ball";
    }

    @Override
    public String getDescription() {
        return "SHOT SPEED UP";
    }
}
