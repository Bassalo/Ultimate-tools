package com.bassalo.ultimatetool;

import com.bassalo.ultimatetool.tool.UltimateToolDurability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public class RepairCoreItem extends Item {

    private final boolean fullRepair;

    public RepairCoreItem(Properties properties, boolean fullRepair) {
        super(properties);
        this.fullRepair = fullRepair;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack coreStack = player.getItemInHand(hand);
        ItemStack toolStack = player.getMainHandItem();

        if (!(toolStack.getItem() instanceof UltimateToolItem)) {
            player.sendOverlayMessage(
                    Component.translatable("ultimatetool.message.hold_tool"));
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1.0F, 1.0F);
            return InteractionResult.PASS;
        }

        int durability = UltimateToolItem.getDurability(toolStack);
        int maxDurability = UltimateToolItem.getMaxDurability(UltimateToolItem.getLevel(toolStack));

        if (durability >= maxDurability) {
            player.sendOverlayMessage(Component.translatable("ultimatetool.message.durability_max")
                    .withStyle(ChatFormatting.RED));
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1.0F, 1.0F);
            return InteractionResult.PASS;
        }

        int repairAmount = fullRepair ? maxDurability : maxDurability / 4;

        UltimateToolDurability.repair(toolStack, repairAmount);

        coreStack.shrink(1);

        player.sendOverlayMessage(Component.translatable(
                "ultimatetool.message.repaired", UltimateToolItem.getDurability(toolStack),
                maxDurability).withStyle(ChatFormatting.GREEN));

        player.playSound(SoundEvents.ANVIL_USE, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
            java.util.function.Consumer<Component> tooltip, net.minecraft.world.item.TooltipFlag tooltipFlag) {
        tooltip.accept(Component.translatable(fullRepair
                ? "ultimatetool.tooltip.repair_core_100" : "ultimatetool.tooltip.repair_core_25"
                    ).withStyle(ChatFormatting.GRAY));
    }
}