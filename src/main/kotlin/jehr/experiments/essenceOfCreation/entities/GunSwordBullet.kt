package jehr.experiments.essenceOfCreation.entities

import jehr.experiments.essenceOfCreation.damageTypes.EoCDamageTypes
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LazyEntityReference
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.round

class GunSwordBullet(entityType: EntityType<out GunSwordBullet>, world: World): ProjectileEntity(entityType, world), FlyingItemEntity {

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

    override fun initDataTracker(builder: DataTracker.Builder) {
        builder.add(damage, 0)
        builder.add(Companion.gravity, 0.0F)
        builder.add(drag, 0.0F)
    }

    override fun tick() {
        super.tick()
        this.world.addParticleClient(ParticleTypes.SQUID_INK, this.x, this.y, this.z, 0.0, 0.0, 0.0)
        this.velocity = this.velocity.add(0.0, -(this.dataTracker.get(Companion.gravity).toDouble()), 0.0)
        val vel = this.velocity
        this.setPos(this.pos.x + vel.x, this.pos.y + vel.y, this.pos.z + vel.z)
    }

    override fun onBlockHit(blockHitResult: BlockHitResult) {
        super.onBlockHit(blockHitResult)
        val world = this.world
        val pos = BlockPos(round(blockHitResult.pos.x).toInt(), round(blockHitResult.pos.y).toInt(), round(blockHitResult.pos.z).toInt())
        val block = this.world.getBlockState(pos)
        if (world is ServerWorld) {
            this.discard()
            if (block.isIn(EoCTags.gunSwordBulletBreakable)) {
                world.setBlockState(pos, Blocks.AIR.defaultState)
            }
        }

        if (world.isClient && block.isIn(EoCTags.gunSwordBulletBreakable)) {
            world.addBlockBreakParticles(pos, block)
            this.discard()
        }
    }

    override fun onEntityHit(entityHitResult: EntityHitResult) {
        if (this.world is ServerWorld) {
            val target = entityHitResult.entity
            val velocity = this.velocity.length()
            val damage = this.dataTracker.get(damage)
            val ds = DamageSource(this.world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(
                    EoCDamageTypes.gunSwordDamageType.value).get())
            target.damage(this.world as ServerWorld, ds, damage.toFloat())
            // TODO: Velocity-based damage?
        }
        this.discard()
    }
}