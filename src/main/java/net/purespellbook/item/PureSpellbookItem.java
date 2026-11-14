package net.purespellbook.item;

import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Pure Spellbook: 12-slot spellbook with no equipment stat bonuses and an enchanted glint.
 */
public class PureSpellbookItem extends SpellBook {
    public PureSpellbookItem() {
        super(12, ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
