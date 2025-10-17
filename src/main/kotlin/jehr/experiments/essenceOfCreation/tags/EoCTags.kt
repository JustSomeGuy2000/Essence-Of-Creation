package jehr.experiments.essenceOfCreation.tags

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object EoCTags {
    val gunSwordBulletBreakable = registerForBlock("gun_sword_bullet_breakable")
    val upgradeableGunSword = registerForItem("upgradeable_gun_sword")

    fun init() {}

    fun registerForBlock(name: String): TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, Identifier.of(EoCMain.MOD_ID, name))

    fun registerForItem(name: String): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.MOD_ID, name))
}