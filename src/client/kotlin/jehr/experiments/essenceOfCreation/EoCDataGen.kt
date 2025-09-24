package jehr.experiments.essenceOfCreation

import jehr.experiments.essenceOfCreation.blocks.*
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper.Companion.Progress
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.items.EssenceOfCreation
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.client.data.*
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

object EoCDataGen : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::EoCLangProviderEnUs)
		pack.addProvider(::EoCModelProvider)
		pack.addProvider(::EoCBlockLootTableProvider)
	}
}

class EoCLangProviderEnUs(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricLanguageProvider(dataOutput, registryLookup) {
	val id = EoCMain.MOD_ID

	override fun generateTranslations(wrapperLookup: RegistryWrapper.WrapperLookup, builder: TranslationBuilder) {
		builder.add("item.$id.${EssenceOfCreation.ID}", "Essence of Creation")
		builder.add("itemGroup.$id.essence_of_creation", "Essence Of Creation")
		builder.add("itemTooltip.$id.essence_of_creation", "Have you ever felt that the world wasn't enough?")
		builder.add("block.$id.${ScaffoldSeed.ID}", "Scaffold Seed")
		builder.add("item.$id.${ScaffoldSeed.ID}", "Scaffold Seed")
		builder.add("block.$id.${ScaffoldTrunk.ID}", "Scaffold Trunk")
		builder.add("item.$id.${ScaffoldTrunk.ID}", "Scaffold Trunk")
		builder.add("block.$id.${ScaffoldStripper.ID}", "Scaffold Stripper")
		builder.add("item.$id.${ScaffoldStripper.ID}", "Scaffold Stripper")
		builder.add("block.$id.${SpatialDisplacer.ID}", "Spatial Displacer")
		builder.add("item.$id.${SpatialDisplacer.ID}", "Spatial Displacer")
		builder.add("block.$id.${RoggenStatue.ID}", "Statue of the Rye God")
		builder.add("item.$id.${RoggenStatue.ID}", "Statue of the Rye God")
		builder.add("effect.$id.${BlessingOfRye.ID}", "Blessing of Rye")
		builder.add("item.$id.rye", "Rye")
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

		bsmg.blockStateCollector.accept(
			VariantsBlockModelDefinitionCreator.of(
				EoCBlocks.spatialDisplacer,
				BlockStateModelGenerator.createWeightedVariant(TexturedModel.ORIENTABLE_WITH_BOTTOM.upload(EoCBlocks.spatialDisplacer, bsmg.modelCollector))
			)
		)

		bsmg.registerSimpleCubeAll(EoCBlocks.roggenStatue)
	}

	override fun generateItemModels(img: ItemModelGenerator) {
		img.register(EoCItems.essenceOfCreation, Models.GENERATED)
		img.register(EoCItems.rye, Models.GENERATED)
	}
}

class EoCBlockLootTableProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricBlockLootTableProvider(dataOutput, registryLookup) {

	override fun generate() {
		addDrop(EoCBlocks.spatialDisplacer)
		addDropWithSilkTouch(EoCBlocks.scaffoldStripper)
	}
}