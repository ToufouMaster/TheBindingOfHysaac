package fr.toufoumaster.commands;

import com.hypixel.hytale.builtin.commandmacro.MacroCommandParameter;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.player.IsaacComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class IsaacComponentCommands extends AbstractCommandCollection {

    public IsaacComponentCommands() {
        super("isaac", "change isaac component values");
        addSubCommand(new HealthCommands());
    }
    public static class HealthCommands extends AbstractCommandCollection {

        public HealthCommands() {
            super("health", "change isaac health value");
            addSubCommand(new DepleteHealthCommand());
        }

        public static class RefillHealthCommand extends AbstractPlayerCommand {

            private static DefaultArg<Integer> healthRefill;

            public RefillHealthCommand() {
                super("refill", "set isaac health");
                healthRefill = withDefaultArg("health", "amount of damage isaac will unSUFFER", ArgTypes.INTEGER, 1, "half a heart");
            }

            @Override
            protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
                IsaacComponent component = store.getComponent(ref,
                        IsaacComponent.getComponentType());
                if (component == null) return;

                //component.getHealthBar().hurt(healthRefill.get(context));
            }
        }

        public static class DepleteHealthCommand extends AbstractPlayerCommand {

            private static DefaultArg<Integer> healthDepletion;

            public DepleteHealthCommand() {
                super("deplete", "set isaac health");
                healthDepletion = withDefaultArg("health", "amount of damage isaac will SUFFER", ArgTypes.INTEGER, 1, "half a heart");
            }

            @Override
            protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
                IsaacComponent component = store.getComponent(ref,
                        IsaacComponent.getComponentType());
                if (component == null) return;

                component.getHealthBar().hurt(healthDepletion.get(context));
            }
        }
    }
}
