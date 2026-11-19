package com.bassalo.ultimatetool;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class UltimateToolCommands {

    private static final Set<UUID> DISABLED_ULTIMATES = new HashSet<>();

    private UltimateToolCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                literal("ultimatearea")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ItemStack stack = player.getMainHandItem();

                            if (!(stack.getItem() instanceof UltimateToolItem)) {
                                player.sendSystemMessage(Component.translatable("ultimatetool.message.hold_tool"));
                                return 0;
                            }

                            int level = UltimateToolItem.getLevel(stack);

                            if (level < UltimateToolItem.MAX_LEVEL) {
                                player.sendSystemMessage(Component.translatable("ultimatetool.message.area_level_required"));
                                return 0;
                            }

                            boolean mode5x5 = !stack.getOrDefault(ModDataComponents.AREA_MODE_5X5.get(), true);

                            stack.set(ModDataComponents.AREA_MODE_5X5.get(), mode5x5);

                            player.sendSystemMessage(Component.translatable(
                                    "ultimatetool.message.mining_mode",
                                    mode5x5 ? "5×5" : "3×3"
                            ));

                            return 1;
                        })
        );

        event.getDispatcher().register(
                literal("ultimate")
                        .then(literal("on")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    DISABLED_ULTIMATES.remove(player.getUUID());

                                    player.sendSystemMessage(
                                            Component.translatable("ultimatetool.message.ultimate_enabled")
                                    );

                                    return 1;
                                }))
                        .then(literal("off")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    DISABLED_ULTIMATES.add(player.getUUID());

                                    player.sendSystemMessage(
                                            Component.translatable("ultimatetool.message.ultimate_disabled")
                                    );

                                    return 1;
                                }))
        );
    }

    public static boolean isUltimateEnabled(ServerPlayer player) {
        return !DISABLED_ULTIMATES.contains(player.getUUID());
    }
}