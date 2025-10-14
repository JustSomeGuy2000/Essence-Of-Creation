package jehr.experiments.essenceOfCreation.entityModels

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.renderStates.GunSwordBulletRenderState
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.util.Identifier

class GunSwordBulletModel(part: ModelPart): EntityModel<GunSwordBulletRenderState>(part) {

    companion object {
        val layer = EntityModelLayer(Identifier.of(EoCMain.MOD_ID, "gun_sword_bullet"), "main")

        fun getTexturedModelData(): TexturedModelData {
            val md = ModelData()
            val modelPartData = md.root
            modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), ModelTransform.rotation(0F, 0F, 0F))
            return TexturedModelData.of(md, 8, 4)
        }
    }

    val base: ModelPart = part.getChild(EntityModelPartNames.CUBE)

}