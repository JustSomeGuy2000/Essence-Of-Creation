package jehr.experiments.essenceOfCreation.blockEntityRenderers

import jehr.experiments.essenceOfCreation.blockEntities.EoCBlockEntities
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories

object EoCBlockEntityRenderers {

    fun init() {
        BlockEntityRendererFactories.register(EoCBlockEntities.refractorBlockEntity, ::RefractorBlockEntityRenderer)
    }
}