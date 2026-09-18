package com.example.killeffect.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ItemButtonWidget extends ButtonWidget {

    private final ItemStack icon;
    private boolean selected;

    public ItemButtonWidget(int x, int y, int size, ItemStack icon, Text tooltip, PressAction onPress) {
        super(x, y, size, size, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.icon = icon;
        setTooltip(Tooltip.of(tooltip));
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();

        int bg = selected ? 0xAA55FF55 : (isHovered() ? 0xAAAAAAAA : 0xAA333333);
        context.fill(x, y, x + w, y + h, bg);
        context.drawBorder(x, y, w, h, selected ? 0xFF55FF55 : 0xFF000000);
        context.drawItem(icon, x + w / 2 - 8, y + h / 2 - 8);
    }
}
