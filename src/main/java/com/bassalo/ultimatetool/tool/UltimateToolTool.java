package com.bassalo.ultimatetool.tool;

import com.bassalo.ultimatetool.event.ConfigHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class UltimateToolTool {

    private UltimateToolTool() {
    }

    public static Tool create(HolderLookup.RegistryLookup<Block> blockLookup) {
        List<Holder<Block>> allowedBlocks = new ArrayList<>();
        Set<String> allowedBlockIds = ConfigHandler.getAllowedBlocks();

        for (Holder.Reference<Block> holder : blockLookup.listElements().toList()) {
            String blockId = holder.key().identifier().toString();

            if (allowedBlockIds.contains(blockId)) {
                allowedBlocks.add(holder);
            }
        }

        HolderSet<Block> allowedBlockSet = HolderSet.direct(allowedBlocks);

        return new Tool(
                List.of(
                        Tool.Rule.minesAndDrops(allowedBlockSet, 9.0F)
                ),
                1.0F,
                1,
                false
        );
    }
}