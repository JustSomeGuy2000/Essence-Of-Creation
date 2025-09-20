package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.EoCMain
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

        var moveRate = 5
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
                    world.setBlockState(validPos, state.with(progress, Progress.SEARCHING).with(directionSet, false))
                    world.getBlockEntity(validPos, EoCBlockEntities.scaffoldStripperBlockEntity).getOrNull()?.changeStored(old)
                    world.setBlockState(pos, Blocks.AIR.defaultState)
                    produced.add(validPos)
                } else {
                    world.setBlockState(pos, state.with(progress, Progress.HALTED))
                }
            }
            Progress.SEARCHING -> {

            }
            Progress.MOVING -> {

            }
            Progress.HALTED -> {}
        }
        for (newPos in produced) {
            world.scheduleBlockTick(newPos, world.getBlockState(newPos).block, moveRate)
        }
    }

    override fun getStateForNeighborUpdate(state: BlockState, world: WorldView, tickView: ScheduledTickView, pos: BlockPos, direction: Direction?, neighborPos: BlockPos?, neighborState: BlockState?, random: Random?): BlockState? {
        if (state.get(progress) == Progress.NEW) {
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