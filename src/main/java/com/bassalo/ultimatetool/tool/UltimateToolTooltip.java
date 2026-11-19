package com.bassalo.ultimatetool.tool;

import com.bassalo.ultimatetool.ModDataComponents;
import com.bassalo.ultimatetool.UltimateToolItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Locale;
import java.util.function.Consumer;

public final class UltimateToolTooltip {

    private UltimateToolTooltip() { }

    public static void append(ItemStack stack, TooltipContext context, Consumer<Component> tooltip) {
        int level = UltimateToolItem.getLevel(stack);
        int levelProgress = UltimateToolItem.getLevelProgress(stack);
        int totalBlocksBroken = UltimateToolItem.getTotalBlocksBroken(stack);
        int durability = UltimateToolItem.getDurability(stack);
        int maxDurability = UltimateToolItem.getMaxDurability(level);

        var enchantmentLookup = context.registries().lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> efficiency = enchantmentLookup.getOrThrow(Enchantments.EFFICIENCY);

        Holder<Enchantment> fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        ItemEnchantments enchantments = stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY);

        int efficiencyLevel = enchantments.getLevel(efficiency);
        int fortuneLevel = enchantments.getLevel(fortune);

        tooltip.accept(Component.translatable("ultimatetool.tooltip.level", formatNumber(level)).withStyle(ChatFormatting.GOLD)
        );

        if (level < UltimateToolItem.MAX_LEVEL) {
            int requiredBlocks = UltimateToolItem.getRequiredBlocks(level);

            tooltip.accept(Component.translatable("ultimatetool.tooltip.progress",
                    formatNumber(levelProgress), formatNumber(requiredBlocks)).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(Component.translatable(
                    "ultimatetool.tooltip.progress_max").withStyle(ChatFormatting.YELLOW));
        }

        tooltip.accept(Component.translatable("ultimatetool.tooltip.efficiency",
                toRoman(efficiencyLevel)).withStyle(ChatFormatting.AQUA));

        tooltip.accept(Component.translatable(
                "ultimatetool.tooltip.fortune", toRoman(fortuneLevel)).withStyle(ChatFormatting.AQUA));

        addSpacer(tooltip);

        tooltip.accept(Component.translatable("ultimatetool.tooltip.blocks_mined",
                formatNumber(totalBlocksBroken)).withStyle(ChatFormatting.GRAY));

        addSpacer(tooltip);

        float durabilityRatio = maxDurability > 0 ? (float) durability / maxDurability : 0.0F;

        ChatFormatting durabilityColor;

        if (durabilityRatio > 0.60F) {
            durabilityColor = ChatFormatting.GREEN;
        } else if (durabilityRatio > 0.25F) {
            durabilityColor = ChatFormatting.YELLOW;
        } else {
            durabilityColor = ChatFormatting.RED;
        }

        tooltip.accept(Component.translatable("ultimatetool.tooltip.durability",
                formatNumber(durability), formatNumber(maxDurability)).withStyle(durabilityColor));

        tooltip.accept(getDurabilityBar(durability, maxDurability));

        if (level >= 5) {
            addSpacer(tooltip);

            if (level == 5) {
                tooltip.accept(Component.translatable("ultimatetool.tooltip.mining_mode", "3×3")
                        .withStyle(ChatFormatting.YELLOW)
                );
            } else {
                boolean mode5x5 = stack.getOrDefault(ModDataComponents.AREA_MODE_5X5.get(), true);

                tooltip.accept(Component.translatable("ultimatetool.tooltip.mining_mode",
                        mode5x5 ? "5×5" : "3×3").withStyle(ChatFormatting.YELLOW));
            }
        }

        if (level >= UltimateToolItem.MAX_LEVEL) {
            addSpacer(tooltip);

            tooltip.accept(Component.translatable("ultimatetool.tooltip.ultimate"
                    ).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

            tooltip.accept(Component.translatable("ultimatetool.tooltip.ultimate_description"
                    ).withStyle(ChatFormatting.GRAY));

            tooltip.accept(Component.translatable("ultimatetool.tooltip.ultimate_radius",
                    formatNumber(UltimateToolItem.ULTIMATE_RADIUS)).withStyle(ChatFormatting.GRAY));

            tooltip.accept(Component.translatable("ultimatetool.tooltip.ultimate_cooldown",
                    formatNumber(UltimateToolItem.ULTIMATE_COOLDOWN_MINUTES)).withStyle(ChatFormatting.GRAY));

            tooltip.accept(Component.translatable("ultimatetool.tooltip.ultimate_cost",
                    formatNumber(UltimateToolItem.ULTIMATE_DURABILITY_COST)).withStyle(ChatFormatting.RED));
        }
    }

    private static Component getDurabilityBar(int durability, int maxDurability) {
        int barLength = 12;

        float ratio = maxDurability > 0 ? (float) durability / maxDurability : 0.0F;

        ratio = Mth.clamp(ratio, 0.0F, 1.0F);

        int filled = Math.round(barLength * ratio);

        int color = Mth.hsvToRgb(ratio / 3.0F, 1.0F, 1.0F);

        Component filledBar = Component.literal("█".repeat(filled)).withStyle(
                style -> style.withColor(color).withBold(true));

        Component emptyBar = Component.literal("█".repeat(barLength - filled)).withStyle(
                style -> style.withColor(0x555555).withBold(true));

        return filledBar.copy().append(emptyBar);
    }

    private static String formatNumber(int number) {
        return String.format(Locale.GERMANY, "%,d", number);
    }

    private static String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }

    private static void addSpacer(Consumer<Component> tooltip) {
        tooltip.accept(Component.literal(""));
    }
}