package jehr.experiments.essenceOfCreation.criteria

import com.mojang.serialization.Codec
import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.items.EoCItems
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.Optional
import java.util.function.Predicate

class RyeNotCriterion: AbstractCriterion<RyeNotCriterion.Conditions>() {

    companion object {
        const val ID = "inv_of_rye"
    }

    override fun getConditionsCodec() = Conditions.codec

    fun trigger(player: ServerPlayerEntity) {
        val inv = player.inventory
        var filledWithRye = 0
        for (slotNum in 1..inv.mainStacks.size) {
            val stack = inv.getStack(slotNum)
            if (stack.isOf(EoCItems.rye) && stack.count == 64) {
                filledWithRye += 1
            }
        }
        if (player.offHandStack.isOf(EoCItems.rye) && player.offHandStack.count == 64) {
            filledWithRye += 1
        }
        this.trigger(player) {cond -> filledWithRye == inv.mainStacks.size}
    }

    override fun trigger(player: ServerPlayerEntity?, predicate: Predicate<Conditions>) {
        super.trigger(player, predicate)
    }

    data class Conditions(val playerPredicate: Optional<LootContextPredicate>): AbstractCriterion.Conditions {
        companion object {
            val codec: Codec<Conditions> =
                LootContextPredicate.CODEC.optionalFieldOf("player").xmap(::Conditions, Conditions::player).codec()
        }

        override fun player(): Optional<LootContextPredicate> = playerPredicate
    }
}