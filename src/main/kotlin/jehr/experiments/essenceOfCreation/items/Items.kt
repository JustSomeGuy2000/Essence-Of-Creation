package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.DeathProtectionComponent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object EoCItems {

    val essenceOfCreation = register("essence_of_creation", ::EssenceOfCreation, Item.Settings().rarity(Rarity.UNCOMMON))
    const val RYE_ID = "rye"
    val rye = register(RYE_ID, ::Item, Item.Settings())
    const val TOR_ID = "totem_of_unrying"
    val totemOfUnrying = register(TOR_ID, ::Item, Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1).component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent(listOf(
        ClearAllEffectsConsumeEffect(),
        ApplyEffectsConsumeEffect(listOf(
            StatusEffectInstance(StatusEffects.REGENERATION, 900, 1),
            StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1),
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0),
            StatusEffectInstance(EoCStatusEffects.blessingOfRye, BlessingOfRye.ampTime, 1)
        ))
    ))))

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(EoCMain.EoCItemGroupKey).register{
            it.add(this.essenceOfCreation)
            it.add(this.rye)
            it.add(this.totemOfUnrying)
        }
    }

    fun register(name: String, factory: (Item.Settings) -> Item, settings: Item.Settings): Item {
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.MOD_ID, name))
        val item = factory(settings.registryKey(itemKey))
        Registry.register(Registries.ITEM, itemKey, item)
        return item
    }
}