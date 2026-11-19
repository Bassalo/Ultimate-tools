package com.bassalo.ultimatetool.event;

import com.bassalo.ultimatetool.Config;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

import java.util.Set;

public final class ConfigHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<String> LOGGED_BLOCK_IDS = Set.of(
            "minecraft:stone",
            "minecraft:cobblestone",
            "minecraft:iron_ore",
            "minecraft:gold_ore",
            "minecraft:deepslate",
            "minecraft:deepslate_iron_ore",
            "minecraft:deepslate_gold_ore",
            "minecraft:deepslate_diamond_ore",
            "minecraft:deepslate_emerald_ore",
            "minecraft:netherrack",
            "minecraft:end_stone"
    );

    private ConfigHandler() {
    }

    private static volatile Set<String> allowedBlocks = Set.of();

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != Config.SPEC) {
            return;
        }

        allowedBlocks = Set.copyOf(Config.ALLOWED_BLOCKS.get());
        logAllowedBlocks("loaded");
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != Config.SPEC) {
            return;
        }

        allowedBlocks = Set.copyOf(Config.ALLOWED_BLOCKS.get());
        logAllowedBlocks("reloaded");
    }

    @SubscribeEvent
    public static void onConfigUnload(ModConfigEvent.Unloading event) {
        if (event.getConfig().getSpec() != Config.SPEC) {
            return;
        }

        allowedBlocks = Set.of();
        LOGGER.info("[UltimateTool Config] Config unloaded: allowedBlocks cache reset");
    }

    private static void logAllowedBlocks(String action) {
        LOGGER.info("[UltimateTool Config] Config {}: allowedBlocks count={}", action, allowedBlocks.size());
        for (String blockId : LOGGED_BLOCK_IDS) {
            LOGGER.info("[UltimateTool Config] allowedBlocks contains {}={}", blockId, allowedBlocks.contains(blockId));
        }
    }

    public static boolean isBlockAllowed(String blockId) {
        return allowedBlocks.contains(blockId);
    }

    public static Set<String> getAllowedBlocks() {
        return allowedBlocks;
    }
}
