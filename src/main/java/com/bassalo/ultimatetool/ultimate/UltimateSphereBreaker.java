package com.bassalo.ultimatetool.ultimate;

import com.bassalo.ultimatetool.UltimateToolItem;
import com.bassalo.ultimatetool.effect.UltimateToolEffects;
import com.bassalo.ultimatetool.event.ConfigHandler;
import com.bassalo.ultimatetool.tool.UltimateToolDurability;
import com.bassalo.ultimatetool.tool.UltimateToolProgression;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class UltimateSphereBreaker {

    private static final Set<UUID> BREAKING = new HashSet<>();

    private UltimateSphereBreaker() {
    }

    public static boolean isBreaking(ServerPlayer player) {
        return BREAKING.contains(player.getUUID());
    }

    public static void breakSphere(ServerPlayer player, BlockPos center, int radius) {
        UUID playerId = player.getUUID();

        if (!BREAKING.add(playerId)) {
            return;
        }

        try {
            ServerLevel level = player.level();
            ItemStack stack = player.getMainHandItem();

            UltimateToolEffects.playUltimateExplosion(level, center);
            UltimateToolEffects.spawnSphereParticles(level, center, radius);

            int radiusSquared = radius * radius;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + y * y + z * z > radiusSquared) {
                            continue;
                        }

                        BlockPos pos = center.offset(x, y, z);
                        BlockState state = level.getBlockState(pos);

                        if (state.isAir() || !isBlockAllowed(state)) {
                            continue;
                        }

                        if (!player.gameMode.destroyBlock(pos)) {
                            continue;
                        }

                        UltimateToolProgression.addBlocksBroken(player, stack, 1);
                    }
                }
            }

            UltimateToolDurability.consume(
                    stack,
                    UltimateToolItem.ULTIMATE_DURABILITY_COST
            );
        } finally {
            BREAKING.remove(playerId);
        }
    }

    private static boolean isBlockAllowed(BlockState state) {
        String blockId =
                state.getBlock().builtInRegistryHolder().key()
                        .identifier().toString();

        return ConfigHandler.isBlockAllowed(blockId);
    }
}