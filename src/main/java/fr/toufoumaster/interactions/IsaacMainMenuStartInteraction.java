package fr.toufoumaster.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.IsaacGame;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;

import javax.annotation.Nonnull;
import java.awt.*;

public class IsaacMainMenuStartInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<IsaacMainMenuStartInteraction> CODEC = BuilderCodec.builder(
            IsaacMainMenuStartInteraction.class, IsaacMainMenuStartInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) return;
        commandBuffer.getExternalData().getWorld().execute(() -> {
            Ref<EntityStore> pRef = interactionContext.getEntity();

            PlayerRef playerRef = commandBuffer.getComponent(pRef, PlayerRef.getComponentType());
            if (playerRef == null) return;
            playerRef.sendMessage(Message.raw("Game Loading").color(Color.GREEN));
            IsaacGame game = IsaacGame.getInstance();
            game.startGame(pRef);
        });
    }
}