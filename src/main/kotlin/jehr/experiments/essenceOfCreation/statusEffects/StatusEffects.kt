package jehr.experiments.essenceOfCreation.statusEffects

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier

object EoCStatusEffects {

    val blessingOfRye = register(BlessingOfRye.ID, ::BlessingOfRye)

    fun init() {}

    fun register(name: String, effect: () -> StatusEffect): RegistryEntry.Reference<StatusEffect> = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EoCMain.MOD_ID, name), effect())
}