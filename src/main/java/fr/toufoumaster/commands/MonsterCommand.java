package fr.toufoumaster.commands;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.components.NPCSoftCollisionComponent;
import fr.toufoumaster.npc.monsters.AI.*;
import fr.toufoumaster.npc.monsters.AI.pickups.*;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.npc.monsters.MonsterAI;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;
import java.util.Objects;

public class MonsterCommand extends AbstractPlayerCommand {

    RequiredArg<String> monsterName;

    public MonsterCommand() {
        super("monster", "spawn monsters");
        this.monsterName = withRequiredArg("name", "monster name id", ArgTypes.STRING);
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        String name = this.monsterName.get(context);
        TransformComponent playerTransformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (playerTransformComponent == null) return;

        spawnMonster(store, name, playerTransformComponent.getPosition(), playerTransformComponent.getRotation());
    }

    public static MonsterAI getMonsterAiFromName(String name) {
        return switch (name) {
            case "Item_Pedestal" -> new ItemPedestalMonsterAI();
            case "Isaac_Bomb" -> new IsaacBombMonsterAI();
            // Pickups
            case "Pickup_Chest" -> new PickupChestMonsterAI();
            case "Pickup_Chest_Gold" -> new PickupChestGoldMonsterAI();
            case "Pickup_Penny" -> new PickupPennyMonsterAI(1);
            case "Pickup_Penny_Double" -> new PickupPennyMonsterAI(2);
            case "Pickup_Nickel" -> new PickupPennyMonsterAI(5);
            case "Pickup_Dime" -> new PickupPennyMonsterAI(10);
            case "Pickup_Key" -> new PickupKeyMonsterAI(1, false);
            case "Pickup_Key_Double" -> new PickupKeyMonsterAI(2, false);
            case "Pickup_Key_Gold" -> new PickupKeyMonsterAI(0, true);
            case "Pickup_Bomb" -> new PickupBombMonsterAI(1, false);
            case "Pickup_Bomb_Double" -> new PickupBombMonsterAI(2, false);
            case "Pickup_Bomb_Gold" -> new PickupBombMonsterAI(0, true);
            case "Pickup_Heart_Red" -> new PickupHeartMonsterAI(2);
            case "Pickup_Heart_Red_Half" -> new PickupHeartMonsterAI(1);
            case "Pickup_Heart_Red_Double" -> new PickupHeartMonsterAI(4);
            case "Pickup_Heart_Soul" -> new PickupHeartSoulMonsterAI(2);
            case "Pickup_Heart_Soul_Half" -> new PickupHeartSoulMonsterAI(1);
            case "Pickup_Heart_Soul_Double" -> new PickupHeartSoulMonsterAI(4);
            case "Pickup_Heart_Black" -> new PickupHeartBlackMonsterAI(2);
            case "Pickup_Heart_Black_Half" -> new PickupHeartBlackMonsterAI(1);
            case "Pickup_Heart_Black_Double" -> new PickupHeartBlackMonsterAI(4);
            case "Pickup_Heart_Eternal" -> new PickupHeartEternalMonsterAI();
            // Monsters
            case "Poop" -> new PoopMonsterAI();
            case "Poop_Gold" -> new PoopGoldMonsterAI();
            case "Horf" -> new HorfMonsterAI();
            case "Fly" -> new FlyMonsterAI();
            case "Attack_Fly" -> new AttackFlyMonsterAI();
            case "Dip" -> new DipMonsterAI();
            case "Spider" -> new SpiderMonsterAI();
            case "Clotty" -> new ClottyMonsterAI();
            case "Pooter" -> new PooterMonsterAI();
            case "BabyPlum" -> new BabyPlumMonsterAI();
            default -> new MonsterAI();
        };
    }

    public static Ref<EntityStore> spawnMonster(Store<EntityStore> store, String name, Vector3d position, Vector3f rotation) {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.putComponent(TransformComponent.getComponentType(), new TransformComponent(position.clone(), rotation.clone()));
        holder.ensureComponent(UUIDComponent.getComponentType());
        holder.ensureComponent(Velocity.getComponentType());
        holder.ensureComponent(NPCSoftCollisionComponent.getComponentType());

        IsaacMonsterComponent isaacMonsterComponent = holder.ensureAndGetComponent(IsaacMonsterComponent.getComponentType());

        MonsterAI ai = getMonsterAiFromName(name);
        isaacMonsterComponent.setAI(ai);
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(ai.getModel());
        if (modelAsset == null) return null;
        Model model = Model.createScaledModel(modelAsset, ai.getModelScale());

        int statIndex = EntityStatType.getAssetMap().getIndex("Health");
        EntityStatMap entityStatMapComponent = holder.ensureAndGetComponent(EntityStatsModule.get().getEntityStatMapComponentType());
        EntityStatValue health = entityStatMapComponent.get(statIndex);
        if (health == null) return null;
        StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, ai.getMaxHealth() - 10);
        entityStatMapComponent.putModifier(statIndex, "MonsterHealth", modifier);

        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(PersistentModel.getComponentType(),
                new PersistentModel(model.toReference()));
        holder.addComponent(BoundingBox.getComponentType(),
                new BoundingBox(model.getBoundingBox()));
        holder.ensureComponent(HeadRotation.getComponentType());
        NPCEntity npc = new NPCEntity();
        npc.setRoleName(name);
        holder.addComponent(Objects.requireNonNull(NPCEntity.getComponentType()), npc);
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        ai.onCreation(holder);

        return store.addEntity(holder, AddReason.SPAWN);
    }
}
