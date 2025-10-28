package jehr.experiments.essenceOfCreation.entities

import jehr.experiments.essenceOfCreation.EoCMain
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.world.World

object EoCEntities {

    val gunSwordBullet = register(GunSwordBullet.ID, ::GunSwordBullet, {
        this.dimensions(0.1F, 0.1F).maxTrackingRange(25)
    })
    val gunSwordBulletWC = register(GunSwordBullet.ID_WC, GunSwordBullet::WindCharge,{
        this.dimensions(0.1F, 0.1F).maxTrackingRange(25)
    })
    val gunSwordBulletSB = register(GunSwordBullet.ID_SB, GunSwordBullet::SonicBoom, {
        this.dimensions(0.5F, 0.5F).maxTrackingRange(25)
    })
    val balefulSnowballEntity = register(BalefulSnowballEntity.ID, ::BalefulSnowballEntity, {
        this.dimensions(0.3F, 0.3F).maxTrackingRange(10).dropsNothing().trackingTickInterval(10)
    })

    fun init() {}

    fun <T : Entity> register(name: String, factory: (EntityType<T>, World) -> T, modifiers: EntityType.Builder<T>.() -> EntityType.Builder<T>, spawnGroup: SpawnGroup = SpawnGroup.MISC): EntityType<T> =
        Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(EoCMain.MOD_ID, name),
            EntityType.Builder.create(
                    factory,
                    spawnGroup)
                .apply{modifiers}
                .build(RegistryKey.of(
                    RegistryKeys.ENTITY_TYPE,
                    Identifier.of(EoCMain.MOD_ID, name)
                )
            )
        )
}