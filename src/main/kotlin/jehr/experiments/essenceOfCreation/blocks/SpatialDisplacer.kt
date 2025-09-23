package jehr.experiments.essenceOfCreation.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SpatialDisplacer(settings: Settings): Block(settings) {

    companion object {
        const val ID = "spatial_displacer"

    }

    override fun onSteppedOn(world: World, pos: BlockPos, state: BlockState, entity: Entity) {
        var attachedCount = 1
        var currentPos = pos
        while (world.getBlockState(currentPos.down()).isOf(EoCBlocks.spatialDisplacer)){
            currentPos = currentPos.down()
            attachedCount += 1
        }
        if (!world.isClient && entity is LivingEntity) {
            TeleportRandomlyConsumeEffect(attachedCount * 8.0F).onConsume(world, null, entity)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
    }
}