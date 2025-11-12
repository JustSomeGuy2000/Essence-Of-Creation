package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.blocks.EssentialExtractor
import jehr.experiments.essenceOfCreation.blocks.EssentialInfuser
import jehr.experiments.essenceOfCreation.blocks.ScaffoldStripper
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.utils.CombinedBoolDir
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.block.HayBlock
import net.minecraft.client.data.BlockStateModelGenerator
import net.minecraft.client.data.BlockStateVariantMap
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.Models
import net.minecraft.client.data.TextureKey
import net.minecraft.client.data.TextureMap
import net.minecraft.client.data.TexturedModel
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import kotlin.collections.iterator

class EoCModelProvider(dataOutput: FabricDataOutput): FabricModelProvider(dataOutput) {

	override fun getName() = "EssenceOfCreationModelProvider"

	override fun generateBlockStateModels(bsmg: BlockStateModelGenerator) {

		bsmg.registerSimpleCubeAll(EoCBlocks.scaffoldSeed)
		bsmg.registerSimpleCubeAll(EoCBlocks.scaffoldTrunk)

		val stripperNew = TexturedModel.CUBE_ALL.upload(EoCBlocks.scaffoldStripper, bsmg.modelCollector)
		val stripperSearching = bsmg.createSubModel(
            EoCBlocks.scaffoldStripper, "_searching", Models.CUBE_ALL,
			TextureMap::all)
		val stripperMoving = bsmg.createSubModel(
            EoCBlocks.scaffoldStripper, "_moving", Models.CUBE_ALL,
			TextureMap::all)
		val stripperHalted = bsmg.createSubModel(
            EoCBlocks.scaffoldStripper, "_halted", Models.CUBE_ALL,
			TextureMap::all)
		bsmg.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(EoCBlocks.scaffoldStripper).with(
			BlockStateVariantMap.models(ScaffoldStripper.Companion.progress)
				.register(ScaffoldStripper.Companion.Progress.NEW, BlockStateModelGenerator.createWeightedVariant(stripperNew))
				.register(ScaffoldStripper.Companion.Progress.SEARCHING, BlockStateModelGenerator.createWeightedVariant(stripperSearching))
				.register(ScaffoldStripper.Companion.Progress.MOVING, BlockStateModelGenerator.createWeightedVariant(stripperMoving))
				.register(ScaffoldStripper.Companion.Progress.HALTED, BlockStateModelGenerator.createWeightedVariant(stripperHalted))))

		bsmg.blockStateCollector.accept(
			VariantsBlockModelDefinitionCreator.of(
				EoCBlocks.spatialDisplacer,
				BlockStateModelGenerator.createWeightedVariant(TexturedModel.ORIENTABLE_WITH_BOTTOM.upload(EoCBlocks.spatialDisplacer, bsmg.modelCollector))
			)
		)

