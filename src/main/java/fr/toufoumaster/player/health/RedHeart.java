package fr.toufoumaster.player.health;

import com.hypixel.hytale.math.vector.Vector2i;
import fr.toufoumaster.dungeons.Room;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class RedHeart {
    RedHeart previous;
    int fullness = 0;

    public RedHeart(RedHeart previous) {
        this.previous = previous;
    }

    public int refill(int amount) {
        int used = Math.min(getHeartType().getMaxFullness() - fullness, amount);
        fullness += used;
        return amount - used;
    }

    public int deplete(int amount) {
        int canDeplete = Math.min(fullness, amount);
        fullness -= canDeplete;
        return amount-canDeplete;
    }

    public void setFullness(int fullness) {
        this.fullness = fullness;
    }

    public int getFullness() {
        return fullness;
    }

    public boolean isFull() {
        return fullness == getHeartType().getMaxFullness();
    }

    public HeartType getHeartType() {
        return HeartType.RedHeart;
    }

    public RedHeart getPrevious() {
        return previous;
    }

    public String getFullnessTexturePath() {
        return switch (getFullness()) {
            case 1 -> "RedHeartHalf";
            case 2 -> "RedHeartFull";
            default -> "RedHeartEmpty";
        };
    }
}
