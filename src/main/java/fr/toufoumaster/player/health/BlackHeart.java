package fr.toufoumaster.player.health;

public class BlackHeart extends RedHeart {

    public BlackHeart(RedHeart previous) {
        super(previous);
    }

    public HeartType getHeartType() {
        return HeartType.BlackHeart;
    }

    @Override
    public String getFullnessTexturePath() {
        return switch (getFullness()) {
            case 2 -> "BlackHeartFull";
            default -> "BlackHeartHalf";
        };
    }
}
