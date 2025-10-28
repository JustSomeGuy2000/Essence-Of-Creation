package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.EoCMain
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.HayBlock
import net.minecraft.block.MapColor
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier

object EoCBlocks {

    val scaffoldTrunk = register(ScaffoldTrunk.ID, ::ScaffoldTrunk, AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.SCAFFOLDING).mapColor(DyeColor.BROWN))
    val scaffoldSeed = register(ScaffoldSeed.ID, ::ScaffoldSeed, AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.SCAFFOLDING).mapColor(DyeColor.YELLOW))
    val scaffoldStripper = register(ScaffoldStripper.ID, ::ScaffoldStripper, AbstractBlock.Settings.create().sounds(BlockSoundGroup.IRON).mapColor(DyeColor.GRAY).hardness(8.0f).resistance(8.0f))
    val spatialDisplacer = register(SpatialDisplacer.ID, ::SpatialDisplacer, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.MAGENTA).luminance{10}.hardness(2.0f).resistance(6.0f))
    val roggenStatue = register(RoggenStatue.ID, ::RoggenStatue, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.BROWN).hardness(1.0f).resistance(2.0f))
    const val RYE_BALE_ID = "rye_bale"
    val ryeBale = register(RYE_BALE_ID, ::HayBlock, AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS).mapColor(DyeColor.BROWN).hardness(0.1f).resistance(0.05f))
    val essentialExtractor = register(EssentialExtractor.ID, ::EssentialExtractor, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY).hardness(6.0f).resistance(5.0f).luminance { state ->  if (state.get(EssentialExtractor.condition).bool) 13 else 0 })
    val essentialInfuser = register(EssentialInfuser.ID, ::EssentialInfuser, AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).mapColor(DyeColor.GRAY).hardness(6.0f).resistance(1200.0F).luminance { state -> if (state.get(EssentialInfuser.active)) 13 else 0 })
    val balefulPoppy = register(BalefulPoppy.ID, ::BalefulPoppy, AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS).mapColor(DyeColor.RED).noCollision().breakInstantly().offset(AbstractBlock.OffsetType.XZ).pistonBehavior(PistonBehavior.DESTROY))
    val refractor = register(Refractor.ID, ::Refractor, AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).strength(4.0F).luminance{ state -> 15 }.nonOpaque().solidBlock(Blocks::never))

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(EoCMain.EoCItemGroupKey).register{
            it.add(scaffoldSeed)
            it.add(scaffoldTrunk)
            it.add(scaffoldStripper)
            it.add(spatialDisplacer)
            it.add(roggenStatue)
            it.add(ryeBale)
            it.add(essentialExtractor)
            it.add(essentialInfuser)
            it.add(refractor)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register {
            it.add(balefulPoppy)
        }
    }

    fun register(name: String, factory: (AbstractBlock.Settings) -> Block, settings: AbstractBlock.Settings, registerItem: Boolean = true): Block {
        val blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(EoCMain.MOD_ID, name))
        val block = factory(settings.registryKey(blockKey))
        if (registerItem) {
            val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.MOD_ID, name))
            val item = BlockItem(block, Item.Settings().registryKey(itemKey))
            Registry.register(Registries.ITEM, itemKey, item)
        }
        Registry.register(Registries.BLOCK, blockKey, block)
        return block
    }
}