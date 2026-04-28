package fr.toufoumaster.player;

public class IsaacStats {
    private float speed;
    private float tearRate;
    private float tearRateMultiplier;
    private float damage;
    private float damageMultiplier;
    private float range;
    private float rangeMultiplier;
    private float shootSpeed;
    private float shootSpeedMultiplier;
    private float tearSize;
    private int luck;
    private float devilChance;
    private float angelChance;

    //public float tearHeight; // 32 is one block height;

    public IsaacStats(float speed, float tearRate, float tearRateMultiplier, float damage, float damageMultiplier, float range, float rangeMultiplier, float shootSpeed, float shootSpeedMultiplier, int luck, float devilChance, float angelChance, float tearSize) {
        this.speed = speed;
        this.tearRate = tearRate;
        this.tearRateMultiplier = tearRateMultiplier;
        this.damage = damage;
        this.damageMultiplier = damageMultiplier;
        this.range = range;
        this.rangeMultiplier = rangeMultiplier;
        this.shootSpeed = shootSpeed;
        this.shootSpeedMultiplier = shootSpeedMultiplier;
        this.luck = luck;
        this.tearSize = tearSize;
        this.devilChance = devilChance;
        this.angelChance = angelChance;

        //this.tearHeight = 23.75f;
    }

    public IsaacStats() {
        /*this(
                1.0f,
                2.73f,
                1.0f,
                3.5f,
                1.0f,
                6.5f,
                1.0f,
                1.0f,
                1.0f,
                0,
                0.0f,
                0.0f
        );*/
        this(
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                0,
                0.0f,
                0.0f,
                0.0f
        );
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTearRate() {
        return tearRate;
    }

    public void setTearRate(float tearRate) {
        this.tearRate = tearRate;
    }

    public float getTearRateMultiplier() {
        return tearRateMultiplier;
    }

    public void setTearRateMultiplier(float tearRateMultiplier) {
        this.tearRateMultiplier = tearRateMultiplier;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getRangeMultiplier() {
        return rangeMultiplier;
    }

    public void setRangeMultiplier(float rangeMultiplier) {
        this.rangeMultiplier = rangeMultiplier;
    }

    public float getShootSpeed() {
        return shootSpeed;
    }

    public void setShootSpeed(float shootSpeed) {
        this.shootSpeed = shootSpeed;
    }

    public float getShootSpeedMultiplier() {
        return shootSpeedMultiplier;
    }

    public void setShootSpeedMultiplier(float shootSpeedMultiplier) {
        this.shootSpeedMultiplier = shootSpeedMultiplier;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public float getDevilChance() {
        return devilChance;
    }

    public void setDevilChance(float devilChance) {
        this.devilChance = devilChance;
    }

    public float getAngelChance() {
        return angelChance;
    }

    public void setAngelChance(float angelChance) {
        this.angelChance = angelChance;
    }

    public void setTearSize(float tearSize) {
        this.tearSize = tearSize;
    }

    public float getEffectiveTearRate() {
        return getTearRate()*getTearRateMultiplier();
    }

    public float  getFireRateDelay(float tr) {
        float maxFireRate = 120f;
        if (tr > maxFireRate) {
            return 5;
        } else if (tr >= 0) {
            return (float) (16 - 6 * Math.sqrt(tr*1.3+1));
        } else if (tr > -0.77) {
            return (float) (16 - 6 * Math.sqrt(tr*1.3+1) - 6 * tr);
        } else {
            return 16 - 6 * tr;
        }
    }

    public float getTearSize() {
        return tearSize;
    }
}
