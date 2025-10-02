package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.EssentialExtractorBlockEntity
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.utils.CombinedBoolDir
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.floor


class EssentialExtractor(settings: Settings): BlockWithEntity(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(condition, CombinedBoolDir.FALSE_NORTH)
    }

    companion object {
        var condition: EnumProperty<CombinedBoolDir> = EnumProperty.of("state", CombinedBoolDir::class.java)

        const val ID = "essential_extractor"
        const val EXTRACT_TIME = 100
        const val FUEL_PER_TICK = 1
        /**Update the Extractor. Perform one of the following actions, evaluated in the following order:
         * - Load fuel: If there is no loaded fuel, source is available, and fuel is available, consume one item of fuel and load fuel equivalent to its value in Companion.fuels.
         * - Finish extraction: If progress == EXTRACT_TIME, consume one item of source and increment accumulator by its value in Companion.sources. If accumulator >= 1, take its integer component and add that amount of Essences of Creation to output. Nothing happens if output is occupied by some other item.
         * - Progress extraction: If there is loaded fuel, source is available, and progress < EXTRACT_TIME, increment progress by 1, and decrement fuel by FUEL_PER_TICK.
         * - Reduce fuel: If there is loaded fuel but nothing else, reduce it.*/
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
            val be = blockEntity
            if (be is EssentialExtractorBlockEntity) {
                if (be.currentFuel == 0 && be.fuel.item in fuels && be.source.item in sources) {
                    val consumedFuel = be.fuel.copyWithCount(1).item
                    be.fuel.decrement(1)
                    be.maxFuel = fuels[consumedFuel]!!
                    be.currentFuel = fuels[consumedFuel]!!
                    be.progress += 1
                    world.setBlockState(pos, state.with(condition, CombinedBoolDir.modBool(state.get(condition)) {true}))
                } else if (be.progress == EXTRACT_TIME && (be.output.isOf(EoCItems.essenceOfCreation) || be.output.isEmpty)) {
                    val consumedSource = be.source.copyWithCount(1).item
                    be.source.decrement(1)
                    be.accumulator += sources[consumedSource]!!
                    be.progress = 0
                    if (be.accumulator >= 1) {
                        val produced = floor(be.accumulator).toInt()
                        be.accumulator -= produced
                        if (be.output.isOf(EoCItems.essenceOfCreation)) {
                            be.output.increment(produced)
                        } else if (be.output.isEmpty) {
                            be.output = ItemStack(EoCItems.essenceOfCreation, 1)
                        }
                    }
                    world.setBlockState(pos, state.with(condition, CombinedBoolDir.modBool(state.get(condition)) {false}))
                } else if (be.currentFuel != 0 && be.source.item in sources && be.progress < EXTRACT_TIME) {
                    be.progress += 1
                    be.currentFuel -= FUEL_PER_TICK
                } else if (be.currentFuel != 0) {
                    be.currentFuel -= FUEL_PER_TICK
                }
            }
        }

        /**How much Essence should result from extraction of the given item.*/
        val sources = mapOf<Item, Double>(Items.GOLD_BLOCK to 1.0)
        /**How much fuel value the given item provides.*/
        val fuels = mapOf<Item, Int>(Items.IRON_BLOCK to 100)
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

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(condition)
    }
}