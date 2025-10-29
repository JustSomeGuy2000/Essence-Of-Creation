package jehr.experiments.essenceOfCreation.blockEntities

import jehr.experiments.essenceOfCreation.blocks.Refractor
import jehr.experiments.essenceOfCreation.screenHandlers.RefractorScreenHandler
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Stainable
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.block.entity.BeamEmitter
import net.minecraft.block.entity.BeamEmitter.BeamSegment
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.LockableContainerBlockEntity
import net.minecraft.component.ComponentMap
import net.minecraft.component.ComponentsAccess
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.ContainerLock
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.BlockTags
import net.minecraft.screen.BeaconScreenHandler
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.DyeColor
import net.minecraft.util.Nameable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.ColorHelper
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

class RefractorBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(EoCBlockEntities.refractorBlockEntity, pos, state), NamedScreenHandlerFactory, BeamEmitter, Stainable, Nameable {

    companion object {
        const val ID = "${Refractor.ID}_block_entity"
        val defaultName: Text = Text.translatable("container.refractor")

        const val INDEX_LEVEL = 0
        const val INDEX_PRIMARY = 1
        const val INDEX_SECONDARY = 2

        const val KEY_PRIMARY = "primary_effect"
        const val KEY_SECONDARY = "secondary_effect"
        const val KEY_CUSTOM_NAME = "CustomName"
        const val KEY_LEVEL = "level"

        const val EFFECT_AMPLIFIER = 1
        val ownerEffectsByLevel = listOf(
            listOf(StatusEffects.SPEED, StatusEffects.HASTE, StatusEffects.NIGHT_VISION),
            listOf(StatusEffects.STRENGTH, StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST),
            listOf(StatusEffects.REGENERATION, StatusEffects.LUCK),
            listOf(StatusEffects.SATURATION, StatusEffects.CONDUIT_POWER))
        val enemyEffectsByLevel = listOf(
            listOf(StatusEffects.WEAVING, StatusEffects.OOZING, StatusEffects.INFESTED),
            listOf(StatusEffects.SLOWNESS, StatusEffects.MINING_FATIGUE),
            listOf(StatusEffects.DARKNESS, StatusEffects.HUNGER),
            listOf(StatusEffects.BLINDNESS, StatusEffects.LEVITATION)
        )

        /**Time to analyse how the Beacon works.*/
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: RefractorBlockEntity) {
            val x = pos.x
            val y = pos.y
            val z = pos.z
            var blockPos: BlockPos
            // if the block entity is within bounds, reset the beam segments and set minY to just below this block. This ensures the check will succeed next time.
            if (blockEntity.minY < y) {
                blockPos = pos
                blockEntity.firstBeamSegments = mutableListOf()
                blockEntity.minY = pos.y - 1
            } else {
                blockPos = BlockPos(x, blockEntity.minY + 1, z)
            }

            /**The topmost beam segment, if any.*/
            var beamSegment = if (blockEntity.firstBeamSegments.isEmpty())
                null
            else
                blockEntity.firstBeamSegments[blockEntity.firstBeamSegments.size - 1]
            val topmostBlock = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)

            var upPointer = 0
            // check for glass and unobstucted view of sky
            while (upPointer < 10 && blockPos.y <= topmostBlock) {
                val blockState = world.getBlockState(blockPos)
                // change beam colour if needed
                if (blockState.block is Stainable) {
                    val colour = (blockState.block as Stainable).color.entityColor // DyeColors represent different colours depending on use case
                    if (blockEntity.firstBeamSegments.size <= 1) {
                        beamSegment = BeamSegment(colour)
                        blockEntity.firstBeamSegments.add(beamSegment)
                    } else if (beamSegment != null) {
                        if (colour == beamSegment.color) {
                            beamSegment.increaseHeight()
                        } else {
                            beamSegment = BeamSegment(ColorHelper.average(beamSegment.color, colour))
                            blockEntity.firstBeamSegments.add(beamSegment)
                        }
                    }
                } else {
                    // opaque block encountered, cannot activate beam
                    if (beamSegment == null || blockState.opacity >= 15 && !blockState.isOf(Blocks.BEDROCK)) {
                        blockEntity.firstBeamSegments.clear()
                        blockEntity.minY = topmostBlock
                        break
                    }

                    beamSegment.increaseHeight()
                }

                // setup for checking the next block up
                blockPos = blockPos.up()
                blockEntity.minY++
                upPointer++
            }

