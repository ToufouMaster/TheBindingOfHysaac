package fr.toufoumaster.player;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class IsaacItem {

    public void onTearHit(Ref<EntityStore> ref, Ref<EntityStore> entityRef) {

    }

    public void onTearMiss(Ref<EntityStore> ref) {

    }

    public void onIsaacHit() {

    }

    public void onMobKill() {

    }

    public void onRoomEnter() {

    }

    public void onRoomClear() {

    }

    public IsaacStats getStats() {
        return new IsaacStats();
    }

    public Vector3d applyTearPosition(Vector3d position, double delta, double aliveTime) {
        return position;
    }

    public Vector3d applyTearVelocity(Vector3d velocity, double delta, double aliveTime) {
        return velocity;
    }


    public String getName() {
        return "";
    }

    public String getDescription() {
        return "";
    }
}
