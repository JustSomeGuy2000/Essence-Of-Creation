package jehr.experiments.essenceOfCreation.entityRenderers

import jehr.experiments.essenceOfCreation.entities.EoCEntities
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.FlyingItemEntityRenderer

object EoCEntityRenderers {

    fun init() {
        EntityRendererRegistry.register(EoCEntities.gunSwordBullet) { FlyingItemEntityRenderer(it) }
    }
}