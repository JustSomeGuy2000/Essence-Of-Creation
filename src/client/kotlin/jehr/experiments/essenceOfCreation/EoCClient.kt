package jehr.experiments.essenceOfCreation

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.handledScreens.EoCHandledScreens
import jehr.experiments.essenceOfCreation.particles.EoCParticles
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.particle.FlameParticle
import net.minecraft.client.render.BlockRenderLayer

object EoCClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, EoCBlocks.scaffoldSeed, EoCBlocks.scaffoldTrunk)
		EoCHandledScreens.init()
		ParticleFactoryRegistry.getInstance().register(EoCParticles.purpleFlame, FlameParticle::Factory)
	}
}