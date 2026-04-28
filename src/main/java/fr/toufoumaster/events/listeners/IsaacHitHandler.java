package fr.toufoumaster.events.listeners;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.events.IsaacHitEvent;
import fr.toufoumaster.events.TearHitEvent;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;
import fr.toufoumaster.player.health.HealthBar;

import java.util.function.Consumer;

public class IsaacHitHandler implements Consumer<IsaacHitEvent> {

    @Override
    public void accept(IsaacHitEvent event) {
        Ref<EntityStore> ref = event.player();
        Ref<EntityStore> ennemyRef = event.enemy();
        if (!ref.isValid() || !ennemyRef.isValid()) return;

        Store<EntityStore> store = ref.getStore();
        IsaacComponent isaacComponent = store.getComponent(ref, IsaacComponent.getComponentType());
        if (isaacComponent == null) return;
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        World world = store.getExternalData().getWorld();

        world.execute(() -> {
            HealthBar healthBar = isaacComponent.getHealthBar();
            if (healthBar.isRecovering()) return;
            healthBar.hurt(event.damage());

            //SoundUtil.

            CustomUIHud hud = player.getHudManager().getCustomHud();
            if (hud == null) return;
            hud.update(true, new UICommandBuilder());
        });
    }
}