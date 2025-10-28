package jehr.experiments.essenceOfCreation.utils

import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

fun scalarToVector(scalar: Double, pitch: Float, yaw: Float, roll: Float): Vec3d {
    val x = -MathHelper.sin((yaw * (Math.PI / 180.0)).toFloat()) * MathHelper.cos((pitch * (Math.PI / 180.0)).toFloat()).toDouble()
    val y = -MathHelper.sin(((pitch + roll) * (Math.PI / 180.0)).toFloat()).toDouble()
    val z = MathHelper.cos((yaw * (Math.PI / 180.0)).toFloat()) * MathHelper.cos((pitch * (Math.PI / 180.0)).toFloat()).toDouble()
    return Vec3d(x, y, z).normalize().multiply(scalar)
}

fun damageSourceOf(world: World, type: RegistryKey<DamageType>, source: Entity? = null, attacker: Entity? = null) = DamageSource(world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(type.value).get(), source, attacker)