            val level = blockEntity.level
            // every 2 seconnds
            if (world.time % 80L == 0L) {
                // check for activation conditions if inactive
                if (!blockEntity.firstBeamSegments.isEmpty()) {
                    blockEntity.level = updateLevel(world, x, y, z)
                }

                // apply player effects if the Beacon is active
                if (blockEntity.level > 0 && !blockEntity.firstBeamSegments.isEmpty()) {
                    applyPlayerEffects(
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
                blockEntity.secondBeamSegments = blockEntity.firstBeamSegments
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
                    // The opposite of the first branch, i.e. Beacon deactivated
                    } else if (oldActive && !newActive) {
                        BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE)
                    }
                }
            }
        }

        /**Detect if the beacon should be active, and if so, its level*/
        fun updateLevel(world: World, x: Int, y: Int, z: Int): Int {
            var completeTiers = 0

            // Detect for 4 levels down, which is the maximum tiers for a Beacon
            for (tierDetectDownMarker in 1..4) {
                val currentY = y - tierDetectDownMarker
                if (currentY < world.bottomY) break

                var currentX = x - tierDetectDownMarker
                var tierComplete = true
                // currentX will have values in 2a+1, where a is the tier.
                // also if a tier isn't complete all tiers below it are negated.
                while (currentX <= x + tierDetectDownMarker && tierComplete) {

                    var currentZ = z - tierDetectDownMarker
                    // same for z
                    // so, an ever increasing square downwards is detected
                    while (currentZ <= z + tierDetectDownMarker) {
                        if (!world.getBlockState(BlockPos(currentX, currentY, currentZ)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
                            tierComplete = false
                            break
                        }
                        currentZ += 1
                    }

                    currentX += 1
                }

                if (!tierComplete) break

                completeTiers = tierDetectDownMarker
            }

            return completeTiers
        }

        /**Name and code is self-explanatory.*/
        fun applyPlayerEffects(world: World, pos: BlockPos, beaconLevel: Int, primaryEffect: RegistryEntry<StatusEffect>?, secondaryEffect: RegistryEntry<StatusEffect>?) {
            if (!world.isClient && primaryEffect != null) {
                val effectRange = (beaconLevel * 10 + 10).toDouble()
                var doubleEffect = 0
                if (beaconLevel >= 4 && primaryEffect == secondaryEffect) {
                    doubleEffect = 1
                }

                val effectDuration = (9 + beaconLevel * 2) * 20
                val box = Box(pos).expand(effectRange).stretch(0.0, world.height.toDouble(), 0.0)
                val list = world.getNonSpectatingEntities(PlayerEntity::class.java, box)

                for (playerEntity in list) {
                    playerEntity.addStatusEffect(StatusEffectInstance(primaryEffect, effectDuration, doubleEffect + EFFECT_AMPLIFIER, true, true))
                }

                if (beaconLevel >= 4 && (primaryEffect != secondaryEffect) && secondaryEffect != null) {
                    for (playerEntity in list) {
                        playerEntity.addStatusEffect(StatusEffectInstance(secondaryEffect, effectDuration, 0, true, true))
                    }
                }
            }
        }

        fun playSound(world: World?, pos: BlockPos?, sound: SoundEvent?) {
            world?.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f)
        }

        fun readStatusEffect(view: ReadView, key: String) = view.read(key, Registries.STATUS_EFFECT.entryCodec).getOrNull()

        fun writeStatusEffect(view: WriteView, key: String, effect: RegistryEntry<StatusEffect>?) = if (effect != null) effect.key.ifPresent{view.putString(key, it.value.toString())} else {}

    }

    /**Why are there two lists of beam segemnts? If this is not empty, it means the beacon can successfully activate.*/
    var firstBeamSegments = mutableListOf<BeamSegment>()
    /**Why are there two lists of beam segemnts?*/
    var secondBeamSegments = mutableListOf<BeamSegment>()
    /**What is this even? Initally set to one block below the world minimum Y value.*/
    var minY = -255
    /**Beacon power lvel, presumably.*/
    var level = 0
    /**Whether this is locked*/
    var lock: ContainerLock = ContainerLock.EMPTY
    var backingCustomName: Text? = null

    /**First effect to apply*/
    var primary: RegistryEntry<StatusEffect>? = null
    /**Second effect to apply*/
    var secondary: RegistryEntry<StatusEffect>? = null

    /**Allows Screens to interact with this.*/
    val delegate = object: PropertyDelegate{
        override fun get(index: Int) = when(index) {
            INDEX_LEVEL -> this@RefractorBlockEntity.level
            INDEX_PRIMARY -> BeaconScreenHandler.getRawIdForStatusEffect(this@RefractorBlockEntity.primary)
            INDEX_SECONDARY -> BeaconScreenHandler.getRawIdForStatusEffect(this@RefractorBlockEntity.secondary)
            else -> {throw IllegalArgumentException()}
        }

        override fun set(index: Int, value: Int) = when(index) {
            INDEX_LEVEL -> this@RefractorBlockEntity.level = value
            INDEX_PRIMARY -> {
                if ((this@RefractorBlockEntity.world?.isClient ?: false) && this@RefractorBlockEntity.firstBeamSegments.isNotEmpty()) {
                    playSound(this@RefractorBlockEntity.world, this@RefractorBlockEntity.pos, SoundEvents.BLOCK_BEACON_ACTIVATE)
                }
                this@RefractorBlockEntity.primary = BeaconScreenHandler.getStatusEffectForRawId(value)
            }
            INDEX_SECONDARY -> this@RefractorBlockEntity.secondary = BeaconScreenHandler.getStatusEffectForRawId(value)
            else -> {throw IllegalArgumentException()}
        }

        override fun size() = 3
    }

    override fun getDisplayName(): Text = Text.translatable(this.cachedState.block.translationKey)
    override fun getName() = this.backingCustomName ?: defaultName
    override fun getColor() = DyeColor.RED
    override fun getBeamSegments(): List<BeamSegment> = this.firstBeamSegments

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?) =
        if (LockableContainerBlockEntity.checkUnlocked(player, this.lock, this.displayName)) RefractorScreenHandler(syncId, playerInventory, this.delegate, ScreenHandlerContext.create(this.world, this.pos)) else null

    override fun markRemoved() {
        playSound(this.world, this.pos, SoundEvents.BLOCK_BEACON_DEACTIVATE)
        super.markRemoved()
    }

    override fun readData(view: ReadView) {
        super.readData(view)
        this.primary = readStatusEffect(view, KEY_PRIMARY)
        this.secondary = readStatusEffect(view, KEY_SECONDARY)
        this.backingCustomName = tryParseCustomName(view, KEY_CUSTOM_NAME)
        this.lock = ContainerLock.read(view)
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        writeStatusEffect(view, KEY_PRIMARY, this.primary)
        writeStatusEffect(view, KEY_SECONDARY, this.secondary)
        view.putInt(KEY_LEVEL, this.level)
        view.putNullable(KEY_CUSTOM_NAME, TextCodecs.CODEC, this.customName)
        this.lock.write(view)
    }

    override fun toInitialChunkDataNbt(registries: RegistryWrapper.WrapperLookup?): NbtCompound = this.createComponentlessNbt(registries)

    override fun toUpdatePacket(): BlockEntityUpdateS2CPacket = BlockEntityUpdateS2CPacket.create(this)

    override fun readComponents(components: ComponentsAccess) {
        super.readComponents(components)
        this.backingCustomName = components.get(DataComponentTypes.CUSTOM_NAME)
        this.lock = components.getOrDefault(DataComponentTypes.LOCK, ContainerLock.EMPTY)
    }

    override fun addComponents(builder: ComponentMap.Builder?) {
        super.addComponents(builder)
        builder!!.add(DataComponentTypes.CUSTOM_NAME, this.customName)
        if (this.lock != ContainerLock.EMPTY) {
            builder.add(DataComponentTypes.LOCK, this.lock)
        }
    }

    override fun setWorld(world: World) {
        super.setWorld(world)
        this.minY = world.bottomY - 1
    }
}