package com.bassalo.ultimatetool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class UltimateToolCoreItem extends Item {

    public UltimateToolCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip, net.minecraft.world.item.TooltipFlag tooltipFlag) {
        tooltip.accept(Component.translatable("ultimatetool.tooltip.ultimate_tool_core")
                .withStyle(ChatFormatting.GRAY));
    }
}