package fr.toufoumaster.npc.components;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.npc.NPCComponents;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Vector;

public class NPCSoftCollisionComponent implements Component<EntityStore> {
    public static BuilderCodec<NPCSoftCollisionComponent> CODEC;

    Vector<Ref<EntityStore>> collidingReferences;

    static {
        CODEC = (BuilderCodec.builder(NPCSoftCollisionComponent.class, NPCSoftCollisionComponent::new)).build();
               /*.append(
                        new KeyedCodec("Origin", Vector3d.CODEC), (o, i) -> o.origin = i, (o) -> o.origin

                )).add().build();*/
    }

    NPCSoftCollisionComponent() {
        collidingReferences = new Vector<>();
    }

    public static ComponentType<EntityStore, NPCSoftCollisionComponent> getComponentType() {
        return NPCComponents.NPC_SOFT_COLLISION_COMPONENT_TYPE;
    }

    @NullableDecl
    @Override
    public NPCSoftCollisionComponent clone() {
        return new NPCSoftCollisionComponent();
    }

    public Vector<Ref<EntityStore>> getCollidingReferences() {
        return collidingReferences;
    }
}
