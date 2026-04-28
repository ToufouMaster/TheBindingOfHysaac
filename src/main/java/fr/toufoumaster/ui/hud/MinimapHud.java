package fr.toufoumaster.ui.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.IsaacGame;
import fr.toufoumaster.dungeons.DungeonLayout;
import fr.toufoumaster.dungeons.Room;
import fr.toufoumaster.dungeons.RoomType;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.health.HealthBar;
import fr.toufoumaster.player.health.HeartType;
import fr.toufoumaster.player.health.RedHeart;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Vector;

public class MinimapHud extends CustomUIHud {

    public MinimapHud(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder commandBuilder) {
        commandBuilder.append("Hud/MiniMapSmall.ui");
    }

    @Override
    public void update(boolean clear, @NonNullDecl UICommandBuilder commandBuilder) {
        commandBuilder.append("Hud/Hud.ui");

        Ref<EntityStore> ref = getPlayerRef().getReference();
        if (ref == null || !ref.isValid()) return;
        Store<EntityStore> store = ref.getStore();

        IsaacComponent component = store.getComponent(ref, IsaacComponent.getComponentType());
        if (component == null) return;

        IsaacGame game = IsaacGame.getInstance();
        // MiniMap
        commandBuilder.append("#HUD", "Hud/MiniMapSmall.ui");
        if (game.generatedLayout != null) {
            int id = 0;
            for (int i = 0; i < game.generatedLayout.getRooms().size(); i++) {
                Room room = game.generatedLayout.getRooms().get(i);
                Vector2i shapeSize = Room.getRoomShapeSize(room.getShape());
                Vector2i position = IsaacGame.getInstance().curPos;
                if (position.getX() - room.getPos().getX() > 2 || position.getX() - room.getPos().getX() < -2 || position.getY() - room.getPos().getY() > 2 || position.getY() - room.getPos().getY() < -2) continue;
                final StringBuilder sb = getMinimapCellString(room, shapeSize, position);
                final StringBuilder sbicon = getMinimapCellIconString(room, shapeSize);

                commandBuilder.appendInline("#HUD #MiniMap", sb.toString());
                if (sbicon != null)
                    commandBuilder.appendInline("#HUD #MiniMap["+id+"]", sbicon.toString());
                id++;
            }
        }

        // HealthBar
        commandBuilder.append("#HUD", "Hud/HealthBar.ui");
        HealthBar healthBar = component.getHealthBar();
        int angelicHeart = healthBar.getAngelicHeart();
        Vector<RedHeart> hearts = healthBar.getHearts();

        for (int i = 0; i < hearts.size(); i++) {
            RedHeart heart = hearts.get(i);
            StringBuilder sb = healthBar.getHeartContainerHudString(new Vector2i(i%6, i/6), heart, i == angelicHeart && healthBar.hasAngelicHeart);
            commandBuilder.appendInline("#HUD #HealthBar", sb.toString());
        }

        // Stat Ui
        commandBuilder.append("#HUD", "Hud/StatsList.ui");

        // Consumables
        int coinAmount = component.getCoinAmount();
        int bombAmount = component.getBombAmount();
        int keyAmount = component.getKeyAmount();

        commandBuilder.set("#HUD #Coins #Amount.Text", (coinAmount >= 10) ? String.valueOf(coinAmount) : "0"+coinAmount );
        commandBuilder.set("#HUD #Bombs #Amount.Text", (bombAmount >= 10) ? String.valueOf(bombAmount) : "0"+bombAmount );
        commandBuilder.set("#HUD #Keys #Amount.Text", (keyAmount >= 10) ? String.valueOf(keyAmount) : "0"+keyAmount );
        commandBuilder.set("#HUD #Bombs #Img.AssetPath", (component.getBombGolden()) ? "UI/Custom/Hud/Stats/BombGolden.png" : "UI/Custom/Hud/Stats/Bomb.png" );
        commandBuilder.set("#HUD #Keys #Img.AssetPath", (component.getKeyGolden()) ? "UI/Custom/Hud/Stats/KeyGolden.png" : "UI/Custom/Hud/Stats/Key.png" );

        // Stats
        String speedStat = String.format("%.2f", component.getSpeedStat());
        commandBuilder.set("#HUD #Speed #Amount.Text", (component.getSpeedStat() >= 10) ? speedStat : "0"+speedStat );
        String damageStat = String.format("%.2f", component.getDamageStat());
        commandBuilder.set("#HUD #Damage #Amount.Text", (component.getDamageStat() >= 10) ? damageStat : "0"+damageStat );
        String tearRateStat = String.format("%.2f", component.getTearRateStat());
        commandBuilder.set("#HUD #TearRate #Amount.Text", (component.getTearRateStat() >= 10) ? tearRateStat : "0"+tearRateStat );
        String rangeStat = String.format("%.2f", component.getRangeStat());
        commandBuilder.set("#HUD #Range #Amount.Text", (component.getRangeStat() >= 10) ? rangeStat : "0"+rangeStat );
        String shootSpeedStat = String.format("%.2f", component.getShootSpeedStat());
        commandBuilder.set("#HUD #ShootSpeed #Amount.Text", (component.getShootSpeedStat() >= 10) ? shootSpeedStat : "0"+shootSpeedStat );
        String luckStat = String.format("%.2f", component.getLuckStat());
        commandBuilder.set("#HUD #Luck #Amount.Text", (component.getLuckStat() >= 10) ? luckStat : "0"+luckStat );
        String devilChanceStat = String.format("%.2f", component.getDevilChanceStat()*100);
        commandBuilder.set("#HUD #DevilChance #Amount.Text", (component.getDevilChanceStat() >= 0.10) ? devilChanceStat : "0"+devilChanceStat );
        String angelChanceStat = String.format("%.2f", component.getAngelChanceStat()*100);
        commandBuilder.set("#HUD #AngelChance #Amount.Text", (component.getAngelChanceStat() >= 0.10) ? angelChanceStat : "0"+angelChanceStat );

        // Passive Items
        commandBuilder.append("#HUD", "Hud/ItemList.ui");

        for (int i = 0; (i < component.getPassiveItems().size() && i < 32); i++) {
            int left = (i%4)*64;
            int top = (i/4)*64;
            String itemName = component.getPassiveItems().get(i).getName();
            String sb = "AssetImage { Anchor: (Width: 64, Height: 64, Left: " +
                    left + ", Top: " + top + ");" +
                    "AssetPath: \"Items/IsaacItems/" +
                    itemName + ".png\";" +
                    "}";
            commandBuilder.appendInline("#HUD #ItemList", sb);
        }

        super.update(clear, commandBuilder);
    }

