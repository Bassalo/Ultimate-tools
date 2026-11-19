package com.bassalo.ultimatetool;

import com.bassalo.ultimatetool.event.BlockBreakHandler;
import com.bassalo.ultimatetool.event.ConfigHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Ultimatetool.MODID)
public class Ultimatetool {

    public static final String MODID = "ultimatetool";

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);

    public static final DeferredHolder<Item, UltimateToolItem> ULTIMATE_TOOL =
            ITEMS.registerItem("ultimate_tool", UltimateToolItem::new,
                    properties -> properties.stacksTo(1));

    public static final DeferredHolder<Item, RepairCoreItem> REPAIR_CORE_25 =
            ITEMS.registerItem("repair_core_25",
                    properties -> new RepairCoreItem(properties, false));

    public static final DeferredHolder<Item, RepairCoreItem> REPAIR_CORE_100 =
            ITEMS.registerItem("repair_core_100",
                    properties -> new RepairCoreItem(properties, true));

    public static final DeferredHolder<Item, UltimateToolCoreItem> ULTIMATE_TOOL_CORE =
            ITEMS.registerItem("ultimate_tool_core", UltimateToolCoreItem::new);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ULTIMATE_TOOL_TAB =
            CREATIVE_MODE_TABS.register("ultimate_tool_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ultimatetool.ultimate_tool_tab"))
                    .icon(() -> new ItemStack(ULTIMATE_TOOL.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ULTIMATE_TOOL.get());
                        output.accept(REPAIR_CORE_25.get());
                        output.accept(REPAIR_CORE_100.get());
                        output.accept(ULTIMATE_TOOL_CORE.get());
                    })
                    .build());

    public Ultimatetool(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);

        modEventBus.register(ConfigHandler.class);

        NeoForge.EVENT_BUS.register(BlockBreakHandler.class);
        NeoForge.EVENT_BUS.register(UltimateToolCommands.class);
    }
}
