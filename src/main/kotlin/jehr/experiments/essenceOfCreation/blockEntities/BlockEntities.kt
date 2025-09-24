package jehr.experiments.essenceOfCreation.blockEntities

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.RoggenStatue
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object EoCBlockEntities {
    val scaffoldStripperBlockEntity = register("${ScaffoldStripper.ID}_memory", ::ScaffoldStripperBlockEntity, EoCBlocks.scaffoldStripper)
    val roggenStatueBlockEntity = register("${RoggenStatue.ID}_memory", ::RoggenStatueBlockEntity, EoCBlocks.roggenStatue)

    fun init() {}

    private fun <T : BlockEntity> register(name: String, entityFactory: FabricBlockEntityTypeBuilder.Factory<out T>, block: Block): BlockEntityType<T> {
        val id: Identifier = Identifier.of(EoCMain.MOD_ID, name)
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create (entityFactory, block).build())
    }
}