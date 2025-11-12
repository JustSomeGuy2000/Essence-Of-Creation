package jehr.experiments.essenceOfCreation.datagen

import jehr.experiments.essenceOfCreation.blocks.EoCBlocks
import jehr.experiments.essenceOfCreation.items.EoCItems
import jehr.experiments.essenceOfCreation.tags.EoCTags
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

class EoCItemTagProvider(output: FabricDataOutput, registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>): FabricTagProvider.ItemTagProvider(output, registryLookup) {

	override fun getName() = "EssenceOfCreationItemTagProvider"

	override fun configure(wrapperLookup: RegistryWrapper.WrapperLookup?) {
		valueLookupBuilder(ItemTags.SWORDS)
			.add(EoCItems.cane)
		valueLookupBuilder(EoCTags.upgradeableGunSword)
			.add(EoCItems.diamondGunSword)
			.add(EoCItems.netheriteGunSword)
		valueLookupBuilder(EoCTags.triggersIntentOfCreation)
			.add(EoCBlocks.balefulPoppy.asItem())
			.add(EoCItems.balefulSnowballItem)
			.add(EoCItems.interitorHeart)
	}
}