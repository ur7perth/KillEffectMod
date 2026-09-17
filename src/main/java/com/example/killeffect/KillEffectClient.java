package com.example.killeffect;

import com.example.killeffect.config.KillEffectConfig;
import com.example.killeffect.effect.KillEffectManager;
import com.example.killeffect.gui.KillEffectScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KillEffectClient implements ClientModInitializer {

    public static final String MOD_ID = "killeffect";
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        KillEffectConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.killeffect.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.category.killeffect"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new KillEffectScreen());
                }
            }
            KillEffectManager.tick();
        });
    }
}
