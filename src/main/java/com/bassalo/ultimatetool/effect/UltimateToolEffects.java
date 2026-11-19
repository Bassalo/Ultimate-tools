package com.bassalo.ultimatetool.effect;

import com.bassalo.ultimatetool.UltimateToolItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class UltimateToolEffects {

    private UltimateToolEffects() { }

    public static void spawnChargeParticles(ServerPlayer player) {
        ServerLevel serverLevel = player.level();

        for (int i = 0; i < 500; i++) {
            double angle = Math.random() * Math.PI * 2.0;
            double radius = 1.0 + Math.random() * 2.5;
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + Math.random() * 3.0;
            double z = player.getZ() + Math.sin(angle) * radius;

            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        for (int i = 0; i < 350; i++) {
            double angle = Math.random() * Math.PI * 2.0;
            double radius = 0.5 + Math.random() * 2.0;
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 0.2 + Math.random() * 2.5;
            double z = player.getZ() + Math.sin(angle) * radius;

            serverLevel.sendParticles(ParticleTypes.CRIT, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static void spawnSphereParticles(ServerLevel serverLevel, BlockPos center, int radius) {
        for (int i = 0; i < 300; i++) {
            double theta = Math.random() * Math.PI * 2.0;
            double phi = Math.acos(2.0 * Math.random() - 1.0);
            double x = center.getX() + 0.5 + radius * Math.sin(phi) * Math.cos(theta);
            double y = center.getY() + 0.5 + radius * Math.cos(phi);
            double z = center.getZ() + 0.5 + radius * Math.sin(phi) * Math.sin(theta);

            serverLevel.sendParticles(ParticleTypes.CRIT, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static void showNoDurabilityParticles(ServerPlayer player, BlockPos pos) {
        player.level().sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                12, 0.25, 0.25, 0.25, 0.05);
    }

    public static void playUltimateExplosion(ServerLevel level, BlockPos center) {
        level.playSound(null, center, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);

        level.sendParticles(ParticleTypes.EXPLOSION, center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                3, 0.5, 0.5, 0.5, 0.0);
    }

    public static void playLevelUpEffects(ServerPlayer player, int level) {
        ServerLevel serverLevel = player.level();

        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(),
                40, 0.8, 1.0, 0.8, 0.2);

        serverLevel.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 1.0, player.getZ(),
                80, 1.0, 1.0, 1.0, 0.2);

        serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(),
                60, 0.8, 1.0, 0.8, 0.15);

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_TWINKLE,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        if (level >= UltimateToolItem.MAX_LEVEL) {
            serverLevel.playSound(null, player.blockPosition(),
                    SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.PLAYERS, 1.0F, 1.0F);

            serverLevel.playSound(null, player.blockPosition(), SoundEvents.FIREWORK_ROCKET_TWINKLE,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public static void sendUltimateError(ServerPlayer player, Component message) {
        player.sendOverlayMessage(message);
        player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1.0F, 1.0F);
    }
}