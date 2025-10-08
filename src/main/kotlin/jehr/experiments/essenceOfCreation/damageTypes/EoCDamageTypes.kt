package jehr.experiments.essenceOfCreation.damageTypes

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

object EoCDamageTypes {

    val gunSwordDamageType = register("gun_sword")
    fun gunSwordDamageSource(world: World) = DamageSource(world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(gunSwordDamageType.value).get())

    fun init() {}

    fun register(name: String): RegistryKey<DamageType> = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(EoCMain.MOD_ID, name))
}