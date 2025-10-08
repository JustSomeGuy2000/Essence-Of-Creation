package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.item.BoneMealItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.particle.ParticleTypes
import net.minecraft.particle.ParticleUtil
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import kotlin.random.Random

class SuperBoneMeal(settings: Settings): Item(settings) {

    companion object {
        const val ID = "super_bone_meal"
        const val MAX_GROWTH = 16
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (!context.world.isClient) {
            val world = context.world
            val blockPos = context.blockPos
            var blockState: BlockState
            var globalGrow = 0

            repeat(5) { offsetX ->
                repeat(5) { offsetZ ->
                    val tempBlockPos = BlockPos(blockPos.x + offsetX - 1, blockPos.y, blockPos.z + offsetZ - 1)
                    blockState = world.getBlockState(tempBlockPos)
                    var growCounter = 1
                    var tempBlock = blockState.block as? Fertilizable
                    while (tempBlock != null && tempBlock.isFertilizable(world, tempBlockPos, blockState) && tempBlock.canGrow(world, world.random, tempBlockPos, blockState) && growCounter <= MAX_GROWTH) {
                        tempBlock.grow(world as ServerWorld, world.random, tempBlockPos, blockState)
                        blockState = world.getBlockState(tempBlockPos)
                        tempBlock = blockState.block as? Fertilizable
                        growCounter += 1
                    }
                    if (growCounter != 1) {
                        globalGrow += 1
                        world.addParticleClient(ParticleTypes.HAPPY_VILLAGER, tempBlockPos.x.toDouble(), tempBlockPos.y.toDouble(), tempBlockPos.z.toDouble(), 0.0, 0.0 ,0.0)
                    }
                }
            }
            if (globalGrow > 0) {
                context.stack.decrement(1)
            }
        }

        return ActionResult.SUCCESS
    }
}