package fr.toufoumaster.player;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.TheBindingOfHysaac;
import fr.toufoumaster.ui.pages.RoomManagerActivePage;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.UUID;

public class IsaacTearComponent implements Component<EntityStore> {

    public static BuilderCodec<IsaacTearComponent> CODEC;
    Vector3d origin;
    double angle;
    UUID shooter;

    private double aliveTime;

    private float damage;
    private float speed;
    private float range;

    static {
        CODEC = (BuilderCodec.builder(IsaacTearComponent.class, IsaacTearComponent::new)
                .append(
                        new KeyedCodec<>("Origin", Vector3d.CODEC), (o, i) -> o.origin = i, (o) -> o.origin
                ).add()
                .append(
                        new KeyedCodec<>("Angle", Codec.DOUBLE), (o, i) -> o.angle = i, (o) -> o.angle
                ).add()
                .append(
                        new KeyedCodec<>("AliveTime", Codec.DOUBLE), (o, i) -> o.aliveTime = i, (o) -> o.aliveTime
                ).add()
                .append(
                        new KeyedCodec<>("Shooter", Codec.UUID_BINARY), (o, i) -> o.shooter = i, (o) -> o.shooter
                ).add()
                .append(
                        new KeyedCodec<>("Damage", Codec.FLOAT), (o, i) -> o.damage = i, (o) -> o.damage
                ).add()
                .append(
                        new KeyedCodec<>("Speed", Codec.FLOAT), (o, i) -> o.speed = i, (o) -> o.speed
                ).add()
                .append(
                        new KeyedCodec<>("Range", Codec.FLOAT), (o, i) -> o.range = i, (o) -> o.range
                ).add()
        ).build();
    }

    public IsaacTearComponent(Vector3d origin) {
        this.origin = origin.clone();
    }

    public IsaacTearComponent() {
        aliveTime = 0;
        damage = 0;
        speed = 0;
        range = 0;
    }

    public static ComponentType<EntityStore, IsaacTearComponent> getComponentType() {
        return TheBindingOfHysaac.ISAAC_TEAR_COMPONENT_TYPE;
    }

    @NullableDecl
    @Override
    public IsaacTearComponent clone() {
        return new IsaacTearComponent();
    }

    public void setShooterUUID(UUID uuid) {
        this.shooter = uuid;
    }

    public UUID getShooterUUID() {
        return this.shooter;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return this.angle;
    }

    public void setOrigin(Vector3d position) {
        this.origin = position.clone();
    }

    public Vector3d getOrigin() {
        return this.origin;
    }


    public float getRange() {
        return range;
    }

    public float getShootSpeed() {
        return speed;
    }

    public float getDamage() {
        return damage;
    }

    public double getAliveTime() {
        return aliveTime;
    }


    public void setRange(float range) {
        this.range = range;
    }

    public void setShootSpeed(float speed) {
        this.speed = speed;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setAliveTime(double aliveTime) {
        this.aliveTime = aliveTime;
    }

    public static Ref<EntityStore> spawnTear(Ref<EntityStore> ref, UUID uuid, Vector3d position, Vector3f rotation, float damage, float range, float shootSpeed, float tearSize) {
        if (ref == null || !ref.isValid()) return null;
        Store<EntityStore> store = ref.getStore();

        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("IsaacTear");
        if (modelAsset == null) return null;
        Model model = Model.createScaledModel(modelAsset, tearSize);

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(),
                new TransformComponent(new Vector3d(position.clone()), new Vector3f()));
        //holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(rotation));
        holder.ensureComponent(UUIDComponent.getComponentType());
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));

        IsaacTearComponent isaacTearComponent = new IsaacTearComponent();
        isaacTearComponent.setOrigin(position.clone());
        isaacTearComponent.setShooterUUID(uuid);
        isaacTearComponent.setAngle(rotation.getY()+Math.PI/2);
        isaacTearComponent.setDamage(damage);
        isaacTearComponent.setRange(range);
        isaacTearComponent.setShootSpeed(shootSpeed);
        holder.addComponent(IsaacTearComponent.getComponentType(), isaacTearComponent);
        holder.addComponent(PersistentModel.getComponentType(),
                new PersistentModel(model.toReference()));
        holder.addComponent(BoundingBox.getComponentType(),
                new BoundingBox(model.getBoundingBox()));
        holder.addComponent(NetworkId.getComponentType(),
                new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.ensureComponent(PropComponent.getComponentType());
        holder.ensureComponent(ProjectileModule.get().getProjectileComponentType());
        holder.addComponent(Velocity.getComponentType(), new Velocity(new Vector3d(rotation.toVector3d()).scale(3)));

        return store.addEntity(holder, AddReason.SPAWN);
    }

}