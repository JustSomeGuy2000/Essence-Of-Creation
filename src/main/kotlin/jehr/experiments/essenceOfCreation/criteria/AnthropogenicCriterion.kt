package jehr.experiments.essenceOfCreation.criteria

import com.mojang.serialization.Codec
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.Optional
import java.util.function.Predicate

class AnthropogenicCriterion: AbstractCriterion<AnthropogenicCriterion.Conditions>() {

    companion object {
        const val ID = "anthropogenic_criterion"
    }

    override fun getConditionsCodec() = Conditions.codec

    fun trigger(player: ServerPlayerEntity) = trigger(player) {true}

    override fun trigger(player: ServerPlayerEntity, predicate: Predicate<Conditions>) = super.trigger(player, predicate)

    class Conditions(val playerPredicate: Optional<LootContextPredicate>): AbstractCriterion.Conditions {

        companion object {
            val codec: Codec<Conditions> = LootContextPredicate.CODEC.optionalFieldOf("player").xmap(::Conditions, Conditions::player).codec()
        }

        override fun player(): Optional<LootContextPredicate> = playerPredicate
    }
}