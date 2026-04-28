package fr.toufoumaster.player.items;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemSpeedBall extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        stats.setSpeed(0.3f);
        stats.setShootSpeed(0.2f);
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Speed Ball";
    }

    @Override
    public String getDescription() {
        return "SPEED UP, SHOT SPEED UP";
    }
}
