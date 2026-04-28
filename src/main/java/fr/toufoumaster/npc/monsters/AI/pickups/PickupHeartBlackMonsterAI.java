package fr.toufoumaster.npc.monsters.AI.pickups;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.health.HealthBar;
import fr.toufoumaster.player.health.HeartType;
import fr.toufoumaster.player.health.RedHeart;

import java.util.Objects;

public class PickupHeartBlackMonsterAI extends PickupMonsterAI {

    int heart;

    public PickupHeartBlackMonsterAI(int heart) {
        super();
        this.heart = heart;
    }

    @Override
    public void onPlayerContact(Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef) {
        if (!isAlive(store, ref)) return;
        if (ref == null || !ref.isValid()) return;
        if (getTimeAlive() < 1.5f) return;

        Ref<EntityStore> pRef = playerRef.getReference();
        if (pRef == null || !pRef.isValid()) return;

        IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
        if (isaacComponent == null) return;

        boolean canRecover = false;
        int remaining = heart;
        HealthBar healthBar = isaacComponent.getHealthBar();
        for (RedHeart heart : healthBar.getHearts()) {
            if (!heart.isFull() && (heart.getHeartType() == HeartType.SoulHeart ||heart.getHeartType() == HeartType.BlackHeart)) {
                remaining = heart.refill(remaining);
                canRecover = true;
            }
        }
        while (remaining > 0 && healthBar.getHearts().size() < 12) {
            int fullness = Math.min(remaining, HeartType.BlackHeart.getMaxFullness());
            healthBar.addHeart(HeartType.BlackHeart).setFullness(fullness);
            remaining -= fullness;
            canRecover = true;
        }
        if (canRecover) {
            Player player = store.getComponent(pRef, Player.getComponentType());
            if (player == null) return;

            CustomUIHud hud = player.getHudManager().getCustomHud();
            if (hud == null) return;
            hud.update(true, new UICommandBuilder());
            super.onPlayerContact(store, ref, playerRef);
        }
    }

    @Override
    public String getModel() {
        return switch (this.heart) {
            case 1 -> "Pickup_Heart_Black_Half";
            case 4 -> "Pickup_Heart_Black_Double";
            default -> "Pickup_Heart_Black";
        };
    }

}
