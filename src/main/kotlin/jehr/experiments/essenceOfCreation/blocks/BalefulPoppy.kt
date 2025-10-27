package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.utils.damageSourceOf
import net.minecraft.block.BlockState
import net.minecraft.block.FlowerBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityCollisionHandler
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class BalefulPoppy(settings: Settings): FlowerBlock(StatusEffects.WITHER, 60F, settings) {

    companion object {
        const val ID = "baleful_poppy"
        const val MAX_CD = 5
    }

    var cooldown = 0

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity, handler: EntityCollisionHandler?) {
        if (world is ServerWorld) {
            this.cooldown -= 1
            if (this.cooldown <= 0) {
                entity.slowMovement(state, Vec3d(0.8, 0.75, 0.8))
                entity.damage(world, damageSourceOf(world, DamageTypes.MAGIC), 3F)
                val vel = entity.velocity
                entity.setVelocity(vel.x, 0.0, vel.y)
                entity.velocityModified = true
                cooldown = MAX_CD
            }
        }
    }
}