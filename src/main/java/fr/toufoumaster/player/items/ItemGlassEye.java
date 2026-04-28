package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemGlassEye extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setDamage(0.75f);
        stats.setLuck(1);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Glass Eye";
    }

    @Override
    public String getDescription() {
        return "DMG UP, LUCK UP";
    }
}
