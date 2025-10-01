package jehr.experiments.essenceOfCreation.criteria

import com.mojang.serialization.Codec
import jehr.experiments.essenceOfCreation.items.EoCItems
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.Optional
import java.util.function.Predicate

class RyeTotemCriterion: AbstractCriterion<RyeTotemCriterion.Conditions>() {

    companion object {
        const val ID = "used_totem_of_unrying"
    }

    override fun getConditionsCodec() = Conditions.codec

    fun trigger(player: ServerPlayerEntity, stack: ItemStack) {
        trigger(player) { stack.isOf(EoCItems.totemOfUnrying) }
    }

    override fun trigger(player: ServerPlayerEntity?, predicate: Predicate<Conditions>) {
        super.trigger(player, predicate)
    }

    data class Conditions(val playerPredicate: Optional<LootContextPredicate>): AbstractCriterion.Conditions {
        companion object {
            val codec: Codec<Conditions> = LootContextPredicate.CODEC.optionalFieldOf("player").xmap(::Conditions, Conditions::player).codec()
        }

        override fun player() = playerPredicate
    }
}