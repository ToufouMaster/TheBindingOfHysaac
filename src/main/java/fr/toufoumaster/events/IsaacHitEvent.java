package fr.toufoumaster.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record IsaacHitEvent(@Nonnull Ref<EntityStore> player, @Nonnull Ref<EntityStore> enemy, int damage) implements IEvent<Void> {

    public static void dispatch(@Nonnull Ref<EntityStore> isaac, @Nonnull Ref<EntityStore> enemy, int damage) {
        IEventDispatcher<IsaacHitEvent, IsaacHitEvent> dispatcher = HytaleServer.get()
                .getEventBus().dispatchFor(IsaacHitEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new IsaacHitEvent(isaac, enemy, damage));
        }
    }
}
