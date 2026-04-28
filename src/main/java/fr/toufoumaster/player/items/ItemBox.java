package fr.toufoumaster.player.items;

import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;
import fr.toufoumaster.utils.ItemUtils;
import fr.toufoumaster.utils.PickupUtils;

// Change the name here too
public class ItemBox extends IsaacItem {

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        // Start Replacing Stats Here
        //  	Spawns various pickups: 1 coin, 1 Key, 1 Bomb, 1 Heart, 1 card/rune, 1 pill and 1 trinket.
        // Stop Replacing Stats Here
        return stats;
    }

    @Override
    public String getName() {
        return "Box"; // Change the name here
    }

    @Override
    public String getDescription() {
        return "Not yet implemented"; // Change the description here
    }
}