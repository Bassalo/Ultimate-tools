package com.bassalo.ultimatetool;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class Config {

    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALLOWED_BLOCKS =
            BUILDER
                    .defineListAllowEmpty(
                            "allowedBlocks",
                            List.of(
                                    "minecraft:stone",
                                    "minecraft:cobblestone",
                                    "minecraft:granite",
                                    "minecraft:diorite",
                                    "minecraft:andesite",
                                    "minecraft:tuff",
                                    "minecraft:obsidian",
                                    "minecraft:coal_ore",
                                    "minecraft:copper_ore",
                                    "minecraft:iron_ore",
                                    "minecraft:gold_ore",
                                    "minecraft:lapis_ore",
                                    "minecraft:redstone_ore",
                                    "minecraft:diamond_ore",
                                    "minecraft:emerald_ore",
                                    "minecraft:deepslate",
                                    "minecraft:deepslate_coal_ore",
                                    "minecraft:deepslate_copper_ore",
                                    "minecraft:deepslate_iron_ore",
                                    "minecraft:deepslate_gold_ore",
                                    "minecraft:deepslate_lapis_ore",
                                    "minecraft:deepslate_redstone_ore",
                                    "minecraft:deepslate_diamond_ore",
                                    "minecraft:deepslate_emerald_ore",
                                    "minecraft:netherrack",
                                    "minecraft:blackstone",
                                    "minecraft:basalt",
                                    "minecraft:nether_gold_ore",
                                    "minecraft:nether_quartz_ore",
                                    "minecraft:end_stone"
                            ),
                            () -> "",
                            Config::validateBlockName
                    );

    public static final ModConfigSpec.IntValue LEVEL_1_REQUIRED =
            BUILDER
                    .defineInRange(
                            "level1Required",
                            1000,
                            1,
                            Integer.MAX_VALUE
                    );

    public static final ModConfigSpec.IntValue LEVEL_2_REQUIRED =
            BUILDER
                    .defineInRange(
                            "level2Required",
                            2500,
                            1,
                            Integer.MAX_VALUE
                    );

    public static final ModConfigSpec.IntValue LEVEL_3_REQUIRED =
            BUILDER
                    .defineInRange(
                            "level3Required",
                            5000,
                            1,
                            Integer.MAX_VALUE
                    );

    public static final ModConfigSpec.IntValue LEVEL_4_REQUIRED =
            BUILDER
                    .defineInRange(
                            "level4Required",
                            10000,
                            1,
                            Integer.MAX_VALUE
                    );

    public static final ModConfigSpec.IntValue LEVEL_5_REQUIRED =
            BUILDER
                    .defineInRange(
                            "level5Required",
                            50000,
                            1,
                            Integer.MAX_VALUE
                    );

    public static final ModConfigSpec SPEC =
            BUILDER.build();

    private static boolean validateBlockName(Object value) {
        return value instanceof String blockName
                && BuiltInRegistries.BLOCK.containsKey(
                Identifier.parse(blockName)
        );
    }
}