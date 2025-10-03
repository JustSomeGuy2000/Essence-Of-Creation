package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.EssentialExtractorBlockEntity
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.particles.EoCParticles
import jehr.experiments.essenceOfCreation.utils.CombinedBoolDir
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import kotlin.math.floor


class EssentialExtractor(settings: Settings): BlockWithEntity(settings) {

    init {
        this.defaultState = this.stateManager.defaultState.with(condition, CombinedBoolDir.FALSE_NORTH)
    }

    companion object {
        var condition: EnumProperty<CombinedBoolDir> = EnumProperty.of("state", CombinedBoolDir::class.java)

        const val ID = "essential_extractor"
        const val EXTRACT_TIME = 100 // t(icks)/o(utput)_b(lock)
        const val FUEL_PER_TICK = 1 // f(uel)/t
        /**Update the Extractor. Perform one of the following actions, evaluated in the following order:
         * - Load fuel: If there is no loaded fuel, source is available, and fuel is available, consume one item of fuel and load fuel equivalent to its value in Companion.fuels.
         * - Finish extraction: If progress == EXTRACT_TIME, consume one item of source and increment accumulator by its value in Companion.sources. If accumulator >= 1, take its integer component and add that amount of Essences of Creation to output. Nothing happens if output is occupied by some other item.
         * - Progress extraction: If there is loaded fuel, source is available, and progress < EXTRACT_TIME, increment progress by 1, and decrement fuel by FUEL_PER_TICK.
         * - Reduce fuel: If there is loaded fuel but nothing else, reduce it. If fuel is now empty, immediately reload.*/
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
            val be = blockEntity
            if (be is EssentialExtractorBlockEntity) {
                if (be.source.isEmpty || be.source.item !in sources) {
                    be.progress = 0
                }
                if (be.currentFuel == 0 && be.fuel.item in fuels && be.source.item in sources) {
                    val consumedFuel = be.fuel.copyWithCount(1).item
                    be.fuel.decrement(1)
                    be.maxFuel = fuels[consumedFuel]!!
                    be.currentFuel = fuels[consumedFuel]!!
                    be.progress += 1
                    world.setBlockState(pos, state.with(condition, CombinedBoolDir.modBool(state.get(condition)) {true}))
                } else if (be.progress == EXTRACT_TIME && (be.output.isOf(EoCItems.essenceOfCreation) || be.output.isEmpty) && be.output.count < 64) {
                    val consumedSource = be.source.copyWithCount(1).item
                    be.source.decrement(1)
                    be.accumulator += sources[consumedSource]!!
                    be.progress = 0
                    if (be.accumulator >= 1) {
                        val produced = floor(be.accumulator).toInt()
                        be.accumulator -= produced
                        if (be.output.isOf(EoCItems.essenceOfCreation)) {
                            be.output.increment(produced)
                        } else if (be.output.isEmpty) {
                            be.output = ItemStack(EoCItems.essenceOfCreation, 1)
                        }
                    }
                } else if (be.currentFuel != 0 && be.source.item in sources && be.progress < EXTRACT_TIME) {
                    be.progress += 1
                    be.currentFuel -= FUEL_PER_TICK
                } else if (be.currentFuel != 0) {
                    be.currentFuel -= FUEL_PER_TICK
                    if (be.currentFuel == 0 && be.fuel.item in fuels && be.source.item in sources) {
                        val consumedFuel = be.fuel.copyWithCount(1).item
                        be.fuel.decrement(1)
                        be.maxFuel = fuels[consumedFuel]!!
                        be.currentFuel = fuels[consumedFuel]!!
                        be.progress += 1
                        world.setBlockState(pos, state.with(condition, CombinedBoolDir.modBool(state.get(condition)) {true}))
                    } else if (be.currentFuel == 0) {
                        be.progress = 0
                        world.setBlockState(pos, state.with(condition, CombinedBoolDir.modBool(state.get(condition)) {false}))
                    }
                }
            }
        }

