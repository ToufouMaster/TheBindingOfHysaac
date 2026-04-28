package fr.toufoumaster.player.items;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;

public class ItemHookWorm extends IsaacItem {

    public ItemHookWorm() {
    }

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        stats.setTearRate(0.4f);
        stats.setRange(1.5f);
        // TODO: Apply spectral tear buff
        return stats;
    }

    @Override
    public String getName() {
        return "Hook Worm";
    }

    @Override
    public String getDescription() {
        return "ZIP ZOOP";
    }

    @Override
    public Vector3d applyTearVelocity(Vector3d velocity, double delta, double aliveTime) {
        double curTime = Math.IEEEremainder(aliveTime, 1)+0.5;
        if (curTime >= 0.3 && curTime < 0.5) {
            velocity.rotateY((float) Math.PI/2);
        }
        if (curTime >= ((aliveTime < 0.5) ? 0.9 : 0.8) && curTime < 1.0) {
            velocity.rotateY((float) -Math.PI/2);
        }
        return velocity;
    }
}
