package fr.toufoumaster.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Random;

import static fr.toufoumaster.commands.MonsterCommand.spawnMonster;

public class PickupUtils {

    public static Ref<EntityStore> spawnPickupCoin(Store<EntityStore> store, Vector3d position, Vector3f rotation) {
        Random random = new Random();
        int coinType = random.nextInt(100);
        String monsterId = "Pickup_Dime";
        if (coinType < 95) {
            monsterId = "Pickup_Penny";
        } else if (coinType < 99) {
            monsterId = "Pickup_Nickel";
        }
        return spawnMonster(store, monsterId, position, rotation);
    }

    public static Ref<EntityStore> spawnPickupHeart(Store<EntityStore> store, Vector3d position, Vector3f rotation) {
        Random random = new Random();
        int heartType = random.nextInt(100);
        String monsterId = "Pickup_Heart_Black";
        if (heartType < 40) {
            monsterId = "Pickup_Heart_Red";
        } else if (heartType < 80) {
            monsterId = "Pickup_Heart_Red_Half";
        } else if (heartType < 85) {
            monsterId = "Pickup_Heart_Red_Double";
        } else if (heartType < 95) {
            monsterId = "Pickup_Heart_Soul";
        }
        return spawnMonster(store, monsterId, position, rotation);
    }

    public static Ref<EntityStore> spawnPickupKey(Store<EntityStore> store, Vector3d position, Vector3f rotation) {
        Random random = new Random();
        int keyType = random.nextInt(100);
        String monsterId = "Pickup_Key_Gold";
        if (keyType < 98) {
            monsterId = "Pickup_Key";
        }
        return spawnMonster(store, monsterId, position, rotation);
    }

    public static Ref<EntityStore> spawnPickupBomb(Store<EntityStore> store, Vector3d position, Vector3f rotation) {
        Random random = new Random();
        int bombType = random.nextInt(100);
        String monsterId = "Pickup_Bomb_Gold";
        if (bombType < 75) {
            monsterId = "Pickup_Bomb";
        } else if (bombType < 98) {
            monsterId = "Pickup_Bomb_Double";
        }
        return spawnMonster(store, monsterId, position, rotation);
    }

    public static void applyVelocityToPickup(Ref<EntityStore> pickup) {
        if (pickup == null || !pickup.isValid()) return;
        Random random = new Random();
        Store<EntityStore> store = pickup.getStore();
        Velocity velocity = store.getComponent(pickup, Velocity.getComponentType());
        if (velocity == null) return;

        velocity.addInstruction(new Vector3d(random.nextFloat()*2f - 1f, 0.2f, random.nextFloat()*2f - 1f).scale(15), new VelocityConfig(), ChangeVelocityType.Add);
    }
}
