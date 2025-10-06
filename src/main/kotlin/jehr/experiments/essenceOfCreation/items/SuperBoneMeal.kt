package jehr.experiments.essenceOfCreation.items

import net.minecraft.block.Fertilizable
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult

class SuperBoneMeal(settings: Settings): Item(settings) {

    companion object {
        const val ID = "super_bone_meal"
        const val MAX_GROWTH = 32
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (context.world.isClient) {
            val world = context.world
            val blockPos = context.blockPos
            val blockState = world.getBlockState(blockPos)
            val block = blockState.block
            val targetPos = blockPos.offset((context.side))

            if (block is Fertilizable && block.isFertilizable(world, blockPos, blockState)) {
                var growCounter = 1
                while (block.canGrow(world, world.random, blockPos, blockState) || growCounter > MAX_GROWTH) {
                    block.grow(world as ServerWorld, world.random, blockPos, blockState)
                    growCounter += 1
                }
                context.stack.decrement(1)
                return ActionResult.SUCCESS
            } else if (blockState.isSideSolidFullSquare(world, blockPos, context.side)) {

                context.stack.decrement(1)
            } else return ActionResult.PASS
        }

        return ActionResult.SUCCESS
    }
}