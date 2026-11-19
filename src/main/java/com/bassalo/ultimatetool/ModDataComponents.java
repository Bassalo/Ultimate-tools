package com.bassalo.ultimatetool;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    Ultimatetool.MODID
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>
            LEVEL =
            DATA_COMPONENTS.registerComponentType(
                    "level",
                    builder -> builder.persistent(Codec.INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>
            LEVEL_PROGRESS =
            DATA_COMPONENTS.registerComponentType(
                    "level_progress",
                    builder -> builder.persistent(Codec.INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>
            TOTAL_BLOCKS_BROKEN =
            DATA_COMPONENTS.registerComponentType(
                    "total_blocks_broken",
                    builder -> builder.persistent(Codec.INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>>
            DURABILITY =
            DATA_COMPONENTS.registerComponentType(
                    "durability",
                    builder -> builder.persistent(Codec.INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>
            AREA_MODE_5X5 =
            DATA_COMPONENTS.registerComponentType(
                    "area_mode_5x5",
                    builder -> builder.persistent(Codec.BOOL)
            );

    private ModDataComponents() { }
}