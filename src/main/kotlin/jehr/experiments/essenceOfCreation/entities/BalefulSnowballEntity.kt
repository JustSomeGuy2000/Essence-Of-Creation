package jehr.experiments.essenceOfCreation.entities

import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.utils.damageSourceOf
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.projectile.thrown.ThrownItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class BalefulSnowballEntity: ThrownItemEntity {

    constructor(entityType: EntityType<out BalefulSnowballEntity>, world: World): super(entityType, world)

    constructor(world: World, owner: LivingEntity, stack: ItemStack): super(EoCEntities.balefulSnowballEntity, owner, world, stack)

    constructor(world: World, x: Double, y: Double, z: Double, stack: ItemStack): super(EoCEntities.balefulSnowballEntity, x, y, z, world, stack)

    companion object {
        const val ID = "baleful_snowball_entity"
    }

    override fun getDefaultItem() = EoCItems.balefulSnowballItem

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        super.onEntityHit(entityHitResult)
        val entity = entityHitResult.entity
        if (this.world is ServerWorld) {
            entity.damage(this.world as ServerWorld, damageSourceOf(this.world, DamageTypes.THROWN, this, this.getOwner()), 5F)
            if (entity is LivingEntity) {
                val dir = this.velocity.normalize()
                entity.takeKnockback(2.0, -dir.x, -dir.z)
                entity.velocityModified = true
            }
        }
    }

    override fun onCollision(hitResult: HitResult) {
        super.onCollision(hitResult)
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
            this.discard()
        }
    }

    override fun handleStatus(status: Byte) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            val particleEffect: ParticleEffect? = this.getParticleParameters()

            repeat(8) {
                this.world.addParticleClient(particleEffect, this.x, this.y, this.z, 0.0, 0.0, 0.0)
            }
        }
    }

    private fun getParticleParameters(): ParticleEffect? {
        val itemStack = this.stack
        return (if (itemStack.isEmpty) ParticleTypes.ITEM_SNOWBALL else ItemStackParticleEffect(
            ParticleTypes.ITEM,
            itemStack
        ))
    }
}