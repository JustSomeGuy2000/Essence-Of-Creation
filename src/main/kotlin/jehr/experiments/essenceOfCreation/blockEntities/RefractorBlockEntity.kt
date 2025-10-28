package jehr.experiments.essenceOfCreation.blockEntities

import jehr.experiments.essenceOfCreation.blocks.Refractor
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Stainable
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BeamEmitter
import net.minecraft.block.entity.BeamEmitter.BeamSegment
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.LockableContainerBlockEntity
import net.minecraft.component.ComponentsAccess
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.ContainerLock
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.screen.BeaconScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.ColorHelper
import net.minecraft.world.Heightmap
import net.minecraft.world.World

class RefractorBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(null, pos, state), NamedScreenHandlerFactory, BeamEmitter, Stainable, Nameable {

    companion object {
        const val ID = "${Refractor.ID}_block_entity"
        val defaultName: Text = Text.translatable("container.refractor")

        /**Time to analyse how the Beacon works.*/
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: RefractorBlockEntity) {
            val x = pos.x
            val y = pos.y
            val z = pos.z
            var blockPos: BlockPos
            if (blockEntity.minY < y) {
                blockPos = pos
                blockEntity._beamSegments = mutableListOf()
                blockEntity.minY = pos.y - 1
            } else {
                blockPos = BlockPos(x, blockEntity.minY + 1, z)
            }

            // Get the topmost (presumably) beam segment, if any
            var beamSegment = if (blockEntity._beamSegments.isEmpty())
                null
            else
                blockEntity._beamSegments.get(blockEntity._beamSegments.size - 1)
            val topmostBlock = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)

            var upPointer = 0
            // check for unobstucted view of sky
            while (upPointer < 10 && blockPos.y <= topmostBlock) {
                val blockState = world.getBlockState(blockPos)
                // change beam colour
                if (blockState.block is Stainable) {
                    val colour = (blockState.block as Stainable).color.entityColor // DyeColors represent different colours depending on use case
                    if (blockEntity._beamSegments.size <= 1) {
                        beamSegment = BeamSegment(colour)
                        blockEntity._beamSegments.add(beamSegment)
                    } else if (beamSegment != null) {
                        if (colour == beamSegment.color) {
                            beamSegment.increaseHeight()
                        } else {
                            beamSegment = BeamSegment(ColorHelper.average(beamSegment.color, colour))
                            blockEntity._beamSegments.add(beamSegment)
                        }
                    }
                } else {
                    // opaque block encountered, cannot activate beam
                    if (beamSegment == null || blockState.opacity >= 15 && !blockState.isOf(Blocks.BEDROCK)) {
                        blockEntity._beamSegments.clear()
                        blockEntity.minY = topmostBlock
                        break
                    }

                    beamSegment.increaseHeight()
                }

                // set up for checking the next block up
                blockPos = blockPos.up()
                blockEntity.minY++
                upPointer++
            }

            // ???

            val level = blockEntity.level
            if (world.time % 80L == 0L) {
                if (!blockEntity._beamSegments.isEmpty()) {
                    blockEntity.level = dummyUpdateLevel(world, x, y, z)
                }

                if (blockEntity.level > 0 && !blockEntity._beamSegments.isEmpty()) {
                    dummyApplyPlayerEffects(
                        world,
                        pos,
                        blockEntity.level,
                        blockEntity.primary,
                        blockEntity.secondary
                    )
                    BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT)
                }
            }

            if (blockEntity.minY >= topmostBlock) {
                blockEntity.minY = world.bottomY - 1
                /**If the level is greater than 0, it is active in some way*/
                val oldActive = level > 0
                // No idea what the difference between these two is
                blockEntity.laterBeamSegments = blockEntity._beamSegments
                if (!world.isClient) {
                    /**Looks the same as `oldActive`. Probably the level can change between this and the first assignment of `level`.*/
                    val newActive = blockEntity.level > 0
                    // Beacon used to be inactive but now is not, i.e. Beacon activated
                    if (!oldActive && newActive) {
                        BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE)

                        for (serverPlayerEntity in world.getNonSpectatingEntities(
                            ServerPlayerEntity::class.java,
                            Box(
                                x.toDouble(),
                                y.toDouble(),
                                z.toDouble(),
                                x.toDouble(),
                                (y - 4).toDouble(),
                                z.toDouble()
                            ).expand(10.0, 5.0, 10.0)
                        )) {
                            Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, blockEntity.level)
                        }
                    // Theopposite of the first branch, i.e. Beacon deactivated
                    } else if (oldActive && !newActive) {
                        BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE)
                    }
                }
            }
        }

        fun dummyUpdateLevel(world: World, x: Int, y: Int, z: Int): Int = TODO()
        fun dummyApplyPlayerEffects(world: World, pos: BlockPos, level: Int, primary: RegistryEntry<StatusEffect>?, secondary: RegistryEntry<StatusEffect>?): Unit = TODO()

    }

    /**Why are there two lists of beam segemnts?*/
    var _beamSegments = mutableListOf<BeamSegment>()
    /**Why are there two lists of beam segemnts?*/
    var laterBeamSegments = mutableListOf<BeamSegment>()
    /**What is this even? Initally set to one block below the minimum Y value.*/
    var minY = -255
    /**Beacon power lvel, presumably.*/
    var level = 0
    /**Whether this is locked*/
    var lock: ContainerLock = ContainerLock.EMPTY
    var _customName = defaultName

    /**First effect to apply*/
    var primary: RegistryEntry<StatusEffect>? = null
    /**Second effect to apply*/
    var secondary: RegistryEntry<StatusEffect>? = null

    /**Allows Screens to interact with this.*/
    val delegate = object: PropertyDelegate{
        override fun get(index: Int): Int {
            TODO("Not yet implemented")
        }

        override fun set(index: Int, value: Int) {
            TODO("Not yet implemented")
        }

        override fun size(): Int {
            TODO("Not yet implemented")
        }
    }

    override fun getDisplayName(): Text = Text.translatable(this.cachedState.block.translationKey)
    override fun getName(): Text = this._customName
    override fun getColor() = DyeColor.RED
    override fun getBeamSegments(): List<BeamSegment> = this._beamSegments

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory?, player: PlayerEntity?) =
        if (LockableContainerBlockEntity.checkUnlocked(player, this.lock, this.displayName)) BeaconScreenHandler(syncId, playerInventory, this.delegate, ScreenHandlerContext.create(this.world, this.pos)) else null

    override fun readData(view: ReadView?) {
        super.readData(view)
    }

    override fun writeData(view: WriteView?) {
        super.writeData(view)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup?): NbtCompound? {
        return super.toInitialChunkDataNbt(registries)
    }

    override fun readComponents(components: ComponentsAccess?) {
        super.readComponents(components)
    }

    override fun writeComponentlessData(view: WriteView?) {
        super.writeComponentlessData(view)
    }

    override fun setWorld(world: World) {
        super.setWorld(world)
        this.minY = world.bottomY - 1
    }
}