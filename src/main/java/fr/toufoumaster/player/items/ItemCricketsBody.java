package fr.toufoumaster.player.items;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.IsaacStats;
import fr.toufoumaster.player.IsaacTearComponent;

import java.util.Vector;

public class ItemCricketsBody extends IsaacItem {

    static Vector<Ref<EntityStore>> refIdsBlackList = new Vector<>();;

    @Override
    public IsaacStats getStats() {
        IsaacStats stats = new IsaacStats();
        stats.setTearRate(0.5f);
        stats.setRangeMultiplier(-0.2f);
        stats.setTearSize(0.1f);
        // TODO: Implement transformations (simply add 1 to the transform value)
        return stats;
    }

    @Override
    public void onTearMiss(Ref<EntityStore> ref) {
        trySplittingTear(ref);
    }

    @Override
    public void onTearHit(Ref<EntityStore> ref, Ref<EntityStore> entityRef) {
        trySplittingTear(ref);
    }

    public void trySplittingTear(Ref<EntityStore> ref) {
        if (refIdsBlackList.contains(ref)) {
            refIdsBlackList.remove(ref);
            return;
        }

        Store<EntityStore> store = ref.getStore();

        IsaacTearComponent isaacTearComponent = store.getComponent(ref, IsaacTearComponent.getComponentType());
        if (isaacTearComponent == null) return;

        PlayerRef playerRef = Universe.get().getPlayer(isaacTearComponent.getShooterUUID());
        if (playerRef == null) return;
        Ref<EntityStore> pRef = playerRef.getReference();
        if (pRef == null || !pRef.isValid()) return;
        IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
        if (isaacComponent == null) return;
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;


        TransformComponent transformComponentClone = transformComponent.clone();
        Vector3d position = transformComponentClone.getPosition().clone();
        position.setY(isaacTearComponent.getOrigin().getY());
        Vector3f rotation = transformComponentClone.getRotation().clone();

        for (int i = 0; i < 4; i++) {
            rotation.add(0, (float) Math.PI/2, 0);
            Ref<EntityStore> tearRef = IsaacTearComponent.spawnTear(playerRef.getReference(), isaacTearComponent.getShooterUUID(), position, rotation, isaacComponent.getDamageStat()/2, 0.5f, isaacComponent.getShootSpeedStat(), isaacComponent.getTearSizeStat()*0.4f);
            if (tearRef == null) continue;
            refIdsBlackList.add(tearRef);
        }
    }

    @Override
    public String getName() {
        return "Cricket's Body";
    }

    @Override
    public String getDescription() {
        return "Bursting shots + tears up";
    }
}
