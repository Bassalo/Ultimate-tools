package com.bassalo.ultimatetool.ultimate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class PendingUltimate {

    private final ServerLevel level;
    private final BlockPos center;
    private int ticksRemaining;

    public PendingUltimate(
            ServerLevel level,
            BlockPos center,
            int ticksRemaining
    ) {
        this.level = level;
        this.center = center;
        this.ticksRemaining = ticksRemaining;
    }

    public void tick() {
        ticksRemaining--;
    }

    public boolean isReady() {
        return ticksRemaining <= 0;
    }

    public ServerLevel level() {
        return level;
    }

    public BlockPos center() {
        return center;
    }
}