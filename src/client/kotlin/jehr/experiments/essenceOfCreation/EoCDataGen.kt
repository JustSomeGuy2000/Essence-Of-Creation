package jehr.experiments.essenceOfCreation

import jehr.experiments.essenceOfCreation.blocks.*
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper.Companion.Progress
import jehr.experiments.essenceOfCreation.criteria.EoCCriteria
import jehr.experiments.essenceOfCreation.criteria.RyeNotCriterion
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.items.EssenceOfCreation
import jehr.experiments.essenceOfCreation.statusEffects.BlessingOfRye
import jehr.experiments.essenceOfCreation.utils.RoggenLore
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.client.data.*
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object EoCDataGen : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::EoCLangProviderEnUs)
		pack.addProvider(::EoCModelProvider)
		pack.addProvider(::EoCBlockLootTableProvider)
//		pack.addProvider(::EoCRecipeProvider)
		pack.addProvider(::EoCAdvancementProvider)
	}
}

class EoCLangProviderEnUs(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricLanguageProvider(dataOutput, registryLookup) {
	val id = EoCMain.MOD_ID

	override fun getName() = "EssenceOfCreationLanguageProvider(en-us)"

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
		builder.add("block.$id.${EoCBlocks.RYE_BALE_ID}", "Rye Bale")
		builder.add("item.$id.${EoCBlocks.RYE_BALE_ID}", "Rye Bale")
		builder.add("lore.roggen.book_title", RoggenLore.TITLE)
		for (lore in RoggenLore.entries) {
			builder.add("lore.roggen.${lore.name}.header", lore.header)
			for ((num, page) in lore.body.withIndex()) {
				builder.add("lore.roggen.${lore.name}.page$num", page)
			}
		}
		builder.add("item.$id.${EoCItems.TOR_ID}", "Totem of Unrying")
		builder.add("block.$id.${EssentialExtractor.ID}", "Essential Extractor")
		builder.add("item.$id.${EssentialExtractor.ID}", "Essential Extractor")
	}
}

class EoCModelProvider(dataOutput: FabricDataOutput): FabricModelProvider(dataOutput) {

	override fun getName() = "EssenceOfCreationModelProvider"

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
		bsmg.registerSingleton(EoCBlocks.ryeBale, TexturedModel.CUBE_COLUMN)
	}

	override fun generateItemModels(img: ItemModelGenerator) {
		img.register(EoCItems.essenceOfCreation, Models.GENERATED)
		img.register(EoCItems.rye, Models.GENERATED)
		img.register(EoCItems.totemOfUnrying, Models.GENERATED)
	}
}

class EoCBlockLootTableProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricBlockLootTableProvider(dataOutput, registryLookup) {

	override fun getName() = "EssenceOfCreationBlockLootTableProvider"

	override fun generate() {
		addDrop(EoCBlocks.spatialDisplacer)
		addDropWithSilkTouch(EoCBlocks.scaffoldStripper)
	}
}

class EoCRecipeProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricRecipeProvider(dataOutput, registryLookup) {

	override fun getName() = "EssenceOfCreationRecipeProvider"

	override fun getRecipeGenerator(registryLookup: RegistryWrapper.WrapperLookup?, exporter: RecipeExporter?)
		= object : RecipeGenerator(registryLookup, exporter) {
			override fun generate() {
				createShapeless(RecipeCategory.MISC, EoCBlocks.ryeBale, 1).input(EoCItems.rye, 9).offerTo(exporter)
			}
		}
}

class EoCAdvancementProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricAdvancementProvider(dataOutput, registryLookup) {

	override fun getName(): String?  = "EssenceOfCreationAdvancementProvider"

	override fun generateAdvancement(wrapperLookup: RegistryWrapper.WrapperLookup?, exporter: Consumer<AdvancementEntry?>?) {
		val essenceOfCreation = Advancement.Builder.create()
			.display(EoCItems.essenceOfCreation, Text.literal("Essence Of Creation"), Text.literal("Expand your horizons."), Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure"), AdvancementFrame.TASK, true, true, false)
			.criterion("get_eoc",
			InventoryChangedCriterion.Conditions.items(EoCItems.essenceOfCreation))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "got_eoc").toString())

		val ryesAndShine = Advancement.Builder.create().parent(essenceOfCreation)
			.display(EoCItems.rye, Text.literal("Ryes And Shine"), Text.literal("Roggen smiles upon you."), null, AdvancementFrame.TASK, true, true, false)
			.criterion("get_rye", InventoryChangedCriterion.Conditions.items(EoCItems.rye))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "get_rye").toString())

		val ryeNot = Advancement.Builder.create().parent(ryesAndShine)
			.display(EoCBlocks.ryeBale, Text.literal("Rye Not?"), Text.literal("Rye not, indeed?"), null, AdvancementFrame.GOAL, true, true, false)
			.criterion("inv_of_rye", EoCCriteria.ryeNotCriterion.create(RyeNotCriterion.Conditions(Optional.empty())))
			.build(exporter, Identifier.of(EoCMain.MOD_ID, "inventory_of_rye").toString())
	}
}