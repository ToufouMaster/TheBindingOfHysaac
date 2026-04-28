package fr.toufoumaster.dungeons;

import java.util.List;
import java.util.Random;

public class Rng extends Random {

    public Rng(int seed) {
        super(seed);
    }

    public Rng() {
    }

    public <T> void shuffle (List<T> list) {
        if (list.size() < 2)
            return;

        for (var i = list.size() - 1; i > 0; i--) {
            var randIdx = (this.nextInt(i + 1));

            var t = list.get(randIdx);
            list.set(randIdx, list.get(i));
            list.set(i, t);
        }
    }
}
