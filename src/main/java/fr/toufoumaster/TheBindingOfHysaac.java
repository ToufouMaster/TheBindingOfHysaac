package fr.toufoumaster;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigPauseTimeCommand;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.blocks.BlockComponents;
import fr.toufoumaster.commands.CameraCommands;
import fr.toufoumaster.commands.IsaacComponentCommands;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.events.IsaacHitEvent;
import fr.toufoumaster.events.MobKillEvent;
import fr.toufoumaster.events.TearHitEvent;
import fr.toufoumaster.events.TearMissEvent;
import fr.toufoumaster.events.listeners.*;
import fr.toufoumaster.interactions.IsaacBombUseInteraction;
import fr.toufoumaster.interactions.IsaacDoorEnterInteraction;
import fr.toufoumaster.interactions.IsaacMainMenuStartInteraction;
import fr.toufoumaster.interactions.IsaacTearUseInteraction;
import fr.toufoumaster.npc.NPCComponents;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacTearComponent;
import fr.toufoumaster.systems.*;
import fr.toufoumaster.systems.monsters.IsaacMonsterSystem;
import fr.toufoumaster.ui.pages.RoomManagerActivePage;
import fr.toufoumaster.ui.pages.RoomManagerPageSupplier;
import fr.toufoumaster.utils.RoomRegister;

import javax.annotation.Nonnull;

public class TheBindingOfHysaac extends JavaPlugin {

    public static ComponentType<EntityStore, IsaacComponent> ISAAC_COMPONENT_TYPE;
    public static ComponentType<EntityStore, IsaacTearComponent> ISAAC_TEAR_COMPONENT_TYPE;
    public static ComponentType<EntityStore, IsaacMonsterComponent> ISAAC_MONSTER_COMPONENT_TYPE;

    public TheBindingOfHysaac(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        new IsaacGame();
        this.getCommandRegistry().registerCommand(new CameraCommands());
        this.getCommandRegistry().registerCommand(new IsaacComponentCommands());
        this.getCommandRegistry().registerCommand(new MonsterCommand());
        ISAAC_COMPONENT_TYPE = this.getEntityStoreRegistry().registerComponent(IsaacComponent.class, "IsaacComponent", IsaacComponent.CODEC);
        ISAAC_TEAR_COMPONENT_TYPE = this.getEntityStoreRegistry().registerComponent(IsaacTearComponent.class, "IsaacTear", IsaacTearComponent.CODEC);
        ISAAC_MONSTER_COMPONENT_TYPE = this.getEntityStoreRegistry().registerComponent(IsaacMonsterComponent.class, "IsaacMonster", IsaacMonsterComponent.CODEC);
        NPCComponents.registerComponents(this);
        BlockComponents.registerComponents(this);

        this.getEntityStoreRegistry().registerSystem(new ProjectileUpdateSystem());
        this.getEntityStoreRegistry().registerSystem(new NPCUpdateSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerUpdateSystem());
        this.getEntityStoreRegistry().registerSystem(new IsaacMonsterSystem());
        this.getEntityStoreRegistry().registerSystem(new onMonsterDeathSystem());
        this.getEntityStoreRegistry().registerSystem(new onMonsterHitSystem());
        NPCComponents.registerSystems(this);

        this.getEventRegistry().register(TearMissEvent.class, new TearMissHandler());
        this.getEventRegistry().register(TearHitEvent.class, new TearHitHandler());
        //this.getEventRegistry().register(MobKillEvent.class, new MobKillHandler());
        this.getEventRegistry().register(IsaacHitEvent.class, new IsaacHitHandler());
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, OnPlayerJoinListener::onPlayerReady);
        NPCComponents.registerEvents(this);


        OpenCustomUIInteraction.registerCustomPageSupplier(this, RoomManagerActivePage.class, "RoomManager", new RoomManagerPageSupplier());

        this.getCodecRegistry(Interaction.CODEC).register("Isaac_Bomb_Use_Interaction", IsaacBombUseInteraction.class, IsaacBombUseInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Isaac_Tear_Use_Interaction", IsaacTearUseInteraction.class, IsaacTearUseInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Isaac_Door_Enter_Interaction", IsaacDoorEnterInteraction.class, IsaacDoorEnterInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Isaac_Main_Menu_Start_Interaction", IsaacMainMenuStartInteraction.class, IsaacMainMenuStartInteraction.CODEC);
    }

    @Override
    protected void start() {
        super.start();
        RoomRegister.register();
        World world = Universe.get().getDefaultWorld();
        if (world == null) return;
        world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
            worldTimeResource.setDayTime(0, world, store);
            WorldConfigPauseTimeCommand.pauseTime(ConsoleSender.INSTANCE, world, store);
        });
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}