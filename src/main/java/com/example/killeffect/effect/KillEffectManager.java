package com.example.killeffect.effect;

import com.example.killeffect.config.KillEffectConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class KillEffectManager {

    private static final Random RANDOM = Random.create();

    private static class ActiveEffect {
        KillEffectType type;
        Vec3d pos;
        int age;
        int duration;
        float speed;
    }

    private static final List<ActiveEffect> ACTIVE = new ArrayList<>();

    public static void onPlayerKill(Vec3d pos) {
        KillEffectType type = KillEffectConfig.selectedEffect;
        float speed = KillEffectConfig.getSpeed(type);

        ActiveEffect effect = new ActiveEffect();
        effect.type = type;
        effect.pos = pos;
        effect.age = 0;
        effect.duration = Math.max(1, (int) (20 / speed));
        effect.speed = speed;
        ACTIVE.add(effect);

        playInitialSound(type);
    }

    private static void playInitialSound(KillEffectType type) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        var soundManager = client.getSoundManager();
        switch (type) {
            case LIGHTNING -> soundManager.play(PositionedSoundInstance.master(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f));
            case TNT -> soundManager.play(PositionedSoundInstance.master(SoundEvents.ENTITY_TNT_PRIMED, 1.0f, 1.0f));
            case TOTEM -> soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_TOTEM_USE, 1.0f, 1.0f));
            case FIREWORK -> soundManager.play(PositionedSoundInstance.master(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.0f));
            case ANVIL -> soundManager.play(PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f));
        }
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) return;

        var iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            ActiveEffect e = iterator.next();
            spawnFrame(world, e);
            e.age++;
            if (e.age >= e.duration) iterator.remove();
        }
    }

    private static void spawnFrame(ClientWorld world, ActiveEffect e) {
        double x = e.pos.x, y = e.pos.y, z = e.pos.z;
        int perTick = Math.max(1, Math.round(3 * e.speed));

        switch (e.type) {
            case LIGHTNING -> {
                for (int i = 0; i < perTick; i++) {
                    double h = RANDOM.nextDouble() * 3.0;
                    world.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y + h, z,
                            (RANDOM.nextDouble() - 0.5) * 0.3, 0.2, (RANDOM.nextDouble() - 0.5) * 0.3);
                }
                if (e.age == 0) {
                    for (int i = 0; i < 10; i++) world.addParticle(ParticleTypes.FLASH, x, y + 1, z, 0, 0, 0);
                }
            }
            case TNT -> {
                for (int i = 0; i < perTick; i++) {
                    world.addParticle(ParticleTypes.SMOKE, x, y + 0.5, z,
                            (RANDOM.nextDouble() - 0.5) * 0.2, 0.15, (RANDOM.nextDouble() - 0.5) * 0.2);
                }
                if (e.age == e.duration - 1) world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y + 0.5, z, 0, 0, 0);
            }
            case TOTEM -> {
                for (int i = 0; i < perTick * 3; i++) {
                    world.addParticle(ParticleTypes.TOTEM_OF_UNDYING, x, y + 1, z,
                            (RANDOM.nextDouble() - 0.5) * 0.5, RANDOM.nextDouble() * 0.5, (RANDOM.nextDouble() - 0.5) * 0.5);
                }
            }
            case FIREWORK -> {
                for (int i = 0; i < perTick; i++) {
                    world.addParticle(ParticleTypes.FIREWORK, x, y + 1, z,
                            (RANDOM.nextDouble() - 0.5) * 0.3, RANDOM.nextDouble() * 0.4, (RANDOM.nextDouble() - 0.5) * 0.3);
                }
                if (e.age == e.duration - 1) {
                    for (int i = 0; i < 20; i++) {
                        world.addParticle(ParticleTypes.FIREWORK, x, y + 1, z,
                                (RANDOM.nextDouble() - 0.5) * 1.2, RANDOM.nextDouble() * 1.2, (RANDOM.nextDouble() - 0.5) * 1.2);
                    }
                }
            }
            case ANVIL -> {
                for (int i = 0; i < perTick; i++) {
                    world.addParticle(ParticleTypes.CRIT, x, y + 0.5, z,
                            (RANDOM.nextDouble() - 0.5) * 0.3, 0.1, (RANDOM.nextDouble() - 0.5) * 0.3);
                }
            }
        }
    }
}
