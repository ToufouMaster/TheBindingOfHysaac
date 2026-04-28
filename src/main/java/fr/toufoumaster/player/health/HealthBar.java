package fr.toufoumaster.player.health;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.IsaacGame;
import fr.toufoumaster.dungeons.Room;

import java.util.Vector;

public class HealthBar {
    public static int MAX_HEART = 12;
    private Vector<RedHeart> hearts = new Vector<>();
    public boolean hasAngelicHeart = false;
    public float recoveryTimer = 0;

    public HealthBar() {
        for (int i = 0; i < 3; i++) {
            addHeart(HeartType.RedHeart).setFullness(HeartType.RedHeart.getMaxFullness());
        }
        /*for (int i = 0; i < 2; i++) {
            addHeart(HeartType.SoulHeart).setFullness(HeartType.SoulHeart.getMaxFullness());
            addHeart(HeartType.BlackHeart).setFullness(HeartType.BlackHeart.getMaxFullness());
        }*/
        //hearts.get(2).setFullness(1);
        //this.hasAngelicHeart = true;
    }

    public Vector<RedHeart> getHearts() {
        return hearts;
    }

    public void setHearts(Vector<RedHeart> hearts) {
        this.hearts = hearts;
    }

    public void hurt(int amount) {
        int remaining = amount;
        recoveryTimer = 0;
        int angelicHeart = getAngelicHeart();
        if (hasAngelicHeart && getHearts().size()-1 == angelicHeart) {
            hasAngelicHeart = false;
            return;
        }

        // Deplete hp and remove hearts
        for (int i = getHearts().size()-1; i >= 0; i--) {
            RedHeart heart = hearts.get(i);
            remaining = heart.deplete(remaining);
            if (heart.getHeartType() == HeartType.RedHeart) continue;
            if (heart.fullness != 0) continue;
            // Apply heart effects (Golden, Black...)
            if (heart.getHeartType() == HeartType.BlackHeart) {
                IsaacGame game = IsaacGame.getInstance();
                Room room = game.generatedLayout.getRoom(game.curPos);
                for (Ref<EntityStore> threatMonster : room.getThreatMonsters()) {
                    Damage damage = new Damage(new Damage.EnvironmentSource("blackheart"), DamageCause.ENVIRONMENT, 40);
                    DamageSystems.executeDamage(threatMonster, threatMonster.getStore(), damage);
                }
            }
            hearts.remove(heart);
        }

        if (remaining > 0) {
            //player.sendMessage(Message.raw("You dead"));
        }
    }

    public boolean isRecovering() {
        return recoveryTimer < 1.5f;
    }

    public RedHeart addHeart(HeartType heartType) {
        RedHeart heart = null;
        RedHeart lastHeart = (getHearts().isEmpty()) ? null : getHearts().getLast();
        switch (heartType) {
            case RedHeart -> {
                int last = getLastHeart(heartType);

                if (last ==  -1) { heart = new RedHeart(null); last = 0;
                } else { heart = new RedHeart(getHearts().get(last)); }

                getHearts().add(last, heart);
            }
            case SoulHeart -> {
                heart = new SoulHeart(lastHeart);
                getHearts().add(heart);
            }
            case BlackHeart -> {
                heart = new BlackHeart(lastHeart);
                getHearts().add(heart);
            }
        }
        if (getHearts().size() > MAX_HEART) {
            getHearts().remove(MAX_HEART);
        }
        return heart;
    }

    public int getLastHeart(HeartType heartType) {
        int last = -1;
        Vector<RedHeart> hearts = getHearts();
        for (int i = 0; i < hearts.size(); i++) {
            RedHeart heart = hearts.get(i);
            if (heart == null) continue;
            if (heart.getHeartType() == heartType) {
                last = i;
            }
        }
        return last;
    }

    public int getAngelicHeart() {
        Vector<RedHeart> hearts = getHearts();
        if (hearts.isEmpty()) return 0;
        if (hearts.getLast().getHeartType() == HeartType.RedHeart) return hearts.size()-1;
        for (int i = 0; i < hearts.size(); i++) {
            RedHeart heart = hearts.get(i);
            // TODO: fix this null problem
            if (heart == null || heart.getHeartType() != HeartType.RedHeart) {
                RedHeart prevHeart = heart.getPrevious();
                if (prevHeart == null) continue;
                if (prevHeart.getHeartType() == HeartType.RedHeart) return i - 1;
            }
        }
        return 0;
    }

    public StringBuilder getHeartContainerHudString(Vector2i pos, RedHeart heart, boolean isAngelic) {
        Vector2i size = new Vector2i(52, 48);
        StringBuilder sb = new StringBuilder("Group {Background: (PatchStyle(TexturePath: ");
        sb.append("\"Hud/HealthBar/"+heart.getFullnessTexturePath()+".png\"");
        sb.append(")); Anchor: (Width: ");
        sb.append(size.getX());
        sb.append(", Height: ");
        sb.append(size.getY());
        sb.append(", Left: ");
        sb.append(pos.getX()*size.getX()+4);
        sb.append(", Top: ");
        sb.append(pos.getY()*size.getY()+4);
        sb.append(");");

        if (isAngelic) {
            sb.append("Group {Background: (PatchStyle(TexturePath: \"" + "Hud/HealthBar/AngelicPiece.png" + "\"");
            sb.append(")); Anchor: (Width: ");
            sb.append(size.getX());
            sb.append(", Height: ");
            sb.append(size.getY());
            sb.append(");}");
        }

        sb.append("}");
        return sb;
    }
}
