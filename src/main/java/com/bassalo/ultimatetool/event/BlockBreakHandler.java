package com.bassalo.ultimatetool.event;

import com.bassalo.ultimatetool.ModDataComponents;
import com.bassalo.ultimatetool.UltimateToolCommands;
import com.bassalo.ultimatetool.UltimateToolItem;
import com.bassalo.ultimatetool.effect.UltimateToolEffects;
import com.bassalo.ultimatetool.tool.UltimateToolDurability;
import com.bassalo.ultimatetool.tool.UltimateToolProgression;
import com.bassalo.ultimatetool.ultimate.UltimateManager;
import com.bassalo.ultimatetool.ultimate.UltimateSphereBreaker;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlockBreakHandler {

    private static final Set<UUID> AREA_BREAKING = new HashSet<>();
    private static final Map<UUID, Long> DURABILITY_ERROR_COOLDOWNS = new HashMap<>();

    private static final long DURABILITY_ERROR_COOLDOWN_MS = 5_000L;
    private static final int AREA_SIZE_LEVEL_5 = 3;
    private static final int AREA_SIZE_LEVEL_6 = 5;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        UltimateManager.tick(event.getServer());
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack stack = event.getEntity().getMainHandItem();

        if (!(stack.getItem() instanceof UltimateToolItem)) {
            return;
        }

        BlockState state = event.getState();
        String blockId = state.getBlock().builtInRegistryHolder().key().identifier().toString();
        boolean allowed = ConfigHandler.isBlockAllowed(blockId);
        int durability = UltimateToolItem.getDurability(stack);

        if (!allowed) {
            event.setNewSpeed(0.0F);
        } else if (durability <= 0) {
            event.setNewSpeed(0.0F);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof UltimateToolItem)) {
            return;
        }

        if (UltimateToolItem.getDurability(stack) <= 0) {
            showNoDurabilityFeedback(player, event.getPos());
            event.setCanceled(true);
            return;
        }

        if (UltimateToolItem.getLevel(stack) != UltimateToolItem.MAX_LEVEL) {
            return;
        }

        if (!player.isShiftKeyDown()) {
            return;
        }

        if (!UltimateToolCommands.isUltimateEnabled(player)) {
            return;
        }

        if (!UltimateToolDurability.has(stack, UltimateToolItem.ULTIMATE_DURABILITY_COST)) {
            UltimateToolEffects.sendUltimateError(
                    player,
                    Component.translatable("ultimatetool.message.ultimate_insufficient_durability")
            );
            event.setCanceled(true);
            return;
        }

        UltimateManager.activate(player, event.getPos());
        event.setCanceled(true);
    }

    private static void showNoDurabilityFeedback(ServerPlayer player, BlockPos pos) {
        UltimateToolEffects.showNoDurabilityParticles(player, pos);

        UUID playerId = player.getUUID();
        long currentTime = System.currentTimeMillis();
        Long cooldownEnd = DURABILITY_ERROR_COOLDOWNS.get(playerId);

        if (cooldownEnd != null && currentTime < cooldownEnd) {
            return;
        }

        DURABILITY_ERROR_COOLDOWNS.put(
                playerId,
                currentTime + DURABILITY_ERROR_COOLDOWN_MS
        );

        player.sendOverlayMessage(
                Component.translatable("ultimatetool.message.no_durability")
                        .withStyle(ChatFormatting.RED)
        );
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof UltimateToolItem)) {
            return;
        }

        BlockState state = event.getState();
        Block block = state.getBlock();

        if (!isBlockAllowed(block)) {
            event.setCanceled(true);
            return;
        }

        if (UltimateToolItem.getDurability(stack) <= 0) {
            event.setCanceled(true);
            return;
        }

        if (UltimateSphereBreaker.isBreaking(player)) {
            return;
        }

        UltimateToolItem.updateEnchantments(
                stack,
                player.level().registryAccess().lookupOrThrow(
                        net.minecraft.core.registries.Registries.ENCHANTMENT
                )
        );

        UltimateToolDurability.consume(stack, 1);
        UltimateToolProgression.addBlocksBroken(player, stack, 1);

        int level = UltimateToolItem.getLevel(stack);

        if (level < 5) {
            return;
        }

        if (!AREA_BREAKING.add(player.getUUID())) {
            return;
        }

        try {
            int areaSize = level >= UltimateToolItem.MAX_LEVEL
                    ? stack.getOrDefault(ModDataComponents.AREA_MODE_5X5.get(), true)
                      ? AREA_SIZE_LEVEL_6
                      : AREA_SIZE_LEVEL_5
                    : AREA_SIZE_LEVEL_5;

            breakArea(player, event.getPos(), areaSize);
        } finally {
            AREA_BREAKING.remove(player.getUUID());
        }
    }

    private static boolean isBlockAllowed(Block block) {
        String blockId = block.builtInRegistryHolder().key().identifier().toString();
        return ConfigHandler.isBlockAllowed(blockId);
    }

    private static void breakArea(ServerPlayer player, BlockPos center, int areaSize) {
        for (BlockPos pos : getAreaPositions(player, center, areaSize)) {
            if (pos.equals(center)) {
                continue;
            }

            BlockState state = player.level().getBlockState(pos);

            if (state.isAir() || !isBlockAllowed(state.getBlock())) {
                continue;
            }

            if (!UltimateToolDurability.has(player.getMainHandItem(), 1)) {
                break;
            }

            player.gameMode.destroyBlock(pos);
        }
    }

    private static Direction getMiningDirection(ServerPlayer player) {
        float pitch = player.getXRot();

        if (pitch > 45.0F) {
            return Direction.DOWN;
        }

        if (pitch < -45.0F) {
            return Direction.UP;
        }

        return player.getDirection();
    }

    private static Set<BlockPos> getAreaPositions(
            ServerPlayer player,
            BlockPos center,
            int areaSize
    ) {
        Set<BlockPos> positions = new HashSet<>();
        Direction direction = getMiningDirection(player);
        int radius = areaSize / 2;

        for (int first = -radius; first <= radius; first++) {
            for (int second = -radius; second <= radius; second++) {
                BlockPos pos;

                if (direction.getAxis() == Direction.Axis.Y) {
                    pos = center.offset(first, 0, second);
                } else if (direction.getAxis() == Direction.Axis.X) {
                    pos = center.offset(0, first, second);
                } else {
                    pos = center.offset(first, second, 0);
                }

                positions.add(pos);
            }
        }

        return positions;
    }
}
