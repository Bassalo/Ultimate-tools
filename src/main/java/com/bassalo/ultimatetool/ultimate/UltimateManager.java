package com.bassalo.ultimatetool.ultimate;

import com.bassalo.ultimatetool.UltimateToolItem;
import com.bassalo.ultimatetool.effect.UltimateToolEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class UltimateManager {

    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final Map<UUID, PendingUltimate> PENDING = new HashMap<>();

    private static final long COOLDOWN_MS = UltimateToolItem.ULTIMATE_COOLDOWN_MINUTES * 60_000L;

    private static final int DELAY_TICKS = 4;

    private UltimateManager() { }

    public static void activate(ServerPlayer player, BlockPos center) {
        UUID playerId = player.getUUID();
        long currentTime = System.currentTimeMillis();

        Long cooldownEnd = COOLDOWNS.get(playerId);

        if (cooldownEnd != null) {
            if (currentTime < cooldownEnd) {
                UltimateToolEffects.sendUltimateError(
                        player, createCooldownMessage(cooldownEnd - currentTime));
                return;
            }

            COOLDOWNS.remove(playerId);
        }

        COOLDOWNS.put(playerId, currentTime + COOLDOWN_MS);

        PENDING.put(playerId, new PendingUltimate(player.level(), center, DELAY_TICKS));

        UltimateToolEffects.spawnChargeParticles(player);
    }

    public static void tick(MinecraftServer server) {
        if (PENDING.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, PendingUltimate>> iterator = PENDING.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingUltimate> entry = iterator.next();
            UUID playerId = entry.getKey();
            PendingUltimate pending = entry.getValue();

            pending.tick();

            if (!pending.isReady()) {
                continue;
            }

            ServerPlayer player = server.getPlayerList().getPlayer(playerId);

            if (!canExecute(player, pending)) {
                COOLDOWNS.remove(playerId);
                iterator.remove();
                continue;
            }

            UltimateSphereBreaker.breakSphere(player, pending.center(), UltimateToolItem.ULTIMATE_RADIUS);

            iterator.remove();
        }
    }

    private static boolean canExecute(ServerPlayer player, PendingUltimate pending) {
        if (player == null) {
            return false;
        }

        if (player.level() != pending.level()) {
            return false;
        }

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof UltimateToolItem)) {
            return false;
        }

        if (UltimateToolItem.getLevel(stack) != UltimateToolItem.MAX_LEVEL) {
            return false;
        }

        return UltimateToolItem.getDurability(stack) >= UltimateToolItem.ULTIMATE_DURABILITY_COST;
    }

    private static Component createCooldownMessage(long remainingMs) {
        long totalSeconds = Math.max(1L, (remainingMs + 999L) / 1_000L);
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;

        return Component.translatable("ultimatetool.message.ultimate_cooldown", minutes, seconds);
    }
}