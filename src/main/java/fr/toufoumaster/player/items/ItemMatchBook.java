package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMatchBook extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        // one black heart, spawns two or three bombs
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Match Book";
    }

    @Override
    public String getDescription() {
        return "Not yet implemented";
    }
}
