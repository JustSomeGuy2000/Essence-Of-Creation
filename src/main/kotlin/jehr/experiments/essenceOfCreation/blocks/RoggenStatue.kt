package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.RoggenStatueBlockEntity
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class RoggenStatue(settings: Settings): BlockWithEntity(settings) {

    companion object {
        const val ID = "roggen_statue"
        var facing: EnumProperty<Direction> = Properties.FACING
        const val APPLY_TIMER = 20
        const val APPLY_RADIUS = 100.0

        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
            val entity = blockEntity as? RoggenStatueBlockEntity
            if (entity == null || world.isClient) {
                return
            }
            entity.ticksSinceLast += 1
            if (entity.ticksSinceLast < APPLY_TIMER) {
                return
            }
            entity.ticksSinceLast = 0
            val origin = Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            world.players.forEach { player ->
                val effect = StatusEffectInstance(EoCStatusEffects.blessingOfRye, BlessingOfRye.AMP_TIME, 1)
                if (!player.hasStatusEffect(effect.effectType) && origin.isInRange(player.pos, APPLY_RADIUS) && (player.gameMode?.isSurvivalLike ?: false)) {
                    player.addStatusEffect(StatusEffectInstance(effect))
                }
            }
        }
    }

    override fun getCodec(): MapCodec<RoggenStatue> = createCodec(::RoggenStatue)

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = RoggenStatueBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T?>): BlockEntityTicker<T?>? = validateTicker(type, EoCBlockEntities.roggenStatueBlockEntity, RoggenStatue::tick)

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(facing)
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?): BlockState? {
        //TODO: Remove blessing of rye
    }
}