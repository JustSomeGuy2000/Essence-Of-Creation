package jehr.experiments.essenceOfCreation.blocks

import com.mojang.serialization.MapCodec
import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import jehr.experiments.essenceOfCreation.blockEntities.RoggenStatueBlockEntity
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects
import jehr.experiments.essenceOfCreation.utils.RoggenLore
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.component.Component
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.WrittenBookContentComponent
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.StringNbtReader
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.RawFilteredPair
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.Optional

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
                val effect = StatusEffectInstance(EoCStatusEffects.blessingOfRye, BlessingOfRye.ampTime, 1)
                if (!player.hasStatusEffect(effect.effectType) && origin.isInRange(player.pos, APPLY_RADIUS) && (player.gameMode?.isSurvivalLike ?: false)) {
                    player.addStatusEffect(StatusEffectInstance(effect))
                    world.playSound(null, pos, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.BLOCKS)
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

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity): BlockState? {
        if (!world.isClient) {
            player.removeStatusEffect(EoCStatusEffects.blessingOfRye)
            world.playSound(null, pos, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.BLOCKS)
            val stack = ItemStack(Items.WRITTEN_BOOK)
            val title = RawFilteredPair.of(Text.translatable("lore.roggen.book_title").toString())
            /*Note: using a translation key for the title causes a crash, as the string representation exceeds the limit of 32 characters put in place by the encoder. Apparently this is enforced in game as a 16-character limit. I should have read the wiki page before going into the code to figure things out. However, I still don't know exactly why it crashes. Neither the translation key, translated value, or toString() equal the 50 characters Minecraft says I'm trying to encode. Just ignore it for now.*/
            val plcTitle = RawFilteredPair.of(RoggenLore.TITLE)
            val contents = WrittenBookContentComponent(plcTitle, "???", 3, RoggenLore.toBookText(RoggenLore.entries.random()), true)
            stack.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, contents)
            val drop = ItemEntity(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            world.spawnEntity(drop)
            super.onBreak(world, pos, state, player)
        }
        return state
    }
}