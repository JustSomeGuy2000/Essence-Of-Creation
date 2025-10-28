package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.entities.BalefulSnowballEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.ProjectileEntity.ProjectileCreator
import net.minecraft.entity.projectile.thrown.SnowballEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ProjectileItem
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Position
import net.minecraft.world.World

class BalefulSnowballItem(settings: Settings): Item(settings), ProjectileItem {

    companion object {
        const val ID = "baleful_snowball"
        const val POWER = 1.5F
    }

    override fun createEntity(world: World, pos: Position, stack: ItemStack, direction: Direction) =
        BalefulSnowballEntity(world, pos.x, pos.y, pos.z, stack)

    override fun use(world: World, user: PlayerEntity, hand: Hand?): ActionResult {
        val itemStack = user.getStackInHand(hand)
        world.playSound(null, user.x, user.y, user.z, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
        if (world is ServerWorld) {
            ProjectileEntity.spawnWithVelocity({ world: ServerWorld?, owner: LivingEntity, stack: ItemStack ->
                BalefulSnowballEntity(world as World, owner, stack)
            }, world, itemStack, user, 0.0f, POWER, 1.0f)
        }

        itemStack.decrementUnlessCreative(1, user)
        return ActionResult.SUCCESS
    }
}