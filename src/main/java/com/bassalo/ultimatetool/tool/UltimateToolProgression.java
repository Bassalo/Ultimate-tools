package com.bassalo.ultimatetool.tool;

import com.bassalo.ultimatetool.ModDataComponents;
import com.bassalo.ultimatetool.UltimateToolItem;
import com.bassalo.ultimatetool.effect.UltimateToolEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class UltimateToolProgression {

    private UltimateToolProgression() { }

    public static void addBlocksBroken(ServerPlayer player, ItemStack stack, int amount) {
        int level = UltimateToolItem.getLevel(stack);
        int levelProgress = UltimateToolItem.getLevelProgress(stack);
        int totalBlocksBroken = UltimateToolItem.getTotalBlocksBroken(stack);

        totalBlocksBroken += amount;

        if (level < UltimateToolItem.MAX_LEVEL) {
            levelProgress += amount;
            int requiredBlocks = UltimateToolItem.getRequiredBlocks(level);

            if (levelProgress >= requiredBlocks) {
                level++;
                levelProgress -= requiredBlocks;

                updateDurabilityAfterLevelUp(stack, level);
                UltimateToolEffects.playLevelUpEffects(player, level);
                player.sendSystemMessage(createLevelUpMessage(level));
            }
        }

        stack.set(ModDataComponents.LEVEL.get(), level);
        stack.set(ModDataComponents.LEVEL_PROGRESS.get(), levelProgress);
        stack.set(ModDataComponents.TOTAL_BLOCKS_BROKEN.get(), totalBlocksBroken);
    }

    private static void updateDurabilityAfterLevelUp(ItemStack stack, int newLevel) {
        int oldLevel = newLevel - 1;
        int oldMaxDurability = UltimateToolItem.getMaxDurability(oldLevel);

        float durabilityRatio = oldMaxDurability > 0
                ? (float) UltimateToolItem.getDurability(stack) / oldMaxDurability : 1.0F;

        int newMaxDurability = UltimateToolItem.getMaxDurability(newLevel);
        int newDurability = Math.round(newMaxDurability * durabilityRatio);

        stack.set(ModDataComponents.DURABILITY.get(), newDurability);
    }

    private static Component createLevelUpMessage(int level) {
        return Component.translatable("ultimatetool.message.level_up", level)
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }
}