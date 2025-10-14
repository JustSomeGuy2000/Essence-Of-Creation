package jehr.experiments.essenceOfCreation.items

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.components.EoCComponents
import jehr.experiments.essenceOfCreation.entities.GunSwordBullet
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.WeaponComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.RangedWeaponItem
import net.minecraft.item.consume.UseAction
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.util.function.Predicate
import kotlin.math.max

class GunSword(settings: Settings): RangedWeaponItem(settings) {

    companion object {
        const val BASE_ID = "gun_sword"

        fun generateBaseSettings(meleeDamageMod: Double, meleeAtkSpdMod: Double, durability: Int, bulletDmg: Int, bulletCost: Int, gravity: Float = Info.BASE_GRAVITY, shotVelocity: Float = Info.BASE_VELOCITY, drag: Float = Info.BASE_DRAG): Settings  =
            Settings()
                .maxDamage(durability)
                .component(DataComponentTypes.WEAPON, WeaponComponent(1))
                .component(EoCComponents.gunSwordInfoComponent, Info(bulletDmg, bulletCost, gravity, shotVelocity, drag))
                .attributeModifiers(AttributeModifiersComponent.builder()
                    .add(EntityAttributes.ATTACK_DAMAGE,
                        EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, meleeDamageMod, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                    .add(
                        EntityAttributes.ATTACK_SPEED,
                        EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, meleeAtkSpdMod, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                    .build()
                )
    }

    override fun getProjectiles(): Predicate<ItemStack> = Predicate<ItemStack>{stack -> true}
    override fun getRange() = 25
    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?) = Info.MAX_USE_TIME
    override fun getUseAction(stack: ItemStack?) = UseAction.BOW

    override fun use(world: World, user: PlayerEntity, hand: Hand?): ActionResult? {
        val xpCost = this.getInfo()?.bulletCost ?: return ActionResult.PASS
        if (user.totalExperience >= xpCost) {
            user.setCurrentHand(hand)
            return ActionResult.CONSUME
        } else return ActionResult.PASS
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int): Boolean {
        if (world.isClient) return false
        val accuracy = max(Info.BASE_DIVERGENCE - (Info.MAX_USE_TIME - remainingUseTicks) * Info.ACCURATISE_RATE, 0.0F)
        val info = getInfo() ?: return false
        this.spawnAndShoot(world, user, info, accuracy)
        return true
    }

    override fun shoot(shooter: LivingEntity, projectile: ProjectileEntity, index: Int, speed: Float, divergence: Float, yaw: Float, target: LivingEntity?) {
        /*Yaw is between 90 and -90, negative values upwards, 0 is parallel to ground.
         * Pitch is between 180 and -180, 0 is south (z-axis), negative values towards positive x (east).*/
        projectile.setVelocity(shooter, shooter.pitch, shooter.yaw + yaw, 0.0F, speed, divergence)
        if (shooter is PlayerEntity) {
            shooter.addExperience(-(this.getInfo()?.bulletCost ?: 0))
            shooter.itemCooldownManager.set(ItemStack(this), Info.COOLDOWN)
        }
    }

    fun spawnAndShoot(world: World, shooter: LivingEntity, info: Info, divergence: Float): Boolean {
        if (world.isClient) return false
        val bullet = GunSwordBullet(world, shooter, info.bulletDmg, info.gravity, info.drag)
        bullet.setPosition(shooter.pos.add(0.0, 1.5, 0.0))
        val res = world.spawnEntity(bullet)
        this.shoot(shooter, bullet, 0, info.shotVelocity, divergence, 0.0F, null)
        return true
    }

    fun getInfo(): Info? {
        val info = this.components.get(EoCComponents.gunSwordInfoComponent)
        if (info == null) {
            EoCMain.logger.warn("No info found for this Gun-Sword!")
            return null
        }
        return info
    }

    data class Info(val bulletDmg: Int, val bulletCost: Int, val gravity: Float = BASE_GRAVITY, val shotVelocity: Float = BASE_VELOCITY, val drag: Float = BASE_DRAG) {
        companion object {
            /**Blocks per tick downwards.*/
            const val BASE_GRAVITY = 0.005F
            /**Blocks per second? in direction of travel. I'm pretty sure its ticks, but it doesn't seem so in game.*/
            const val BASE_VELOCITY = 1.0F
            /**Blocks per tick per tick opposite the velocity*/
            const val BASE_DRAG = 0.0005F
            /**Hip-fire inaccuracy, units unknown.*/
            const val BASE_DIVERGENCE = 5.0F
            /**Rate at which inaccuracy decreases, in ?/t.*/
            const val ACCURATISE_RATE = BASE_DIVERGENCE/60
            /**How long before it automatically shoots, in ticks.*/
            const val MAX_USE_TIME = 72000
            /**Cooldown, in ticks.*/
            const val COOLDOWN = 40

            val codec: Codec<Info> = RecordCodecBuilder.create { builder ->
                builder.group(
                    Codec.INT.fieldOf("bullet_damage").forGetter(Info::bulletDmg),
                    Codec.INT.fieldOf("buller_cost").forGetter(Info::bulletCost),
                    Codec.FLOAT.fieldOf("gravity").forGetter(Info::gravity),
                    Codec.FLOAT.fieldOf("shot_velocity").forGetter(Info::shotVelocity),
                    Codec.FLOAT.fieldOf("drag").forGetter(Info::drag)
                ).apply(builder, ::Info)
            }
        }
    }
}