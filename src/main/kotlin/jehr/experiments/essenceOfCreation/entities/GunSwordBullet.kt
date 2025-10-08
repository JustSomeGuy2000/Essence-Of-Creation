package jehr.experiments.essenceOfCreation.entities

import net.minecraft.entity.EntityType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.world.World

class GunSwordBullet<T : GunSwordBullet>(entityType: EntityType<T>, world: World): ProjectileEntity(entityType, world) {

    val damage = 0

    override fun initDataTracker(builder: DataTracker.Builder?) {}
}