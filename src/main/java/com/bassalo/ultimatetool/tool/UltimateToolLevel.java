package com.bassalo.ultimatetool.tool;

public enum UltimateToolLevel {

    LEVEL_1(1, 3, 1),
    LEVEL_2(2, 4, 2),
    LEVEL_3(3, 5, 3),
    LEVEL_4(4, 5, 4),
    LEVEL_5(5, 5, 5),
    LEVEL_6(6, 5, 5);

    private final int level;
    private final int efficiency;
    private final int fortune;

    UltimateToolLevel(
            int level,
            int efficiency,
            int fortune
    ) {
        this.level = level;
        this.efficiency = efficiency;
        this.fortune = fortune;
    }

    public int getLevel() {
        return level;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public int getFortune() {
        return fortune;
    }

    public static UltimateToolLevel fromLevel(int level) {
        return switch (level) {
            case 1 -> LEVEL_1;
            case 2 -> LEVEL_2;
            case 3 -> LEVEL_3;
            case 4 -> LEVEL_4;
            case 5 -> LEVEL_5;
            case 6 -> LEVEL_6;
            default -> LEVEL_1;
        };
    }
}