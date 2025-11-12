package jehr.experiments.essenceOfCreation.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object EoCDataGen : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		pack.addProvider(::EoCLangProviderEnUs)
		pack.addProvider(::EoCModelProvider)
		pack.addProvider(::EoCBlockLootTableProvider)
		pack.addProvider(::EoCRecipeProvider)
		pack.addProvider(::EoCAdvancementProvider)
		pack.addProvider(::EoCItemTagProvider)
		pack.addProvider(::EoCBlockTagprovider)
		pack.addProvider(::EoCEntityTagProvider)
		pack.addProvider(::EoCEnchantmentProvider)
	}
}