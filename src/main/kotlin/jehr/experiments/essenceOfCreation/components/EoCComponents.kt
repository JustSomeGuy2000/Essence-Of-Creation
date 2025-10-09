package jehr.experiments.essenceOfCreation.components

import com.mojang.serialization.Codec
import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.items.GunSword
import net.minecraft.component.ComponentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object EoCComponents {

    val gunSwordInfoComponent = register("${GunSword.BASE_ID}_info_component", GunSword.Info.codec)

    fun init() {}

    fun <T> register(name: String, codec: Codec<T>): ComponentType<T> = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(EoCMain.MOD_ID, name), ComponentType.builder<T>().codec(codec).build())
}