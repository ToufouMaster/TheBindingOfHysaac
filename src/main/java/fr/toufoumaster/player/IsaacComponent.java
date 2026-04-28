package fr.toufoumaster.player;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import fr.toufoumaster.TheBindingOfHysaac;
import fr.toufoumaster.player.health.HealthBar;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Vector;

public class IsaacComponent implements Component<EntityStore> {
    CharacterType characterType;
    HealthBar healthBar;
    private int coinAmount, bombAmount, keyAmount;
    private boolean bombGolden, keyGolden;

    IsaacStats stats;
    public float tearTimer;
    Vector<IsaacItem> passiveItems;

    public static BuilderCodec<IsaacComponent> CODEC;
    static {
        CODEC = (BuilderCodec.builder(IsaacComponent.class, IsaacComponent::new)).build();
    }

    public IsaacComponent() {
        this(CharacterType.Isaac);
    }

    public IsaacComponent(CharacterType characterType) {
        this.characterType = characterType;
        this.healthBar = new HealthBar();
        this.passiveItems = new Vector<>();
        this.passiveItems.addAll(this.characterType.getItems());
        this.coinAmount = 0;
        this.bombAmount = 0;
        this.keyAmount = 0;
        this.bombGolden = false;
        this.keyGolden = false;

        this.tearTimer = 0f;
        this.stats = new IsaacStats();
    }

    @NullableDecl
    @Override
    public IsaacComponent clone() {
        return new IsaacComponent(this.characterType);
    }

    public static ComponentType<EntityStore, IsaacComponent> getComponentType() {
        return TheBindingOfHysaac.ISAAC_COMPONENT_TYPE;
    }

    public HealthBar getHealthBar() {
        return this.healthBar;
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    public int getBombAmount() {
        return bombAmount;
    }

    public int getKeyAmount() {
        return keyAmount;
    }

    public void setCoinAmount(int coinAmount) {
        this.coinAmount = Math.min(99, getCoinAmount() + coinAmount);
    }

    public void setBombAmount(int bombAmount) {
        this.bombAmount = Math.min(99, getBombAmount() + bombAmount);
    }

    public void setKeyAmount(int keyAmount) {
        this.keyAmount = Math.min(99, getKeyAmount() + keyAmount);;
    }

    public void setBombGolden(boolean golden) {
        this.bombGolden = golden;
    }

    public void setKeyGolden(boolean golden) {
        this.keyGolden = golden;
    }

    public boolean getBombGolden() {
        return this.bombGolden;
    }

    public boolean getKeyGolden() {
        return this.keyGolden;
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public IsaacStats getStats() {
        return stats;
    }

    public Vector<IsaacItem> getPassiveItems() {
        return passiveItems;
    }

    public void addPassiveItem(IsaacItem item) {
        passiveItems.add(item);
    }

    public boolean isBombGolden() {
        return bombGolden;
    }

    public boolean isKeyGolden() {
        return keyGolden;
    }

    public float getSpeedStat() {
        float baseSpeed = characterType.getStats().getSpeed();
        return Math.clamp(baseSpeed + getTotalSpeedUp(), 0.1f, 2.0f);
    }

    public float getDamageStat() {
        float baseDamage = characterType.getStats().getDamage()*characterType.getStats().getDamageMultiplier();
        float flatDamageUp = 0;
        float itemMultiplier = (float) Math.sqrt(getTotalDamageUp()*1.2f+1f);
        return (baseDamage * itemMultiplier + flatDamageUp) * (1+getTotalDamageMultiplier()); //* (1 + getTotalDamageMultiplier());
    }

    public float getTearRateStat() {
        float baseTearRate = characterType.getStats().getTearRate()*characterType.getStats().getTearRateMultiplier();
        return Math.clamp(baseTearRate + getTotalTearRateUp() * getTotalTearRateMultiplier(), 0.1f, 5.0f);
    }

    public float getRangeStat() {
        float baseRange = characterType.getStats().getRange()*characterType.getStats().getRangeMultiplier();
        return Math.clamp(baseRange + getTotalRangeUp() * getTotalRangeMultiplier(), 1f, 250.0f);
    }

    public float getShootSpeedStat() {
        float baseShootSpeed = characterType.getStats().getShootSpeed()*characterType.getStats().getShootSpeedMultiplier();
        return Math.clamp(baseShootSpeed + getTotalShootSpeedUp() * getTotalShootSpeedMultiplier(), 1f, 250.0f);
    }

    public float getLuckStat() {
        return characterType.getStats().getLuck() + getTotalLuckUp();
    }

    public float getAngelChanceStat() {
        return Math.clamp(characterType.getStats().getAngelChance() + getTotalAngelChanceUp(), 0f, 1f);
    }

    public float getDevilChanceStat() {
        return Math.clamp(characterType.getStats().getDevilChance() + getTotalDevilChanceUp(), 0f, 1f);
    }

    public float getTearSizeStat() {
        return Math.clamp(characterType.getStats().getTearSize() + getTotalTearSizeUp(), 0.1f, 10f); // clamp tear size to avoid complete absurdness
    }

    public float getTotalDamageUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getDamage();
        }
        return value;
    }

    public float getTotalDamageMultiplier() {
        float multiplier = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            multiplier += item.getStats().getDamageMultiplier();
        }
        return multiplier;
    }

    public double getDamageUpItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getDamage() != 0.0f).count();
    }

    public float getTotalSpeedUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getSpeed();
        }
        return value;
    }

    public double getSpeedUpItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getSpeed() != 0.0f).count();
    }

    public float getTotalTearRateUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getTearRate();
        }
        return value;
    }

    public float getTotalTearRateMultiplier() {
        float multiplier = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            multiplier += item.getStats().getTearRateMultiplier();
        }
        return multiplier;
    }

    public double getTearRateUpItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getTearRate() != 0.0f).count();
    }

    public float getTotalRangeUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getRange();
        }
        return value;
    }

    public float getTotalRangeMultiplier() {
        float multiplier = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            multiplier += item.getStats().getRangeMultiplier();
        }
        return multiplier;
    }

    public double getRangeItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getRange() != 0.0f).count();
    }

    public float getTotalShootSpeedUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getShootSpeed();
        }
        return value;
    }

    public float getTotalShootSpeedMultiplier() {
        float multiplier = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            multiplier += item.getStats().getShootSpeedMultiplier();
        }
        return multiplier;
    }

    public double getShootSpeedItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getShootSpeed() != 0.0f).count();
    }

    public float getTotalLuckUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getLuck();
        }
        return value;
    }

    public double getLuckItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getLuck() != 0.0f).count();
    }

    public float getTotalDevilChanceUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getDevilChance();
        }
        return value;
    }

    public double getDevilChanceItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getDevilChance() != 0.0f).count();
    }

    public float getTotalAngelChanceUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getAngelChance();
        }
        return value;
    }

    public double getAngelChanceItemCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getAngelChance() != 0.0f).count();
    }

    public double getTearSizeCount() {
        return getPassiveItems().stream().filter((item) -> item.getStats().getTearSize() != 0.0f).count();
    }

    public float getTotalTearSizeUp() {
        float value = 0.0f;
        for (IsaacItem item : getPassiveItems()) {
            value += item.getStats().getTearSize();
        }
        return value;
    }

    public boolean canShoot() {
        return tearTimer > (stats.getFireRateDelay(getTearRateStat())+1) / 15; // Divided by two to fit the original gameplay (/30)
    }

    public void updateHud() {
    }
}
