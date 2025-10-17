package jehr.experiments.essenceOfCreation.entities

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LazyEntityReference
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.storage.WriteView
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World
import net.minecraft.world.explosion.AdvancedExplosionBehavior
import java.util.Optional
import java.util.function.Function
import kotlin.math.roundToInt

open class GunSwordBullet(entityType: EntityType<out GunSwordBullet>, world: World): PersistentProjectileEntity(entityType, world), FlyingItemEntity {

    constructor(world: World, owner: LivingEntity, damage: Int, gravity: Float, drag: Float): this(EoCEntities.gunSwordBullet, world) {
        this.setOwner(LazyEntityReference(owner))
        this.dataTracker.set(Companion.damage, damage)
        this.dataTracker.set(Companion.gravity, gravity)
        this.dataTracker.set(Companion.drag, drag)
    }

    companion object {
        val damage: TrackedData<Int> = DataTracker.registerData(GunSwordBullet::class.java, TrackedDataHandlerRegistry.INTEGER)
        val gravity: TrackedData<Float> = DataTracker.registerData(
            GunSwordBullet::class.java, TrackedDataHandlerRegistry.FLOAT)
        val drag: TrackedData<Float> = DataTracker.registerData(GunSwordBullet::class.java, TrackedDataHandlerRegistry.FLOAT)
        const val ID = "gun_sword_bullet"
        const val ID_WC = "${ID}_wc"
        const val ID_SB = "${ID}_sb"

        val wcExplosionBehaviour = AdvancedExplosionBehavior(true, false, Optional.of(1.22F), Registries.BLOCK.getOptional(
            BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()))
    }

    val internalStack = ItemStack(EoCItems.gsbItem)

    override fun getStack() = this.internalStack
    override fun getDefaultItemStack() = this.internalStack

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(damage, 0)
        builder.add(Companion.gravity, 0.0F)
        builder.add(drag, 0.0F)
    }

    override fun tick() {
        super.tick()
        this.world.addParticleClient(ParticleTypes.SQUID_INK, this.x, this.y, this.z, 0.0, 0.0, 0.0)
        if (this.inGroundTime > 0) {
            this.discard()
        }
    }

    override fun onCollision(hitResult: HitResult?) {
        super.onCollision(hitResult)
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        val world = this.world
        val pos = blockHitResult.blockPos
        val block = this.world.getBlockState(pos)
        if (!world.isClient) {
            if (block.isIn(EoCTags.gunSwordBulletBreakable)) {
                world.breakBlock(pos, false)
            }
            this.discard()
        }
        super.onBlockHit(blockHitResult)
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        //super.onEntityHit(entityHitResult)
        if (!world.isClient) {
            val target = entityHitResult.entity
            val damage = this.dataTracker.get(damage)
            val ds = DamageSource(this.world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(
                DamageTypes.ARROW.value).get()) //TODO: Make the damage type work
            target.damage(this.world as ServerWorld, ds, damage.toFloat())
            // TODO: Velocity-based damage?
            this.discard()
        }
    }

    override fun writeCustomData(view: WriteView?) {
        try {
            super.writeCustomData(view)
        } catch(_: Exception) {
            EoCMain.logger.warn("Ghost bullet!")
        }
    }

    override fun applyGravity() {
        this.velocity = this.velocity.add(0.0, -(this.dataTracker.get(Companion.gravity).toDouble()), 0.0)
    }

    class WindCharge(entityType: EntityType<out WindCharge>, world: World): GunSwordBullet(entityType, world) {

        constructor(world: World, owner: LivingEntity, damage: Int, gravity: Float, drag: Float): this(EoCEntities.gunSwordBulletWC, world) {
            this.setOwner(LazyEntityReference(owner))
            this.dataTracker.set(Companion.damage, damage)
            this.dataTracker.set(Companion.gravity, gravity)
            this.dataTracker.set(Companion.drag, drag)
        }

        override fun onCollision(hitResult: HitResult?) {
            val pos = this.pos
            this.world.createExplosion(this, null, wcExplosionBehaviour, pos.x, pos.y, pos.z, 1.2F, false, World.ExplosionSourceType.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST)
            super.onCollision(hitResult)
            this.discard()
        }
    }

    class SonicBoom(entityType: EntityType<out SonicBoom>, world: World): GunSwordBullet(entityType, world) {

        constructor(world: World, owner: LivingEntity, damage: Int, gravity: Float, drag: Float): this(EoCEntities.gunSwordBulletSB, world) {
            this.setOwner(LazyEntityReference(owner))
            this.dataTracker.set(Companion.damage, damage)
            this.dataTracker.set(Companion.gravity, gravity)
            this.dataTracker.set(Companion.drag, drag)
        }

        override fun onCollision(hitResult: HitResult?) {
            this.sonicBoom(hitResult)
            super.onCollision(hitResult)
            this.discard()
        }

        fun sonicBoom(hitResult: HitResult?): Boolean {
            val world = this.world
            if (this.owner == null || hitResult == null || world !is ServerWorld) return false
            val pos = this.pos
            if (hitResult.type == HitResult.Type.ENTITY) {
                val owner = world.getEntity(this.owner!!.uuid)
                if (owner == null) return false
                val entityHitResult = hitResult as EntityHitResult
                val target = entityHitResult.entity
                val diffVector = target.pos.subtract(owner.pos)
                val length = diffVector.length()
                val stepVector = diffVector.normalize()
                for (i in 0..(length.roundToInt() + 4)) {
                    val current = owner.pos.add(stepVector.multiply(i.toDouble()))
                    world.spawnParticles(ParticleTypes.SONIC_BOOM, current.x, current.y, current.z, 1, 0.0, 1.0, 0.0, 0.0)
                }
                if (target is LivingEntity) {
                    val xzkbMultiplier = 4.0 * (1.0 - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE))
                    val ykbMultiplier = 2.5 * (1.0 - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE))
                    target.addVelocity(stepVector.x * xzkbMultiplier, stepVector.y * ykbMultiplier, stepVector.z * xzkbMultiplier)
                }
                world.playSound(this, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 3.0F, 1.0F)
            }
            return true
        }
    }
}