package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.blocks.ScaffoldSeed.Companion.Specs
import jehr.experiments.essenceOfCreation.utils.ConnectionStatus
import jehr.experiments.essenceOfCreation.utils.CrawlableConnection
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
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
        this.defaultState = this.stateManager.defaultState.with(up, ConnectionStatus.NONE).with(
            down, ConnectionStatus.NONE).with(north, ConnectionStatus.NONE).with(
            south, ConnectionStatus.NONE).with(east, ConnectionStatus.NONE).with(
            west, ConnectionStatus.NONE)
    }

    companion object: CrawlableConnection {
        override var up: EnumProperty<ConnectionStatus> = EnumProperty.of("up", ConnectionStatus::class.java)
        override var down: EnumProperty<ConnectionStatus> = EnumProperty.of("down", ConnectionStatus::class.java)
        override var north: EnumProperty<ConnectionStatus> = EnumProperty.of("north", ConnectionStatus::class.java)
        override var south: EnumProperty<ConnectionStatus> = EnumProperty.of("south", ConnectionStatus::class.java)
        override var east: EnumProperty<ConnectionStatus> = EnumProperty.of("east", ConnectionStatus::class.java)
        override var west: EnumProperty<ConnectionStatus> = EnumProperty.of("west", ConnectionStatus::class.java)

        override fun getConnections(state: BlockState): Map<Direction, ConnectionStatus> {
            return mapOf(Direction.UP to state.get(up), Direction.DOWN to state.get(down), Direction.NORTH to state.get(north),
                Direction.SOUTH to state.get(south), Direction.EAST to state.get(east), Direction.WEST to state.get(west))
        }

        override fun getIncoming(state: BlockState): List<Direction> {
            val ret = mutableListOf<Direction>()
            for ((dir, conn) in getConnections(state)) {
                if (conn == ConnectionStatus.INCOMING) {
                    ret.add(dir)
                }
            }
            return ret.toList()
        }

        override fun getOutgoing(state: BlockState): List<Direction> {
            val ret = mutableListOf<Direction>()
            for ((dir, conn) in getConnections(state)) {
                if (conn == ConnectionStatus.OUTGOING) {
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
        if (direction in getIncoming(state) && neighborState.isAir) {
            tickView.scheduleBlockTick(pos, this, Specs.BREAK_DELAY)
        }
        return state
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random?) {
        world.setBlockState(pos, Blocks.AIR.defaultState)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(up)
        builder.add(down)
        builder.add(west)
        builder.add(east)
        builder.add(north)
        builder.add(south)
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