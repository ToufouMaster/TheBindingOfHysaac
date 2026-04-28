package fr.toufoumaster.npc.monsters.AI.pickups;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.utils.PickupUtils;

import javax.swing.text.html.parser.Entity;
import java.util.Objects;
import java.util.Random;
import java.util.Vector;

import static fr.toufoumaster.commands.MonsterCommand.spawnMonster;

public class PickupChestGoldMonsterAI extends MonsterAI {

    boolean isOpen = false;
    int dropType;
    public PickupChestGoldMonsterAI() {
        super();
        dropType = (new Random().nextInt() % 50) + 50;
    }

    @Override
    public void tick(float delta, Store<EntityStore> store, Ref<EntityStore> ref) {
        super.tick(delta, store, ref);
        if (!isAlive(store, ref)) return;
        if (ref == null || !ref.isValid()) return;

        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;

        if (isOpen) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Opened", store);
            return;
        }

        if (getTimeAlive() < 1.5f) {
            npcEntity.playAnimation(ref, AnimationSlot.Status, "Spawn", store);
        }
    }

    @Override
    public void onPlayerContact(Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef) {
        if (!isAlive(store, ref)) return;
        if (ref == null || !ref.isValid()) return;

        if (getTimeAlive() < 1.5f) return;
        if (isOpen) return;

        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (npcEntity == null) return;
        Ref<EntityStore> pRef = playerRef.getReference();
        if (pRef == null) return;
        IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
        if (isaacComponent == null) return;

        if (!isaacComponent.getKeyGolden() && isaacComponent.getKeyAmount() == 0) return;
        if (!isaacComponent.getKeyGolden()) isaacComponent.setKeyAmount(isaacComponent.getKeyAmount()-1);

        Player player = store.getComponent(pRef, Player.getComponentType());
        if (player == null) return;

        CustomUIHud hud = player.getHudManager().getCustomHud();
        if (hud == null) return;
        hud.update(true, new UICommandBuilder());

        npcEntity.playAnimation(ref, AnimationSlot.Status, "Open", store);
        isOpen = true;

        dropPickups(store, ref);
    }

    private void dropPickups(Store<EntityStore> store, Ref<EntityStore> ref) {
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) return;

        Random random = new Random();
        // Drop pickups
        if (dropType < 10) { // Drop Chest
            PickupUtils.applyVelocityToPickup(spawnMonster(store, "Pickup_Chest", transformComponent.getPosition(), transformComponent.getRotation()));
        } else if (dropType < 15) { // Drop Locked Chest
            PickupUtils.applyVelocityToPickup(spawnMonster(store, "Pickup_Gold_Chest", transformComponent.getPosition(), transformComponent.getRotation()));
        } else if (dropType < 35) { // Drop Item

        } else { // Drop Pickups
            for (int i = 0; i < 2 + random.nextInt(4); i++) {
                int pickupType = random.nextInt(100);
                if (pickupType < 35) { // Coins
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupCoin(store, transformComponent.getPosition(), transformComponent.getRotation()));
                } else if (pickupType < 55) { // Heart
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupHeart(store, transformComponent.getPosition(), transformComponent.getRotation()));
                } else if (pickupType < 70) { // Key
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupKey(store, transformComponent.getPosition(), transformComponent.getRotation()));
                } else { // Bomb
                    PickupUtils.applyVelocityToPickup(PickupUtils.spawnPickupBomb(store, transformComponent.getPosition(), transformComponent.getRotation()));
                }
            }
        }
    }

    @Override
    public boolean canBeHit() {
        return false;
    }

    @Override
    public boolean isInvincible() {
        return true;
    }

    @Override
    public boolean dealContactDamage() {
        return false;
    }

    @Override
    public String getModel() {
        return "Pickup_Chest_Gold";
    }

    @Override
    public float getModelScale() {
        return 2f;
    }

    @Override
    public boolean isThreat() {
        return false;
    }
}
