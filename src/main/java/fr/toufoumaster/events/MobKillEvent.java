package fr.toufoumaster.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public record MobKillEvent(@Nonnull Ref<EntityStore> victim, @Nonnull Ref<EntityStore> killer) implements IEvent<Void> {

    public static void dispatch(@Nonnull Ref<EntityStore> victim, @Nonnull Ref<EntityStore> killer) {
        IEventDispatcher<MobKillEvent, MobKillEvent> dispatcher = HytaleServer.get()
                .getEventBus().dispatchFor(MobKillEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new MobKillEvent(victim, killer));
        }
    }
}