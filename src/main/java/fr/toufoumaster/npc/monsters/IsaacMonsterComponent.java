package fr.toufoumaster.npc.monsters;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.TheBindingOfHysaac;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nullable;

public class IsaacMonsterComponent implements Component<EntityStore> {

    // TODO: implement RNG for mobs so they can be predicted by the seed. Exemple: Chests.
    private MonsterAI ai;
    public static BuilderCodec<IsaacMonsterComponent> CODEC;

    static {
        CODEC = (BuilderCodec.builder(IsaacMonsterComponent.class, IsaacMonsterComponent::new)).build();
    }

    IsaacMonsterComponent() {
        this.ai = new MonsterAI();
    }

    public void setAI(MonsterAI ai) {
        this.ai = ai;
        ai.setMonsterComponent(this);
    }

    public static ComponentType<EntityStore, IsaacMonsterComponent> getComponentType() {
        return TheBindingOfHysaac.ISAAC_MONSTER_COMPONENT_TYPE;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new IsaacMonsterComponent();
    }

    @Nullable
    public static Vector3d getNearestTarget(Store<EntityStore> store, Vector3d position, double maxDistance) {
        double nearestDistance = maxDistance;
        Vector3d nearestTarget = null;
        for (PlayerRef playerRef : Universe.get().getPlayers()) {
            Ref<EntityStore> pRef = playerRef.getReference();
            if (pRef == null || !pRef.isValid()) continue;
            TransformComponent playerTransformComponent = store.getComponent(pRef, TransformComponent.getComponentType());
            if (playerTransformComponent == null) continue;
            Vector3d playerPosition = playerTransformComponent.getPosition();
            double distance = playerPosition.distanceTo(position);
            if (distance < nearestDistance) {
                nearestTarget = playerPosition;
                nearestDistance = distance;
            }
        }
        return nearestTarget;
    }

    public MonsterAI getAI() {
        return this.ai;
    }


    //@Nonnull
    //public String getModel() {
    //    return "";
    //}
}
