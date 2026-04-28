package fr.toufoumaster.player.items;

import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemPentagram extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        stats.setDamage(1.0f);
        stats.setDevilChance(0.10f);
        stats.setAngelChance(0.10f);
        // TODO: Implement transformations (simply add 1 to the transform value)
        return stats;
    }

    @Override
    public String getName() {
        return "Pentagram";
    }

    @Override
    public String getDescription() {
        return "DMG UP";
    }
}