        /**How much Essence should result from extraction of the given item. Should be items that require a lot of effort to obtain.*/
        val sources = mapOf<Item, Double>(Items.DIAMOND_BLOCK to 1.0, Items.NETHERITE_BLOCK to 20.0, Items.BEACON to 10.0, Items.DRAGON_EGG to 64.0, Items.DRAGON_HEAD to 15.0, Items.ENCHANTED_GOLDEN_APPLE to 25.0, Items.TOTEM_OF_UNDYING to 18.0, Items.MACE to 9.5, Items.TRIDENT to 2.0, Items.ELYTRA to 18.0, Items.BOOKSHELF to 0.015625, Items.WRITTEN_BOOK to 0.03125, Items.CONDUIT to 2.0)
        /**How much fuel value the given item provides, expressed in multiples of FUEL_PER_BLOCK (EXTRACT_TIME * FUEL_PER_TICK = f/o_b = constant amount of fuel required to consume one block). Should be items that can theoretically be broken down for magical energy,.*/
        val fuels: Map<Item, Int> = mapOf(Items.COAL_BLOCK to 0.1, Items.IRON_BLOCK to 1.0, Items.GOLD_BLOCK to 3.0, Items.DIAMOND_BLOCK to 10.0, Items.NETHERITE_BLOCK to 128.0, Items.QUARTZ_BLOCK to 0.1, Items.OBSIDIAN to 1.0, Items.LAPIS_BLOCK to 4.0, Items.NETHER_STAR to 32.0, Items.BOOK to 0.03125, Items.ENCHANTING_TABLE to 8.0, Items.TOTEM_OF_UNDYING to 25.0, Items.SOUL_SAND to 0.015625, Items.SOUL_SOIL to 0.015625, Items.HEART_OF_THE_SEA to 20.0, Items.ECHO_SHARD to 4.0, Items.WITHER_SKELETON_SKULL to 4.0, Items.HEAVY_CORE to 8.0, Items.BLAZE_ROD to 0.020, Items.ENDER_PEARL to 0.025).mapValues { entry -> (entry.value * EXTRACT_TIME * FUEL_PER_TICK).toInt() }
    }

    override fun getCodec(): MapCodec<EssentialExtractor> = createCodec(::EssentialExtractor)

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = EssentialExtractorBlockEntity(pos, state)

    override fun getRenderType(state: BlockState?) = BlockRenderType.MODEL

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T?>?): BlockEntityTicker<T?>? = validateTicker(type, EoCBlockEntities.essentialExtractorBlockEntity, EssentialExtractor::tick)

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hit: BlockHitResult): ActionResult {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is EssentialExtractorBlockEntity) {
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
        if (blockEntity is EssentialExtractorBlockEntity) {
            ItemScatterer.spawn(world, pos, blockEntity)
            world.updateComparators(pos, this)
        }
        super.onStateReplaced(state, world, pos, moved)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
         if (state.get(condition).bool) {
             val x = pos.x + 0.5
             val y = pos.y.toDouble()
             val z = pos.z + 0.5
             if (random.nextDouble() < 0.1) {
                 world.playSoundClient(x, y, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false)
             }

             val dir = state.get(condition).dir
             val axis = dir.axis
             val fallback = random.nextDouble() * 0.6 - 0.3
             val offsetX = if (axis == Direction.Axis.X) dir.offsetX * 0.52 else fallback
             val offsetY = random.nextDouble() * 6.0 / 16.0
             val offsetZ = if (axis == Direction.Axis.Z) dir.offsetZ * 0.52 else fallback
             world.addParticleClient(ParticleTypes.SMOKE, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0)
             world.addParticleClient(EoCParticles.purpleFlame, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0)
             world.addParticleClient(ParticleTypes.SMOKE, x + (random.nextDouble() - 0.5) * 10 / 16, y + 1.1, z + (random.nextDouble() - 0.5) * 10 / 16, 0.0, 0.0, 0.0)
         }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState = this.defaultState.with(condition, CombinedBoolDir.of(false, ctx.horizontalPlayerFacing.opposite))

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        val blockEntity = world.getBlockEntity(pos)
        return if (blockEntity !is EssentialExtractorBlockEntity) 0 else (blockEntity.currentFuel/blockEntity.maxFuel)*15
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        super.appendProperties(builder)
        builder.add(condition)
    }
}