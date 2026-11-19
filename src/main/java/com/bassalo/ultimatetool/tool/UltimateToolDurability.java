package com.bassalo.ultimatetool.tool;

import com.bassalo.ultimatetool.ModDataComponents;
import com.bassalo.ultimatetool.UltimateToolItem;
import net.minecraft.world.item.ItemStack;

public final class UltimateToolDurability {

    private UltimateToolDurability() {
    }

    public static boolean has(ItemStack stack, int amount) {
        return UltimateToolItem.getDurability(stack) >= amount;
    }

    public static int consume(ItemStack stack, int amount) {
        int durability = UltimateToolItem.getDurability(stack);
        int remaining = Math.max(0, durability - amount);

        stack.set(ModDataComponents.DURABILITY.get(), remaining);

        return remaining;
    }

    public static int repair(ItemStack stack, int amount) {
        int durability = UltimateToolItem.getDurability(stack);
        int maxDurability = UltimateToolItem.getMaxDurability(
                UltimateToolItem.getLevel(stack)
        );

        int repaired = Math.min(maxDurability, durability + amount);

        stack.set(ModDataComponents.DURABILITY.get(), repaired);

        return repaired;
    }
}