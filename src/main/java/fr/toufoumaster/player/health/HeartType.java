package fr.toufoumaster.player.health;

public enum HeartType {
    RedHeart(0, 2),
    SoulHeart(1, 2),
    BlackHeart(2, 2),
    BoneHeart(3, 3),
    BrokenHeart(4, 0);

    private int id;
    private int maxFullness;

    HeartType(int id, int maxFullness) {
        this.id = id;
        this.maxFullness = maxFullness;
    }

    public int getMaxFullness() {
        return maxFullness;
    }

    public int getId() {
        return id;
    }

}
