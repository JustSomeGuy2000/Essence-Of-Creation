package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.RefractorBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Stainable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LazyEntityReference
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.DyeColor
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Refractor(settings: Settings): BlockWithEntity(settings), Stainable {

    companion object {
        const val ID = "refractor"
        val codec: MapCodec<Refractor> = createCodec(::Refractor)
    }

    override fun getCodec(): MapCodec<Refractor> = Companion.codec
    override fun getColor() = DyeColor.RED

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = RefractorBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(world: World, state: BlockState, type: BlockEntityType<T>) = validateTicker(type, EoCBlockEntities.refractorBlockEntity,
        RefractorBlockEntity::tick)

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hit: BlockHitResult): ActionResult? {
        val be = world.getBlockEntity(pos)
        if (!world.isClient && be is RefractorBlockEntity) {
            player.openHandledScreen(be)
        }
        return ActionResult.SUCCESS
    }

    override fun onPlaced(world: World, pos: BlockPos?, state: BlockState?, placer: LivingEntity?, itemStack: ItemStack?) {
        super.onPlaced(world, pos, state, placer, itemStack)
        val be = world.getBlockEntity(pos)
        if (placer is PlayerEntity && be is RefractorBlockEntity) {
            be.owner = LazyEntityReference(placer)
        }
    }
}