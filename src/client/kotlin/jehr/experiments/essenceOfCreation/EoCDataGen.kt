package jehr.experiments.essenceOfCreation

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper.Companion.Progress
import jehr.experiments.essenceOfCreation.items.EoCItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.BlockStateVariantMap
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models
import net.minecraft.client.data.TextureMap
import net.minecraft.client.data.TexturedModel
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

object EoCDataGen : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::EoCLangProviderEnUs)
		pack.addProvider(::EoCModelProvider)
	}
}

class EoCLangProviderEnUs(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricLanguageProvider(dataOutput, registryLookup) {
	val id = EoCMain.modId

	override fun generateTranslations(wrapperLookup: RegistryWrapper.WrapperLookup, builder: FabricLanguageProvider.TranslationBuilder) {
		builder.add("item.$id.essence_of_creation", "Essence of Creation")
		builder.add("itemGroup.$id.essence_of_creation", "Essence Of Creation")
		builder.add("itemTooltip.$id.essence_of_creation", "Have you ever felt that the world wasn't enough?")
		builder.add("block.$id.scaffold_seed", "Scaffold Seed")
		builder.add("item.$id.scaffold_seed", "Scaffold Seed")
		builder.add("block.$id.scaffold_trunk", "Scaffold Trunk")
		builder.add("item.$id.scaffold_trunk", "Scaffold Trunk")
		builder.add("block.$id.scaffold_stripper", "Scaffold Stripper")
		builder.add("item.$id.scaffold_stripper", "Scaffold Stripper")
		builder.add("block.$id.spatial_displacer", "Spatial Displacer")
		builder.add("item.$id.spatial_displacer", "Spatial Displacer")
	}
}

class EoCModelProvider(dataOutput: FabricDataOutput): FabricModelProvider(dataOutput) {

	override fun generateBlockStateModels(bsmg: BlockStateModelGenerator) {
		bsmg.registerSimpleCubeAll(EoCBlocks.scaffoldSeed)
		bsmg.registerSimpleCubeAll(EoCBlocks.scaffoldTrunk)

		val stripperNew = TexturedModel.CUBE_ALL.upload(EoCBlocks.scaffoldStripper, bsmg.modelCollector)
		val stripperSearching = bsmg.createSubModel(EoCBlocks.scaffoldStripper, "_searching", Models.CUBE_ALL,
			TextureMap::all)
		val stripperMoving = bsmg.createSubModel(EoCBlocks.scaffoldStripper, "_moving", Models.CUBE_ALL,
			TextureMap::all)
		val stripperHalted = bsmg.createSubModel(EoCBlocks.scaffoldStripper, "_halted", Models.CUBE_ALL,
			TextureMap::all)
		bsmg.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(EoCBlocks.scaffoldStripper).with(
			BlockStateVariantMap.models(ScaffoldStripper.progress)
				.register(Progress.NEW, BlockStateModelGenerator.createWeightedVariant(stripperNew))
				.register(Progress.SEARCHING, BlockStateModelGenerator.createWeightedVariant(stripperSearching))
				.register(Progress.MOVING, BlockStateModelGenerator.createWeightedVariant(stripperMoving))
				.register(Progress.HALTED, BlockStateModelGenerator.createWeightedVariant(stripperHalted))))
	}

	override fun generateItemModels(img: ItemModelGenerator) {
		img.register(EoCItems.essenceOfCreation, Models.GENERATED)
	}
}