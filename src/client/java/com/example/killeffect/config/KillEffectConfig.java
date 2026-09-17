package com.example.killeffect.config;

import com.example.killeffect.effect.KillEffectType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class KillEffectConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("killeffect-client.json");

    public static KillEffectType selectedEffect = KillEffectType.TOTEM;
    public static final Map<KillEffectType, Float> speeds = new EnumMap<>(KillEffectType.class);

    static {
        for (KillEffectType type : KillEffectType.values()) speeds.put(type, 1.0f);
    }

    private static class Data {
        String selectedEffect;
        Map<String, Float> speeds;
    }

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data != null) {
                if (data.selectedEffect != null) {
                    try { selectedEffect = KillEffectType.valueOf(data.selectedEffect); }
                    catch (IllegalArgumentException ignored) {}
                }
                if (data.speeds != null) {
                    data.speeds.forEach((key, value) -> {
                        try { speeds.put(KillEffectType.valueOf(key), value); }
                        catch (IllegalArgumentException ignored) {}
                    });
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void save() {
        Data data = new Data();
        data.selectedEffect = selectedEffect.name();
        Map<String, Float> map = new LinkedHashMap<>();
        speeds.forEach((k, v) -> map.put(k.name(), v));
        data.speeds = map;
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static float getSpeed(KillEffectType type) { return speeds.getOrDefault(type, 1.0f); }

    public static void setSpeed(KillEffectType type, float value) { speeds.put(type, value); save(); }

    public static void setSelected(KillEffectType type) { selectedEffect = type; save(); }
}
