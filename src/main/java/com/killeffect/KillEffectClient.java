package com.killeffect;

import com.killeffect.config.KillEffectConfig;
import com.killeffect.effect.KillEffectRenderer;
import com.killeffect.gui.KillEffectScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KillEffectClient implements ClientModInitializer {
    private static KeyBinding openGuiKey;
    private static final int KILL_CREDIT_WINDOW_TICKS = 60;
    private static final Map<Integer, TrackedTarget> trackedTargets = new HashMap<>();

    @Override
    public void onInitializeClient() {
        KillEffectConfig.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.killeffect.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.killeffect"
        ));

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (world.isClient && entity instanceof PlayerEntity && player == client.player) {
                trackedTargets.put(entity.getId(), new TrackedTarget(entity.getPos(), KILL_CREDIT_WINDOW_TICKS));
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (client.world == null) return;

        while (openGuiKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new KillEffectScreen());
            }
        }

        KillEffectRenderer.tick();

        Iterator<Map.Entry<Integer, TrackedTarget>> it = trackedTargets.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, TrackedTarget> entry = it.next();
            TrackedTarget target = entry.getValue();
            Entity entity = client.world.getEntityById(entry.getKey());

            if (entity instanceof LivingEntity living && !living.isRemoved()) {
                target.lastPos = living.getPos();
                if (living.getHealth() <= 0.0f) {
                    KillEffectRenderer.play(target.lastPos);
                    it.remove();
                    continue;
                }
            } else {
                KillEffectRenderer.play(target.lastPos);
                it.remove();
                continue;
            }

            target.ticksLeft--;
            if (target.ticksLeft <= 0) {
                it.remove();
            }
        }
    }

    private static class TrackedTarget {
        Vec3d lastPos;
        int ticksLeft;

        TrackedTarget(Vec3d lastPos, int ticksLeft) {
            this.lastPos = lastPos;
            this.ticksLeft = ticksLeft;
        }
    }
}
