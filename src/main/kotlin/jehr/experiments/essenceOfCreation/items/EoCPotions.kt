package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object EoCPotions {

    const val PERPLEXING_BREW_ID = "perplexing_brew"
    val perplexingBrew = register("perplexing_brew")

    fun init() {}

    fun register(name: String, vararg effects: StatusEffectInstance): Potion = Registry.register(Registries.POTION, Identifier.of(EoCMain.MOD_ID, name), Potion(name, *effects))
}