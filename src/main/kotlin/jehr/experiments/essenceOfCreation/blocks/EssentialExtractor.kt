package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.EssentialExtractorBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class EssentialExtractor(settings: Settings): BlockWithEntity(settings) {

    companion object {
        const val ID = "essential_extractor"
        const val EXTRACT_TIME = 100
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
            // TODO
        }

        val sources = mapOf<Item, Float>()
        val fuels = mapOf<Item, Int>()
    }

    override fun getCodec(): MapCodec<EssentialExtractor> = createCodec(::EssentialExtractor)

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = EssentialExtractorBlockEntity(pos, state)

    override fun getRenderType(state: BlockState?) = BlockRenderType.MODEL

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T?>?): BlockEntityTicker<T?>? = validateTicker(type, EoCBlockEntities.essentialExtractorBlockEntity, EssentialExtractor::tick)

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hit: BlockHitResult): ActionResult {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is EssentialExtractorBlockEntity) {
                val shf = state.createScreenHandlerFactory(world, pos)
                if (shf != null) {
                    player.openHandledScreen(shf)
                    return ActionResult.SUCCESS
                }
            }
        }
        return ActionResult.FAIL
    }

    override fun onStateReplaced(state: BlockState, world: ServerWorld, pos: BlockPos, moved: Boolean) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is EssentialExtractorBlockEntity) {
            ItemScatterer.spawn(world, pos, blockEntity)
            world.updateComparators(pos, this)
        }
        super.onStateReplaced(state, world, pos, moved)
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        val blockEntity = world.getBlockEntity(pos)
        return if (blockEntity !is EssentialExtractorBlockEntity) 0 else (blockEntity.currentFuel/blockEntity.maxFuel)*15
    }
}