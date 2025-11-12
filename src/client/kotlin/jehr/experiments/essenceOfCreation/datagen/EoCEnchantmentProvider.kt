package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.enchantmentEffects.EoCEnchantmentEffects
import jehr.experiments.essenceOfCreation.enchantmentEffects.FulminationEffect
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition
import net.minecraft.component.EnchantmentEffectComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentLevelBasedValue
import net.minecraft.enchantment.effect.EnchantmentEffectTarget
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

class EoCEnchantmentProvider(output: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricDynamicRegistryProvider(output, registryLookup) {

    override fun getName() = "EssenceOfCreationEnchantmentProvider"

    override fun configure(registryLookup: RegistryWrapper.WrapperLookup, entries: Entries) {
        register(entries, EoCEnchantmentEffects.fulmination, Enchantment.builder(
            Enchantment.definition(
                registryLookup.getOrThrow(RegistryKeys.ITEM).getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                0,
                3,
                Enchantment.leveledCost(3, 10),
                Enchantment.leveledCost(3, 15),
                5,
                AttributeModifierSlot.HAND
            )
        ).addEffect(
            EnchantmentEffectComponentTypes.POST_ATTACK,
            EnchantmentEffectTarget.ATTACKER,
            EnchantmentEffectTarget.VICTIM,
            FulminationEffect(EnchantmentLevelBasedValue.linear(1F, 1F))
        ))
    }

    fun register(entries: Entries, key: RegistryKey<Enchantment>, builder: Enchantment.Builder, vararg conditions: ResourceCondition): RegistryEntry<Enchantment> = entries.add(key, builder.build(key.value), *conditions)
}