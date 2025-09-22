package jehr.experiments.essenceOfCreation.blocks

import jehr.experiments.essenceOfCreation.utils.CrawlableConnection
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.registry.tag.BlockTags
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
import kotlin.math.min

class ScaffoldSeed(settings: Settings): ScaffoldingBlock(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(supportedFrom, Direction.DOWN).with(junction, false)
    }

    companion object: CrawlableConnection {
        const val ID = "scaffold_seed"

        override var supportedFrom: EnumProperty<Direction> = EnumProperty.of("supported_from", Direction::class.java)
        override var junction: BooleanProperty = BooleanProperty.of("junction")

        override fun getOutgoing(world: ServerWorld, state: BlockState, pos: BlockPos): List<Direction> {
            val ret = mutableListOf<Direction>()
            for (dir in DIRECTIONS) {
                val targetState = world.getBlockState(pos.offset(dir))
                if ((targetState.isOf(EoCBlocks.scaffoldTrunk) && targetState.get(ScaffoldTrunk.supportedFrom) == dir.opposite) || (targetState.isOf(EoCBlocks.scaffoldSeed) && targetState.get(supportedFrom) == dir.opposite)) {
                    ret.add(dir)
                }
            }
            return ret.toList()
        }

        val trunk = EoCBlocks.scaffoldTrunk
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
        if (direction == state.get(supportedFrom) && neighborState.isAir) {
            tickView.scheduleBlockTick(pos, this, Specs.BREAK_DELAY)
        }
        return state
    }

    override fun randomTick(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        return
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        if (world.isAir(pos.offset(state.get(supportedFrom)))) {
            world.setBlockState(pos, Blocks.AIR.defaultState)
            return
        }
        this.decide(state, world, pos, random)
    }

    fun decide(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
            val action = random.nextDouble() * 100
            if (action <= Chances.PASS) {
                return
            } else if (action <= Chances.CUM_GROW) {
                this.attemptGrowth(world, pos, state, listOf(state.get(supportedFrom).opposite), false)
            } else {
                val split = when {
                    (action <= Chances.CUM_SPLIT2) -> 2
                    (action <= Chances.CUM_SPLIT3) -> 3
                    (action <= Chances.CUM_SPLIT4) -> 4
                    else -> 5
                }
                val taken = mutableListOf<Direction>()
                for (dir in DIRECTIONS) {
                    if (!world.getBlockState(pos.offset(dir)).isAir) {
                        taken.add(dir)
                    }
                }
                val originalDirs = DIRECTIONS.toMutableList()
                originalDirs.removeAll(taken)
                val directions = originalDirs.take(min(split, originalDirs.size)).toMutableList()
                directions.shuffle()
                if (directions.isEmpty()) {
                    world.scheduleBlockTick(pos, this, Specs.accelDelay)
                } else {
                    this.attemptGrowth(world, pos, state, directions.toList(), true)
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
        builder.add(supportedFrom)
        builder.add(junction)
    }

    fun attemptGrowth(world: ServerWorld, pos: BlockPos, state: BlockState, outgoingDirs: List<Direction>, changeDir: Boolean) {
        for (outDir in outgoingDirs) {
            val target = pos.offset(outDir)
            if ((world.isAir(target) || world.getBlockState(target).isIn(BlockTags.REPLACEABLE)) && target.y <= world.topYInclusive) {
                val newState = state.with(junction, false).with(supportedFrom, outDir.opposite)
                world.setBlockState(target, newState, NOTIFY_ALL)
                this.acclerate(world, target)
            }
        } //set this to a scaffold trunk with the same incomings and outgoings of all outgoing directions.
        val newState = trunk.defaultState.with(ScaffoldTrunk.Companion.junction, changeDir).with(
            ScaffoldTrunk.Companion.supportedFrom, state.get(supportedFrom))
        world.setBlockState(pos, newState, NOTIFY_ALL)
    }

    fun acclerate(world: ServerWorld, pos: BlockPos) {
        world.scheduleBlockTick(pos, world.getBlockState(pos).block, Specs.accelDelay)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext?): VoxelShape = VoxelShapes.fullCube()

    override fun canReplace(state: BlockState?, context: ItemPlacementContext?): Boolean  = false
}