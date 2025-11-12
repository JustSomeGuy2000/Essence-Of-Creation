package jehr.experiments.essenceOfCreation.enchantmentEffects

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.effect.EnchantmentEntityEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

object EoCEnchantmentEffects {

    val fulmination = registerEnchantment(FulminationEffect.ID)
    val fulminationEffect = registerEnchantmentEffect("${FulminationEffect.ID}_effect", FulminationEffect.effectCodec)

    fun init() {}

    fun registerEnchantment(name: String): RegistryKey<Enchantment> = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(EoCMain.MOD_ID, name))

    fun <T: EnchantmentEntityEffect> registerEnchantmentEffect(name: String, codec: MapCodec<T>): MapCodec<T> = Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(EoCMain.MOD_ID, name), codec)
}