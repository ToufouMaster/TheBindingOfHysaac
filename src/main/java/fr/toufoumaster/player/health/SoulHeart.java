package fr.toufoumaster.player.health;

public class SoulHeart extends RedHeart {

    public SoulHeart(RedHeart previous) {
        super(previous);
    }

    public HeartType getHeartType() {
        return HeartType.SoulHeart;
    }

    @Override
    public String getFullnessTexturePath() {
        return switch (getFullness()) {
            case 2 -> "SoulHeartFull";
            default -> "SoulHeartHalf";
        };
    }
}
