package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemMomsCoinPurse extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        // 4pills random
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Mom's Coin Purse";
    }

    @Override
    public String getDescription() {
        return "Not yet implemented";
    }
}
