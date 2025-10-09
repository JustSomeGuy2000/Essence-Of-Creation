package jehr.experiments.essenceOfCreation.entities

import jehr.experiments.essenceOfCreation.items.EoCItems
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityType
import net.minecraft.entity.FlyingItemEntity
import net.minecraft.entity.LazyEntityReference
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.world.World

class GunSwordBullet(entityType: EntityType<out GunSwordBullet>, world: World): ProjectileEntity(entityType, world), FlyingItemEntity {

    constructor(world: World, owner: LivingEntity, damage: Int): this(EoCEntities.gunSwordBullet, world) {
        this.setOwner(LazyEntityReference(owner))
        this.dataTracker.set(Companion.damage, damage)
    }

    companion object {
        val damage: TrackedData<Int> = DataTracker.registerData(GunSwordBullet::class.java, TrackedDataHandlerRegistry.INTEGER)
        const val ID = "gun_sword_bullet"
    }

    val internalStack = ItemStack(EoCItems.gsbItem)

    override fun getStack() = this.internalStack

    override fun initDataTracker(builder: DataTracker.Builder) {
        builder.add(damage, this.dataTracker.get(damage))
    }

    override fun tick() {
        super.tick()
    }

    override fun onBlockHit(blockHitResult: BlockHitResult?) {
        super.onBlockHit(blockHitResult)
    }

    override fun onBlockCollision(state: BlockState?) {
        super.onBlockCollision(state)
    }

    override fun onEntityHit(entityHitResult: EntityHitResult?) {
        super.onEntityHit(entityHitResult)
    }
}