package fr.toufoumaster.player;

import fr.toufoumaster.player.items.ItemCricketsBody;
import fr.toufoumaster.player.items.ItemHookWorm;
import fr.toufoumaster.player.items.ItemPentagram;

import java.util.List;

public enum CharacterType {
    Isaac(4, new IsaacStats(1.0f,
            2.73f,
            1.0f,
            3.5f,
            1.0f,
            6.5f,
            1.0f,
            1.0f,
            1.0f,
            0,
            0.0f,
            0.0f,
            1.0f
    ), List.of(), null, null);

    private int id;
    private IsaacStats stats;
    private List<IsaacItem> items;
    private IsaacItem activeItem;
    private IsaacItem trinket;

    CharacterType(int id, IsaacStats stats) {
        this(id, stats, List.of(), null, null);
    }

    CharacterType(int id,
                  IsaacStats stats,
                  List<IsaacItem> items,
                  IsaacItem activeItem,
                  IsaacItem trinket) {
        this.id = id;
        this.stats = stats;
        this.items = items;
        this.activeItem = activeItem;
        this.trinket = trinket;
    }

    public int getId() {
        return id;
    }

    public IsaacStats getStats() {
        return this.stats;
    }

    public List<IsaacItem> getItems() {
        return items;
    }

    public IsaacItem getActiveItem() {
        return activeItem;
    }

    public IsaacItem getTrinket() {
        return trinket;
    }
}

