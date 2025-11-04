package jehr.experiments.essenceOfCreation.blockEntityRenderers

import net.minecraft.block.entity.BeamEmitter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LazyEntityReference
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d

class RefractorBlockEntityRenderer<T>(ctx: BlockEntityRendererFactory.Context): BeaconBlockEntityRenderer<T>(ctx)
where T: BlockEntity, T: BeamEmitter{

    companion object {
        const val LASER_TICKS = 20
    }

    var remainingLaserTime = 0
    var target: LazyEntityReference<LivingEntity>? = null

    override fun render(entity: T, tickProgress: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int, cameraPos: Vec3d) {
        super.render(entity, tickProgress, matrices, vertexConsumers, light, overlay, cameraPos)
        if (this.remainingLaserTime > 0 && this.target != null) {
            renderLaser(entity, tickProgress, matrices, vertexConsumers, light, overlay, cameraPos)
            this.remainingLaserTime -= 1
        }
    }

    fun renderLaser(entity: T, tickProgress: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int, cameraPos: Vec3d) {
        
    }
}