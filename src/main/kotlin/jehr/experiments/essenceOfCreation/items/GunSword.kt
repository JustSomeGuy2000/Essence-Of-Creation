package jehr.experiments.essenceOfCreation.items

import com.chocohead.mm.api.ClassTinkerers
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import jehr.experiments.essenceOfCreation.components.EoCComponents
import jehr.experiments.essenceOfCreation.entities.GunSwordBullet
import jehr.experiments.essenceOfCreation.utils.EarlyRiser
import jehr.experiments.essenceOfCreation.utils.scalarToVector
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.WeaponComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.RangedWeaponItem
import net.minecraft.item.consume.UseAction
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.joml.Vector3f
import java.util.function.Predicate
import kotlin.math.max

open class GunSword(settings: Settings): RangedWeaponItem(settings) {

    companion object {
        const val BASE_ID = "gunsword"

        fun generateBaseSettings(meleeDamageMod: Double, meleeAtkSpdMod: Double, durability: Int, bulletDmg: Int, bulletCost: Int, gravity: Float = Info.BASE_GRAVITY, shotVelocity: Float = Info.BASE_VELOCITY, drag: Float = Info.BASE_DRAG, cooldown: Int = Info.BASE_COOLDOWN, accTime: Int = Info.ACCURATISE_TIME): Settings  =
            Settings()
                .maxDamage(durability)
                .component(DataComponentTypes.WEAPON, WeaponComponent(1))
                .component(EoCComponents.gunSwordInfoComponent, Info(bulletDmg, bulletCost, gravity, shotVelocity, drag, cooldown, accTime))
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

        fun getInfo(from: LivingEntity): Info? = from.getStackInHand(from.activeHand).get(EoCComponents.gunSwordInfoComponent)

        fun getTranslations(stack: ItemStack): Vec3d {
            var transX = -0.59
            var transY = 0.49
            val transZ = 0.3573153
            if (stack.isOf(EoCItems.ironGunSword)) {
                transX = -0.582
            } else if (stack.isOf(EoCItems.goldGunSword)) {
                transX = -0.585
            } else if (stack.isOf(EoCItems.diamondGunSword)) {
                transX = -0.56
                transY = 0.46
            } else if (stack.isOf(EoCItems.netheriteGunSword)) {
                transX = -0.6
                transY = 0.52
            }
            return Vec3d(transX, transY, transZ)
        }
    }

    open val bulletFactory: (World, LivingEntity, Int, Float, Float) -> GunSwordBullet = ::GunSwordBullet

    override fun getProjectiles(): Predicate<ItemStack> = Predicate<ItemStack>{stack -> true}
    override fun getRange() = 25
    override fun getMaxUseTime(stack: ItemStack?, user: LivingEntity?) = Info.MAX_USE_TIME
    override fun getUseAction(stack: ItemStack?): UseAction = ClassTinkerers.getEnum(UseAction::class.java, EarlyRiser.GUNSWORD_ENUM)

    override fun use(world: World, user: PlayerEntity, hand: Hand?): ActionResult? {
        val xpCost = getInfo(user)?.bulletCost ?: return ActionResult.PASS
        if (user.totalExperience >= xpCost) {
            user.setCurrentHand(hand)
            return ActionResult.CONSUME
        } else return ActionResult.PASS
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int): Boolean {
        if (world.isClient) return false
        val info = getInfo(user) ?: return false
        this.spawnAndShoot(world, user, info, 0F)
        if (user is PlayerEntity) {
            this.applyCost(user, info)
            user.itemCooldownManager.set(ItemStack(this), this.components.get(EoCComponents.gunSwordInfoComponent)?.cd ?: Info.BASE_COOLDOWN)
            stack.damage(1, user, LivingEntity.getSlotForHand(user.activeHand))
        }
        return true
    }

    open fun applyCost(user: LivingEntity, info: Info): Boolean {
        if (user is PlayerEntity) {
            user.addExperience(-(info.bulletCost))
            return true
        }
        return false
    }

    override fun shoot(shooter: LivingEntity, projectile: ProjectileEntity, index: Int, speed: Float, divergence: Float, yaw: Float, target: LivingEntity?) {
        /*Yaw is between 90 and -90, negative values upwards, 0 is parallel to ground.
         * Pitch is between 180 and -180, 0 is south (z-axis), negative values towards positive x (east).*/
        val dir = Vec3d.fromPolar(shooter.pitch, shooter.yaw).normalize()
        projectile.setVelocity(dir.x, dir.y, dir.z, speed, divergence)
    }

    open fun spawnAndShoot(world: World, shooter: LivingEntity, info: Info, divergence: Float): Boolean {
        if (world.isClient) return false
        val bullet = this.bulletFactory(world, shooter, info.bulletDmg, info.gravity, info.drag)
        bullet.setPosition(shooter.pos.add(0.0, 1.5, 0.0))
        this.shoot(shooter, bullet, 0, info.shotVelocity, divergence, 0.0F, null)
        world.spawnEntity(bullet)
        bullet.triggerProjectileSpawned(world as ServerWorld, ItemStack.EMPTY)
        return true
    }