		bsmg.registerSimpleCubeAll(EoCBlocks.roggenStatue)
		val ryeBale = BlockStateModelGenerator.createWeightedVariant(TexturedModel.CUBE_COLUMN.upload(EoCBlocks.ryeBale, bsmg.modelCollector))
		bsmg.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(EoCBlocks.ryeBale, ryeBale)
			.coordinate(
                BlockStateVariantMap.operations(HayBlock.AXIS)
				.register(Direction.Axis.X, BlockStateModelGenerator.ROTATE_X_90.then(BlockStateModelGenerator.ROTATE_Y_90))
				.register(Direction.Axis.Y, BlockStateModelGenerator.NO_OP)
				.register(Direction.Axis.Z, BlockStateModelGenerator.ROTATE_X_90)))

		val extractorInactive = BlockStateModelGenerator.createWeightedVariant(
            TexturedModel.ORIENTABLE_WITH_BOTTOM.upload(
                EoCBlocks.essentialExtractor, bsmg.modelCollector))
		// this thing is held together with a cursed enum, intuition, and duct tape
		val extractorActive = BlockStateModelGenerator.createWeightedVariant(bsmg.createSubModel(
            EoCBlocks.essentialExtractor, "_active", Models.ORIENTABLE_WITH_BOTTOM, textTureMapGenerator(mapOf(
                TextureKey.SIDE to "_side", TextureKey.FRONT to "_front", TextureKey.TOP to "_top", TextureKey.BOTTOM to "_bottom"))))
		bsmg.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(EoCBlocks.essentialExtractor).with(
			BlockStateVariantMap.models(EssentialExtractor.Companion.condition)
				.register(CombinedBoolDir.FALSE_NORTH, extractorInactive)
				.register(CombinedBoolDir.FALSE_UP, extractorInactive)
				.register(CombinedBoolDir.FALSE_DOWN, extractorInactive)
				.register(CombinedBoolDir.TRUE_UP, extractorInactive)
				.register(CombinedBoolDir.TRUE_DOWN, extractorInactive)
				.register(CombinedBoolDir.FALSE_EAST, extractorInactive.apply(BlockStateModelGenerator.ROTATE_Y_90))
				.register(CombinedBoolDir.FALSE_SOUTH, extractorInactive.apply(BlockStateModelGenerator.ROTATE_Y_180))
				.register(CombinedBoolDir.FALSE_WEST, extractorInactive.apply(BlockStateModelGenerator.ROTATE_Y_270))
				.register(CombinedBoolDir.TRUE_NORTH, extractorActive)
				.register(CombinedBoolDir.TRUE_EAST, extractorActive.apply(BlockStateModelGenerator.ROTATE_Y_90))
				.register(CombinedBoolDir.TRUE_SOUTH, extractorActive.apply(BlockStateModelGenerator.ROTATE_Y_180))
				.register(CombinedBoolDir.TRUE_WEST, extractorActive.apply(BlockStateModelGenerator.ROTATE_Y_270))
		))

		val infuserInactive = BlockStateModelGenerator.createWeightedVariant(
            TexturedModel.CUBE_BOTTOM_TOP.upload(
			EoCBlocks.essentialInfuser, bsmg.modelCollector))
		val infuserActive = BlockStateModelGenerator.createWeightedVariant(bsmg.createSubModel(
            EoCBlocks.essentialInfuser, "_active", Models.CUBE_BOTTOM_TOP, textTureMapGenerator(mapOf(
			TextureKey.SIDE to "_side", TextureKey.TOP to "_top", TextureKey.BOTTOM to "_bottom"))))
		bsmg.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(EoCBlocks.essentialInfuser).with(
			BlockStateVariantMap.models(EssentialInfuser.Companion.active)
				.register(false, infuserInactive)
				.register(true, infuserActive)
		))
	}

	/**A way to generate texture factories for `BlockStateModelGenerator()$createSubModel`. This is definitely not the right way to do it, but it works. Manually adds a second suffix to an Identifier, then associates it with the TextureKey of the original.*/
	fun textTureMapGenerator(keys: Map<TextureKey, String>): (Identifier) -> TextureMap
			= fun(id: Identifier): TextureMap =
				TextureMap().apply {
					for ((key, suffix) in keys) {
						put(key, Identifier.of(id.toString() + suffix))
					}
				}

	override fun generateItemModels(img: ItemModelGenerator) {
		img.register(EoCItems.essenceOfCreation, Models.GENERATED)
		img.register(EoCItems.rye, Models.GENERATED)
		img.register(EoCItems.totemOfUnrying, Models.GENERATED)
		img.register(EoCItems.handheldInfuser, Models.GENERATED) //TODO: change to 3D in the future
		img.register(EoCItems.godApple, Models.GENERATED)
		img.register(EoCItems.superBoneMeal, Models.GENERATED)
		img.register(EoCItems.cane, Models.HANDHELD)
		img.register(EoCItems.amethystGunSword, Models.HANDHELD)
		img.register(EoCItems.breezeRodGunSword, Models.HANDHELD)
		img.register(EoCItems.sonicGunSword, Models.HANDHELD)
		img.register(EoCItems.emeraldGunSword, Models.HANDHELD)
		img.register(EoCItems.superGunSword, Models.HANDHELD)
		img.register(EoCItems.balefulSnowballItem, Models.GENERATED)
		img.register(EoCItems.interitorHeart, Models.GENERATED)
	}
}