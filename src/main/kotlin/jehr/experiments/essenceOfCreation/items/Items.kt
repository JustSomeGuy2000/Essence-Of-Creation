package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ConsumableComponents
import net.minecraft.component.type.DeathProtectionComponent
import net.minecraft.component.type.FoodComponent
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
            StatusEffectInstance(StatusEffects.REGENERATION, 900, 2),
            StatusEffectInstance(StatusEffects.ABSORPTION, 100, 2),
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 1),
            StatusEffectInstance(EoCStatusEffects.blessingOfRye, BlessingOfRye.ampTime, 1)
        ))
    ))))
    val handheldInfuser = register(HandheldInfuser.ID, ::HandheldInfuser, Item.Settings().maxCount(1))
    const val GOD_APPLE_ID = "god_apple"
    val godApple = register(GOD_APPLE_ID, ::Item, Item.Settings().rarity(Rarity.RARE).food(
        FoodComponent.Builder().nutrition(8).saturationModifier(1.5F).alwaysEdible().build(),
        ConsumableComponents.food().consumeEffect(ApplyEffectsConsumeEffect(listOf(
            StatusEffectInstance(StatusEffects.REGENERATION, 600, 3),
            StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 2),
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0),
            StatusEffectInstance(StatusEffects.ABSORPTION, 6000, 5),
            StatusEffectInstance(StatusEffects.STRENGTH, 3600, 3),
            StatusEffectInstance(StatusEffects.SPEED, 3600, 1)
        ))).build()
     ).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true))
    val superBoneMeal = register(SuperBoneMeal.ID, ::SuperBoneMeal, Item.Settings())

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(EoCMain.EoCItemGroupKey).register{
            it.add(this.essenceOfCreation)
            it.add(this.rye)
            it.add(this.totemOfUnrying)
            it.add(this.handheldInfuser)
            it.add(this.godApple)
            it.add(this.superBoneMeal)
        }
    }

    fun register(name: String, factory: (Item.Settings) -> Item, settings: Item.Settings): Item {
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.MOD_ID, name))
        val item = factory(settings.registryKey(itemKey))
        Registry.register(Registries.ITEM, itemKey, item)
        return item
    }
}