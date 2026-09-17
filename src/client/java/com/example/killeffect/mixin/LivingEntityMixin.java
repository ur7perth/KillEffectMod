package com.example.killeffect.mixin;

import com.example.killeffect.effect.KillEffectManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void killeffect$onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.getWorld().isClient()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity localPlayer = client.player;
        if (localPlayer == null) return;

        if (source.getAttacker() == localPlayer && self != localPlayer) {
            KillEffectManager.onPlayerKill(new Vec3d(self.getX(), self.getY(), self.getZ()));
        }
    }
}
