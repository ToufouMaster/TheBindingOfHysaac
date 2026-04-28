package fr.toufoumaster.events.listeners;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.events.TearMissEvent;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;

import java.util.Vector;
import java.util.function.Consumer;

public class TearMissHandler implements Consumer<TearMissEvent> {

    @Override
    public void accept(TearMissEvent event) {
        Ref<EntityStore> ref = event.tear();
        if (!ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();

        IsaacTearComponent isaacTearComponent = store.getComponent(ref, IsaacTearComponent.getComponentType());
        if (isaacTearComponent == null) return;


        PlayerRef playerRef = Universe.get().getPlayer(isaacTearComponent.getShooterUUID());
        if (playerRef != null) {
            Ref<EntityStore> pRef = playerRef.getReference();
            if (pRef == null || !pRef.isValid()) return;
            IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
            if (isaacComponent == null) return;

            Vector<String> usedItemName = new Vector<>();
            isaacComponent.getPassiveItems().reversed().forEach((passiveItem) -> {
                if (usedItemName.contains(passiveItem.getName())) return;
                passiveItem.onTearMiss(ref);
                usedItemName.add(passiveItem.getName());
            });
        }

        store.removeEntity(ref, RemoveReason.REMOVE);
    }
}