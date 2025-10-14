package jehr.experiments.essenceOfCreation.entityRenderers

import jehr.experiments.essenceOfCreation.EoCMain
import jehr.experiments.essenceOfCreation.entities.GunSwordBullet
import jehr.experiments.essenceOfCreation.renderStates.GunSwordBulletRenderState
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class GunSwordBulletRenderer(ctx: EntityRendererFactory.Context): EntityRenderer<GunSwordBullet, GunSwordBulletRenderState>(ctx) {

    companion object {
        val texture: Identifier = Identifier.of(EoCMain.MOD_ID, "textures/entity/gun_sword_bullet.png")
    }

    override fun createRenderState() = GunSwordBulletRenderState()

    override fun render(
        state: GunSwordBulletRenderState?,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int
    ) {
        super.render(state, matrices, vertexConsumers, light)
    }
}