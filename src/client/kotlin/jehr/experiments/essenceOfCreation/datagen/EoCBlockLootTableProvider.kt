package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class EoCBlockLootTableProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricBlockLootTableProvider(dataOutput, registryLookup) {

	override fun getName() = "EssenceOfCreationBlockLootTableProvider"

	override fun generate() {
		addDrop(EoCBlocks.spatialDisplacer)
		addDropWithSilkTouch(EoCBlocks.scaffoldStripper)
		addDrop(EoCBlocks.essentialExtractor)
		addDrop(EoCBlocks.essentialInfuser)
		addDrop(EoCBlocks.ryeBale)
		addDrop(EoCBlocks.balefulPoppy)
	}
}