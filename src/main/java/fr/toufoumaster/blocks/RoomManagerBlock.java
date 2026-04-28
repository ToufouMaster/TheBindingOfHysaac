package fr.toufoumaster.blocks;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import fr.toufoumaster.dungeons.RoomShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RoomManagerBlock implements Component<ChunkStore> {

    private RoomShape roomShape;
    private String name;
    private String type;
    public static final BuilderCodec<RoomManagerBlock> CODEC = BuilderCodec.builder(RoomManagerBlock.class, RoomManagerBlock::new)
            .append(new KeyedCodec<>("ShapeId", Codec.INTEGER), (data, value) -> data.roomShape = RoomShape.values()[value], data -> data.roomShape.getId()).add()
            .append(new KeyedCodec<>("Name", Codec.STRING), (data, value) -> data.name = value, data -> data.name).add()
            .append(new KeyedCodec<>("Type", Codec.STRING), (data, value) -> data.type = value, data -> data.type).add()
            .build();

    public RoomManagerBlock() {
        this.roomShape = RoomShape.ROOMSHAPE_1x1;
        this.name = "";
        this.type = "";
    }

    public RoomManagerBlock(RoomManagerBlock block) {
        this.roomShape = block.getShape();
        this.name = block.getName();
        this.type = block.getType();
    }

    public static ComponentType<ChunkStore, RoomManagerBlock> getComponentType() {
        return BlockComponents.ROOM_MANAGER_TYPE;
    }

    @Nullable
    public Component<ChunkStore> clone() {
        return new RoomManagerBlock(this);
    }

    public RoomShape getShape() {
        return roomShape;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setShape(RoomShape shape) {
        this.roomShape = shape;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Nonnull
    public String toString() {
        return "SprinklerBlock{ShapeId=" + this.getShape() + ", Name=" + this.getName() + ", Type=" + this.getType() +"}";
    }
}