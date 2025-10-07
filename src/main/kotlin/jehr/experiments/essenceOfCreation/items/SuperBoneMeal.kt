package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Fertilizable
import net.minecraft.item.BoneMealItem
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import kotlin.random.Random

class SuperBoneMeal(settings: Settings): Item(settings) {

    companion object {
        const val ID = "super_bone_meal"
        const val MAX_GROWTH = 32
        const val MAX_SEARCH_DISPLACEMENT = 10
        val scatterFertilise = listOf<Fertilizable>(Blocks.GRASS_BLOCK as Fertilizable)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (!context.world.isClient) {
            val world = context.world
            val blockPos = context.blockPos
            var blockState = world.getBlockState(blockPos)

            var growCounter = 1
            var tempBlock = blockState.block as? Fertilizable
            while (tempBlock !in scatterFertilise && tempBlock != null && tempBlock.isFertilizable(world, blockPos, blockState) && tempBlock.canGrow(world, world.random, blockPos, blockState) && growCounter <= MAX_GROWTH) {
                tempBlock.grow(world as ServerWorld, world.random, blockPos, blockState)
                blockState = world.getBlockState(blockPos)
                tempBlock = blockState.block as? Fertilizable
                growCounter += 1
            }
            if (growCounter != 1) {
                context.stack.decrement(1)
                BoneMealItem.createParticles(world, blockPos, 1)
                return ActionResult.SUCCESS
            }

            if (blockState.isSideSolidFullSquare(world, blockPos, context.side)) {
                repeat(32) {
                    var randPos = BlockPos(blockPos.x + Random.nextInt(-16, 17), blockPos.y, blockPos.z + Random.nextInt(-16, 17))
                    var searchOffset = 1
                    val originalY = randPos.y.toString()
                    while (world.getBlockState(randPos.up()) != Blocks.AIR.defaultState || world.getBlockState(randPos) == Blocks.AIR.defaultState) {
                        randPos = randPos.withY(originalY.toInt() + searchOffset)
                        searchOffset *= -1
                        searchOffset += if (searchOffset >= 0) 1 else 0
                        if (searchOffset >= MAX_SEARCH_DISPLACEMENT) {
                            randPos = randPos.withY(originalY.toInt())
                            break
                        }
                    }
                    val target = world.getBlockState(randPos).block
                    if (target is Fertilizable) {
                        target.grow(world as ServerWorld, world.random, blockPos, blockState)
                    } else {
                        BoneMealItem.useOnGround(context.stack, world, randPos, context.side)
                    }
                    BoneMealItem.createParticles(world, randPos, 1)
                    context.stack.increment(1)
                    EoCMain.logger.warn("$randPos")
                }
                context.stack.decrement(1)
                return ActionResult.SUCCESS
            } else return ActionResult.PASS
        }

        return ActionResult.SUCCESS
    }
}