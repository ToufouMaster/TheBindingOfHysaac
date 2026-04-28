package fr.toufoumaster.utils;

public class EntitySpawnUtil {

    public static String getMonsterIdFromBlock(String blockId) {
        return switch (blockId) {
            case "Spawner_Attack_Fly"         -> "Attack_Fly"               ;
            case "Spawner_Fly"                -> "Fly"                      ;
            case "Spawner_Horf"               -> "Horf"                     ;
            case "Spawner_Dip"                -> "Dip"                      ;
            case "Spawner_Clotty"             -> "Clotty"                   ;
            case "Spawner_Spider"             -> "Spider"                   ;
            case "Spawner_Pooter"             -> "Pooter"                   ;
            case "Spawner_Squirt"             -> "Fly"                      ;
            case "Spawner_Hopper"             -> "Fly"                      ;
            case "Spawner_Gusher"             -> "Fly"                      ;
            case "Spawner_Fatty"              -> "Fly"                      ;
            case "Spawner_Gaper"              -> "Fly"                      ;
            case "Spawner_Pacer"              -> "Fly"                      ;
            case "Spawner_Level2_Spider"      -> "Spider"                   ;

            case "Poop_Basement"              -> "Poop"                     ;
            case "Poop_Gold_Basement"         -> "Poop_Gold"                ;

            case "Spawner_Chest"              -> "Pickup_Chest"             ;
            case "Spawner_Chest_Gold"         -> "Pickup_Chest_Gold"        ;
            case "Spawner_Penny"              -> "Pickup_Penny"             ;
            case "Spawner_Penny_Double"       -> "Pickup_Penny_Double"      ;
            case "Spawner_Nickel"             -> "Pickup_Nickel"            ;
            case "Spawner_Dime"               -> "Pickup_Dime"              ;
            case "Spawner_Key"                -> "Pickup_Key"               ;
            case "Spawner_Key_Double"         -> "Pickup_Key_Double"        ;
            case "Spawner_Key_Gold"           -> "Pickup_Key_Gold"          ;
            case "Spawner_Bomb"               -> "Pickup_Bomb"              ;
            case "Spawner_Bomb_Double"        -> "Pickup_Bomb_Double"       ;
            case "Spawner_Bomb_Gold"          -> "Pickup_Bomb_Gold"         ;
            case "Spawner_Heart_Red"          -> "Pickup_Heart_Red"         ;
            case "Spawner_Heart_Red_Half"     -> "Pickup_Heart_Red_Half"    ;
            case "Spawner_Heart_Red_Double"   -> "Pickup_Heart_Red_Double"  ;
            case "Spawner_Heart_Soul"         -> "Pickup_Heart_Soul"        ;
            case "Spawner_Heart_Soul_Half"    -> "Pickup_Heart_Soul_Half"   ;
            case "Spawner_Heart_Soul_Double"  -> "Pickup_Heart_Soul_Double" ;
            case "Spawner_Heart_Black"        -> "Pickup_Heart_Black"       ;
            case "Spawner_Heart_Black_Half"   -> "Pickup_Heart_Black_Half"  ;
            case "Spawner_Heart_Black_Double" -> "Pickup_Heart_Black_Double";
            case "Spawner_Heart_Eternal"      -> "Pickup_Heart_Eternal"     ;

            default -> null;
        };
    }
}
