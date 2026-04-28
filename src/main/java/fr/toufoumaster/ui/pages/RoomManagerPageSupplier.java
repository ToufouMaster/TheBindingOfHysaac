package fr.toufoumaster.ui.pages;

import com.hypixel.hytale.builtin.portals.components.PortalDevice;
import com.hypixel.hytale.builtin.portals.components.PortalDeviceConfig;
import com.hypixel.hytale.builtin.portals.ui.PortalDeviceActivePage;
import com.hypixel.hytale.builtin.portals.utils.BlockTypeUtils;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class RoomManagerPageSupplier implements OpenCustomUIInteraction.CustomPageSupplier {
    @Nonnull
    public static final BuilderCodec CODEC;
    private PortalDeviceConfig config;

    public CustomUIPage tryCreate(@Nonnull Ref ref, @Nonnull ComponentAccessor store, @Nonnull PlayerRef playerRef, @Nonnull InteractionContext context) {
        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null) {
            return null;
        } else {
            Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
            if (playerComponent == null) {
                return null;
            } else {
                World world = ((EntityStore)store.getExternalData()).getWorld();
                BlockType blockType = world.getBlockType(targetBlock.x, targetBlock.y, targetBlock.z);
                if (blockType == null) {
                    playerRef.sendMessage(Message.raw("NO 1"));
                    return null;
                } else {
                    return new RoomManagerActivePage(playerRef, new Vector3i(targetBlock.x, targetBlock.y, targetBlock.z));
                }
            }
        }
    }

    static {
        CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(RoomManagerPageSupplier.class, RoomManagerPageSupplier::new).appendInherited(new KeyedCodec("Config", PortalDeviceConfig.CODEC), (supplier, o) -> supplier.config = o, (supplier) -> supplier.config, (supplier, parent) -> supplier.config = parent.config).documentation("The portal device's config.").add()).build();
    }
}
