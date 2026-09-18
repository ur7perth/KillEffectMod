package com.example.killeffect.effect;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public enum KillEffectType {
    LIGHTNING("killeffect.type.lightning", Items.TRIDENT),
    TNT("killeffect.type.tnt", Items.TNT),
    TOTEM("killeffect.type.totem", Items.TOTEM_OF_UNDYING),
    FIREWORK("killeffect.type.firework", Items.FIREWORK_ROCKET),
    ANVIL("killeffect.type.anvil", Items.ANVIL);

    private final String translationKey;
    private final Item icon;

    KillEffectType(String translationKey, Item icon) {
        this.translationKey = translationKey;
        this.icon = icon;
    }

    public Item getIcon() { return icon; }

    public Text getDisplayName() { return Text.translatable(translationKey); }
}
