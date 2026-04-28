package fr.toufoumaster.dungeons;



public enum DungeonFloor {
    Basement(1);

    private final int chapter;
    DungeonFloor(int chapter){
        this.chapter = chapter;
    }

    public int getChapter() { return this.chapter; }
}
