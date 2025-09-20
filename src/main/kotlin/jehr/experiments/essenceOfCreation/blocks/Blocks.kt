package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.EoCMain
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier

object EoCBlocks {

    val scaffoldTrunk = register("scaffold_trunk", ::ScaffoldTrunk, AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.SCAFFOLDING))
    val scaffoldSeed = register("scaffold_seed", ::ScaffoldSeed, AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.SCAFFOLDING).ticksRandomly())
    val scaffoldStripper = register("scaffold_stripper", ::ScaffoldStripper, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON))
    val spatialDisplacer = register("spatial_displacer", ::SpatialDisplacer, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE))

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(EoCMain.EoCItemGroupKey).register{
            it.add(scaffoldSeed)
            it.add(scaffoldTrunk)
            it.add(scaffoldStripper)
            it.add(spatialDisplacer)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register {

        }
    }

    fun register(name: String, factory: (AbstractBlock.Settings) -> Block, settings: AbstractBlock.Settings, registerItem: Boolean = true): Block {
        val blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(EoCMain.modId, name))
        val block = factory(settings.registryKey(blockKey))
        if (registerItem) {
            val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.modId, name))
            val item = BlockItem(block, Item.Settings().registryKey(itemKey))
            Registry.register(Registries.ITEM, itemKey, item)
        }
        Registry.register(Registries.BLOCK, blockKey, block)
        return block
    }
}