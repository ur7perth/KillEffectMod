package com.example.killeffect.gui;

import com.example.killeffect.config.KillEffectConfig;
import com.example.killeffect.effect.KillEffectType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class KillEffectScreen extends Screen {

    public KillEffectScreen() { super(Text.translatable("killeffect.gui.title")); }

    @Override
    protected void init() {
        int slotSize = 40, spacing = 10;
        KillEffectType[] types = KillEffectType.values();
        int totalWidth = types.length * slotSize + (types.length - 1) * spacing;
        int startX = (this.width - totalWidth) / 2;
        int y = this.height / 2 - slotSize / 2 - 20;

        for (int i = 0; i < types.length; i++) {
            KillEffectType type = types[i];
            int x = startX + i * (slotSize + spacing);

            ItemButtonWidget button = new ItemButtonWidget(
                    x, y, slotSize, new ItemStack(type.getIcon()), type.getDisplayName(),
                    btn -> { KillEffectConfig.setSelected(type); this.clearAndInit(); }
            );
            button.setSelected(type == KillEffectConfig.selectedEffect);
            this.addDrawableChild(button);
        }

        this.addDrawableChild(new SpeedSlider(
                this.width / 2 - 100, y + slotSize + 30, 200, 20,
                KillEffectConfig.getSpeed(KillEffectConfig.selectedEffect)
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        Text current = Text.translatable("killeffect.gui.selected", KillEffectConfig.selectedEffect.getDisplayName());
        context.drawCenteredTextWithShadow(this.textRenderer, current, this.width / 2, this.height / 2 + 60, 0xAAAAAA);
    }

    @Override
    public boolean shouldPause() { return false; }

    private static class SpeedSlider extends SliderWidget {
        private static final double MIN = 0.5, MAX = 3.0;

        SpeedSlider(int x, int y, int width, int height, double initialValue) {
            super(x, y, width, height, Text.empty(), (initialValue - MIN) / (MAX - MIN));
            updateMessage();
        }

        private double currentSpeed() { return MIN + this.value * (MAX - MIN); }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.translatable("killeffect.gui.speed", String.format("%.1f", currentSpeed())));
        }

        @Override
        protected void applyValue() {
            KillEffectConfig.setSpeed(KillEffectConfig.selectedEffect, (float) currentSpeed());
        }
    }
}
