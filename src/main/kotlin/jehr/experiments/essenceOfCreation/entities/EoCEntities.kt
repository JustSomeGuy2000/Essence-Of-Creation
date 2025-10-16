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

    val gunSwordBullet = register("gun_sword_bullet", ::GunSwordBullet, {
        this.dimensions(0.1F, 0.1F).maxTrackingRange(25)
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