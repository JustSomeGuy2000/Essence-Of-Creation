package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.EssentialInfuserBlockEntity
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.particles.EoCParticles
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class EssentialInfuser(settings: Settings): BlockWithEntity(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(active, false)
    }

    companion object {
        const val ID = "essential_infuser"
        const val INFUSE_TIME = 100
        const val INFUSE_TIME_F = 100.0
        const val PROGRESS_PER_TICK = 1

        var active: BooleanProperty = BooleanProperty.of("active")

        /**Update the infuser. Increment the progress meter and produce an output if it is full.*/
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
            val be = blockEntity
            if (be is EssentialInfuserBlockEntity && !world.isClient) {
                if (be.essence.isOf(EoCItems.essenceOfCreation) && be.source.item in outputs) {
                    if (be.progress < INFUSE_TIME) {
                        be.progress += PROGRESS_PER_TICK
                        if (!state.get(active)) {
                            world.setBlockState(pos, state.with(active, true))
                        }
                    } else {
                        val result = outputs[be.source.item]
                        if (be.output.isEmpty) {
                            be.output = ItemStack(result, 1)
                        } else if (be.output.isOf(result)) {
                            be.output.increment(1)
                        } else return
                        be.source.decrement(1)
                        be.essence.decrement(1)
                        be.progress = 0
                        if (!(be.essence.isOf(EoCItems.essenceOfCreation) && be.source.item in outputs)) {
                            world.setBlockState(pos, state.with(active, false))
                        }
                    }
                } else if (state.get(active)) {
                    world.setBlockState(pos, state.with(active, false))
                }
            }
        }

        /**Valid input items and what they turn into.*/
        val outputs = mapOf<Item, Item>(
            Blocks.SCAFFOLDING.asItem() to EoCBlocks.scaffoldSeed.asItem(),
            Blocks.PURPUR_PILLAR.asItem() to EoCBlocks.spatialDisplacer.asItem(),
            Items.ENCHANTED_GOLDEN_APPLE to EoCItems.godApple,
            Items.BONE_MEAL to EoCItems.superBoneMeal,
            Items.STICK to EoCItems.cane,
            Items.IRON_SWORD to EoCItems.ironGunSword,
            Items.GOLDEN_SWORD to EoCItems.goldGunSword,
            Items.DIAMOND_SWORD to EoCItems.diamondGunSword,
            Items.NETHERITE_SWORD to EoCItems.netheriteGunSword
        )

        /**Valid input items and what they trun into, accounting for specifics components. First values are if the item can be transformed, second value is what it transforms into*/
        val componentedOutputs = listOf<Pair<(ItemStack) -> Boolean, (ItemStack) -> ItemStack>>()

        fun enchantmentTransformable(input: ItemStack) {
            TODO()
        }
    }

    override fun getCodec(): MapCodec<EssentialInfuser> = createCodec(::EssentialInfuser)

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = EssentialInfuserBlockEntity(pos, state)

    override fun getRenderType(state: BlockState?): BlockRenderType = BlockRenderType.MODEL

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T?>?): BlockEntityTicker<T?>? = validateTicker(type, EoCBlockEntities.essentialInfuserBlockEntity, ::tick)

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hit: BlockHitResult): ActionResult? {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is EssentialInfuserBlockEntity) {
                val shf = state.createScreenHandlerFactory(world, pos)
                if (shf != null) {
                    player.openHandledScreen(shf)
                    return ActionResult.SUCCESS_SERVER
                }
            }
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(state: BlockState, world: ServerWorld, pos: BlockPos, moved: Boolean) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is EssentialInfuserBlockEntity) {
            ItemScatterer.spawn(world, pos, blockEntity)
            world.updateComparators(pos, this)
        }
        super.onStateReplaced(state, world, pos, moved)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if (state.get(active)) {
            val x = pos.x + 0.5 + (random.nextDouble() - 0.5) * 12 / 16
            val y = pos.y + 1.1
            val z = pos.z + 0.5 + (random.nextDouble() - 0.5) * 12 / 16

            if (random.nextDouble() < 0.1) {
                world.playSoundClient(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false)
            }

            world.addParticleClient(EoCParticles.purpleFlame, x, y, z, 0.0, 0.0, 0.0)
        }
    }

    override fun hasComparatorOutput(state: BlockState): Boolean = state.get(active)

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        val be = world.getBlockEntity(pos)
        return if (be is EssentialInfuserBlockEntity) be.progress/INFUSE_TIME else 0
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(active)
    }
}