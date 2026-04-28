package fr.toufoumaster.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.AI.IsaacBombMonsterAI;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;

import javax.annotation.Nonnull;

public class IsaacTearUseInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<IsaacTearUseInteraction> CODEC = BuilderCodec.builder(
            IsaacTearUseInteraction.class, IsaacTearUseInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) return;
        commandBuffer.getExternalData().getWorld().execute(() -> {
            Ref<EntityStore> pRef = interactionContext.getEntity();

            PlayerRef playerRef = commandBuffer.getComponent(pRef, PlayerRef.getComponentType());
            if (playerRef == null) return;
            ModelComponent modelComponent = commandBuffer.getComponent(pRef, ModelComponent.getComponentType());
            if (modelComponent == null) return;
            HeadRotation headRotation = commandBuffer.getComponent(pRef, HeadRotation.getComponentType());
            if (headRotation == null) return;
            IsaacComponent isaacComponent = commandBuffer.getComponent(pRef, IsaacComponent.getComponentType());
            if (isaacComponent == null) return;
            TransformComponent transformComponent = commandBuffer.getComponent(pRef, TransformComponent.getComponentType());
            if (transformComponent == null) return;

            if (!isaacComponent.canShoot()) return;
            isaacComponent.tearTimer = 0f;
            // TODO: add isaac's velocity into tear movement calculations
            IsaacTearComponent.spawnTear(playerRef.getReference(), playerRef.getUuid(), transformComponent.getPosition().clone().add(0, modelComponent.getModel().getEyeHeight(), 0), headRotation.getRotation(), isaacComponent.getDamageStat(), isaacComponent.getRangeStat(), isaacComponent.getShootSpeedStat(), isaacComponent.getTearSizeStat());
        });
    }
}