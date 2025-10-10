package jehr.experiments.essenceOfCreation.tags

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object EoCTags {
    val gunSwordBulletBreakable = registerForBlock("gun_sword_bullet_breakable")

    fun init() {}

    fun registerForBlock(name: String): TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, Identifier.of(EoCMain.MOD_ID, name))
}