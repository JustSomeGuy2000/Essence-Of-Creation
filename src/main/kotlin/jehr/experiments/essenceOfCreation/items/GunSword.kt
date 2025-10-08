package jehr.experiments.essenceOfCreation.items

import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item

class GunSword(settings: Settings, val shotCost: Int, val shotDamage: Int): Item(settings) {

    companion object {
        fun buildAttributeModifiers(damageMod: Double, atkSpdMod: Double): AttributeModifiersComponent  =
            AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, damageMod, EntityAttributeModifier.Operation.ADD_VALUE),
                    AttributeModifierSlot.MAINHAND)
                .add(
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, atkSpdMod, EntityAttributeModifier.Operation.ADD_VALUE),
                    AttributeModifierSlot.MAINHAND)
                .build()
    }
}