    class Amethyst(settings: Settings): GunSword(settings) {

        companion object {
            const val ID = "amethyst_${BASE_ID}"
        }

        override fun applyCost(user: LivingEntity, info: Info): Boolean {
            val world = user.world
            if (world is ServerWorld) {
                user.damage(world, DamageSource(world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(DamageTypes.ARROW.value).get()), info.bulletCost.toFloat())
                return true
            }
            return false
        }
    }

    class BreezeRod(settings: Settings): GunSword(settings) {

        companion object {
            const val ID = "breezy_$BASE_ID"
        }

        override val bulletFactory: (World, LivingEntity, Int, Float, Float) -> GunSwordBullet.WindCharge = GunSwordBullet::WindCharge
    }

    class EchoShard(settings: Settings): GunSword(settings) {

        companion object {
            const val ID = "sonic_$BASE_ID"
        }

        override val bulletFactory: (World, LivingEntity, Int, Float, Float) -> GunSwordBullet.SonicBoom = GunSwordBullet::SonicBoom
    }

    class Emerald(settings: Settings): GunSword(settings) {

        companion object {
            const val ID = "emerald_$BASE_ID"
            const val BOOST_VELOCITY = 3.0
            const val BOOST_BONUS = 5.0F
        }

        override fun spawnAndShoot(world: World, shooter: LivingEntity, info: Info, divergence: Float): Boolean {
            if (world.isClient) return false
            val mhs = shooter.getStackInHand(shooter.activeHand)
            if (mhs.isOf(EoCItems.emeraldGunSword)) {
                val info = getInfo(shooter) ?: return false
                mhs.set(EoCComponents.gunSwordInfoComponent, info.copy(boost = true))
            }
            val addVel = scalarToVector(BOOST_VELOCITY, shooter.pitch, shooter.yaw, 0.0F)
            shooter.addVelocity(addVel)
            shooter.velocityModified = true
            return true
        }

        override fun postHit(stack: ItemStack?, target: LivingEntity?, attacker: LivingEntity?) {
            super.postHit(stack, target, attacker)
            if (stack == null || target == null || attacker == null || attacker.world !is ServerWorld) return
            val info = getInfo(attacker) ?: return
            if ((attacker.velocity.y < 0.1 && attacker.velocity.y > -0.1) || !info.boost) {
                stack.set(EoCComponents.gunSwordInfoComponent, info.copy(boost = false))
                return
            }
            target.damage(attacker.world as ServerWorld, DamageSource(attacker.world.registryManager.getOrThrow(RegistryKeys.DAMAGE_TYPE).getEntry(DamageTypes.ARROW.value).get()), BOOST_BONUS) //TODO: Gun-sword damage type!!!
            stack.set(EoCComponents.gunSwordInfoComponent, info.copy(boost = false))
        }
    }

    data class Info(val bulletDmg: Int, val bulletCost: Int, val gravity: Float = BASE_GRAVITY, val shotVelocity: Float = BASE_VELOCITY, val drag: Float = BASE_DRAG, val cd: Int = BASE_COOLDOWN, val accTime: Int = ACCURATISE_TIME, val boost: Boolean = false) {

        companion object {
            /**Acceleration downwards, in b/t^2.*/
            const val BASE_GRAVITY = 0.005F
            /**Starting velocity, in b/t. At least, I'm pretty sure its ticks, but it doesn't seem so in game.*/
            const val BASE_VELOCITY = 10.0F
            /**Drag force, ib b/t^2 opposite velocity.*/
            const val BASE_DRAG = 0.0005F
            /**Amount of aiming time to 0 inaccuracy, in ticks.*/
            const val ACCURATISE_TIME = 40
            /**How long before it automatically shoots, in ticks.*/
            const val MAX_USE_TIME = 72000
            /**Cooldown, in ticks.*/
            const val BASE_COOLDOWN = 40

            val codec: Codec<Info> = RecordCodecBuilder.create { builder ->
                builder.group(
                    Codec.INT.fieldOf("bullet_damage").forGetter(Info::bulletDmg),
                    Codec.INT.fieldOf("buller_cost").forGetter(Info::bulletCost),
                    Codec.FLOAT.fieldOf("gravity").forGetter(Info::gravity),
                    Codec.FLOAT.fieldOf("shot_velocity").forGetter(Info::shotVelocity),
                    Codec.FLOAT.fieldOf("drag").forGetter(Info::drag),
                    Codec.INT.fieldOf("cooldown").forGetter(Info::cd),
                    Codec.INT.fieldOf("acc_time").forGetter(Info::accTime)
                ).apply(builder, ::Info)
            }
        }
    }
}