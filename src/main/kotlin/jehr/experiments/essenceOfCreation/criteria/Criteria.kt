package jehr.experiments.essenceOfCreation.criteria

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.util.Identifier

object EoCCriteria {

    val ryeNotCriterion = register(RyeNotCriterion.ID, ::RyeNotCriterion)
    val ryeTotemCriterion = register(RyeTotemCriterion.ID, ::RyeTotemCriterion)

    fun init() {}

    fun <C: AbstractCriterion.Conditions, T: AbstractCriterion<C>> register(name: String, creator: () -> T): T = Criteria.register(Identifier.of(EoCMain.MOD_ID, name).toString(), creator())
}