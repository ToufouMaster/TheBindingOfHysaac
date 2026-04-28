package fr.toufoumaster.npc.monsters.AI;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.MonsterAI;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.utils.ItemUtils;
import fr.toufoumaster.utils.PickupUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ItemPedestalMonsterAI extends MonsterAI {

    String itemId;
    int coinRequirement;

    public ItemPedestalMonsterAI() {
        this.itemId = "";
        this.coinRequirement = 0;
    }

    @Override
    public void onCreation(Holder<EntityStore> holder) {
        super.onCreation(holder);
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(getModel());
        if (modelAsset == null) return;
        Model model = Model.createScaledModel(modelAsset, getModelScale(), Map.of("Item", this.itemId.isEmpty() ? "NoItem" : this.itemId));
        PersistentModel persistentModel = holder.getComponent(PersistentModel.getComponentType());
        if (persistentModel == null) return;
        persistentModel.setModelReference(model.toReference());
        holder.replaceComponent(ModelComponent.getComponentType(), new ModelComponent(model));
    }

    @Override
    public void onPlayerContact(Store<EntityStore> store, Ref<EntityStore> ref, PlayerRef playerRef) {
        super.onPlayerContact(store, ref, playerRef);
        if (!isAlive(store, ref)) return;
        if (getTimeAlive() < 1.5f) return;
        if (this.itemId.isEmpty()) return;

        Ref<EntityStore> pRef = playerRef.getReference();
        if (pRef == null || !pRef.isValid()) return;
        IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
        if (isaacComponent == null) return;

        if (this.coinRequirement > 0) {
            if (isaacComponent.getCoinAmount() < this.coinRequirement) return;
            isaacComponent.setCoinAmount(isaacComponent.getCoinAmount() - this.coinRequirement);
        }

        // TODO this is hardcoded like many things in this demo, need to be changed
        if (this.itemId.equals("Pickup_Bomb")) {
            isaacComponent.setBombAmount(isaacComponent.getBombAmount()+1);
        } else if (this.itemId.equals("Pickup_Key")) {
            isaacComponent.setKeyAmount(isaacComponent.getKeyAmount()+1);
        } else {
            ItemUtils.giveItemToPlayer(itemId, playerRef);
        }
        setItem(store, ref, "");
        Player player = store.getComponent(pRef, Player.getComponentType());
        if (player == null) return;
        CustomUIHud hud = player.getHudManager().getCustomHud();
        if (hud == null) return;
        hud.update(true, new UICommandBuilder());
    }

    public void updateItemModel(Store<EntityStore> store, Ref<EntityStore> ref) {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(getModel());
        if (modelAsset == null) return;
        Model model = Model.createScaledModel(modelAsset, getModelScale(), Map.of("Item", this.itemId.isEmpty() ? "NoItem" : (this.itemId.equals("Cricket's Head") || this.itemId.equals("Pentagram")) ? this.itemId : "TextureNotFound"));
        PersistentModel persistentModel = store.getComponent(ref, PersistentModel.getComponentType());
        if (persistentModel == null) return;
        persistentModel.setModelReference(model.toReference());
        store.replaceComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
    }

    public void setCoinRequirement(Store<EntityStore> store, Ref<EntityStore> ref,  int coinRequirement) {
        this.coinRequirement = coinRequirement;
        Nameplate nameplate = store.ensureAndGetComponent(ref, Nameplate.getComponentType());
        nameplate.setText((this.coinRequirement > 0) ? this.coinRequirement+"€" : "");
    }

    public int getCoinRequirement() {
        return coinRequirement;
    }

    public void setItem(Store<EntityStore> store, Ref<EntityStore> ref, String itemName) {
        this.itemId = itemName;
        HytaleLogger.getLogger().atInfo().log("Item set to: "+itemId);
        updateItemModel(store, ref);
    }

    @Override
    public boolean dealContactDamage() {
        return false;
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
    public String getModel() {
        if (this.itemId.startsWith("Pickup_")) return this.itemId;
        return "ItemPedestalModel";
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
