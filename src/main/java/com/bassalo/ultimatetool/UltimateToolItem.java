package com.bassalo.ultimatetool;

import com.bassalo.ultimatetool.tool.UltimateToolLevel;
import com.bassalo.ultimatetool.tool.UltimateToolTool;
import com.bassalo.ultimatetool.tool.UltimateToolTooltip;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;

public class UltimateToolItem extends Item {

    public static final int MAX_LEVEL = 6;
    public static final int ULTIMATE_RADIUS = 10;
    public static final int ULTIMATE_DURABILITY_COST = 5000;
    public static final int ULTIMATE_COOLDOWN_MINUTES = 5;

    public UltimateToolItem(Properties properties) {
        super(properties.component(DataComponents.TOOLTIP_DISPLAY,
                TooltipDisplay.DEFAULT.withHidden(DataComponents.ENCHANTMENTS, true)));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();

        stack.set(ModDataComponents.LEVEL.get(), 1);
        stack.set(ModDataComponents.LEVEL_PROGRESS.get(), 0);
        stack.set(ModDataComponents.TOTAL_BLOCKS_BROKEN.get(), 0);
        stack.set(ModDataComponents.DURABILITY.get(), getMaxDurability(1));
        stack.set(ModDataComponents.AREA_MODE_5X5.get(), true);

        return stack;
    }

    public static int getLevel(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.LEVEL.get(), 1);
    }

    public static int getLevelProgress(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.LEVEL_PROGRESS.get(), 0);
    }

    public static int getTotalBlocksBroken(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TOTAL_BLOCKS_BROKEN.get(), 0);
    }

    public static int getDurability(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.DURABILITY.get(),
                getMaxDurability(getLevel(stack)));
    }

    public static int getMaxDurability(int level) {
        return switch (level) {
            case 1 -> 500;
            case 2 -> 1500;
            case 3 -> 3500;
            case 4 -> 5000;
            case 5 -> 10000;
            case 6 -> 15000;
            default -> 1000;
        };
    }

    public static int getRequiredBlocks(int level) {
        return switch (level) {
            case 1 -> 1000;
            case 2 -> 2500;
            case 3 -> 5000;
            case 4 -> 10000;
            case 5 -> 50000;
            default -> 0;
        };
    }

    public static boolean updateEnchantments(ItemStack stack,
            net.minecraft.core.HolderLookup.RegistryLookup<Enchantment> enchantmentLookup) {
        UltimateToolLevel toolLevel = UltimateToolLevel.fromLevel(getLevel(stack));

        Holder<Enchantment> efficiency = enchantmentLookup.getOrThrow(Enchantments.EFFICIENCY);

        Holder<Enchantment> fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        boolean enchantmentsChanged = enchantments.getLevel(efficiency) != toolLevel.getEfficiency()
                || enchantments.getLevel(fortune) != toolLevel.getFortune();

        if (!enchantmentsChanged) {
            return false;
        }

        if (!stack.has(DataComponents.ENCHANTMENTS)) {
            stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }

        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            mutable.set(efficiency, toolLevel.getEfficiency());
            mutable.set(fortune, toolLevel.getFortune());});

        return true;
    }

    public static boolean updateToolComponent(ItemStack stack, ServerLevel level) {
        var blockLookup = level.registryAccess().lookupOrThrow(Registries.BLOCK);
        var expectedTool = UltimateToolTool.create(blockLookup);
        var currentTool = stack.get(DataComponents.TOOL);

        if (expectedTool.equals(currentTool)) {
            return false;
        }

        stack.set(DataComponents.TOOL, expectedTool);
        return true;
    }

    private static void updateHaste(Player player, int level) {
        int hasteAmplifier;

        if (level >= 5) {
            hasteAmplifier = 1;
        } else if (level >= 4) {
            hasteAmplifier = 0;
        } else {
            return;
        }

        MobEffectInstance currentEffect = player.getEffect(MobEffects.HASTE);

        if (currentEffect == null) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.HASTE, 2, hasteAmplifier, true, false, true));
            return;
        }

        if (currentEffect.getDuration() <= 2 && currentEffect.getAmplifier() != hasteAmplifier) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.HASTE, 2, hasteAmplifier, true, false, true));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        UltimateToolTooltip.append(stack, context, tooltip);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        boolean componentsChanged = updateToolComponent(stack, level);

        componentsChanged |= updateEnchantments(stack,
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));

        if (slot == EquipmentSlot.MAINHAND) {
            updateHaste(player, getLevel(stack));
        }

        if (!componentsChanged) {
            return;
        }

        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();

        if (slot == EquipmentSlot.MAINHAND) {
            player.connection.send(new ClientboundSetEquipmentPacket(player.getId(),
                    List.of(Pair.of(EquipmentSlot.MAINHAND, stack.copy()))));
        }
    }
}