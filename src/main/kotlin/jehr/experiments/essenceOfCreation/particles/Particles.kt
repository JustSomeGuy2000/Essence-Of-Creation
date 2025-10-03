package jehr.experiments.essenceOfCreation.particles

import jehr.experiments.essenceOfCreation.EoCMain
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object EoCParticles {

    val purpleFlame = register("purple_flame", FabricParticleTypes.simple())

    fun init() {}

    fun register(name: String, base: SimpleParticleType): SimpleParticleType = Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EoCMain.MOD_ID, name), base)
}