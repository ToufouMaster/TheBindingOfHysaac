package fr.toufoumaster.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.commands.MonsterCommand;
import fr.toufoumaster.npc.monsters.AI.ItemPedestalMonsterAI;
import fr.toufoumaster.npc.monsters.IsaacMonsterComponent;
import fr.toufoumaster.player.IsaacComponent;
import fr.toufoumaster.player.IsaacItem;
import fr.toufoumaster.player.items.*;

import java.util.Random;

public class ItemUtils {

    public static void spawnItemPedestal(Store<EntityStore> store, String itemName, Vector3d position) {
        spawnItemPedestal(store, itemName, position, 0);
    }

    public static void spawnItemPedestal(Store<EntityStore> store, String itemName, Vector3d position, int coinRequirement) {
        Ref<EntityStore> pedestalRef = MonsterCommand.spawnMonster(store, "Item_Pedestal", position, new Vector3f(0, (float) Math.PI, 0));
        if (pedestalRef == null || !pedestalRef.isValid()) return;

        IsaacMonsterComponent monsterComponent = store.getComponent(pedestalRef, IsaacMonsterComponent.getComponentType());
        if (monsterComponent == null) return;

        ItemPedestalMonsterAI pedestalAI = (ItemPedestalMonsterAI) monsterComponent.getAI();
        pedestalAI.setItem(store, pedestalRef, itemName);
        pedestalAI.setCoinRequirement(store, pedestalRef, coinRequirement);
    }

    public static void giveItemToPlayer(String itemName, PlayerRef playerRef) {
        Ref<EntityStore> pRef = playerRef.getReference();
        if (pRef == null || !pRef.isValid()) return;

        Store<EntityStore> store = pRef.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            IsaacComponent isaacComponent = store.getComponent(pRef, IsaacComponent.getComponentType());
            if (isaacComponent == null) return;
            Player player = store.getComponent(pRef, Player.getComponentType());
            if (player == null) return;
            IsaacItem item = getItemFromName(itemName);
            if (item == null) return;
            isaacComponent.addPassiveItem(item);
            CustomUIHud hud = player.getHudManager().getCustomHud();
            if (hud == null) return;
            hud.update(true, new UICommandBuilder());
            playerRef.sendMessage(Message.raw("=== "+item.getName()+" ===").bold(true));
            playerRef.sendMessage(Message.raw(item.getDescription()).bold(true));
            playerRef.sendMessage(Message.raw("=== "+item.getName().replaceAll(".", "=")+" ===").bold(true));
        });
    }

    private static String[] getItemNames() {
        return new String[] {
                "Pentagram",
                "Hook Worm",
                "Cricket's Body",
                "A Bar of Soap",
                "Belt",
                "Binky",
                "Blood Clot",
                "Blue Cap",
                "Caffeine Pill",
                "Cat-o-nine-tails",
                "Dad's Lost Coin",
                "Glass Eye",
                "Growth Hormones",
                "Jesus Juice",
                "Latch Key",
                "Magic 8 Ball",
                "Magic Mushroom",
                "Magic Scab",
                "MEAT!",
                "Mom's Heels",
                "Mom's Lipstick",
                "Mom's Pearls",
                "Mom's Underwear",
                "Mr. Dolly",
                "Roid Rage",
                "Safety Pin",
                "Screw",
                "Speed Ball",
                "Squeezy",
                "Stapler",
                "Stem Cells",
                "Stye",
                "Synthoil",
                "The Halo",
                /*"Tooth Picks",
                "Torn Photo",
                "Tropicamide",
                "Wire Coat Hanger",
                "Wooden Spoon",*/
        };
    }

    public static String getRandomItemName() {
        String[] itemNames = getItemNames();
        return itemNames[new Random().nextInt(itemNames.length)];
    }

    private static IsaacItem getItemFromName(String itemName) {
        return switch (itemName) {
            case "Pentagram" -> new ItemPentagram();
            case "Hook Worm" -> new ItemHookWorm();
            case "Cricket's Body" -> new ItemCricketsBody();

            case "A Bar of Soap" -> new ItemABarOfSoap();
            case "Belt" -> new itemBelt();
            case "Binky" -> new ItemBinky();
            case "Blood Clot" -> new ItemBloodClot();
            case "Blue Cap" -> new ItemBlueCap();
            case "Caffeine Pill" -> new itemCaffeinePill();
            case "Cat-o-nine-tails" -> new itemCatoninetails();
            case "Dad's Lost Coin" -> new ItemDadsLostCoin();
            case "Glass Eye" -> new ItemGlassEye();
            case "Growth Hormones" -> new ItemGrowthHormones();
            case "Jesus Juice" -> new ItemJesusJuice();
            case "Latch Key" -> new ItemLatchKey();
            case "Magic 8 Ball" -> new ItemMagic8Ball();
            case "Magic Mushroom" -> new ItemMagicMushroom();
            case "Magic Scab" -> new ItemMagicScab();
            case "MEAT!" -> new ItemMeat();
            case "Mom's Heels" -> new ItemMomsHeels();
            case "Mom's Lipstick" -> new ItemMomsLipstick();
            case "Mom's Pearls" -> new ItemMomsPearls();
            case "Mom's Underwear" -> new ItemMomsUnderwear();
            case "Mr. Dolly" -> new ItemMrDolly();
            case "Roid Rage" -> new ItemRoidRage();
            case "Safety Pin" -> new ItemSafetyPin();
            case "Screw" -> new ItemScrew();
            case "Speed Ball" -> new ItemSpeedBall();
            case "Squeezy" -> new ItemSqueezy();
            case "Stapler" -> new ItemStapler();
            case "Stem Cells" -> new ItemStemCells();
            case "Stye" -> new ItemStye();
            case "Synthoil" -> new ItemSynthoil();
            case "The Halo" -> new ItemTheHalo();
            default -> null;
        };
    }
}
