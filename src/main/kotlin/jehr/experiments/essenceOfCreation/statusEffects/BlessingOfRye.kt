package jehr.experiments.essenceOfCreation.statusEffects

import jehr.experiments.essenceOfCreation.items.EoCItems
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import kotlin.math.floor

class BlessingOfRye: StatusEffect(StatusEffectCategory.NEUTRAL, 0x614023) {

    companion object {
        const val ID = "blessing-of-rye"
        const val AMP_TIME = 1200
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean = duration % 20 == 0

    override fun applyUpdateEffect(world: ServerWorld, entity: LivingEntity, amplifier: Int): Boolean {
        if (entity is PlayerEntity) {
            val entry = EoCStatusEffects.blessingOfRye
            if (entity.getStatusEffect(entry)?.duration == 20) {
                entity.removeStatusEffect(entry)
                entity.addStatusEffect(StatusEffectInstance(entry, AMP_TIME, amplifier + 1))
            }
            val stackCount = amplifier/64
            val ryeStacks = mutableListOf<ItemStack>()
            repeat (stackCount) {
                ryeStacks.add( ItemStack(EoCItems.rye, 64))
            }
            if (amplifier % 64 != 0) {
                ryeStacks.add(ItemStack(EoCItems.rye, amplifier % 64))
            }
            for (ryeStack in ryeStacks) {
                val wasAdded = entity.inventory.insertStack(ryeStack)
                if (!wasAdded) {
                    entity.dropStack(world, ryeStack)
                }
            }
            return true
        } else {
            return false
        }
    }
}