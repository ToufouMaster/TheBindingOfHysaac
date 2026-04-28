package fr.toufoumaster.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record TearMissEvent(@Nonnull Ref<EntityStore> tear) implements IEvent<Void> {

    public static void dispatch(@Nonnull Ref<EntityStore> tear) {
        IEventDispatcher<TearMissEvent, TearMissEvent> dispatcher = HytaleServer.get()
                .getEventBus().dispatchFor(TearMissEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new TearMissEvent(tear));
        }
    }
}