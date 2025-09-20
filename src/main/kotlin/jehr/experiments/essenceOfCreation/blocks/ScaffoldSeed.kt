package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.utils.ConnAlterer
import jehr.experiments.essenceOfCreation.utils.ConnectionStatus
import jehr.experiments.essenceOfCreation.utils.CrawlableConnection
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import kotlin.math.min

class ScaffoldSeed(settings: Settings): ScaffoldingBlock(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(age, 1).with(DISTANCE, 0).with(WATERLOGGED, false).with(up, ConnectionStatus.OUTGOING).with(down, ConnectionStatus.INCOMING).with(north, ConnectionStatus.NONE).with(south, ConnectionStatus.NONE).with(east, ConnectionStatus.NONE).with(west, ConnectionStatus.NONE)
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

        val trunk = EoCBlocks.scaffoldTrunk
        const val MAX_AGE = 512
        var age: IntProperty = IntProperty.of("scaffold_seed_age", 1, MAX_AGE)
        object Chances {
            const val PASS = 0
            const val GROW = 96
            const val SPLIT2 = 2
            const val SPLIT3 = 1
            const val SPLIT4 = 0.75
            const val SPLIT5 = 0.25

            const val CUM_GROW = PASS + GROW
            const val CUM_SPLIT2 = CUM_GROW + SPLIT2
            const val CUM_SPLIT3 = CUM_SPLIT2 + SPLIT3
            const val CUM_SPLIT4 = CUM_SPLIT3 + SPLIT4
            init {
                assert (CUM_SPLIT4 + SPLIT5 == 100.0)
            }
        }
        object Specs {
            var accelDelay = 20
            const val BREAK_DELAY = 2
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, Specs.accelDelay)
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

    override fun randomTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        return
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        for (dir in getIncoming(state)) {
            if (world.isAir(pos.offset(dir))) {
                world.setBlockState(pos, Blocks.AIR.defaultState)
                return
            }
        }
        this.decide(state, world, pos, random)
    }

    fun decide(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val currentAge = state.get(age) as Int
        if (currentAge < MAX_AGE) {
            val action = random.nextDouble() * 100
            if (action <= Chances.PASS) {
                return
            } else if (action <= Chances.CUM_GROW) {
                val changeDir = false
                if (changeDir) {
                    val dirs = DIRECTIONS.toMutableList()
                    dirs.removeAll(getOutgoing(state))
                    dirs.shuffle()
                    if (!dirs.isEmpty()) {
                        val newState = ConnAlterer(state, Companion).setOutgoing(dirs.take(1)).getState()
                        world.setBlockState(pos, newState)
                    }
                }
                this.attemptGrowth(world, pos, state, currentAge, listOf(getOutgoing(state)[0].opposite))
            } else {
                val split = when {
                    (action <= Chances.CUM_SPLIT2) -> 2
                    (action <= Chances.CUM_SPLIT3) -> 3
                    (action <= Chances.CUM_SPLIT4) -> 4
                    else -> 5
                }
                val taken = mutableListOf<Direction>()
                for (dir in DIRECTIONS) {
                    if (world.getBlockState(pos.offset(dir)).isOf(trunk)) {
                        taken.add(dir)
                    }
                }
                val originalDirs = DIRECTIONS.toMutableList()
                originalDirs.removeAll(taken)
                val directions = originalDirs.take(min(split, originalDirs.size))
                if (directions.isEmpty()) {
                    world.scheduleBlockTick(pos, this, Specs.accelDelay)
                } else {
                    this.attemptGrowth(world, pos, state, currentAge, directions)
                }
            }
        }
    }

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

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(age)
        builder.add(up)
        builder.add(down)
        builder.add(west)
        builder.add(east)
        builder.add(north)
        builder.add(south)
    }

    fun attemptGrowth(world: ServerWorld, pos: BlockPos, state: BlockState, currentAge: Int, outgoingDirs: List<Direction>) {
        for (outDir in outgoingDirs) {
            val target = pos.offset(outDir)
            if ((world.isAir(target) || world.getBlockState(target).isIn(BlockTags.REPLACEABLE)) && target.y <= world.topYInclusive) {
                val newState = ConnAlterer(state, Companion).setIncoming(listOf(outDir.opposite)).setOutgoing(listOf(outDir)).getState().with(age, if (outgoingDirs.size == 1) currentAge else 0)
                world.setBlockState(pos, newState, NOTIFY_ALL)
                this.acclerate(world, target)
            }
        } //set this to a scaffold trunk with the same incomings and outgoings of all outgoing directions.
        val newState = ConnAlterer(EoCBlocks.scaffoldTrunk.defaultState, ScaffoldTrunk.Companion).setIncoming(getIncoming(state)).setOutgoing(outgoingDirs).getState()
        world.setBlockState(pos, newState, NOTIFY_ALL)
    }

    fun acclerate(world: ServerWorld, pos: BlockPos) {
        world.scheduleBlockTick(pos, world.getBlockState(pos).block, Specs.accelDelay)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext?): VoxelShape = VoxelShapes.fullCube()

    override fun canReplace(state: BlockState?, context: ItemPlacementContext?): Boolean  = false
}