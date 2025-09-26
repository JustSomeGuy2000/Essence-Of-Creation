package jehr.experiments.essenceOfCreation.criteria

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.util.Identifier

object EoCCriteria {

    val ryeNotCriterion = register(RyeNotCriterion.ID, ::RyeNotCriterion)

    fun init() {}

    fun <T: AbstractCriterion.Conditions> register(name: String, creator: () -> AbstractCriterion<T>): AbstractCriterion<T> = Criteria.register(Identifier.of(EoCMain.MOD_ID, name).toString(), creator())
}