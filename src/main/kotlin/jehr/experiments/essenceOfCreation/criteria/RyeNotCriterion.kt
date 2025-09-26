package jehr.experiments.essenceOfCreation.criteria

import com.mojang.serialization.Codec
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.predicate.entity.LootContextPredicate
import java.util.Optional

class RyeNotCriterion: AbstractCriterion<InventoryChangedCriterion.Conditions>() {

    companion object {
        const val ID = "inv_of_rye"
    }

    override fun getConditionsCodec() = TODO()

    data class Conditions(val playerPredicate: Optional<LootContextPredicate>): AbstractCriterion.Conditions {

        val codec: Codec<Conditions> = LootContextPredicate.CODEC.optionalFieldOf("player").xmap(::Conditions, Conditions::player).codec()

        override fun player(): Optional<LootContextPredicate> = playerPredicate
    }
}