    private StringBuilder getMinimapCellIconString(Room room, Vector2i shapeSize) {
        if (room.getRoomType() == RoomType.ROOM_DEFAULT) return null;
        StringBuilder sb = new StringBuilder("Group {Background: (PatchStyle(TexturePath: ");
        sb.append("\"Hud/MiniMapAssets/Icons/").append(room.getRoomType().name()).append(".png\"");
        sb.append(")); Anchor: (Width: ");
        sb.append(shapeSize.getX()*50);
        sb.append(", Height: ");
        sb.append(shapeSize.getY()*50);
        sb.append(");}");
        return sb;
    }

    @NonNullDecl
    private static StringBuilder getMinimapCellString(Room room, Vector2i shapeSize, Vector2i cameraPosition) {
        StringBuilder sb = new StringBuilder("Group {Background: (PatchStyle(TexturePath: ");
        final int cellSize = 50;
        sb.append("\"Hud/MiniMapAssets/"+ room.getShape().getId()+".png\"");
        sb.append(")); Anchor: (Width: ");
        sb.append(shapeSize.getX()*cellSize);
        sb.append(", Height: ");
        sb.append(shapeSize.getY()*cellSize);
        sb.append(", Left: ");
        sb.append(room.getPos().getX()*cellSize - (cameraPosition.getX()-2)*cellSize);
        sb.append(", Top: ");
        sb.append(room.getPos().getY()*cellSize - (cameraPosition.getY()-2)*cellSize);
        sb.append(");}");
        return sb;
    }
}
