package net.purespellbook.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.registry.ModSpells;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PureScrollLootModifier extends LootModifier {
    public static final Codec<PureScrollLootModifier> CODEC = RecordCodecBuilder.create(inst ->
            codecStart(inst).and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                    .apply(inst, PureScrollLootModifier::new));

    private final float chance;

    public PureScrollLootModifier(LootItemCondition[] conditionsIn, float chance) {
        super(conditionsIn);
        this.chance = chance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        String tablePath = context.getQueriedLootTableId().getPath();
        if (tablePath.startsWith("chests/") && context.getRandom().nextFloat() < chance) {
            List<RegistryObject<AbstractSpell>> spells = ModSpells.allPureSpells();
            AbstractSpell spell = spells.get(context.getRandom().nextInt(spells.size())).get();
            ItemStack scroll = new ItemStack(ItemRegistry.SCROLL.get());
            ISpellContainer.createScrollContainer(spell, 1, scroll);
            generatedLoot.add(scroll);
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
