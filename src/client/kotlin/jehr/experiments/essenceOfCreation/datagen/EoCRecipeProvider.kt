package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.block.Blocks
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class EoCRecipeProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricRecipeProvider(dataOutput, registryLookup) {

	override fun getName() = "EssenceOfCreationRecipeProvider"

	override fun getRecipeGenerator(registryLookup: RegistryWrapper.WrapperLookup?, exporter: RecipeExporter?)
		= object : RecipeGenerator(registryLookup, exporter) {
			override fun generate() {
				createShapeless(RecipeCategory.MISC, EoCBlocks.ryeBale, 1)
					.input(EoCItems.rye, 9)
					.criterion(hasItem(EoCItems.rye), conditionsFromItem(EoCItems.rye))
					.offerTo(exporter)
				createShapeless(RecipeCategory.COMBAT, EoCItems.totemOfUnrying, 1)
					.input(Items.TOTEM_OF_UNDYING, 1)
					.input(EoCBlocks.ryeBale, 8)
					.criterion(hasItem(EoCBlocks.ryeBale), conditionsFromItem(EoCBlocks.ryeBale))
					.offerTo(exporter)
				createShaped(RecipeCategory.REDSTONE, EoCBlocks.scaffoldStripper, 1)
					.pattern("iqi")
					.pattern("qsq")
					.pattern("iqi")
					.input('i', Items.IRON_INGOT)
					.input('q', Items.QUARTZ)
					.input('s', EoCBlocks.scaffoldSeed)
					.criterion(hasItem(EoCBlocks.scaffoldSeed), conditionsFromItem(EoCBlocks.scaffoldSeed))
					.offerTo(exporter)
				createShaped(RecipeCategory.MISC, EoCBlocks.essentialExtractor, 1)
					.pattern("ddd")
					.pattern("ifi")
					.pattern("ddd")
					.input('d', Items.COBBLED_DEEPSLATE)
					.input('i', Items.DIAMOND)
					.input('f', Items.FURNACE)
					.criterion(hasItem(EoCItems.essenceOfCreation), conditionsFromItem(EoCItems.essenceOfCreation))
					.offerTo(exporter)
				createShaped(RecipeCategory.MISC, EoCBlocks.essentialInfuser, 1)
					.pattern("i i")
					.pattern("geg")
					.pattern("ooo")
					.input('i', Items.IRON_INGOT)
					.input('g', Items.GLASS)
					.input('e', EoCItems.essenceOfCreation)
					.input('o', Items.OBSIDIAN)
					.criterion(hasItem(EoCItems.essenceOfCreation), conditionsFromItem(EoCItems.essenceOfCreation))
					.offerTo(exporter)
				createShaped(RecipeCategory.MISC, EoCItems.handheldInfuser, 1)
					.pattern("ice")
					.pattern(" hi")
					.input('i', Items.IRON_INGOT)
					.input('h', Items.CHEST)
					.input('c', Items.CROSSBOW)
					.input('e', EoCBlocks.essentialInfuser)
					.criterion(hasItem(EoCBlocks.essentialInfuser), conditionsFromItem(EoCBlocks.essentialInfuser))
					.offerTo(exporter)
				offerNetheriteUpgradeRecipe(EoCItems.diamondGunSword, RecipeCategory.COMBAT, EoCItems.netheriteGunSword)
				createShapeless(RecipeCategory.COMBAT, EoCItems.amethystGunSword, 1)
					.input(EoCTags.upgradeableGunSword)
					.input(Items.AMETHYST_SHARD)
					.criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
					.offerTo(exporter)
				createShapeless(RecipeCategory.COMBAT, EoCItems.breezeRodGunSword, 1)
					.input(EoCTags.upgradeableGunSword)
					.input(Items.BREEZE_ROD)
					.criterion(hasItem(Items.BREEZE_ROD), conditionsFromItem(Items.BREEZE_ROD))
					.offerTo(exporter)
				createShapeless(RecipeCategory.COMBAT, EoCItems.sonicGunSword, 1)
					.input(EoCTags.upgradeableGunSword)
					.input(Items.ECHO_SHARD)
					.criterion(hasItem(Items.ECHO_SHARD), conditionsFromItem(Items.ECHO_SHARD))
					.offerTo(exporter)
				createShapeless(RecipeCategory.COMBAT, EoCItems.emeraldGunSword, 1)
					.input(EoCTags.upgradeableGunSword)
					.input(Items.EMERALD)
					.criterion(hasItem(Items.EMERALD), conditionsFromItem(Items.EMERALD))
					.offerTo(exporter)
				createShaped(RecipeCategory.MISC, EoCBlocks.refractor, 1)
					.pattern("ggg")
					.pattern("ghg")
					.pattern("ooo")
					.input('g', Blocks.GLASS)
					.input('h', EoCItems.interitorHeart)
					.input('o', Blocks.OBSIDIAN)
					.criterion(hasItem(EoCItems.interitorHeart), conditionsFromItem(EoCItems.interitorHeart))
					.offerTo(exporter)
			}
		}
}