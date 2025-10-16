package jehr.experiments.essenceOfCreation.entities

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LazyEntityReference
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.WriteView
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class GunSwordBullet(entityType: EntityType<out GunSwordBullet>, world: World): PersistentProjectileEntity(entityType, world), FlyingItemEntity {

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
        super.onEntityHit(entityHitResult)
        if (!world.isClient) {
            val target = entityHitResult.entity
            val damage = this.dataTracker.get(damage)
            val ds = DamageSource(this.world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(
                DamageTypes.ARROW.value).get())
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
}