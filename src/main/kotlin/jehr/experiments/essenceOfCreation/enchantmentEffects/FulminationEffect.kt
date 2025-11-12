package jehr.experiments.essenceOfCreation.enchantmentEffects

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.enchantment.EnchantmentEffectContext
import net.minecraft.enchantment.EnchantmentLevelBasedValue
import net.minecraft.enchantment.effect.EnchantmentEntityEffect
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

data class FulminationEffect(val amount: EnchantmentLevelBasedValue): EnchantmentEntityEffect {

    companion object {
        const val ID = "fulmination"

        val effectCodec: MapCodec<FulminationEffect> = RecordCodecBuilder.mapCodec{ it.group(
            EnchantmentLevelBasedValue.CODEC.fieldOf("amount")
                .forGetter(FulminationEffect::amount))
            .apply(it, ::FulminationEffect) }
    }

    override fun getCodec() = effectCodec

    override fun apply(world: ServerWorld, level: Int, context: EnchantmentEffectContext, target: Entity, pos: Vec3d) {
       if (target is LivingEntity) {
           val strikePos = target.blockPos
           repeat(this.amount.getValue(level).toInt()) {
               EntityType.LIGHTNING_BOLT.spawn(world, strikePos, SpawnReason.TRIGGERED)
           }
       }
    }
}