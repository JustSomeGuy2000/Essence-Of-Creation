package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.ScaffoldStripperBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import net.minecraft.world.WorldView
import net.minecraft.world.tick.ScheduledTickView
import kotlin.jvm.optionals.getOrNull

class ScaffoldStripper(settings: Settings): BlockWithEntity(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(progress, Progress.NEW).with(direction, Direction.UP).with(directionSet, true)
    }

    companion object {
        const val ID = "scaffold_stripper"
        enum class Progress: StringIdentifiable {
            NEW {
                override fun asString() = "new"
            }, SEARCHING {
                override fun asString() = "searching"
            }, MOVING {
                override fun asString() = "moving"
            }, HALTED {
                override fun asString() = "halted"
            }
        }

        var moveRate = 2
        var progress: EnumProperty<Progress> = EnumProperty.of("progress", Progress::class.java)
        var direction: EnumProperty<Direction> = Properties.FACING
        var directionSet: BooleanProperty = BooleanProperty.of("direction_set")
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = ScaffoldStripperBlockEntity(pos, state)

    override fun getCodec(): MapCodec<ScaffoldStripper> = createCodec(::ScaffoldStripper)

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        if (!world.isClient && state.get(progress) != Progress.HALTED) {
            world.scheduleBlockTick(pos, this, moveRate)
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        val currentProgress = state.get(progress)
        val produced = mutableListOf<BlockPos>()
        when (currentProgress) {
            Progress.NEW -> {
                val valid = this.searchUnconditionally(world, pos)
                if (!valid.isEmpty()) {
                    val validPos = valid.values.toList()[0]
                    val old = world.getBlockState(validPos)
                    world.setBlockState(validPos, state.with(progress, Progress.SEARCHING).with(directionSet, false), NOTIFY_ALL)
                    world.getBlockEntity(validPos, EoCBlockEntities.scaffoldStripperBlockEntity).getOrNull()?.changeStored(old)
                    world.setBlockState(pos, Blocks.AIR.defaultState, NOTIFY_ALL)
                    produced.add(validPos)
                } else {
                    world.setBlockState(pos, state.with(progress, Progress.HALTED))
                }
            }
            Progress.SEARCHING -> {
                val valid = mutableMapOf<Direction, BlockPos>()
                for (dir in DIRECTIONS) {
                    val searchBlock = world.getBlockState(pos.offset(dir))
                    if ((searchBlock.isOf(EoCBlocks.scaffoldSeed)
                                && searchBlock.get(ScaffoldSeed.supportedFrom) == dir.opposite)
                        || (searchBlock.isOf(EoCBlocks.scaffoldTrunk)
                                && searchBlock.get(ScaffoldTrunk.supportedFrom) == dir.opposite)) {
                        valid.put(dir, pos.offset(dir))
                    }
                }
                for ((dir, newPos) in valid) { //spawn new ones and load their entites
                    val old = world.getBlockState(newPos)
                    world.setBlockState(newPos, state.with(direction, dir).with(progress, Progress.MOVING), NOTIFY_ALL)
                    world.getBlockEntity(newPos, EoCBlockEntities.scaffoldStripperBlockEntity).get().changeStored(old)
                    world.scheduleBlockTick(newPos, world.getBlockState(newPos).block, moveRate)
                }
                // replace contained block
                world.setBlockState(pos, world.getBlockEntity(pos, EoCBlockEntities.scaffoldStripperBlockEntity).get().getStored())
            }
            Progress.MOVING -> {
                val contained = world.getBlockEntity(pos, EoCBlockEntities.scaffoldStripperBlockEntity).get().getStored()
                if (contained == null) {
                    // shouldn't be null, but just in case
                    world.setBlockState(pos, state.with(progress, Progress.HALTED), NOTIFY_ALL)
                } else if (contained.isOf(EoCBlocks.scaffoldTrunk) && contained.get(ScaffoldTrunk.junction)) {
                    // switch to searching if its a junction
                    world.setBlockState(pos, state.with(progress, Progress.SEARCHING), NOTIFY_ALL)
                    world.scheduleBlockTick(pos, this, moveRate)
                } else if (contained.isOf(EoCBlocks.scaffoldSeed)) {
                    // consume and delete seeds
                    world.setBlockState(pos, Blocks.AIR.defaultState)
                } else {
                    // move along if there's a scaffold ahead, delete otherwise
                    val nextBlock: BlockState
                    val nextPos: BlockPos
                    if (contained.isOf(EoCBlocks.scaffoldTrunk)) {
                        nextPos = pos.offset(contained.get(ScaffoldTrunk.supportedFrom).opposite)
                        nextBlock = world.getBlockState(nextPos)
                    } else {
                        return
                    }
                    if ((nextBlock.isOf(EoCBlocks.scaffoldTrunk) && nextBlock.get(ScaffoldTrunk.supportedFrom) == contained.get(ScaffoldTrunk.supportedFrom)) || nextBlock.isOf(EoCBlocks.scaffoldSeed)) {
                        val old = world.getBlockState(nextPos)
                        world.setBlockState(pos, contained, NOTIFY_ALL)
                        world.setBlockState(nextPos, state, NOTIFY_ALL)
                        world.getBlockEntity(nextPos, EoCBlockEntities.scaffoldStripperBlockEntity).get().changeStored(old)
                        world.scheduleBlockTick(nextPos, this, moveRate)
                    } else {
                        world.setBlockState(pos, contained, NOTIFY_ALL)
                    }
                }
            }
            Progress.HALTED -> {
                world.setBlockState(pos, state.with(progress, Progress.NEW), NOTIFY_ALL)
                world.scheduleBlockTick(pos, this, moveRate)
            }
        }
        for (newPos in produced) {
            world.scheduleBlockTick(newPos, world.getBlockState(newPos).block, moveRate)
        }
    }

    override fun getStateForNeighborUpdate(state: BlockState, world: WorldView, tickView: ScheduledTickView, pos: BlockPos, direction: Direction?, neighborPos: BlockPos?, neighborState: BlockState?, random: Random?): BlockState? {
        if (state.get(progress) == Progress.NEW || state.get(progress) == Progress.HALTED) {
            tickView.scheduleBlockTick(pos, this, moveRate)
        }
        return state
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(progress)
        builder.add(direction)
        builder.add(directionSet)
    }

    fun searchUnconditionally(world: ServerWorld, pos: BlockPos): Map<Direction, BlockPos> {
        val valid = mutableMapOf<Direction, BlockPos>()
        for (dir in DIRECTIONS) {
            val searching = world.getBlockState(pos.offset(dir))
            if ((searching.isOf(EoCBlocks.scaffoldTrunk) || searching.isOf(EoCBlocks.scaffoldSeed))) {
                valid.put(dir, pos.offset(dir))
            }
        }
        return valid.toMap()
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity?, hit: BlockHitResult?): ActionResult? {
        return super.onUse(state, world, pos, player, hit)
    }
}