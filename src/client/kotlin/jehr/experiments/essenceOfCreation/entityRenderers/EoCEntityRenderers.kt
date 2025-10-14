package jehr.experiments.essenceOfCreation.entityRenderers

import jehr.experiments.essenceOfCreation.entities.EoCEntities
import jehr.experiments.essenceOfCreation.entityModels.GunSwordBulletModel
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EoCEntityRenderers {

    fun init() {
        EntityRendererRegistry.register(EoCEntities.gunSwordBullet) { GunSwordBulletRenderer(it) }
        EntityModelLayerRegistry.registerModelLayer(GunSwordBulletModel.layer, GunSwordBulletModel::getTexturedModelData)
    }
}