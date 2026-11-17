package net.purespellbook.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Pure Rune crafting material with an enchanted glint over the base blank rune texture.
 */
public class PureRuneItem extends Item {
    public PureRuneItem() {
        super(new Item.Properties().rarity(Rarity.RARE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
