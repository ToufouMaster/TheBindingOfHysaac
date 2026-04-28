package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemWireCoatHanger extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setTearRate(0.7f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Wire Coat Hanger";
    }

    @Override
    public String getDescription() {
        return "TEARS UP";
    }
}
