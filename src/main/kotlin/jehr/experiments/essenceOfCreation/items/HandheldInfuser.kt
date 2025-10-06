package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.criteria.EoCCriteria
import net.minecraft.component.type.TooltipDisplayComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import java.util.function.Consumer

class HandheldInfuser(settings: Settings): Item(settings) {

    companion object {
        const val ID = "handheld_infuser"

        val infusables = mapOf<EntityType<*>, EntityType<*>>(
            EntityType.ZOMBIE to EntityType.HUSK,
            EntityType.SKELETON to EntityType.WITHER_SKELETON,
            EntityType.VILLAGER to EntityType.WANDERING_TRADER,
            EntityType.WANDERING_TRADER to EntityType.WITCH,
            EntityType.WITCH to EntityType.VINDICATOR,
            EntityType.VINDICATOR to EntityType.EVOKER,
            EntityType.VEX to EntityType.ALLAY,
            EntityType.SNOW_GOLEM to EntityType.IRON_GOLEM,
            EntityType.COW to EntityType.MOOSHROOM,
            EntityType.BREEZE to EntityType.BLAZE,
            EntityType.SHEEP to EntityType.GOAT,
            EntityType.GOAT to EntityType.LLAMA,
            EntityType.LLAMA to EntityType.CAMEL,
            EntityType.ENDERMITE to EntityType.ENDERMAN,
            EntityType.PIG to EntityType.HOGLIN,
            EntityType.ZOMBIFIED_PIGLIN to EntityType.PIGLIN,
            EntityType.PIGLIN to EntityType.PIGLIN_BRUTE,
            EntityType.ZOGLIN to EntityType.HOGLIN,
            EntityType.SLIME to EntityType.MAGMA_CUBE,
            EntityType.FISHING_BOBBER to EntityType.ARROW,
            EntityType.ARROW to EntityType.SNOWBALL,
            EntityType.SPIDER to EntityType.CAVE_SPIDER,
            EntityType.GUARDIAN to EntityType.ELDER_GUARDIAN,
            EntityType.SQUID to EntityType.GLOW_SQUID)
    }

    @Deprecated("Deprecated in Java")
    override fun appendTooltip(stack: ItemStack, context: TooltipContext, displayComponent: TooltipDisplayComponent, textConsumer: Consumer<Text?>, type: TooltipType) {
        textConsumer.accept(Text.translatable("itemTooltip.${EoCMain.MOD_ID}.$ID.title").formatted(Formatting.GOLD))
        textConsumer.accept(Text.translatable("itemTooltip.${EoCMain.MOD_ID}.$ID.content"))
    }

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        if (attacker.isPlayer && target.type in infusables) {
            val p = target.pos
            if (attacker !is ServerPlayerEntity) return
            var targetStack: ItemStack? = null
            for (stack in attacker.inventory) {
                if (stack.isOf(EoCItems.essenceOfCreation)) targetStack = stack
            }
            if (targetStack == null) return
            targetStack.decrement(1)
            val spawned = infusables[target.type]?.spawn(target.world as ServerWorld, BlockPos(p.x.toInt(), p.y.toInt(), p.z.toInt()), SpawnReason.SPAWN_ITEM_USE)
            spawned?.setPos(p.x, p.y, p.z)
            target.discard()
            EoCCriteria.anthropogenicCriterion.trigger(attacker)
        }
    }
}