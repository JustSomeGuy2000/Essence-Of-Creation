package jehr.experiments.essenceOfCreation.items

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import jehr.experiments.essenceOfCreation.components.EoCComponents
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.WeaponComponent
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World

class GunSword(settings: Settings): Item(settings) {

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

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): ActionResult? {
        return super.use(world, user, hand)
    }

    data class Info(val bulletDmg: Int, val bulletCost: Int, val gravity: Float = BASE_GRAVITY, val shotVelocity: Float = BASE_VELOCITY, val drag: Float = BASE_DRAG) {
        companion object {
            /**Blocks per tick downwards.*/
            const val BASE_GRAVITY = 0.05F
            /**Blocks per tick in direction of travel. Decomposed internally based on player look vector.*/
            const val BASE_VELOCITY = 0.5F
            /**Blocks per tick per tick opposite the velocity*/
            const val BASE_DRAG = 0.0005F

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