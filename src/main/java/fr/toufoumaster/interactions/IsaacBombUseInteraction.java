package fr.toufoumaster.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.AI.IsaacBombMonsterAI;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.player.IsaacComponent;

import javax.annotation.Nonnull;

public class IsaacBombUseInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<IsaacBombUseInteraction> CODEC = BuilderCodec.builder(
            IsaacBombUseInteraction.class, IsaacBombUseInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) return;
        commandBuffer.getExternalData().getWorld().execute(() -> {
            Ref<EntityStore> pRef = interactionContext.getEntity();
            IsaacComponent isaacComponent = commandBuffer.getComponent(pRef, IsaacComponent.getComponentType());
            if (isaacComponent == null) return;

            if (!isaacComponent.getBombGolden() && isaacComponent.getBombAmount() == 0) return;
            if (!isaacComponent.getBombGolden()) isaacComponent.setBombAmount(isaacComponent.getBombAmount()-1);

            Player player = commandBuffer.getComponent(pRef, Player.getComponentType());
            if (player == null) return;

            CustomUIHud hud = player.getHudManager().getCustomHud();
            if (hud == null) return;
            hud.update(true, new UICommandBuilder());

            TransformComponent transformComponent = commandBuffer.getComponent(pRef, TransformComponent.getComponentType());
            if (transformComponent == null) return;
            Ref<EntityStore> bombRef = MonsterCommand.spawnMonster(commandBuffer.getStore(), "Isaac_Bomb", transformComponent.getPosition(), transformComponent.getRotation());
            if (bombRef == null || !bombRef.isValid()) return;
            IsaacMonsterComponent monsterComponent = commandBuffer.getComponent(bombRef, IsaacMonsterComponent.getComponentType());
            if (monsterComponent == null || !(monsterComponent.getAI() instanceof IsaacBombMonsterAI)) return;
            ((IsaacBombMonsterAI)monsterComponent.getAI()).setOwner(pRef);
        });
    }
}