package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.blocks.ScaffoldSeed.Companion.Specs
import jehr.experiments.essenceOfCreation.utils.CrawlableConnection
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView

class ScaffoldTrunk(settings: Settings): ScaffoldingBlock(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(supportedFrom, Direction.DOWN).with(junction, false).with(WATERLOGGED, false)
    }

    companion object: CrawlableConnection {
        const val ID = "scaffold_trunk"
        override var supportedFrom: EnumProperty<Direction> = EnumProperty.of("supported_from", Direction::class.java)
        override var junction: BooleanProperty = BooleanProperty.of("junction")

        override fun getOutgoing(world: ServerWorld, state: BlockState, pos: BlockPos): List<Direction> {
            val ret = mutableListOf<Direction>()
            for (dir in DIRECTIONS) {
                val targetState = world.getBlockState(pos.offset(dir))
                if ((targetState.isOf(EoCBlocks.scaffoldTrunk) && targetState.get(supportedFrom) == dir.opposite) || (targetState.isOf(EoCBlocks.scaffoldSeed) && targetState.get(ScaffoldSeed.Companion.supportedFrom) == dir.opposite)) {
                    ret.add(dir)
                }
            }
            return ret.toList()
        }
    }

    override fun onBlockAdded(state: BlockState?, world: World?, pos: BlockPos?, oldState: BlockState?, notify: Boolean) {
        return
    }

    override fun getStateForNeighborUpdate(state: BlockState, world: WorldView, tickView: ScheduledTickView, pos: BlockPos, direction: Direction, neighborPos: BlockPos, neighborState: BlockState, random: Random?): BlockState? {
        if (direction == state.get(supportedFrom) && neighborState.isAir) {
            tickView.scheduleBlockTick(pos, this, Specs.BREAK_DELAY)
        }
        return state
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random?) {
        world.setBlockState(pos, Blocks.AIR.defaultState)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(supportedFrom)
        builder.add(junction)
    }

    override fun canReplace(state: BlockState?, context: ItemPlacementContext) = !(context.stack.isOf(this.asItem()) || context.stack.isOf(EoCBlocks.scaffoldStripper.asItem()))

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext?): VoxelShape = VoxelShapes.fullCube()

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? = super.getPlacementState(ctx) ?: Blocks.PURPUR_BLOCK.defaultState

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val blockState = world.getBlockState(pos)
        if (!blockState.isAir) {
            return false
        } else {
            for (dir in DIRECTIONS) {
                if (!world.getBlockState(pos.offset(dir)).isAir) {
                    return true
                }
            }
            return false
        }
    }
}