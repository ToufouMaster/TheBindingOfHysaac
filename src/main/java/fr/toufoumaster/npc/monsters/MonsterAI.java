package fr.toufoumaster.npc.monsters;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Random;

public class MonsterAI {

    public IsaacMonsterComponent monsterComponent;
    private float timeAlive;
    public Random random;

    public MonsterAI() {
        this.random = new Random();
    }

    public float getTimeAlive() {
        return timeAlive;
    }

    public void setMonsterComponent(IsaacMonsterComponent monsterComponent) {
        this.monsterComponent = monsterComponent;
    }

    public void tick(float delta, Store<EntityStore> store, Ref<EntityStore> ref) {
        timeAlive += delta;
    }

    public void onPlayerContact(Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef) {
    }

    public void onDamage(Store<EntityStore> store, Ref<EntityStore> ref) {
    }

    public void onDeath(Store<EntityStore> store, Ref<EntityStore> ref) {
    }

    public void onCreation(Holder<EntityStore> holder) {
    }

    public boolean isAlive(Store<EntityStore> store, Ref<EntityStore> ref) {
        int statIndex = EntityStatType.getAssetMap().getIndex("Health");
        EntityStatMap entityStatMapComponent = store.getComponent(ref, EntityStatsModule.get().getEntityStatMapComponentType());
        if (entityStatMapComponent == null) return false;
        EntityStatValue health = entityStatMapComponent.get(statIndex);
        if (health == null) return false;
        return health.get() != 0;
    }

    public boolean canBeHit() {
        return true;
    }

    public boolean isInvincible() {
        return false;
    }

    public boolean dealContactDamage() {
        return true;
    }

    public String getModel() {
        return "";
    }

    public float getModelScale() {
        return 1.0f;
    }

    public float getMaxHealth() {
        return 10f;
    }

    public boolean isThreat() {
        return true;
    }
}
