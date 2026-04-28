package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemStemCells extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setShootSpeed(0.16f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Stem Cells";
    }

    @Override
    public String getDescription() {
        return "SHOT SPEED UP";
    }
}
