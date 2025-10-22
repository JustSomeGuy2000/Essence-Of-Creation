package jehr.experiments.essenceOfCreation.utils

import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

fun scalarToVector(scalar: Double, pitch: Float, yaw: Float, roll: Float): Vec3d {
    val x = -MathHelper.sin((yaw * (Math.PI / 180.0)).toFloat()) * MathHelper.cos((pitch * (Math.PI / 180.0)).toFloat()).toDouble()
    val y = -MathHelper.sin(((pitch + roll) * (Math.PI / 180.0)).toFloat()).toDouble()
    val z = MathHelper.cos((yaw * (Math.PI / 180.0)).toFloat()) * MathHelper.cos((pitch * (Math.PI / 180.0)).toFloat()).toDouble()
    return Vec3d(x, y, z).normalize().multiply(scalar)
}