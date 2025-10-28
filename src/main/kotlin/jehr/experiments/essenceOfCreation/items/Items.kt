package jehr.experiments.essenceOfCreation.items

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.statusEffects.EoCStatusEffects
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.ConsumableComponents
import net.minecraft.component.type.DamageResistantComponent
import net.minecraft.component.type.DeathProtectionComponent
import net.minecraft.component.type.FoodComponent
import net.minecraft.component.type.WeaponComponent
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object EoCItems {

    val essenceOfCreation = register("essence_of_creation", ::EssenceOfCreation, Item.Settings().rarity(Rarity.UNCOMMON))
    const val RYE_ID = "rye"
    val rye = register(RYE_ID, ::Item, Item.Settings())
    const val TOR_ID = "totem_of_unrying"
    val totemOfUnrying = register(TOR_ID, ::Item, Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1).component(DataComponentTypes.DEATH_PROTECTION, DeathProtectionComponent(listOf(
        ClearAllEffectsConsumeEffect(),
        ApplyEffectsConsumeEffect(listOf(
            StatusEffectInstance(StatusEffects.REGENERATION, 900, 2),
            StatusEffectInstance(StatusEffects.ABSORPTION, 100, 2),
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 1),
            StatusEffectInstance(EoCStatusEffects.blessingOfRye, BlessingOfRye.ampTime, 1)
        ))
    ))))
    val handheldInfuser = register(HandheldInfuser.ID, ::HandheldInfuser, Item.Settings().maxCount(1))
    const val GOD_APPLE_ID = "god_apple"
    val godApple = register(GOD_APPLE_ID, ::Item, Item.Settings().rarity(Rarity.RARE).food(
        FoodComponent.Builder().nutrition(8).saturationModifier(1.5F).alwaysEdible().build(),
        ConsumableComponents.food().consumeEffect(ApplyEffectsConsumeEffect(listOf(
            StatusEffectInstance(StatusEffects.REGENERATION, 600, 3),
            StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 2),
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0),
            StatusEffectInstance(StatusEffects.ABSORPTION, 6000, 5),
            StatusEffectInstance(StatusEffects.STRENGTH, 3600, 3),
            StatusEffectInstance(StatusEffects.SPEED, 3600, 1)
        ))).build()
     ).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true))
    val superBoneMeal = register(SuperBoneMeal.ID, ::SuperBoneMeal, Item.Settings())
    const val CANE_ID = "cane"
    val cane = register(CANE_ID, ::Item, Item.Settings().maxDamage(100).repairable(Items.STICK).enchantable(20)
        .component(DataComponentTypes.WEAPON, WeaponComponent(1))
        .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
        .attributeModifiers(AttributeModifiersComponent.builder()
            .add(EntityAttributes.ATTACK_DAMAGE,
                EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 2.0,
                EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.ATTACK_SPEED,
                EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -2.4, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.ATTACK_KNOCKBACK,
                EntityAttributeModifier(Identifier.ofVanilla("base_attack_knockback"), 2.0, EntityAttributeModifier.Operation.ADD_VALUE),
                AttributeModifierSlot.MAINHAND)
            .add(EntityAttributes.MOVEMENT_SPEED,
                EntityAttributeModifier(Identifier.ofVanilla("base_movement_speed"), 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                AttributeModifierSlot.MAINHAND)
            .build()))
    const val GSB_ITEM_ID = "gun_sword_bullet_item"
    val gsbItem = register(GSB_ITEM_ID, ::Item, Item.Settings())
    const val IRON_GUN_SWORD_ID = "iron_${GunSword.BASE_ID}"
    val ironGunSword = register(IRON_GUN_SWORD_ID, ::GunSword, GunSword.generateBaseSettings(5.0, -2.4, 250, 5, 3))
    const val GOLD_GUN_SWORD_ID = "golden_${GunSword.BASE_ID}"
    val goldGunSword = register(GOLD_GUN_SWORD_ID, ::GunSword, GunSword.generateBaseSettings(3.0, -2.4, 32, 5, 5, cooldown = 10))
    const val DIAMOND_GUN_SWORD_ID = "diamond_${GunSword.BASE_ID}"
    val diamondGunSword = register(DIAMOND_GUN_SWORD_ID, ::GunSword, GunSword.generateBaseSettings(6.0, -2.4, 1561, 9, 5))
    const val NETHERITE_GUN_SWORD_ID = "netherite_${GunSword.BASE_ID}"
    val netheriteGunSword = register(NETHERITE_GUN_SWORD_ID, ::GunSword, GunSword.generateBaseSettings(7.0, -2.4, 2031, 18, 10))
    val amethystGunSword = register(GunSword.Amethyst.ID, GunSword::Amethyst, GunSword.generateBaseSettings(6.0, -2.4, 1000, 5, 1, accTime = 20))
    val breezeRodGunSword = register(GunSword.BreezeRod.ID, GunSword::BreezeRod, GunSword.generateBaseSettings(7.0, -2.4, 1900, 2, 3, cooldown = 5, accTime = 10))
    val sonicGunSword = register(GunSword.EchoShard.ID, GunSword::EchoShard, GunSword.generateBaseSettings(7.0, -2.4, 1500, 12, 8, cooldown = 60))
    val emeraldGunSword = register(GunSword.Emerald.ID, GunSword::Emerald, GunSword.generateBaseSettings(6.0, -2.4, 1000, 0, 4))
    const val SUPER_GUN_SWORD_ID = "super_${GunSword.BASE_ID}"
    val superGunSword = register(SUPER_GUN_SWORD_ID, ::GunSword, GunSword.generateBaseSettings(9.0, -2.0, 10000, 50, 0))
    val balefulSnowballItem = register(BalefulSnowballItem.ID, ::BalefulSnowballItem, Item.Settings().maxCount(16))
    const val INTERITOR_HEART_ID = "interitor_heart"
    val interitorHeart = register(INTERITOR_HEART_ID, ::Item, Item.Settings()
        .rarity(Rarity.RARE)
        .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
        .component(DataComponentTypes.DAMAGE_RESISTANT, DamageResistantComponent(DamageTypeTags.IS_EXPLOSION))
        .component(DataComponentTypes.DAMAGE_RESISTANT, DamageResistantComponent(DamageTypeTags.IS_FIRE)))

    fun init() {
        ItemGroupEvents.modifyEntriesEvent(EoCMain.EoCItemGroupKey).register{
            it.add(this.essenceOfCreation)
            it.add(this.rye)
            it.add(this.totemOfUnrying)
            it.add(this.handheldInfuser)
            it.add(this.godApple)
            it.add(this.superBoneMeal)
            it.add(this.cane)
            it.add(this.superGunSword)
            it.add(this.balefulSnowballItem)
            it.add(this.interitorHeart)
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register{
            it.add(this.ironGunSword)
            it.add(this.goldGunSword)
            it.add(this.diamondGunSword)
            it.add(this.netheriteGunSword)
            it.add(this.amethystGunSword)
            it.add(this.breezeRodGunSword)
            it.add(this.sonicGunSword)
            it.add(this.emeraldGunSword)
        }
    }

    fun register(name: String, factory: (Item.Settings) -> Item, settings: Item.Settings): Item {
        val itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EoCMain.MOD_ID, name))
        val item = factory(settings.registryKey(itemKey))
        Registry.register(Registries.ITEM, itemKey, item)
        return item
    }
}